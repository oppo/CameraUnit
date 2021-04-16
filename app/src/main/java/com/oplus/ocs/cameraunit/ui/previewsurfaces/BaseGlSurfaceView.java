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
 * File: - BaseGlSurfaceView.java
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
import android.graphics.Rect;
import android.opengl.GLES32;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;

import androidx.annotation.UiThread;

import com.oplus.ocs.camerax.component.preview.PreviewInterface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class BaseGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, PreviewInterface {
    private static final String TAG = "BaseGlSurfaceView";
    private static final Object STATIC_THREAD_LOCK = new Object();
    private static final int THREAD_WAIT_TIMEOUT = 3000;
    private Rect[] mAreas = null;
    protected int[] mYuvIds = null;
    private final PreviewInteract mInteract = new PreviewInteract();
    private boolean mbSurfaceCreated = false;
    protected static final float[] VERTEX_DATA = {
            -1f,  1f,  0f,  1f,
            -1f, -1f,  0f,  0f,
            1f,  1f,  1f,  1f,
            1f, -1f,  1f,  0f
    };
    private BaseDrawer mBaseDrawer = null;

    @UiThread
    public BaseGlSurfaceView(Context context) {
        super(context);
        init(context);
    }

    @UiThread
    public BaseGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @UiThread
    private void init(Context context) {
        setEGLContextClientVersion(3);
        setRenderer(this);
        mYuvIds = new int[getIdsNum()];
        mBaseDrawer = getDrawer();
        mInteract.init(context);
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

    protected void setDisplaySize(LinkedHashMap<String, Size> sizes) {
        synchronized (this) {
            mInteract.setPreviewSize(new ArrayList<>(sizes.values()));
            mInteract.onSurfaceSet();

            Size previewSize = mInteract.getDisplaySize();
            int totalHeight = previewSize.getHeight() + mInteract.getBlank();

            mAreas = new Rect[sizes.size()];

            if (mInteract.isMainPreviewAsCanvas()) {
                mAreas[0] = new Rect(0, getHeight() - totalHeight, previewSize.getWidth(), previewSize.getHeight());
            } else {
                mAreas[0] = new Rect(0, getHeight() - totalHeight, previewSize.getWidth(), previewSize.getHeight() / 2);
            }

            if (sizes.size() > 1) {
                Iterator<Size> iterator = sizes.values().iterator();
                iterator.next();
                Size subSize = iterator.next();

                if (mInteract.isMainPreviewAsCanvas()) {
                    mAreas[1] = new Rect(0, getHeight() - totalHeight, subSize.getHeight(), subSize.getWidth());
                }
            }
        }
    }

    @Override
    public void setGestureListener(PreviewGestureListener listener) {
        setOnTouchListener(mInteract);
        mInteract.setGestureListener(listener);
    }

    @Override
    public void setOnPreviewChangeListener(OnPreviewChangeListener listener) {
        synchronized (this) {
            mInteract.setOnPreviewChangeListener(listener);
        }
    }

    @Override
    public void setSensorOrientation(int orientation) {
        // do nothing by default
    }

    /**
     * Run on RenderThread.
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");

        setRenderMode(RENDERMODE_WHEN_DIRTY);
        mBaseDrawer.init();
        GLES32.glGenTextures(mYuvIds.length, mYuvIds, 0);
        setSurfaceCreated(true);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // do nothing by default
    }

    /**
     * Call from RenderThread.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if ( !isSurfaceCreated() || (null == mAreas) || !shouldDraw()) {
                return;
            }

            GLES32.glClearColor(0, 0, 0, 0);
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);
            drawFrame(mBaseDrawer, mAreas);
            mInteract.onFrameDrawn();
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

    protected boolean waitSurfaceCreateIfNeeded() {
        synchronized (STATIC_THREAD_LOCK) {
            Log.d(TAG, "waitSurfaceCreateIfNeeded, mbSurfaceCreated:" + isSurfaceCreated());

            if (!isSurfaceCreated()) {
                try {
                    STATIC_THREAD_LOCK.wait(THREAD_WAIT_TIMEOUT);
                } catch (InterruptedException e) {
                    Log.e(TAG, "waitSurfaceCreateIfNeeded: thread wait failed", e);
                }
            }

            return isSurfaceCreated();
        }
    }

    protected static FloatBuffer createBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }

    protected abstract boolean shouldDraw();

    @UiThread
    protected abstract BaseDrawer getDrawer();

    @UiThread
    protected abstract int getIdsNum();

    /**
     * To draw frame on RenderThread.
     */
    protected abstract void drawFrame(BaseDrawer drawer, Rect[] areas);
}
