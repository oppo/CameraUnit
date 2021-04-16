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
 * File: - FocusTouchListener.java
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

package com.oplus.ocs.cameraunit.ui.focus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.ocs.cameraunit.R;
import com.oplus.ocs.base.util.Util;

public class FocusTouchListener implements View.OnTouchListener {
    private static final String TAG = "FocusTouchListener";
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private Size mPreviewSize;
    private int mTopBlank;
    private float mCurrentZoomValue = 1.0f;

    public static final float ZOOM_1X_VALUE = 1.0f;
    private static final float GESTURE_SET_ZOOM_STEP = 0.1f;
    private float mMaxZoomValue = 0;
    private float mMinZoomValue = 0;
    private int mScaleGestureStep = 0;
    private boolean isSupportZoom = false;
    private boolean mbClickable = true;

    public void setClickable(boolean clickable) {
        this.mbClickable = clickable;
    }

    public void init(Context context) {
        mScaleGestureStep = context.getResources().getDimensionPixelSize(R.dimen.zoom_gesture_scale_step);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            private float mStartSpan = 0.0f;
            private float mStartZoomValue = 0;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float currentSpan = detector.getCurrentSpan();
                float scaleTotal = currentSpan - mStartSpan;
                float zoomValue = getZoomByScale(mStartZoomValue, scaleTotal);
                mCurrentZoomValue = zoomValue;

                if (null != mGestureListener) {
                    mGestureListener.scale(zoomValue);
                }

                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                if (!mbClickable) {
                    return false;
                }

                if (!isSupportZoom) {
                    return false;
                }

                mStartSpan = detector.getCurrentSpan();
                mStartZoomValue = mCurrentZoomValue;

                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                // do nothing
            }
        });

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (!mbClickable) {
                    return false;
                }

                float x = e.getX();
                float y = e.getY();
                Log.d(TAG, "onSingleTapUp x: " + x + ", y: " + y);

                if ((null != mGestureListener)
                        && (null != mPreviewSize)
                        && (Float.compare(x, mPreviewSize.getWidth()) <= 0)
                        && y >= mTopBlank
                        && y <= mPreviewSize.getHeight() + mTopBlank) {
                    mGestureListener.singleTapUp(x, y);
                }

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                float x = e.getX();
                float y = e.getY();

                if ((null != mGestureListener)
                        && (null != mPreviewSize)
                        && (Float.compare(x, mPreviewSize.getWidth()) <= 0)
                        && y >= mTopBlank
                        && y <= mPreviewSize.getHeight() + mTopBlank) {
                    mGestureListener.longPress(x, y);
                }
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!mbClickable) {
                    return false;
                }

                mGestureListener.onScroll(e1, e2, distanceX, distanceY);

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                mGestureListener.onFling(e1, e2, velocityX, velocityY);

                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                if (!mbClickable) {
                    return false;
                }

                mGestureListener.onDown(e);

                return super.onDown(e);
            }
        });
    }

    public void setGestureListener(GestureListener listener) {
        mGestureListener = listener;
    }

    public void setPreviewSize(Size mPreviewSize, int topBlank) {
        this.mPreviewSize = mPreviewSize;
        this.mTopBlank = topBlank;
    }

    public void setCurrentZoomValue(float currentZoomValue) {
        mCurrentZoomValue = currentZoomValue;
    }

    public void setZoomParameter(float minZoomValue, float maxZoomValue) {
        mMinZoomValue = minZoomValue;
        mMaxZoomValue = maxZoomValue;
    }

    public void setSupportZoom(boolean supportZoom) {
        isSupportZoom = supportZoom;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getPointerCount() > 1) {
            if (mMaxZoomValue > 0 && mMinZoomValue > 0) {
                mScaleGestureDetector.onTouchEvent(event);
            }
        } else {
            boolean detectedUp = event.getAction() == MotionEvent.ACTION_UP;

            if (!mGestureDetector.onTouchEvent(event) && detectedUp) {
                mGestureListener.onUp(event);
            }
        }

        return true;
    }

    public float getZoomByScale(float mStartZoomValue, float scaleTotal) {
        if (mMaxZoomValue < ZOOM_1X_VALUE) {
            return 0;
        }

        float value = mStartZoomValue + (scaleTotal / mScaleGestureStep) * GESTURE_SET_ZOOM_STEP;
        float minZoomValue = mMinZoomValue;

        return Util.clampFloat(value, minZoomValue, mMaxZoomValue);
    }
}
