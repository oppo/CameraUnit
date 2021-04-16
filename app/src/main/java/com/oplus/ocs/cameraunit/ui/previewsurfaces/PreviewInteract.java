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
 * File: - PreviewInteract.java
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

package com.oplus.ocs.cameraunit.ui.previewsurfaces;

import android.content.Context;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.oplus.ocs.base.util.Util;
import com.oplus.ocs.cameraunit.util.Constant;
import com.oplus.ocs.camerax.component.preview.PreviewInterface;

import java.util.List;
import java.util.Optional;

public class PreviewInteract implements View.OnTouchListener {
    private static final String TAG = "PreviewInteract";
    private static final double COMPARE_LIMIT = 0.01;

    private float mFingerSpacing = 0f;
    private PreviewInterface.OnPreviewChangeListener onPreviewChangeListener = null;
    private PreviewInterface.PreviewGestureListener mGestureListener = null;
    private GestureDetector mGestureDetector = null;
    private Size mViewSize = null;
    private int screenWidth = 0;
    private int mTopBlank = 0;
    private boolean mbMainPreviewAsCanvas = false;
    private boolean mbFirstFrameDrawn = false;
    private Context context;

    private final GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            if ((null != mGestureListener)
                    && (null != mViewSize)
                    && (Float.compare(x, mViewSize.getWidth()) <= 0)
                    && (Float.compare(y, mViewSize.getHeight()) <= 0)) {
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
                    && (null != mViewSize)
                    && (Float.compare(x, mViewSize.getWidth()) <= 0)
                    && (Float.compare(y, mViewSize.getHeight()) <= 0)) {
                mGestureListener.longPress(x, y);
            }
        }
    };

    public boolean setPreviewSize(List<Size> sizes) {
        boolean result;
        int height = screenWidth * sizes.get(0).getWidth() / sizes.get(0).getHeight();

        if ((((double) sizes.get(0).getWidth() / sizes.get(0).getHeight())
                - Constant.PreviewRatio.RATIO_VALUE_16_9) > COMPARE_LIMIT) {
            mTopBlank = 0;
        } else {
            mTopBlank = screenWidth / 9 + Util.dip2px(context, 35);
        }

        if ((sizes.size() == 1) || (sizes.get(0).getWidth() > sizes.get(1).getWidth())) {
            mbMainPreviewAsCanvas = true;
            Size newSize = new Size(screenWidth, height);

            if (newSize.equals(mViewSize)) {
                result = false;
            } else {
                mViewSize = newSize;
                result = true;
            }
        } else {
            mbMainPreviewAsCanvas = false;
            Size newSize = new Size(screenWidth, height * 2);

            if (newSize.equals(mViewSize)) {
                result = false;
            } else {
                mViewSize = newSize;
                result = true;
            }
        }

        if (null != onPreviewChangeListener) {
            onPreviewChangeListener.onPreviewChange(mViewSize, mTopBlank);
        }

        return result;
    }

    public int getBlank() {
        return mTopBlank;
    }

    public boolean isMainPreviewAsCanvas() {
        return mbMainPreviewAsCanvas;
    }

    public Size getDisplaySize() {
        return mViewSize;
    }

    public void setGestureListener(PreviewInterface.PreviewGestureListener listener) {
        mGestureListener = listener;
    }

    public void setOnPreviewChangeListener(PreviewInterface.OnPreviewChangeListener onPreviewChangeListener) {
        this.onPreviewChangeListener = onPreviewChangeListener;
    }

    public void onSurfaceSet() {
        mbFirstFrameDrawn = false;
    }

    public void onFrameDrawn() {
        if (!mbFirstFrameDrawn) {
            Log.d(TAG, "onDrawFrame, first frame draw finish");

            mbFirstFrameDrawn = true;
        }
    }

    public void init(Context context) {
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mGestureDetector = new GestureDetector(context, mSimpleOnGestureListener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getPointerCount() > 1) {
            float currentFingerSpacing = getFingerSpacing(event);

            if (0 != mFingerSpacing) {
                if (Float.compare(currentFingerSpacing, mFingerSpacing) > 0) {
                    // zoom up
                    Optional.ofNullable(mGestureListener).ifPresent(PreviewInterface.PreviewGestureListener::scaleUp);
                } else if (Float.compare(currentFingerSpacing, mFingerSpacing) < 0) {
                    // zoom down
                    Optional.ofNullable(mGestureListener).ifPresent(PreviewInterface.PreviewGestureListener::scaleDown);
                }
            }

            mFingerSpacing = currentFingerSpacing;
        } else {
            mGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }
}
