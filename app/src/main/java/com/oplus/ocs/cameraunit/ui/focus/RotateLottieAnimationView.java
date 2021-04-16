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
 * File: - RotateLottieAnimationView.java
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_180;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_360;

public class RotateLottieAnimationView extends LottieAnimationView implements Rotatable {
    private static final int ANIMATION_SPEED = 270; // 270 deg/sec
    private static final int MILLISECOND_TO_SECOND = 1000;
    public static final int ONE_SECOND = 1 * MILLISECOND_TO_SECOND;
    public static final float HALF_RATIO = 0.5f;
    private int mCurrentDegree = 0; // [0, 359]
    private int mStartDegree = 0;
    private int mTargetDegree = 0;

    private boolean mbClockwise = false;
    private boolean mbEnableAnimation = true;

    private long mAnimationStartTime = 0;
    private long mAnimationEndTime = 0;

    public RotateLottieAnimationView(Context context) {
        super(context);
    }

    public RotateLottieAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateLottieAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setOrientation(int degree, boolean animation) {
        if (getVisibility() == View.VISIBLE) {
            mbEnableAnimation = animation;
        } else {
            mbEnableAnimation = false;
        }

        // make sure in the range of [0, 359]
        degree = (degree >= 0) ? degree % ORIENTATION_360
                : degree % ORIENTATION_360 + ORIENTATION_360;

        if (degree == mTargetDegree) {
            return;
        }

        mTargetDegree = degree;

        if (mbEnableAnimation) {
            mStartDegree = mCurrentDegree;
            mAnimationStartTime = AnimationUtils.currentAnimationTimeMillis();

            int diff = mTargetDegree - mCurrentDegree;
            // make it in range [0, 359]
            diff = (diff >= 0) ? diff : ORIENTATION_360 + diff;

            // Make it in range [-179, 180]. That's the shorted distance between the
            // two angles
            diff = (diff > ORIENTATION_180) ? diff - ORIENTATION_360 : diff;

            mbClockwise = diff >= 0;
            mAnimationEndTime = mAnimationStartTime + Math.abs(diff) * ONE_SECOND / ANIMATION_SPEED;
        } else {
            mCurrentDegree = mTargetDegree;
        }

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();

        if (null == drawable) {
            return;
        }

        Rect bounds = drawable.getBounds();
        int w = bounds.right - bounds.left;
        int h = bounds.bottom - bounds.top;

        if ((w == 0) || (h == 0)) {
            return; // nothing to draw
        }

        if (mCurrentDegree != mTargetDegree) {
            long time = AnimationUtils.currentAnimationTimeMillis();

            if (time < mAnimationEndTime) {
                int deltaTime = (int) (time - mAnimationStartTime);
                int degree = mStartDegree + ANIMATION_SPEED * (mbClockwise ? deltaTime : -deltaTime) / MILLISECOND_TO_SECOND;
                degree = (degree >= 0) ? degree % ORIENTATION_360
                        : degree % ORIENTATION_360 + ORIENTATION_360;
                mCurrentDegree = degree;
                invalidate();
            } else {
                mCurrentDegree = mTargetDegree;
            }
        }

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();
        int width = getWidth() - left - right;
        int height = getHeight() - top - bottom;

        int saveCount = canvas.getSaveCount();

        // Scale down the image first if required.
        if ((getScaleType() == ImageView.ScaleType.FIT_CENTER) && ((width < w) || (height < h))) {
            float ratio = Math.min((float) width / w, (float) height / h);
            canvas.scale(ratio, ratio, width * HALF_RATIO, height * HALF_RATIO);
        }

        canvas.translate(left + width / 2, top + height / 2);
        canvas.rotate(-mCurrentDegree);
        canvas.translate(-w / 2, -h / 2);
        drawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }
}
