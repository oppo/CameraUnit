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
 * File: - SurfaceTexturePreview.java
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

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.UiThread;

import com.oplus.ocs.cameraunit.ui.previewsurfaces.BaseDrawer;
import com.oplus.ocs.cameraunit.ui.previewsurfaces.BaseGlSurfaceView;
import com.oplus.ocs.cameraunit.ui.previewsurfaces.drawer.OESDrawer;
import com.oplus.ocs.cameraunit.util.Constant;
import com.oplus.ocs.camerax.util.Constant.CameraType;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SurfaceTexturePreview extends BaseGlSurfaceView implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "SurfaceTexturePreview";
    private static final int IDS_NUM = 2;

    private final Map<String, SurfaceTexture> mSurfaceTextures = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<String, Surface> mSurfaceMap = new ConcurrentHashMap<>();
    private final Map<SurfaceTexture, Boolean> mFrameArrive = new ConcurrentHashMap<>();
    private final float[][] mMatrix = new float[2][16];

    @UiThread
    public SurfaceTexturePreview(Context context) {
        super(context);
    }

    @UiThread
    public SurfaceTexturePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @UiThread
    @Override
    protected BaseDrawer getDrawer() {
        return new OESDrawer();
    }

    @UiThread
    @Override
    protected int getIdsNum() {
        return IDS_NUM;
    }

    @Override
    public int getSurfaceType() {
        return Constant.CameraSurfaceType.SURFACE_TEXTURE;
    }

    @Override
    public Map<String, Surface> buildSurface(LinkedHashMap<String, Size> sizes, Handler recvHandler) {
        Log.d(TAG, "buildSurface");

        if (!waitSurfaceCreateIfNeeded()) {
            Log.d(TAG, "buildSurface, Surface not created, so return null.");

            // If mbSurfaceCreated is false when thread been notify, that means onSurfaceCreated is not called in current
            // object, when a new instance been created at onCreate will create a new SurfaceTexturePreview object and call
            // onSurfaceCreated() and will notify the old object's wait, so return a null obj to open the block thread.
            return null;
        }

        mFrameArrive.clear();

        for (Map.Entry<String, Size> item : sizes.entrySet()) {
            SurfaceTexture surfaceTexture = mSurfaceTextures.get(item.getKey());

            if (null == surfaceTexture) {
                surfaceTexture = new SurfaceTexture(false);
                mSurfaceTextures.put(item.getKey(), surfaceTexture);
                mSurfaceMap.put(item.getKey(), new Surface(surfaceTexture));
                mFrameArrive.put(surfaceTexture, false);
            }

            surfaceTexture.setOnFrameAvailableListener(this, recvHandler);
            surfaceTexture.setDefaultBufferSize(item.getValue().getWidth(), item.getValue().getHeight());
        }

        setDisplaySize(sizes);

        Log.d(TAG, "buildSurface, end");

        return mSurfaceMap;
    }

    @Override
    public void destroyPreviewBuffer() {
        synchronized (this) {
            Log.d(TAG, "destroyPreviewBuffer");

            for (Map.Entry<String, SurfaceTexture> item : mSurfaceTextures.entrySet()) {
                Surface surface = mSurfaceMap.get(item.getKey());
                surface.release();

                Log.d(TAG, "destroyPreviewBuffer, release surface: " + surface);

                SurfaceTexture surfaceTexture = item.getValue();
                surfaceTexture.release();

                Log.d(TAG, "destroyPreviewBuffer, release surface texture: " + surfaceTexture);
            }

            mSurfaceMap.clear();
            mSurfaceTextures.clear();
        }
    }

    /**
     * Handle in recvHandler thread which set in buildSurface()
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mFrameArrive.put(surfaceTexture, true);

        if (mSurfaceTextures.size() == 1 || isRearMainCameraSurfaceTexture(surfaceTexture)) {
            requestRender();
        }
    }

    @Override
    protected boolean shouldDraw() {
        for (Map.Entry<SurfaceTexture, Boolean> item : mFrameArrive.entrySet()) {
            /*
             * because current draw frame mechanism is need wait the main frame is avaliable will draw the preview. so
             * have this case, if the first frame is main so will trigger render, buf the sub don't come, here will return
             * false. the buffer don't consume, the preview will freeze.
             * so if the item's value is false and the texture is main, return false. other case use the surface texture
             * size
             */
            if (!item.getValue() && isRearMainCameraSurfaceTexture(item.getKey())) {
                return false;
            }
        }

        return mSurfaceTextures.size() > 0;
    }

    @Override
    protected void drawFrame(BaseDrawer drawer, Rect[] areas) {
        if (mSurfaceTextures.isEmpty()) {
            return;
        }

        Iterator<SurfaceTexture> iterator = mSurfaceTextures.values().iterator();
        SurfaceTexture one = iterator.next();
        one.attachToGLContext(mYuvIds[0]);
        one.updateTexImage();
        one.getTransformMatrix(mMatrix[0]);

        SurfaceTexture two = null;

        if (iterator.hasNext()) {
            two = iterator.next();
            two.attachToGLContext(mYuvIds[1]);
            two.updateTexImage();
            two.getTransformMatrix(mMatrix[1]);
        }

        FloatBuffer[] vertexes = new FloatBuffer[2];
        vertexes[0] = createBuffer(VERTEX_DATA);
        vertexes[1] = createBuffer(VERTEX_DATA);

        drawer.draw(mYuvIds, mMatrix, areas, vertexes);

        one.detachFromGLContext();

        if (null != two) {
            two.detachFromGLContext();
        }
    }

    private boolean isRearMainCameraSurfaceTexture(SurfaceTexture surfaceTexture) {
        return surfaceTexture.equals(mSurfaceTextures.get(CameraType.REAR_MAIN_CAMERA));
    }
}
