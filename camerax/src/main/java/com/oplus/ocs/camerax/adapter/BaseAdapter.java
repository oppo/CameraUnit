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
 * File: - BaseAdapter.java
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

package com.oplus.ocs.camerax.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oplus.ocs.base.common.ConnectionResult;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.util.Constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public abstract class BaseAdapter {
    Context mAppContext = null;

    public BaseAdapter(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    public Context getAppContext() {
        return mAppContext;
    }

    public abstract boolean isPlatformSupported();

    public boolean isSupportAsyncAuthenticate() {
        return false;
    }

    public void authenticateAsync(AuthSucceedListener succeedListener, AuthFailedListener failedListener, Handler handler) {
        // do nothing
    }

    public boolean authenticateSync() {
        return true;
    }

    public abstract void init();

    @Nullable
    public abstract List<String> getSupportModeType();

    public abstract List<String> getSupportCameraType(@Constant.CameraMode String cameraModeType);

    public abstract List<Integer> getSupportFeatures(@Constant.CameraMode String cameraModeType,
            @Constant.CameraType String cameraType);

    public abstract void openCamera(@Constant.CameraMode String cameraModeType, @Constant.CameraType String cameraType,
            @NonNull CameraStatusListener listener, Handler handler);
    public abstract void closeCamera(@Constant.CameraMode String cameraModeType, @Constant.CameraType String cameraType,
            @NonNull CameraStatusListener listener, Handler handler);

    public abstract void configureSession(ConfigureBean configure, Handler mCameraHandler);

    public abstract void startPreview(ConfigureBean configure, PreviewCallback callback, Handler handler);
    public abstract void stopPreview();

    public abstract void startRecording(ConfigureBean configure, RecordingStatusCallback callback, Handler handler);

    public abstract void resumeRecording();

    public abstract void pauseRecording();

    public abstract void stopRecording(RecordingStatusCallback callback);

    public abstract void takePicture(ConfigureBean configure, PictureCallback callback, Handler pictureCallback);

    public interface AuthFailedListener {
        void onAuthFailed(ConnectionResult result);
    }

    public interface AuthSucceedListener {
        void onAuthSucceed();
    }

    public interface CameraStatusListener {
        void onCameraOpened(@Constant.CameraMode String cameraModeType, @Constant.CameraType String cameraType);
        void onCameraClosed();
        void onCameraDisconnected();
        void onCameraError(@ErrorCode int errorCode, String errorMsg);
        void onSessionConfigured();
        void onFlashModeSupportListChanged(List<String> list);
        void onFlashModeChanged(@Constant.FlashMode String flashMode);

        @IntDef({ErrorCode.CODE_UNKNOWN_ERROR, ErrorCode.CODE_CAMERA_OCCUPIED_ERROR, ErrorCode.CODE_DEVICE_ERROR,
                ErrorCode.CODE_PROCESS_ERROR, ErrorCode.CODE_SERVICE_ERROR, ErrorCode.CODE_NOT_SUPPORT_ERROR,
                ErrorCode.CODE_MEMORY_ERROR, ErrorCode.CODE_PARAMETER_ERROR, ErrorCode.CODE_STREAM_SURFACE_ERROR,
                ErrorCode.CODE_ILLEGAL_STATE_ERROR, ErrorCode.CODE_AUTHENTICATE_FAILED_ERROR
        })
        @Retention(RetentionPolicy.SOURCE)
        @interface ErrorCode {
            int CODE_UNKNOWN_ERROR = 0;
            int CODE_CAMERA_OCCUPIED_ERROR = 1;
            int CODE_DEVICE_ERROR = 2;
            int CODE_PROCESS_ERROR = 3;
            int CODE_SERVICE_ERROR = 4;
            int CODE_NOT_SUPPORT_ERROR = 5;
            int CODE_MEMORY_ERROR = 6;
            int CODE_PARAMETER_ERROR = 7;
            int CODE_STREAM_SURFACE_ERROR = 8;
            int CODE_ILLEGAL_STATE_ERROR = 9;
            int CODE_AUTHENTICATE_FAILED_ERROR = 10;
        }
    }

    public interface PreviewCallback {
        void onPreviewStarted();
        void onBokehStateCallback(int state);
        void onPreviewStop();
    }

    public interface PictureCallback {
        void onShutterCallback(long timestamp);
        void onPictureTaken(byte[] picture, long timeStamp);
        void onPictureTakenFailed();
    }

    public interface RecordingStatusCallback {
        void onRecordingPreStart(ConfigureBean configure, Surface recorderSurface, int orientation, Size videoSize,
                boolean frameRate, int sensorOrientation);
        void onRecordingStarted();
        void onRecordingResume();
        void onRecordingPaused();
        void onRecordingStopped();
        void onRecordingFinish(Bitmap bitmap, String videoPath);
    }
}