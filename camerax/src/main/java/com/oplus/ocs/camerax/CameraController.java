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
 * File: - CameraController.java
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

package com.oplus.ocs.camerax;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oplus.ocs.camera.CameraParameter;
import com.oplus.ocs.camerax.adapter.AdapterFactory;
import com.oplus.ocs.camerax.adapter.BaseAdapter;
import com.oplus.ocs.camerax.util.Constant;
import com.oplus.ocs.camerax.component.video.VideoControl;
import com.oplus.ocs.camerax.component.video.VideoStateCallback;

import java.util.List;
import java.util.Optional;

import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.SLOW_VIDEO_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.VIDEO_MODE;
import static com.oplus.ocs.camerax.adapter.BaseAdapter.CameraStatusListener.ErrorCode.CODE_AUTHENTICATE_FAILED_ERROR;
import static com.oplus.ocs.camerax.adapter.BaseAdapter.CameraStatusListener.ErrorCode.CODE_NOT_SUPPORT_ERROR;
import static com.oplus.ocs.camerax.util.Constant.VideoFps.FRAME_RATE_30;

public class CameraController {
    private static final String TAG = "CameraController";

    private BaseAdapter mCameraAdapter = null;
    private HandlerThread mAuthThread = null;
    private HandlerThread mCameraThread = null;
    private Handler mAuthHandler = null;
    private Handler mCameraHandler = null;
    private boolean mInstanceInit = false;
    private boolean mbCameraOpened = false;
    private boolean mbSessionConfigured = false;

    private String mCameraModeType = "";
    private String mCameraType = null;

    private final ConditionVariable mVideoRecorderVariable = new ConditionVariable();
    private final VideoControl mVideoControl = new VideoControl();
    private CameraStatusCallback mCameraStatusCallback = null;

    /**
     * The listener for listen status of camera open, configure session and flash changed.
     */
    private final BaseAdapter.CameraStatusListener mCameraStatusListener = new BaseAdapter.CameraStatusListener() {
        @Override
        public void onCameraOpened(@Constant.CameraMode String cameraModeType, @Constant.CameraType String cameraType) {
            Log.d(TAG, "onCameraOpened");

            mCameraModeType = cameraModeType;
            mCameraType = cameraType;
            mbCameraOpened = true;
            Optional.ofNullable(mCameraStatusCallback).ifPresent((a) -> a.onCameraOpened(cameraType));
        }

        @Override
        public void onCameraClosed() {
            Log.d(TAG, "onCameraClosed");

            mbCameraOpened = false;
            Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onCameraClosed);
        }

        @Override
        public void onCameraDisconnected() {
            Log.d(TAG, "onCameraDisconnected");

            mbCameraOpened = false;
        }

        @Override
        public void onCameraError(@ErrorCode int errorCode, String errorMsg) {
            Log.d(TAG, "onCameraError, errorCode: " + errorCode + ", errorMsg: " + errorMsg);

            mbCameraOpened = false;
            Optional.ofNullable(mCameraStatusCallback).ifPresent(callback -> callback.onCameraError(errorCode, errorMsg));
        }

