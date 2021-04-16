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
 * File: - TexturePreview.java
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
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.widget.LinearLayout;

import androidx.annotation.UiThread;

import com.oplus.ocs.cameraunit.ui.previewsurfaces.PreviewInteract;
import com.oplus.ocs.cameraunit.util.Constant;
import com.oplus.ocs.camerax.component.preview.PreviewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TexturePreview extends TextureView implements PreviewInterface, TextureView.SurfaceTextureListener {
    private static final String TAG = "TexturePreview";
    private static final Object STATIC_THREAD_LOCK = new Object();
    private static final int THREAD_WAIT_TIMEOUT = 3000;
    private Surface mSurface = null;
    private final PreviewInteract mInteract = new PreviewInteract();
    private boolean mbSurfaceCreated = false;

    public TexturePreview(Context context) {
        super(context);
        init(context);
    }

    public TexturePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @UiThread
    private void init(Context context) {
        setSurfaceTextureListener(this);

        synchronized (this) {
            mInteract.init(context);
        }
    }

    @Override
    public void setSensorOrientation(int orientation) {
        // do nothing by default
    }

    @Override
    public int getSurfaceType() {
        return Constant.CameraSurfaceType.SURFACE_TEXTURE;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        setSurfaceCreated(true);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        setSurfaceCreated(true);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        synchronized (this) {
            mInteract.onFrameDrawn();
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
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) TexturePreview.this.getLayoutParams();

                    synchronized (TexturePreview.this) {
                        params.height = mInteract.getDisplaySize().getHeight();
                        params.width = mInteract.getDisplaySize().getWidth();
                        params.setMargins(0, mInteract.getBlank(), 0, 0);
                    }

                    TexturePreview.this.setLayoutParams(params);
                }
            });
        }

        if (!waitSurfaceCreateIfNeeded()) {
            Log.d(TAG, "buildSurface, Surface not created, so return null.");

            // If mbSurfaceCreated is false when thread been notify, that means onSurfaceCreated is not called in current
            // object, when a new instance been created at onCreate will create a new SurfaceTexturePreview object and call
            // onSurfaceCreated() and will notify the old object's wait, so return a null obj to open the block thread.
            return null;
        }

        Map<String, Surface> result = new HashMap<>();
        result.put(sizes.entrySet().iterator().next().getKey(), mSurface);
        return result;
    }

    @Override
    public void destroyPreviewBuffer() {
        // do nothing by default
    }

    @Override
    public void setGestureListener(PreviewGestureListener listener) {
        synchronized (this) {
            mInteract.setGestureListener(listener);
        }
    }

    @Override
    public void setOnPreviewChangeListener(OnPreviewChangeListener listener) {
        synchronized (this) {
            mInteract.setOnPreviewChangeListener(listener);
        }
    }

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

    protected boolean waitSurfaceCreateIfNeeded() {
        synchronized (STATIC_THREAD_LOCK) {
            Log.d(TAG, "waitSurfaceCreateIfNeeded, mbSurfaceCreated:" + isSurfaceCreated());

            if (!isSurfaceCreated()) {
                try {
                    STATIC_THREAD_LOCK.wait(THREAD_WAIT_TIMEOUT);
                } catch (InterruptedException e) {
                    Log.e(TAG, "waitSurfaceCreateIfNeeded: wait timeout!", e);
                }
            }

            return isSurfaceCreated();
        }
    }

    public boolean isSurfaceCreated() {
        synchronized (STATIC_THREAD_LOCK) {
            return mbSurfaceCreated;
        }
    }

    public void setSurfaceCreated(boolean isSurfaceCreated) {
        synchronized (STATIC_THREAD_LOCK) {
            this.mbSurfaceCreated = isSurfaceCreated;

            if (isSurfaceCreated) {
                STATIC_THREAD_LOCK.notifyAll();

                Log.d(TAG, "setSurfaceCreated, notifyAll");
            }
        }
    }
}
