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
 * File: - CameraUnitAdapter.java
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

package com.oplus.ocs.camerax.adapter.cameraunit;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oplus.ocs.camera.CameraDevice;
import com.oplus.ocs.camera.CameraDeviceConfig;
import com.oplus.ocs.camera.CameraDeviceInfo;
import com.oplus.ocs.camera.CameraFlashCallback;
import com.oplus.ocs.camera.CameraParameter;
import com.oplus.ocs.camera.CameraPictureCallback;
import com.oplus.ocs.camera.CameraPreviewCallback;
import com.oplus.ocs.camera.CameraRecordingCallback;
import com.oplus.ocs.camera.CameraStateCallback;
import com.oplus.ocs.camera.CameraUnit;
import com.oplus.ocs.camera.CameraUnitClient;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.adapter.BaseAdapter;
import com.oplus.ocs.camerax.component.preview.PreviewInterface;
import com.oplus.ocs.camerax.features.FeatureFactory;
import com.oplus.ocs.camerax.util.CameraUtil;
import com.oplus.ocs.camerax.util.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION;
import static com.oplus.ocs.camera.CameraDeviceConfig.CameraSurfaceType.SURFACE_TEXTURE;
import static com.oplus.ocs.camera.CameraParameter.AI_NIGHT_VIDEO_MODE;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_3HDR_MODE;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_DYNAMIC_FPS;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_STABILIZATION_MODE;
import static com.oplus.ocs.camera.CameraParameter.VideoStabilizationMode.SUPER_STABILIZATION;
import static com.oplus.ocs.camera.CameraParameter.VideoStabilizationMode.VIDEO_STABILIZATION;
import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.MULTI_CAMERA_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.NIGHT_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.PHOTO_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.PORTRAIT_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.SLOW_VIDEO_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.VIDEO_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraType.FRONT_MAIN;
import static com.oplus.ocs.camera.CameraUnitClient.CameraType.REAR_MAIN;
import static com.oplus.ocs.camera.CameraUnitClient.CameraType.REAR_MAIN_FRONT_MAIN;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.COMMON_FEATURE_AE_AF;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.COMMON_FEATURE_EXPOSURE_COMPENSATION;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.COMMON_FEATURE_FLASH;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.PHOTO_FEATURE_CAPTURE_HDR;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.PHOTO_FEATURE_PORTRAIT_BLUR;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_FPS;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_HDR;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION;
import static com.oplus.ocs.camerax.util.Constant.VideoResolution.VIDEO_RESOLUTION_720P;

public class CameraUnitAdapter extends BaseAdapter {
    private static final String TAG = "CameraUnitAdapter";
    private static final int RE_AUTH_COUNT_MAX = 2;
    private static final int FIXED_FRONT_WIDTH = 640;
    private static final int FIXED_FRONT_HEIGHT = 480;

    protected final Map<String/*CameraModeType*/, List<String>/*CameraType*/> mCameraCharacteristicMap = new HashMap<>();
    private int mRetryCount = 0;
    private boolean mbAuthSuccess = false;
    private volatile CameraDevice mMainCameraDevice = null;
    private volatile CameraDeviceInfo mCameraDeviceInfo = null;

    private Size mPreviewSize = null;
    private Size mSubPreviewSize = new Size(FIXED_FRONT_WIDTH, FIXED_FRONT_HEIGHT);
    private Size mVideoSize = null;
    private Size mPictureSize = null;
    private boolean mbFirstFrameArrived = false;
    private PreviewCallback mPreviewCallback = null;

    public CameraUnitAdapter(Context mAppContext) {
        super(mAppContext);
    }

    @Override
    public boolean isPlatformSupported() {
        return (null != CameraUnit.getCameraClient(getAppContext()));
    }

    @Override
    public boolean isSupportAsyncAuthenticate() {
        // 在能力开放中，异步鉴权指的是在初始化的时候，鉴权直接放过，延迟到创建camera session时通过session callback 返回结果，
        // 判断是否是由于鉴权失败导致的fail。而不支持异步鉴权的情况下，就需要通过callback来判断是否鉴权成功了。
        Log.d(TAG, "init: isSupportAsyncAuthenticate: " + CameraUnitClient.isSupportAsyncAuthenticate(getAppContext()));

        return !CameraUnitClient.isSupportAsyncAuthenticate(getAppContext());
    }

