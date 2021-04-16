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
 * File: - ExposureControlSeekBar.java
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
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.ocs.cameraunit.R;

import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_0;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_180;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_270;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_90;

public class ExposureControlSeekBar extends View implements Rotatable {
    public static final String TAG = "ExposureControlSeekBar";
    public static final int MAX_VALUE = 100;
    private static final int DRAWABLE_GAP = 8;
    private static final int BAR_WIDTH = 3;

    private float mValue = 0f;
    private Drawable mBottomDrawable = null;
    private OnSeekBarChangeListener mOnSeekBarChangeListener = null;
    private float mXCoordinate = -1;
    private boolean mbShowBar = false;
    private int mExporebarHeight = 0;
    private int mOrientation = 0;
    private Paint mLinePaint = null;
    private Path mLinePath = null;

    public ExposureControlSeekBar(Context context) {
        this(context, null);
    }

    public ExposureControlSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExposureControlSeekBar(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public ExposureControlSeekBar(Context context, AttributeSet attrs, int defStyle, int styleRes) {
        super(context, attrs);
        Resources resources = context.getResources();
        mExporebarHeight = resources.getDimensionPixelSize(R.dimen.exporebar_height);
        initProgressBar();

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(BAR_WIDTH);
        mLinePaint.setPathEffect(new DashPathEffect(new float[]{resources.getDimensionPixelSize(
                R.dimen.focus_view_exposurebar_dash_width),
                resources.getDimensionPixelSize(R.dimen.focus_view_exposurebar_dash_gap)}, 0));
        mLinePaint.setColor(context.getColor(R.color.default_style_color));
        mLinePath = new Path();
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        mOnSeekBarChangeListener = listener;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initProgressBar() {
        mBottomDrawable = getContext().getDrawable(R.drawable.exposure_control_bar_bottom);
        setProgress(MAX_VALUE / 2f);
    }

    public float getValue() {
        return mValue;
    }

    private void setProgress(float progress) {
        mValue = progress;

        if (getHeight() == 0) {
            return;
        }

        mXCoordinate = mExporebarHeight - mBottomDrawable.getIntrinsicWidth() / 2
                - progress / MAX_VALUE * (mExporebarHeight - mBottomDrawable.getIntrinsicWidth());
        refreshProgress();
    }

    private void drawButton(Canvas canvas) {
        switch (mOrientation) {
            case ORIENTATION_0:
            case ORIENTATION_180:
                mBottomDrawable.setBounds(0, (int) mXCoordinate - mBottomDrawable.getIntrinsicWidth() / 2,
                        mBottomDrawable.getIntrinsicWidth(), (int) mXCoordinate + mBottomDrawable.getIntrinsicWidth() / 2);
                break;

            case ORIENTATION_90:
                canvas.rotate(-ORIENTATION_90, (int) mXCoordinate, mBottomDrawable.getIntrinsicWidth() / 2);
                mBottomDrawable.setBounds((int) mXCoordinate - mBottomDrawable.getIntrinsicWidth() / 2, 0,
                        (int) mXCoordinate + mBottomDrawable.getIntrinsicWidth() / 2, mBottomDrawable.getIntrinsicWidth());
                break;

            case ORIENTATION_270:
                canvas.rotate(ORIENTATION_90, mExporebarHeight - (int) mXCoordinate,
                        mBottomDrawable.getIntrinsicWidth() / 2);
                mBottomDrawable.setBounds((mExporebarHeight - (int) mXCoordinate) - mBottomDrawable.getIntrinsicWidth() / 2, 0,
                        (mExporebarHeight - (int) mXCoordinate) + mBottomDrawable.getIntrinsicWidth() / 2,
                        mBottomDrawable.getIntrinsicWidth());
                break;

            default:
                break;
        }

        mBottomDrawable.draw(canvas);
    }

    private void drawUnFilled(Canvas canvas) {
        mLinePath.reset();

        switch (mOrientation) {
            case ORIENTATION_0:
            case ORIENTATION_180:
                mLinePath.moveTo(mBottomDrawable.getIntrinsicWidth() / 2, 0);
                mLinePath.lineTo(mBottomDrawable.getIntrinsicWidth() / 2,
                        (int) mXCoordinate - mBottomDrawable.getIntrinsicHeight() / 2 - DRAWABLE_GAP);
                canvas.drawPath(mLinePath, mLinePaint);
                break;

            case ORIENTATION_90:
                mLinePath.moveTo(0, mBottomDrawable.getIntrinsicWidth() / 2);
                mLinePath.lineTo((int) mXCoordinate - mBottomDrawable.getIntrinsicHeight() / 2 - DRAWABLE_GAP,
                        mBottomDrawable.getIntrinsicWidth() / 2);
                canvas.drawPath(mLinePath, mLinePaint);
                break;

            case ORIENTATION_270:
                int lineToX = mExporebarHeight - (int) mXCoordinate + mBottomDrawable.getIntrinsicHeight() / 2 + DRAWABLE_GAP;

                if (lineToX > mExporebarHeight) {
                    lineToX = mExporebarHeight;
                }

                mLinePath.moveTo(mExporebarHeight, mBottomDrawable.getIntrinsicWidth() / 2);
                mLinePath.lineTo(lineToX, mBottomDrawable.getIntrinsicWidth() / 2);
                canvas.drawPath(mLinePath, mLinePaint);
                break;

            default:
                break;
        }
    }

    private void drawFilled(Canvas canvas) {
        mLinePath.reset();

        switch (mOrientation) {
            case ORIENTATION_0:
            case ORIENTATION_180:
                mLinePath.moveTo(mBottomDrawable.getIntrinsicWidth() / 2, getHeight());
                mLinePath.lineTo(mBottomDrawable.getIntrinsicWidth() / 2,
                        (int) mXCoordinate + mBottomDrawable.getIntrinsicHeight() / 2
                                + DRAWABLE_GAP);
                canvas.drawPath(mLinePath, mLinePaint);
                break;

            case ORIENTATION_90:
                int lineToX = (int) mXCoordinate + mBottomDrawable.getIntrinsicHeight() / 2
                        + DRAWABLE_GAP;

                if (lineToX > mExporebarHeight) {
                    lineToX = mExporebarHeight;
                }

                mLinePath.moveTo(getHeight(), mBottomDrawable.getIntrinsicWidth() / 2);
                mLinePath.lineTo(lineToX, mBottomDrawable.getIntrinsicWidth() / 2);
                canvas.drawPath(mLinePath, mLinePaint);
                break;

            case ORIENTATION_270:
                mLinePath.moveTo(0, mBottomDrawable.getIntrinsicWidth() / 2);
                mLinePath.lineTo((mExporebarHeight - (int) mXCoordinate)
                        - mBottomDrawable.getIntrinsicHeight() / 2
                        - DRAWABLE_GAP, mBottomDrawable.getIntrinsicWidth() / 2);
                canvas.drawPath(mLinePath, mLinePaint);
                break;

            default:
                break;
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        if (mXCoordinate < 0) {
            setProgress(MAX_VALUE / 2f);
        }

        if (mbShowBar) {
            drawUnFilled(canvas);
            drawFilled(canvas);
        }

        drawButton(canvas);
        canvas.restore();
    }

    private void clamp(int min, int max) {
        if (mXCoordinate > max) {
            mXCoordinate = max;
        }

        if (mXCoordinate < min) {
            mXCoordinate = min;
        }
    }

    public void setMoveProgress(float progress) {
        float currentProgress = mValue + progress;

        if (currentProgress < 0) {
            currentProgress = 0;
        }

        if (currentProgress > MAX_VALUE) {
            currentProgress = MAX_VALUE;
        }

        setProgress(currentProgress);
        mOnSeekBarChangeListener.onProgressMoveChanged(currentProgress);
    }

    public void resetProgress() {
        setProgress(MAX_VALUE / 2f);
    }

    private void refreshProgress() {
        clamp(mBottomDrawable.getIntrinsicWidth() / 2, mExporebarHeight - mBottomDrawable.getIntrinsicWidth() / 2);

        if (null != mOnSeekBarChangeListener) {
            mOnSeekBarChangeListener.onProgressChange(mValue / MAX_VALUE);
        }

        invalidate();
    }

    public void setBarVisibility(boolean visibility) {
        mbShowBar = visibility;
        invalidate();
    }

    @Override
    public void setOrientation(int orientation, boolean animation) {
        if (mOrientation == orientation) {
            return;
        }

        mOrientation = orientation;

        if (null != mOnSeekBarChangeListener) {
            mOnSeekBarChangeListener.onOrientationChange(mOrientation);
        }
    }

    public interface OnSeekBarChangeListener {
        void onProgressChange(float progress);

        void onOrientationChange(int orientation);

        void onProgressMoveChanged(float progress);
    }
}
