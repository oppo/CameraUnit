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
 * File: - ImageReaderPreview.java
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
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.opengl.GLES32;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.UiThread;

import com.oplus.ocs.cameraunit.ui.previewsurfaces.BaseDrawer;
import com.oplus.ocs.cameraunit.ui.previewsurfaces.BaseGlSurfaceView;
import com.oplus.ocs.cameraunit.ui.previewsurfaces.drawer.YuvDrawer;
import com.oplus.ocs.cameraunit.util.Constant;
import com.oplus.ocs.camerax.util.Constant.CameraType;
import com.oplus.ocs.camerax.util.Constant.Orientation;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImageReaderPreview extends BaseGlSurfaceView implements ImageReader.OnImageAvailableListener {
    private static final String TAG = "ImageReaderPreview";
    private static final int IDS_NUM = 4;
    private static final float[] BACK_MATRIX = new float[] {
            0.0f, -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f, 1.0f};
    private static final float[] FRONT_MATRIX = new float[] {
            0.0f, -1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 1.0f};

    private final float[][] mCurrentMatrix = new float[2][];
    private final Map<String, ImageReader> mReaders = new ConcurrentHashMap<>();
    private final Map<ImageReader, Boolean> mFrameArrive = Collections.synchronizedMap(new LinkedHashMap<>());

    @UiThread
    public ImageReaderPreview(Context context) {
        super(context);
    }

    @UiThread
    public ImageReaderPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @UiThread
    @Override
    protected BaseDrawer getDrawer() {
        return new YuvDrawer();
    }

    @UiThread
    @Override
    protected int getIdsNum() {
        return IDS_NUM;
    }

    @Override
    public int getSurfaceType() {
        return Constant.CameraSurfaceType.IMAGE_READER;
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

        synchronized (this) {
            mFrameArrive.clear();

            for (Map.Entry<String, Size> item : sizes.entrySet()) {
                Size size = item.getValue();
                String key = item.getKey();
                ImageReader reader = mReaders.get(key);

                if (null == reader) {
                    reader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.YUV_420_888, 2);
                    mReaders.put(key, reader);
                } else if ((reader.getWidth() != size.getWidth()) || (reader.getHeight() != size.getHeight())) {
                    reader.close();

                    reader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.YUV_420_888, 2);
                    mReaders.put(key, reader);
                }

                mFrameArrive.put(reader, false);
                reader.setOnImageAvailableListener(this, recvHandler);
            }

            setDisplaySize(sizes);

            Map<String, Surface> surfaceMap = new HashMap<>();

            for (Map.Entry<String, ImageReader> item : mReaders.entrySet()) {
                surfaceMap.put(item.getKey(), item.getValue().getSurface());
            }

            return surfaceMap;
        }
    }

    @Override
    public void destroyPreviewBuffer() {
        synchronized (this) {
            if (mReaders.size() > 0) {
                for (Map.Entry<String, ImageReader> item : mReaders.entrySet()) {
                    item.getValue().close();
                }

                mReaders.clear();
                mFrameArrive.clear();
            }
        }
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        synchronized (this) {
            mFrameArrive.put(reader, true);

            if ((mReaders.size() == 1) || isRearMainCameraImageReader(reader)) {
                requestRender();
            }
        }
    }

    @Override
    protected boolean shouldDraw() {
        for (Map.Entry<ImageReader, Boolean> item : mFrameArrive.entrySet()) {
            if (!item.getValue() && isRearMainCameraImageReader(item.getKey())) {
                return false;
            }
        }

        return mReaders.size() > 0;
    }

    @Override
    protected void drawFrame(BaseDrawer drawer, Rect[] areas) {
        synchronized (this) {
            Iterator<ImageReader> iterator = mReaders.values().iterator();

            Image one = iterator.next().acquireLatestImage();

            if (null == one) {
                return;
            }

            int imageHeight = one.getHeight();
            int imageWidth = one.getWidth();
            Image.Plane[] planes = one.getPlanes();
            ByteBuffer yBuffer = planes[0].getBuffer();
            ByteBuffer uvBuffer = planes[2].getBuffer();
            int stride = planes[0].getRowStride();

            prepareTexture(mYuvIds[0], GLES32.GL_TEXTURE0, GLES32.GL_LUMINANCE, yBuffer, stride, imageHeight);
            prepareTexture(mYuvIds[1], GLES32.GL_TEXTURE1, GLES32.GL_LUMINANCE_ALPHA, uvBuffer, stride / 2, imageHeight / 2);

            FloatBuffer[] vertexes = new FloatBuffer[2];

            float contentRate = 1.0f;

            if (imageWidth != stride) {
                contentRate = ((float) imageWidth) /  ((float) stride);
            }

            float[] vertexData = new float[16];

            System.arraycopy(VERTEX_DATA, 0, vertexData, 0, 16);

            if (mCurrentMatrix[0] == BACK_MATRIX) {
                vertexData[7] = 1 - contentRate;
                vertexData[15] = 1 - contentRate;
            } else {
                vertexData[3] = contentRate;
                vertexData[11] = contentRate;
            }

            vertexes[0] = createBuffer(vertexData);

            Image two = null;

            if (iterator.hasNext()) {
                two = iterator.next().acquireLatestImage();
                imageHeight = two.getHeight();
                imageWidth = two.getWidth();

                planes = two.getPlanes();
                yBuffer = planes[0].getBuffer();
                uvBuffer = planes[2].getBuffer();
                stride = planes[0].getRowStride();

                prepareTexture(mYuvIds[2], GLES32.GL_TEXTURE2, GLES32.GL_LUMINANCE, yBuffer, stride, imageHeight);
                prepareTexture(mYuvIds[3], GLES32.GL_TEXTURE3, GLES32.GL_LUMINANCE_ALPHA, uvBuffer, stride / 2, imageHeight / 2);

                contentRate = 1.0f;

                if (imageWidth != stride) {
                    contentRate = ((float) imageWidth) /  ((float) stride);
                }

                vertexData = new float[16];

                System.arraycopy(VERTEX_DATA, 0, vertexData, 0, 16);

                if (mCurrentMatrix[1] == BACK_MATRIX) {
                    vertexData[7] = 1 - contentRate;
                    vertexData[15] = 1 - contentRate;
                } else {
                    vertexData[3] = contentRate;
                    vertexData[11] = contentRate;
                }

                vertexes[1] = createBuffer(vertexData);
            }

            drawer.draw(mYuvIds, mCurrentMatrix, areas, vertexes);

            one.close();

            if (null != two) {
                two.close();
            }
        }
    }

    @Override
    public void setSensorOrientation(int orientation) {
        if (Orientation.ORIENTATION_270 == orientation) {
            mCurrentMatrix[0] = FRONT_MATRIX;
            mCurrentMatrix[1] = BACK_MATRIX;
        } else {
            mCurrentMatrix[0] = BACK_MATRIX;
            mCurrentMatrix[1] = FRONT_MATRIX;
        }
    }

    private void prepareTexture(int textureId, int target, int format, ByteBuffer buffer, int width, int height) {
        GLES32.glActiveTexture(target);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureId);
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_CLAMP_TO_EDGE);
        GLES32.glTexParameterf(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_CLAMP_TO_EDGE);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_NEAREST);
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_NEAREST);
        GLES32.glTexImage2D(GLES32.GL_TEXTURE_2D, 0, format, width, height, 0, format, GLES32.GL_UNSIGNED_BYTE, buffer);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, GLES32.GL_NONE);
    }

    private boolean isRearMainCameraImageReader(ImageReader imageReader) {
        return imageReader.equals(mReaders.get(CameraType.REAR_MAIN_CAMERA));
    }
}