    @Override
    public void authenticateAsync(@NonNull AuthSucceedListener succeedListener, @NonNull AuthFailedListener failedListener,
            Handler handler) {
        Log.d(TAG, "authenticateAsync, start");

        CameraUnit.getCameraClient(getAppContext()).addOnConnectionSucceedListener(() -> {
            Log.d(TAG, "authenticateAsync, onAuthSucceed");

            mbAuthSuccess = true;
            succeedListener.onAuthSucceed();
        }, handler)
        .addOnConnectionFailedListener(result -> {
            Log.w(TAG, "authenticateAsync, onAuthFailed, errorCode: " + result.getErrorCode()
                    + ", errorMessage: " + result.getErrorMessage());

            mbAuthSuccess = false;

            if (mRetryCount < RE_AUTH_COUNT_MAX) {
                mRetryCount++;
                authenticateAsync(succeedListener, failedListener, handler);
            } else {
                failedListener.onAuthFailed(result);
            }
        }, handler);
    }

    @Override
    public boolean authenticateSync() {
        Log.d(TAG, "authenticateSync, start");

        // sdk 会在CameraUnitClient构造函数中触发鉴权
        CameraUnit.getCameraClient(getAppContext());
        mbAuthSuccess = true;
        // 在能力开放中，如果支持异步鉴权，则在初始化中直接放过，等待 session callback 中 re-check。
        return true;
    }

    @Override
    public void init() {
        if (!mbAuthSuccess) {
            return;
        }

        initCameraCharacteristicMap();
    }

    protected void initCameraCharacteristicMap() {
        CameraUnitClient client = CameraUnit.getCameraClient(getAppContext());
        // return like this:
        // {
        // front_main=[video_mode, photo_mode],
        // rear_main_front_main=[multi_camera_mode],
        // rear_main=[video_mode, photo_mode, night_mode, slowvideo_mode],
        // rear_wide=[video_mode, photo_mode, night_mode]
        // }
        Map<String, List<String>> allSupportMode = client.getAllSupportCameraMode();

        Log.d(TAG, "init: allSupportMode: " + allSupportMode);

        if (null != allSupportMode) {
            for (Map.Entry<String, List<String>> listEntry : allSupportMode.entrySet()) {
                for (String cameraMode : listEntry.getValue()) {
                    if (mCameraCharacteristicMap.containsKey(cameraMode)) {
                        List<String> cameraList = mCameraCharacteristicMap.get(cameraMode);

                        if (null == cameraList) {
                            cameraList = new ArrayList<>();
                        }

                        if (!cameraList.contains(listEntry.getKey())) {
                            cameraList.add(listEntry.getKey());
                        }
                    } else {
                        List<String> cameraList = new ArrayList<>();
                        cameraList.add(listEntry.getKey());
                        mCameraCharacteristicMap.put(cameraMode, cameraList);
                    }
                }
            }
        }

        Log.d(TAG, "init: mCameraCharacteristicMap: " + mCameraCharacteristicMap);
    }

    public List<String> getSupportModeType() {
        if (mCameraCharacteristicMap.isEmpty()) {
            return null;
        }

        return new ArrayList<>(mCameraCharacteristicMap.keySet());
    }

    public List<String> getSupportCameraType(@Constant.CameraMode String cameraModeType) {
        if (mCameraCharacteristicMap.isEmpty()) {
            return null;
        }

        return mCameraCharacteristicMap.get(cameraModeType);
    }

    @Override
    public List<Integer> getSupportFeatures(String cameraModeType, String cameraType) {
        return FeatureFactory.getInstance().getSupportFeatureList(getAppContext(), cameraModeType, cameraType);
    }