        @Override
        public void onSessionConfigured() {
            mbSessionConfigured = true;
            Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onCameraConfigured);
        }

        @Override
        public void onFlashModeSupportListChanged(List<String> list) {
            Optional.ofNullable(mCameraStatusCallback).ifPresent(callback -> callback.onFlashModeSupportListChanged(list));
        }

        @Override
        public void onFlashModeChanged(String flashMode) {
            Optional.ofNullable(mCameraStatusCallback).ifPresent(callback -> callback.onFlashModeChanged(flashMode));
        }
    };

    private final BaseAdapter.RecordingStatusCallback mRecordingStatusCallback = new BaseAdapter.RecordingStatusCallback() {
        @Override
        public void onRecordingPreStart(ConfigureBean configure, Surface recorderSurface, int orientation, Size videoSize,
                boolean shouldMirror, int sensorOrientation) {
            boolean isSlowVideo = SLOW_VIDEO_MODE.equals(configure.getCameraModeType());
            // The MediaRecorder videoFrameRate must be 30 fps for slow video mode
            int frameRate = isSlowVideo ? FRAME_RATE_30 : configure.getVideoFps().getUpper();
            boolean isVideoHdrMode = (CameraParameter.CommonStateValue.ON.equals(configure.getVideoHdrMode()));
            int captureFrameRate = configure.getVideoFps().getUpper();
            mVideoControl.start(recorderSurface, sensorOrientation, videoSize, frameRate, isVideoHdrMode, isSlowVideo,
                    shouldMirror, orientation, mVideoStateCallback, captureFrameRate);
        }

        @Override
        public void onRecordingStarted() {
            Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onRecordingStart);
        }

        @Override
        public void onRecordingResume() {
            Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onRecordingResume);
        }

        @Override
        public void onRecordingPaused() {
            Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onRecordingPaused);
        }

        @Override
        public void onRecordingStopped() {
            Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onRecordingStop);
        }

        @Override
        public void onRecordingFinish(Bitmap bitmap, String videoPath) {
            Optional.ofNullable(mCameraStatusCallback).ifPresent(callback -> callback.onRecordingFinish(bitmap, videoPath));
        }
    };

    private final VideoStateCallback mVideoStateCallback = new VideoStateCallback() {
        @Override
        public void onUpdateUI(Bitmap bitmap) {
            mRecordingStatusCallback.onRecordingFinish(bitmap, mVideoControl.getCurrentPath());
        }

        @Override
        public void onStartFinish() {
            // do nothing
        }

        @Override
        public void onRecorderStopFinish() {
            mVideoRecorderVariable.open();
        }
    };

    private CameraController() {
    }

    public VideoControl getVideoControl() {
        return mVideoControl;
    }

    public void setCameraStatusCallback(CameraStatusCallback callback) {
        mCameraStatusCallback = callback;
    }

    private static class SingletonHolder {
        private static final CameraController sInstance = new CameraController();
    }

    public static CameraController getInstance() {
        return SingletonHolder.sInstance;
    }

    public synchronized void init(Context appContext) {
        if (null == mAuthThread) {
            mAuthThread = new HandlerThread("Auth Handler");
            mAuthThread.start();
            mAuthHandler = new Handler(mAuthThread.getLooper());
        }

        if (null == mCameraThread) {
            mCameraThread = new HandlerThread("Camera Handler");
            mCameraThread.start();
            mCameraHandler = new Handler(mCameraThread.getLooper());
        }

        mCameraAdapter = AdapterFactory.getDeviceAdapter(appContext);

        // 如果平台不支持，则直接返回Error回调给业务方。
        if (mCameraAdapter.isPlatformSupported()) {
            // 判断是否支持异步鉴权，如果支持，则不需要设置监听器，直接可进行操作，如果后续鉴权失败，
            // 会在创建session的时候收到创建session失败的回调，如果不支持异步鉴权，需要等鉴权结果回来后再进行后续操作。
            if (mCameraAdapter.isSupportAsyncAuthenticate()) {
                mCameraAdapter.authenticateAsync(() -> {
                    // init adapter first
                    mCameraAdapter.init();
                    Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onCameraReady);
                }, result -> {
                    Optional.ofNullable(mCameraStatusCallback).ifPresent(callback ->
                            callback.onCameraError(CODE_AUTHENTICATE_FAILED_ERROR, "Authenticate failed!"));
                }, mAuthHandler);
            } else {
                if (mCameraAdapter.authenticateSync()) {
                    // init adapter first
                    mCameraAdapter.init();
                    Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onCameraReady);
                } else {
                    Optional.ofNullable(mCameraStatusCallback).ifPresent(callback ->
                            callback.onCameraError(CODE_AUTHENTICATE_FAILED_ERROR, "Authenticate failed!"));
                }
            }
        } else {
            Optional.ofNullable(mCameraStatusCallback).ifPresent(callback ->
                    callback.onCameraError(CODE_NOT_SUPPORT_ERROR, "Not supported by this device!"));
        }

        mInstanceInit = true;
    }

    public synchronized void deInit() {
        if (!mInstanceInit) {
            return;
        }

        mInstanceInit = false;
        mAuthHandler.removeCallbacksAndMessages(null);
        mAuthThread.quitSafely();
        mAuthThread = null;
        mCameraHandler.removeCallbacksAndMessages(null);
        mCameraThread.quitSafely();
        mCameraThread = null;
    }

    /**
     * Call it after onCameraReady
     */
    @Nullable
    public List<String> getSupportModeType() {
        return mCameraAdapter.getSupportModeType();
    }

    /**
     * Call it after onCameraReady
     */
    public List<String> getSupportCameraType(@Constant.CameraMode String cameraMode) {
        return mCameraAdapter.getSupportCameraType(cameraMode);
    }

    /**
     * Call it after onCameraReady
     */
    public List<Integer> getSupportFeatures(@Constant.CameraMode String cameraMode, @Constant.CameraType String cameraType) {
        return mCameraAdapter.getSupportFeatures(cameraMode, cameraType);
    }

    private void openCamera(ConfigureBean configure, Handler handler) {
        Log.d(TAG, "openCamera: start");

        String cameraMode = configure.getCameraModeType();
        String cameraType = configure.getCameraType();

        if (mbCameraOpened && mbSessionConfigured) {
            configure.setNeedReopenCamera(true);
            closeCamera(configure, handler);
        } else {
            mCameraAdapter.openCamera(cameraMode, cameraType, mCameraStatusListener, handler);
        }
    }

    private void closeCamera(ConfigureBean configure, Handler handler) {
        Log.d(TAG, "closeCamera: start");

        String cameraMode = configure.getCameraModeType();
        String cameraType = configure.getCameraType();

        if ((TextUtils.equals(VIDEO_MODE, cameraMode) || TextUtils.equals(SLOW_VIDEO_MODE, cameraMode))
                && !mVideoControl.isStopped()) {
            Log.d(TAG, "closeCamera, wait record stop.");

            mVideoRecorderVariable.close();
            mVideoRecorderVariable.block(3000);
        }

        mbSessionConfigured = false;
        mVideoControl.release();

        mCameraAdapter.closeCamera(cameraMode, cameraType, mCameraStatusListener, handler);
    }

    public void onConfigureChange(@NonNull ConfigureBean configure) {
        Log.d(TAG, "onConfigureChange: " + configure);

        if (!mbCameraOpened
                || !configure.getCameraModeType().equals(mCameraModeType)
                || !configure.getCameraType().equals(mCameraType)) {
            openCamera(configure, mCameraHandler);
        } else if (!mbSessionConfigured) {
            mCameraAdapter.configureSession(configure, mCameraHandler);
        } else {
            if (configure.isNeedConfigureSession()) {
                configure.setNeedConfigureSession(false);
                mCameraAdapter.configureSession(configure, mCameraHandler);
            } else {
                startPreview(configure);
            }
        }
    }

    public void resume() {
        Log.d(TAG, "resume: camera host resumed!");
    }

    public void pause(@NonNull ConfigureBean configure) {
        Log.d(TAG, "pause: camera host paused!");

        if (!mVideoControl.isStopped() && !mVideoControl.isStopping()) {
            stopRecording(true);
        }

        if (mbCameraOpened) {
            closeCamera(configure, mCameraHandler);
        }
    }

    public void startPreview(@NonNull ConfigureBean configure) {
        Log.d(TAG, "startPreview: start");

        mCameraAdapter.startPreview(configure, new BaseAdapter.PreviewCallback() {
            @Override
            public void onPreviewStarted() {
                Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onPreviewStart);
            }

            @Override
            public void onBokehStateCallback(int state) {
                Optional.ofNullable(mCameraStatusCallback).ifPresent(a -> a.onBokehStateChanged(state));
            }

            @Override
            public void onPreviewStop() {
                Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onPreviewStop);
            }
        }, mCameraHandler);
    }

    public void stopPreview() {
        mCameraAdapter.stopPreview();
        Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onPreviewStop);
    }

    public void startRecording(ConfigureBean configure) {
        mCameraAdapter.startRecording(configure, mRecordingStatusCallback, mCameraHandler);
    }

    public void resumeRecording() {
        mVideoControl.resume();
        mCameraAdapter.resumeRecording();
    }

    public void pauseRecording() {
        mVideoControl.pause();
        mCameraAdapter.pauseRecording();
    }

    public void stopRecording(boolean isPaused) {
        if (mVideoControl.isStoppable() || isPaused) {
            mVideoControl.stop(mVideoStateCallback);
            mCameraAdapter.stopRecording(mRecordingStatusCallback);
        }
    }

    public void takePicture(ConfigureBean configureBean) {
        mCameraAdapter.takePicture(configureBean, new BaseAdapter.PictureCallback() {
            @Override
            public void onShutterCallback(long timestamp) {
                Optional.ofNullable(mCameraStatusCallback).ifPresent(callback -> callback.onShutterCallback(timestamp));
            }

            @Override
            public void onPictureTaken(byte[] picture, long timeStamp) {
                Optional.ofNullable(mCameraStatusCallback).ifPresent(callback -> callback.onPictureTaken(picture, timeStamp));
            }

            @Override
            public void onPictureTakenFailed() {
                Optional.ofNullable(mCameraStatusCallback).ifPresent(CameraStatusCallback::onPictureTakenFailed);
            }
        }, mCameraHandler);
    }
}