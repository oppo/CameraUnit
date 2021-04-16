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
 * File: - SurfacePreview.java
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

package com.oplus.ocs.cameraunit.ui.previewsurfaces.view;

import android.app.Activity;
import android.content.Context;
import android.os.ConditionVariable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.oplus.ocs.cameraunit.ui.previewsurfaces.PreviewInteract;
import com.oplus.ocs.cameraunit.util.Constant;
import com.oplus.ocs.camerax.component.preview.PreviewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SurfacePreview extends SurfaceView implements PreviewInterface, SurfaceHolder.Callback2 {
    private static final String TAG = "SurfacePreview";

    private final PreviewInteract mInteract = new PreviewInteract();
    private SurfaceHolder mSurfaceHolder = null;
    private final ConditionVariable mSurfaceCondition = new ConditionVariable();

    @Override
    public Size getDisplaySize() {
        synchronized (this) {
            return mInteract.getDisplaySize();
        }
    }

    @Override
    public int getTopBlank() {
        synchronized (this) {
            return mInteract.getBlank();
        }
    }

    @Override
    public void setSensorOrientation(int orientation) {
        // do nothing by default
    }

    @Override
    public int getSurfaceType() {
        return Constant.CameraSurfaceType.SURFACE_VIEW;
    }

    @Override
    public void setGestureListener(PreviewGestureListener listener) {
        synchronized (this) {
            setOnTouchListener(mInteract);
            mInteract.setGestureListener(listener);
        }
    }

    @Override
    public void setOnPreviewChangeListener(OnPreviewChangeListener listener) {
        synchronized (this) {
            mInteract.setOnPreviewChangeListener(listener);
        }
    }

    public SurfacePreview(Context context) {
        super(context);

        getHolder().addCallback(this);

        synchronized (this) {
            mInteract.init(context);
        }
    }

    public SurfacePreview(Context context, AttributeSet attrs) {
        super(context, attrs);

        getHolder().addCallback(this);

        synchronized (this) {
            mInteract.init(context);
        }
    }

    @Override
    public Map<String, Surface> buildSurface(LinkedHashMap<String, Size> sizes, Handler recvHandler) {
        Log.d(TAG, "buildSurface");

        boolean needResize = false;

        synchronized (this) {
            needResize = mInteract.setPreviewSize(new ArrayList<>(sizes.values()));
        }

        if (needResize) {
            mSurfaceCondition.close();
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) SurfacePreview.this.getLayoutParams();

                    synchronized (this) {
                        params.height = mInteract.getDisplaySize().getHeight();
                        params.width = mInteract.getDisplaySize().getWidth();
                        params.setMargins(0, mInteract.getBlank(), 0, 0);
                    }

                    SurfacePreview.this.setLayoutParams(params);
                }
            });
        }

        mSurfaceCondition.block();

        Map<String, Surface> result = new HashMap<>();
        result.put(sizes.entrySet().iterator().next().getKey(), mSurfaceHolder.getSurface());
        return result;
    }

    @Override
    public void destroyPreviewBuffer() {
        // do nothing
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        mSurfaceCondition.open();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
        mSurfaceCondition.open();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        synchronized (this) {
            mInteract.onFrameDrawn();
        }
    }
}