    @Override
    public void openCamera(@Constant.CameraMode String cameraModeType, @Constant.CameraType String cameraType,
            @NonNull CameraStatusListener listener, Handler handler) {
        Log.d(TAG, "openCamera: " + cameraType);

        CameraUnit.getCameraClient(getAppContext()).openCamera(cameraType, new CameraStateCallback() {
            @Override
            public void onCameraOpened(CameraDevice cameraDevice) {
                Log.d(TAG, "onCameraOpened: " + cameraDevice);

                mMainCameraDevice = cameraDevice;
                mCameraDeviceInfo = CameraUnit.getCameraClient(getAppContext()).getCameraDeviceInfo(cameraType, cameraModeType);
                mMainCameraDevice.registerFlashCallback(new CameraFlashCallback() {
                    @Override
                    public void onFlashModeSupportListChanged(List<String> list) {
                        Log.d(TAG, "onFlashModeSupportListChanged: " + list);

                        listener.onFlashModeSupportListChanged(list);
                    }

                    @Override
                    public void onFlashModeChanged(@CameraParameter.FlashMode String mode) {
                        Log.d(TAG, "onFlashModeChanged" + mode);

                        listener.onFlashModeChanged(mode);
                    }
                }, handler);

                listener.onCameraOpened(cameraModeType, cameraType);
            }

            @Override
            public void onCameraClosed() {
                Log.d(TAG, "onCameraClosed: " + mMainCameraDevice);

                mMainCameraDevice = null;

                listener.onCameraClosed();
            }

            @Override
            public void onCameraDisconnected() {
                listener.onCameraDisconnected();
            }

            @Override
            public void onCameraError(@Nullable CameraErrorResult result) {
                if (null != result) {
                    // error code has been defined in sdk ErrorResult.java but not public by some unknown reasons.
                    if (10001 == result.getErrorCode()) {
                        listener.onCameraError(CameraStatusListener.ErrorCode.CODE_PARAMETER_ERROR, result.getErrorInfo());
                    } else if (10002 == result.getErrorCode()) {
                        listener.onCameraError(CameraStatusListener.ErrorCode.CODE_STREAM_SURFACE_ERROR, result.getErrorInfo());
                    } else if (10003 == result.getErrorCode()) {
                        listener.onCameraError(CameraStatusListener.ErrorCode.CODE_ILLEGAL_STATE_ERROR, result.getErrorInfo());
                    } else {
                        listener.onCameraError(result.getErrorCode(), result.getErrorInfo());
                    }
                } else {
                    listener.onCameraError(CameraStatusListener.ErrorCode.CODE_UNKNOWN_ERROR, "");
                }
            }

            @Override
            public void onSessionConfigured() {
                Log.d(TAG, "onSessionConfigured: ");
                listener.onSessionConfigured();
            }

            @Override
            public void onSessionConfigureFail(CameraErrorResult result) {
                Log.d(TAG, "onSessionConfigureFail: " + result);
            }

            @Override
            public void onSessionClosed() {
            }
        }, handler);
    }

    @Override
    public void closeCamera(@Constant.CameraMode String cameraModeType, @Constant.CameraType String cameraType,
            @NonNull CameraStatusListener listener, Handler handler) {
        Log.d(TAG, "closeCamera, cameraType: " + cameraType);

        mbFirstFrameArrived = false;

        if (null != mMainCameraDevice) {
            mMainCameraDevice.close(true);
        }
    }

    @Override
    public void configureSession(ConfigureBean configure, Handler mCameraHandler) {
        Log.d(TAG, "configureSession: start");

        if (null == mMainCameraDevice) {
            Log.w(TAG, "configureSession: camera device has already closed!");

            return;
        }

        updateTargetSurfaceSize(configure);
        CameraDeviceConfig.Builder builder = mMainCameraDevice.createCameraDeviceConfig();
        setConfigParameter(builder, configure);
        mMainCameraDevice.configure(builder.build());
    }

    @Override
    public void startPreview(ConfigureBean configure, PreviewCallback callback, Handler handler) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        mPreviewCallback = callback;

        Log.d(TAG, "startPreview from: " + ((elements.length >= 4) ? elements[3] : "unknown") + ", mCameraDevice: "
                + mMainCameraDevice + ", mHandler: " + handler);

        if (null == mMainCameraDevice) {
            Log.e(TAG, "mMainCameraDevice is null!");

            return;
        }

        setPreviewParameter(configure);
        Map<String, Surface> previewSurfaceMap = buildPreviewSurface(configure, handler);

