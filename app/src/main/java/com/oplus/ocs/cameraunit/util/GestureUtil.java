/*
 * Copyright (c) 2021 OPPO.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * File: - GestureUtil.java
 * Description:
 *     N/A
 *
 * Version: 1.0.0
 * Date: 2021-04-09
 * Owner: Jero Yang
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~ Revision History ~~~~~~~~~~~~~~~~~~~~~~~
 * <author>             <date>           <version>              <desc>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Jero Yang           2021-04-09           1.0.0         project init
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.oplus.ocs.cameraunit.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieOnCompositionLoadedListener;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.ocs.cameraunit.R;
import com.oplus.ocs.cameraunit.ui.focus.ExposureControlSeekBar;
import com.oplus.ocs.cameraunit.ui.focus.FocusIndicatorRotateLayout;
import com.oplus.ocs.cameraunit.ui.focus.FocusTouchListener;
import com.oplus.ocs.cameraunit.ui.focus.GestureListener;
import com.oplus.ocs.cameraunit.ui.focus.RotateLottieAnimationView;
import com.oplus.ocs.base.util.Util;
import com.oplus.ocs.cameraunit.ui.ZoomView;

import java.util.List;

public class GestureUtil implements ExposureControlSeekBar.OnSeekBarChangeListener, GestureListener,
        ZoomView.ZoomClickListener {
    private static final String TAG = "GestureUtil";
    private static final int MSG_HIDE_ALL_FOCUS_VIEW = 11;
    private static final int CLEAR_FOCUS_VIEW_DELAY = 2000;

    private final Activity mActivity;
    private final RelativeLayout mFrameLayout;
    private FocusIndicatorRotateLayout mFocusIndicator = null;
    private ExposureControlSeekBar mExposureControlBar = null;
    private RotateLottieAnimationView mExposureAnimView = null;
    private View mExposureContainer = null;
    private final Handler mHandler;
    private GestureConfigListener mGestureConfigListener;

    private static final int EXPOSURE_IDLE = -1;
    private static final int EXPOSURE_MIN_DISTANCE = 5;
    private static final int MOVE_MIN_DISTANCE = 8;
    private int mExposureState = EXPOSURE_IDLE;

    private final int mFocusWidth;
    private final int mFocusHeight;
    private final int mExposureContainerWidth;
    private final int mExposureContainerHeight;
    private final int mExposureBarMargin;

    private final FocusTouchListener focusTouchListener;
    private int mPreviewWidth; // The width of the preview frame layout.
    private int mPreviewHeight; // The height of the preview frame layout.
    private int mTopBlank;

    private final ZoomView mZoomView;
    private float mCurrentZoomValue = 1.0f;
    private boolean mFlingAble = true;

    @SuppressLint("ClickableViewAccessibility")
    public GestureUtil(Activity activity) {
        mActivity = activity;
        mFrameLayout = mActivity.findViewById(R.id.gesture_layout);
        mZoomView = mActivity.findViewById(R.id.zoom_change_view);
        mHandler = new MainHandler(mActivity.getMainLooper());

        Resources resources = mActivity.getResources();
        mFocusWidth = resources.getDimensionPixelSize(R.dimen.focus_view_size);
        mFocusHeight = resources.getDimensionPixelSize(R.dimen.focus_view_size);
        mExposureContainerHeight = resources.getDimensionPixelSize(R.dimen.exposure_container_size);
        mExposureContainerWidth = resources.getDimensionPixelSize(R.dimen.exposure_container_size);
        mExposureBarMargin = resources.getDimensionPixelSize(R.dimen.focus_view_exposurebar_margin);
        mPreviewWidth = Util.getScreenWidth(mActivity);
        mPreviewHeight = Util.getScreenHeight(mActivity);

        focusTouchListener = new FocusTouchListener();
        focusTouchListener.init(activity);
        focusTouchListener.setGestureListener(this);
        mFrameLayout.setOnTouchListener(focusTouchListener);
        mZoomView.setListener(this);
    }

    public void initZoomValue(float currentZoom, boolean isSupportZoom, List<String> zoomPoints) {
        focusTouchListener.setSupportZoom(isSupportZoom);
        mZoomView.setSupportZoom(isSupportZoom);
        mCurrentZoomValue = currentZoom;
        focusTouchListener.setCurrentZoomValue(mCurrentZoomValue);

        if (isSupportZoom && (null != zoomPoints) && (zoomPoints.size() > 0)) {
            mZoomView.initZoomValues(mCurrentZoomValue, zoomPoints);
            float minZoomValue = Float.parseFloat(zoomPoints.get(0));
            float maxZoomValue = Float.parseFloat(zoomPoints.get(zoomPoints.size() - 1));
            focusTouchListener.setZoomParameter(minZoomValue, maxZoomValue);
        } else {
            mZoomView.initZoomValues(mCurrentZoomValue, null);
        }
    }

    public void setClickable(boolean clickable) {
        mZoomView.setClickable(clickable);
        focusTouchListener.setClickable(clickable);
    }

    public void setFlingAble(boolean flingAble){
        mFlingAble = flingAble;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void initFocusExposureIndicator() {
        if ((null == mFocusIndicator) || (null == mExposureControlBar)) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            inflater.inflate(R.layout.focus_exposure_indicator, mFrameLayout);

            if (null == mFocusIndicator) {
                mFocusIndicator = mActivity.findViewById(R.id.focus_indicator_rotate_layout);
                mFocusIndicator.setBackground(mActivity.getDrawable(R.drawable.ic_focus_indicator));
                mFocusIndicator.setRotation(0);
            }

            if (null == mExposureContainer) {
                mExposureContainer = mActivity.findViewById(R.id.exposure_container);
            }

            if (null == mExposureAnimView) {
                mExposureAnimView = mActivity.findViewById(R.id.exposure_animation_view);
                mExposureAnimView.setBackground(mActivity.getDrawable(R.drawable.exposure_anim_bg));
                mExposureAnimView.setAnimation(R.raw.camera_exposure_anim);
                int tintColor = mActivity.getColor(R.color.default_style_color);
                mExposureAnimView.addLottieOnCompositionLoadedListener(new LottieOnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(LottieComposition composition) {
                        List<KeyPath> list = mExposureAnimView.resolveKeyPath(new KeyPath("**"));

                        for (KeyPath path : list) {
                            mExposureAnimView.addValueCallback(path, LottieProperty.COLOR, lottieFrameInfo -> tintColor);

                            mExposureAnimView.addValueCallback(path, LottieProperty.STROKE_COLOR, lottieFrameInfo -> tintColor);
                        }
                    }
                });
            }

            if (null == mExposureControlBar) {
                mExposureControlBar = mActivity.findViewById(R.id.exposure_indicator_rotate_layout);
                mExposureControlBar.setForceDarkAllowed(false);
                mExposureControlBar.setOnSeekBarChangeListener(this);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mExposureControlBar.getLayoutParams();
                layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.focus_indicator_rotate_layout);
                layoutParams.addRule(RelativeLayout.ALIGN_TOP, R.id.focus_indicator_rotate_layout);

                mExposureControlBar.setOrientation(0, false);
            }
        }
    }

    public void setPreviewSize(Size size, int topBlank) {
        if (null != size && (size.getWidth() != mPreviewWidth || size.getHeight() != mPreviewHeight)) {
            Log.d(TAG, "size height: " + size.getHeight() + ", size width: " + size.getWidth());

            this.mTopBlank = topBlank;
            focusTouchListener.setPreviewSize(size, topBlank);
            mPreviewWidth = size.getWidth();
            mPreviewHeight = size.getHeight();
        }
    }

    public void setGestureConfigListener(GestureConfigListener mGestureConfigListener) {
        this.mGestureConfigListener = mGestureConfigListener;
    }

    private void resetManualExposure(boolean resetExposureCompensation) {
        if (null != mExposureControlBar) {
            mExposureControlBar.setVisibility(View.INVISIBLE);
            mExposureControlBar.setBarVisibility(false);
        }

        if ((null != mExposureControlBar)
                && resetExposureCompensation
                && ((mExposureState != EXPOSURE_IDLE)
                || (Float.compare(mExposureControlBar.getValue(),
                (float) ExposureControlSeekBar.MAX_VALUE / 2) != 0))) {
            mExposureControlBar.resetProgress();
        }

        mExposureState = EXPOSURE_IDLE;
    }

    public void changeExposureBarLayoutParameter(int focusLeft, int focusWidth) {
        RelativeLayout.LayoutParams exposureControlParams = (RelativeLayout.LayoutParams) mExposureControlBar.getLayoutParams();

        if (focusLeft + focusWidth >= mPreviewWidth - EXPOSURE_MIN_DISTANCE) {
            exposureControlParams.setMargins(-focusWidth, 0, 0, 0);
        } else {
            exposureControlParams.setMargins(mExposureBarMargin, 0, 0, 0);
        }
    }

    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.v(TAG, "handleMessage, msg: " + msg.what);

            if (MSG_HIDE_ALL_FOCUS_VIEW == msg.what) {
                if (null != mFocusIndicator) {
                    mFocusIndicator.clear();
                    resetManualExposure(false);
                }

                if (null != mExposureContainer) {
                    mExposureContainer.setVisibility(View.GONE);
                }

                if (null != mGestureConfigListener) {
                    mGestureConfigListener.cancelAutoFocus();
                }
            }
        }
    }

    @Override
    public void onOrientationChange(int orientation) {
        Log.v(TAG, "onOrientationChange, orientation: " + orientation);
    }

    @Override
    public void onProgressMoveChanged(float progress) {
        Log.v(TAG, "onProgressMoveChanged, progress: " + progress);

        float ratio = 1 - progress / ExposureControlSeekBar.MAX_VALUE;
        mExposureAnimView.setProgress(ratio);
    }

    @Override
    public void longPress(float x, float y) {
        Log.d(TAG, "longPress");
    }

    @Override
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if ((null == mExposureControlBar) || (mExposureControlBar.getVisibility() != View.VISIBLE)) {
            return;
        }

        if (Math.abs(distanceY) > MOVE_MIN_DISTANCE) {
            if (null != mFocusIndicator) {
                mFocusIndicator.setVisibility(View.INVISIBLE);
            }

            if (null != mExposureContainer) {
                mExposureContainer.setVisibility(View.VISIBLE);
                float progress = distanceY / mPreviewHeight * 100;
                mExposureControlBar.setBarVisibility(true);
                mExposureControlBar.setMoveProgress(progress);
            }
        }
    }

    @Override
    public void onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling velocityX: " + velocityX + ", velocityY: " + velocityY);

        if ((null != mFocusIndicator && mFocusIndicator.getVisibility() == View.VISIBLE)
                || (null != mExposureContainer && mExposureContainer.getVisibility() == View.VISIBLE)) {
            return;
        }

        if (!mFlingAble) {
            return;
        }

        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            if (velocityX > 0) {
                mGestureConfigListener.switchToNextLeft();
            } else {
                mGestureConfigListener.switchToNextRight();
            }
        }
    }

    @Override
    public void onDown(MotionEvent e1) {
        mHandler.removeMessages(MSG_HIDE_ALL_FOCUS_VIEW);
    }

    @Override
    public void onUp(MotionEvent e1) {
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_ALL_FOCUS_VIEW, CLEAR_FOCUS_VIEW_DELAY);
    }

    @Override
    public void singleTapUp(float x, float y) {
        Log.d(TAG, "singleTapUp");

        resetManualExposure(true);
        initFocusExposureIndicator();

        int focusWidth = mFocusWidth;
        int focusHeight = mFocusHeight;
        int exposureContainerWidth = mExposureContainerWidth;
        int exposureContainerHeight = mExposureContainerHeight;

        int previewWidth = mPreviewWidth;
        int previewHeight = mPreviewHeight;

        // Use margin to set the focus indicator to the touched area.
        RelativeLayout.LayoutParams focusIndicatorParams = (RelativeLayout.LayoutParams) mFocusIndicator.getLayoutParams();
        int left = Util.clampInt((int) (x - (float) focusWidth / 2f), 0, previewWidth - focusWidth);
        int top = Util.clampInt((int) (y - (float) focusHeight / 2f), 0, previewHeight + mTopBlank - focusHeight);
        focusIndicatorParams.setMargins(left, top, 0, 0);
        // Disable "center" rule because we no longer want to put it in the center.
        int[] focusIndicatorRules = focusIndicatorParams.getRules();
        focusIndicatorRules[RelativeLayout.CENTER_IN_PARENT] = 0;
        mFocusIndicator.requestLayout();

        RelativeLayout.LayoutParams exposureAnimParams = (RelativeLayout.LayoutParams) mExposureContainer.getLayoutParams();
        left = Util.clampInt((int) (x - (float) exposureContainerWidth / 2f), 0, previewWidth - exposureContainerWidth);
        top = Util.clampInt((int) (y - (float) exposureContainerHeight / 2f), 0, previewHeight + mTopBlank
                - exposureContainerHeight);
        exposureAnimParams.setMargins(left, top, 0, 0);
        // Disable "center" rule because we no longer want to put it in the center.
        int[] rules = exposureAnimParams.getRules();
        rules[RelativeLayout.CENTER_IN_PARENT] = 0;
        mExposureContainer.requestLayout();

        changeExposureBarLayoutParameter(left, focusWidth);

        mExposureContainer.setVisibility(View.GONE);
        mFocusIndicator.showStart();

        if (mFocusIndicator.getVisibility() == View.VISIBLE) {
            mExposureControlBar.setVisibility(View.VISIBLE);
        }

        float rectLeft = (x - (float) focusWidth / 2) / mPreviewWidth;
        float rectTop = (y - (float) focusWidth / 2) / (mPreviewHeight + mTopBlank);
        float rectRight = (x + (float) focusWidth / 2) / mPreviewWidth;
        float rectBottom = (y + (float) focusWidth / 2) / (mPreviewHeight + mTopBlank);

        if (null != mGestureConfigListener) {
            mGestureConfigListener.autoFocus(new RectF(rectLeft, rectTop, rectRight, rectBottom));
        }

        mHandler.removeMessages(MSG_HIDE_ALL_FOCUS_VIEW);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_ALL_FOCUS_VIEW, CLEAR_FOCUS_VIEW_DELAY);
    }

    @Override
    public void onProgressChange(float progress) {
        Log.v(TAG, "onProgressChange, progress: " + progress);

        if (null != mGestureConfigListener) {
            mGestureConfigListener.exposureCompensation(progress);
        }
    }

    @Override
    public void scale(float value) {
        mCurrentZoomValue = value;
        mZoomView.onZoomChange(mCurrentZoomValue);

        if (null != mGestureConfigListener) {
            mGestureConfigListener.zoomChange(mCurrentZoomValue);
        }
    }

    @Override
    public void zoomClick(float zoomValue) {
        mCurrentZoomValue = zoomValue;
        focusTouchListener.setCurrentZoomValue(mCurrentZoomValue);

        if (null != mGestureConfigListener) {
            mGestureConfigListener.zoomChange(mCurrentZoomValue);
        }
    }

    public interface GestureConfigListener {
        void autoFocus(RectF rectF);

        void cancelAutoFocus();

        void exposureCompensation(float value);

        void zoomChange(float value);

        void switchToNextRight();

        void switchToNextLeft();
    }
}