        if (null != previewSurfaceMap) {
            mMainCameraDevice.startPreview(previewSurfaceMap, new CameraPreviewCallback() {
                @Override
                public void onPreviewMetaReceived(CameraPreviewResult result) {
                    super.onPreviewMetaReceived(result);

                    if ((null != result) && PORTRAIT_MODE.equals(configure.getCameraModeType())) {
                        Optional.ofNullable(mPreviewCallback)
                                .ifPresent(callback -> callback.onBokehStateCallback(result.getBokehState()));
                    }

                    if (!mbFirstFrameArrived) {
                        mbFirstFrameArrived = true;

                        Log.d(TAG, "onPreviewMetaReceived, first frame arrived");

                        Optional.ofNullable(mPreviewCallback).ifPresent(PreviewCallback::onPreviewStarted);
                    }
                }
            }, handler);
        }
    }

    @Override
    public void stopPreview() {
        if (null != mMainCameraDevice) {
            mMainCameraDevice.stopPreview();
            Optional.ofNullable(mPreviewCallback).ifPresent(PreviewCallback::onPreviewStop);
        }
    }

    @Override
    public void startRecording(ConfigureBean configure, RecordingStatusCallback callback, Handler handler) {
        if (null != mMainCameraDevice) {
            CameraDeviceInfo cameraDeviceInfo = getCameraDeviceInfo(configure.getCameraModeType(), configure.getCameraType());
            Integer sensorOrientation = cameraDeviceInfo.get(SENSOR_ORIENTATION);
            Size videoSize = (null != mVideoSize) ? mVideoSize : mPreviewSize;

            int orientation = configure.getOrientation();
            boolean shouldMirror = CameraUnitClient.CameraType.FRONT_MAIN.equals(configure.getCameraType());

            if (shouldMirror) {
                // For mtk platform, in qcom platform it will not take effect.
                int jpegOrientation = getJpegOrientation(cameraDeviceInfo, orientation);

                // This tag should set with CAPTURE_FLIP_MODE together for video mirror enable when use front
                // camera recording.
                mMainCameraDevice.setParameter(CaptureRequest.JPEG_ORIENTATION, jpegOrientation);
            }

            Surface recordingSurface = mMainCameraDevice.getVideoSurface();
            callback.onRecordingPreStart(configure, recordingSurface, orientation, videoSize, shouldMirror, sensorOrientation);

            mMainCameraDevice.startRecording(new CameraRecordingCallback() {
                @Override
                public void onRecordingResult(CameraRecordingResult recordingResult) {
                    super.onRecordingResult(recordingResult);
                    if (Constant.RecordingState.RECORDING_PAUSE == recordingResult.getRecordingState()) {
                        callback.onRecordingPaused();
                    } else if (Constant.RecordingState.RECORDING_RESUME == recordingResult.getRecordingState()) {
                        callback.onRecordingResume();
                    } else if (Constant.RecordingState.RECORDING_START == recordingResult.getRecordingState()) {
                        callback.onRecordingStarted();
                    } else if (Constant.RecordingState.RECORDING_STOP == recordingResult.getRecordingState()) {
                        callback.onRecordingStopped();
                    }
                }
            }, handler);
        }
    }

    @Override
    public void resumeRecording() {
        if (null != mMainCameraDevice) {
            mMainCameraDevice.resumeRecording();
        }
    }

    @Override
    public void pauseRecording() {
        if (null != mMainCameraDevice) {
            mMainCameraDevice.pauseRecording();
        }
    }

    @Override
    public void stopRecording(RecordingStatusCallback callback) {
        if (null != mMainCameraDevice) {
            mMainCameraDevice.stopRecording();
        }
    }

    @Override
    public void takePicture(ConfigureBean configure, PictureCallback callback, Handler handler) {
        CameraDeviceInfo info = getCameraDeviceInfo(configure.getCameraModeType(), configure.getCameraType());
        int jpegOrientation = getJpegOrientation(info, configure.getOrientation());

        mMainCameraDevice.setParameter(CaptureRequest.JPEG_ORIENTATION, jpegOrientation);
        mMainCameraDevice.takePicture(new CameraPictureCallback() {
            @Override
            public void onCaptureShutter(long timestamp) {
                super.onCaptureShutter(timestamp);
                callback.onShutterCallback(timestamp);
            }

            @Override
            public void onImageReceived(CameraPictureImage image) {
                super.onImageReceived(image);

                if (null == image) {
                    Log.e(TAG, "onImageReceived, image is null");

                    return;
                }

                Log.d(TAG, "onImageReceived, takePicture receive image from Aps");

                callback.onPictureTaken(image.getImage(), image.getTimestamp());

                Log.d(TAG, "onImageReceived, takePicture insert media store finish");
                Log.e(TAG, "onImageReceived");
            }

            @Override
            public void onCaptureFailed(CameraPictureResult result) {
                super.onCaptureFailed(result);

                Log.d(TAG, "onCaptureFailed, result: " + result);

                callback.onPictureTakenFailed();
            }
        }, handler);
    }

    private Map<String, Surface> buildPreviewSurface(ConfigureBean configure, Handler handler) {
        List<String> cameras = mCameraDeviceInfo.getPhysicalCameraTypeList();
        PreviewInterface previewHandle = configure.getPreviewHandle();

        if ((null != cameras) && (null != previewHandle)) {
            previewHandle.setSensorOrientation(mCameraDeviceInfo.get(SENSOR_ORIENTATION));
            LinkedHashMap<String, Size> sizes = new LinkedHashMap<>();

            for (String type : cameras) {
                if ((cameras.size() > 1) && FRONT_MAIN.equals(type)) {
                    sizes.put(type, mSubPreviewSize);
                } else {
                    sizes.put(type, mPreviewSize);
                }
            }

            Map<String, Surface> surfaceMap = previewHandle.buildSurface(sizes, handler);

            Log.d(TAG, "buildPreviewSurface, preview surfaceMap: " + surfaceMap + ", cameras: " + cameras);

            return surfaceMap;
        }

        return null;
    }

    private void setPreviewParameter(ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();

        if (VIDEO_MODE.equals(modeType) || SLOW_VIDEO_MODE.equals(modeType)) {
            mMainCameraDevice.setParameter(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, configure.getVideoFps());
            // 前置录像镜像开启时，设置该参数
            boolean shouldMirror = FRONT_MAIN.equals(cameraType) && configure.isFrontCameraVideoMirrorEnable();
            mMainCameraDevice.setParameter(CameraParameter.CAPTURE_FLIP_MODE_ENABLE, shouldMirror);
        }

        if (PHOTO_MODE.equals(configure.getCameraModeType())) {
            FeatureFactory.getInstance().getFeature(PHOTO_FEATURE_CAPTURE_HDR)
                    .setPreviewParameter(mMainCameraDevice, modeType, cameraType, configure.getCaptureHdrMode());
        }

        if (PORTRAIT_MODE.equals(configure.getCameraModeType())) {
            FeatureFactory.getInstance().getFeature(PHOTO_FEATURE_PORTRAIT_BLUR)
                    .setPreviewParameter(mMainCameraDevice, modeType, cameraType, configure.getBlurIndex());
        }

        FeatureFactory.getInstance().getFeature(FeatureFactory.FeatureIds.COMMON_FEATURE_ZOOM)
                .setPreviewParameter(mMainCameraDevice, modeType, cameraType, configure.getZoomLevel());
        FeatureFactory.getInstance().getFeature(COMMON_FEATURE_FLASH)
                .setPreviewParameter(mMainCameraDevice, modeType, cameraType, configure.getFlashMode());
        FeatureFactory.getInstance().getFeature(COMMON_FEATURE_AE_AF)
                .setPreviewParameter(mMainCameraDevice, modeType, cameraType, configure.getAutoFocusRectF());
        FeatureFactory.getInstance().getFeature(COMMON_FEATURE_EXPOSURE_COMPENSATION)
                .setPreviewParameter(mMainCameraDevice, modeType, cameraType, configure.getExposureProgress());
    }


    private List<Size> getSupportPreviewSizeList(ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        double currentRatio = configure.getPreviewRatio();
        // 根据ui feature的配置，将对应的feature存入map，用于获取feature 组合对应的功能支持情况。
        Map<String, String> configFeatures = getConfiguredFeaturesByConfigure(configure);

        if (VIDEO_MODE.equals(modeType) || SLOW_VIDEO_MODE.equals(modeType)) {
            List<Size> supportVideoSize = getCameraDeviceInfo(modeType, cameraType).getSupportVideoSize(configFeatures);
            return CameraUtil.getSizeListByRatio(supportVideoSize, currentRatio);
        } else {
            List<Size> supportVideoSize = getCameraDeviceInfo(modeType, cameraType)
                    .getSupportPreviewSize(SURFACE_TEXTURE, configFeatures);
            return CameraUtil.getSizeListByRatio(supportVideoSize, currentRatio);
        }
    }

    private List<Size> getSupportVideoSizeList(ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        double currentRatio = configure.getPreviewRatio();
        // 根据ui feature的配置，将对应的feature存入map，用于获取feature 组合对应的功能支持情况。
        Map<String, String> configFeatures = getConfiguredFeaturesByConfigure(configure);

        if (VIDEO_MODE.equals(modeType) || SLOW_VIDEO_MODE.equals(modeType)) {
            List<Size> supportVideoSize = getCameraDeviceInfo(modeType, cameraType).getSupportVideoSize(configFeatures);
            if (null != supportVideoSize) {
                List<Size> copyOnWriteArrayList = new CopyOnWriteArrayList<>(supportVideoSize);
                return CameraUtil.getSizeListByRatio(copyOnWriteArrayList, currentRatio);
            }
        }

        return null;
    }

    private List<Size> getSupportPictureSizeList(ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        double currentRatio = configure.getPreviewRatio();
        // 根据ui feature的配置，将对应的feature存入map，用于获取feature 组合对应的功能支持情况。
        Map<String, String> configFeatures = getConfiguredFeaturesByConfigure(configure);
        List<Size> supportPictureSize = getCameraDeviceInfo(modeType, cameraType).getSupportPictureSize(configFeatures);

        return CameraUtil.getSizeListByRatio(supportPictureSize, currentRatio);
    }

    /**
     * 根据 ui 配置更新preview size， video size 和 picture size，用于在创建surface时使用。
     */
    private void updateTargetSurfaceSize(ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        double currentRatio = configure.getPreviewRatio();
        List<Size> previewSizes = getSupportPreviewSizeList(configure);
        List<Size> videoSizes = getSupportVideoSizeList(configure);
        List<Size> pictureSizes = getSupportPictureSizeList(configure);

        if (MULTI_CAMERA_MODE.equals(modeType)) {
            mPreviewSize = CameraUtil.getOptimalPreviewSize(getAppContext(), previewSizes, currentRatio);
            mSubPreviewSize = new Size(FIXED_FRONT_WIDTH, FIXED_FRONT_HEIGHT);
        } else if (NIGHT_MODE.equals(modeType) || PORTRAIT_MODE.equals(modeType) || PHOTO_MODE.equals(modeType)) {
            mPreviewSize = CameraUtil.getOptimalPreviewSize(getAppContext(), previewSizes, currentRatio);
            // choose the support biggest capture size
            mPictureSize = pictureSizes.get(0);
        } else {
            if (!Constant.DisplayResolution.DISPLAY_RESOLUTION_NORMAL.equals(configure.getVideoResolution())) {
                // 目前这里是硬编码，由于list中只有 [1920x1080, 1280x720]， 因此在非 720p 的时候取第一个。
                if ((null != previewSizes) && !previewSizes.isEmpty()) {
                    mPreviewSize = previewSizes.get(0);
                }

                if ((null != videoSizes) && !videoSizes.isEmpty()) {
                    mVideoSize = videoSizes.get(0);
                }
            } else {
                if ((null != previewSizes) && !previewSizes.isEmpty()) {
                    mPreviewSize = previewSizes.get(previewSizes.size() - 1);
                }

                if ((null != videoSizes) && !videoSizes.isEmpty()) {
                    mVideoSize = videoSizes.get(videoSizes.size() - 1);
                }
            }
        }

        Log.d(TAG, "updateTargetSurfaceSize, mPreviewSize: " + mPreviewSize + ", mVideoSize: " + mVideoSize
                + "mPictureSize: " + mPictureSize + "previewSizes: " + previewSizes);
    }

    private void setConfigParameter(CameraDeviceConfig.Builder builder, ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        builder.setMode(modeType);

        // preview config
        if (MULTI_CAMERA_MODE.equals(modeType) && REAR_MAIN_FRONT_MAIN.equals(cameraType)) {
            List<CameraDeviceConfig.PreviewConfig> previewConfigs = new ArrayList<>();
            previewConfigs.add(new CameraDeviceConfig.PreviewConfig(REAR_MAIN, mPreviewSize, configure.getPreviewSurfaceType()));
            previewConfigs.add(new CameraDeviceConfig.PreviewConfig(FRONT_MAIN, mSubPreviewSize, configure.getPreviewSurfaceType()));
            builder.setPreviewConfig(previewConfigs);
        } else {
            builder.setPreviewConfig(Collections.singletonList(new CameraDeviceConfig.PreviewConfig(configure.getCameraType(),
                    mPreviewSize, configure.getPreviewSurfaceType())));
        }

        // picture config
        if (NIGHT_MODE.equals(modeType) || PORTRAIT_MODE.equals(modeType) || PHOTO_MODE.equals(modeType)) {
            builder.setPictureConfig(Collections.singletonList(new CameraDeviceConfig.PictureConfig(
                    configure.getCameraType(), mPictureSize, ImageFormat.JPEG)));
        }

        // video config
        if (VIDEO_MODE.equals(modeType)) {
            builder.setVideoSize(mVideoSize);

            FeatureFactory.getInstance().getFeature(VIDEO_FEATURE_VIDEO_HDR)
                    .setConfigureParameter(builder, configure.getVideoHdrMode());
            FeatureFactory.getInstance().getFeature(VIDEO_FEATURE_VIDEO_STABILIZATION)
                    .setConfigureParameter(builder, configure.getStabilizationMode());
            FeatureFactory.getInstance().getFeature(VIDEO_FEATURE_VIDEO_FPS)
                    .setConfigureParameter(builder, configure.getVideoFps());
            FeatureFactory.getInstance().getFeature(VIDEO_FEATURE_VIDEO_AI_NIGHT)
                    .setConfigureParameter(builder, configure.isVideoAiNightOn());
        } else if (SLOW_VIDEO_MODE.equals(modeType)) {
            builder.setVideoSize(mVideoSize);
            FeatureFactory.getInstance().getFeature(VIDEO_FEATURE_VIDEO_FPS)
                    .setConfigureParameter(builder, configure.getVideoFps());
        }
    }

    private static Map<String, String> getConfiguredFeaturesByConfigure(ConfigureBean configure) {
        Map<String, String> configFeatures = new HashMap<>();
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();

        if (VIDEO_MODE.equals(modeType) && !CameraUnitClient.CameraType.REAR_SAT.equals(cameraType)) {

            if (!FRONT_MAIN.equals(cameraType)) {
                String videoHdrMode = configure.getVideoHdrMode();
                String stbMode = configure.getStabilizationMode();

                if (Constant.CommonStateValue.ON.equals(videoHdrMode)) {
                    configFeatures.put(VIDEO_3HDR_MODE.getKeyName(), Constant.CommonStateValue.ON);
                }

                if (VIDEO_STABILIZATION.equals(stbMode) || SUPER_STABILIZATION.equals(stbMode)) {
                    configFeatures.put(VIDEO_STABILIZATION_MODE.getKeyName(), stbMode);
                }
            }

            // because this value will effect the sdk decision, so if the value is off, don't need configure.
            if (configure.isVideoAiNightOn()) {
                configFeatures.put(AI_NIGHT_VIDEO_MODE.getKeyName(), Integer.toString(1));
            }
        } else if (SLOW_VIDEO_MODE.equals(modeType)) {
            configFeatures.put(VIDEO_DYNAMIC_FPS.getKeyName(), configure.getVideoFps().toString());
        }

        return configFeatures;
    }

    private CameraDeviceInfo getCameraDeviceInfo(String modeType, String cameraType) {
        CameraDeviceInfo deviceInfo = CameraUnit.getCameraClient(getAppContext()).getCameraDeviceInfo(cameraType, modeType);

        // if in multi-camera mode, will return more then one cameras, get the back camera device info.
        if (MULTI_CAMERA_MODE.equals(modeType) && (deviceInfo.getPhysicalCameraTypeList().size() > 1)) {
            deviceInfo = CameraUnit.getCameraClient(getAppContext()).getCameraDeviceInfo(REAR_MAIN, MULTI_CAMERA_MODE);
        }

        return deviceInfo;
    }

    public static int getJpegOrientation(CameraDeviceInfo info, int orientation) {
        int rotation = 0;

        Integer facing = info.get(CameraCharacteristics.LENS_FACING);

        if (OrientationEventListener.ORIENTATION_UNKNOWN != orientation) {
            if ((null != facing) && (CameraCharacteristics.LENS_FACING_FRONT == facing)) {
                rotation = (info.get(CameraCharacteristics.SENSOR_ORIENTATION) - orientation
                        + Constant.Orientation.ORIENTATION_360) % Constant.Orientation.ORIENTATION_360;
            } else {
                rotation = (info.get(CameraCharacteristics.SENSOR_ORIENTATION) + orientation)
                        % Constant.Orientation.ORIENTATION_360;
            }
        } else {
            rotation = info.get(CameraCharacteristics.SENSOR_ORIENTATION);
        }

        return rotation;
    }
}