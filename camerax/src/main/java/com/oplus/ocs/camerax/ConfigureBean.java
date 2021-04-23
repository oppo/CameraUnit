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
 * File: - ConfigureBean.java
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

import android.graphics.RectF;
import android.util.Range;

import androidx.annotation.NonNull;

import com.oplus.ocs.camerax.component.preview.PreviewInterface;
import com.oplus.ocs.camerax.util.Constant;

import java.util.List;

import static com.oplus.ocs.camerax.util.Constant.VideoFps.FRAME_RATE_240;
import static com.oplus.ocs.camerax.util.Constant.VideoFps.FRAME_RATE_30;


public class ConfigureBean {
    private String mCameraMode = Constant.CameraMode.VIDEO_MODE;
    private String mCameraType = Constant.CameraType.REAR_MAIN_CAMERA;

    // feature configures
    private String mFeatureVideoHdrMode = Constant.CommonStateValue.OFF;
    private String mFeatureCaptureHdrMode = Constant.CommonStateValue.OFF;
    private Range<Integer> mFeatureVideoFps = new Range<>(FRAME_RATE_30, FRAME_RATE_30);
    private String mFeatureVideoResolution = Constant.DisplayResolution.DISPLAY_RESOLUTION_NORMAL;
    private String mFeatureStabilizationMode = null;
    private String mFeatureFlashMode = Constant.FlashMode.FLASH_OFF;
    private RectF mFeatureAutoFocusRectF = null;
    private boolean mbFeatureVideoAiNightOn = false;
    private double mFeaturePreviewRatio = Constant.PreviewRatio.RATIO_VALUE_16_9;
    private float mFeatureExposureProgress = 0.5f;
    private float mFeatureZoomLevel = 1.0f;
    private int mFeaturePortraitBlurIndex = 60;

    // setting configures
    private boolean mbSettingFrontCameraVideoMirrorEnable = true;
    private boolean mbConfigureSession = false;
    private boolean mbReopenCamera = false;
    private List<String> mStabilizationList;
    private int mOrientation = Constant.Orientation.ORIENTATION_0;

    // preview configure
    private PreviewInterface mPreviewHandle = null;
    private int mPreviewSurfaceType = Constant.CameraSurfaceType.SURFACE_TEXTURE;

    public String getCaptureHdrMode() {
        return mFeatureCaptureHdrMode;
    }

    public String getCameraModeType() {
        return mCameraMode;
    }

    public Range<Integer> getVideoFps() {
        return mFeatureVideoFps;
    }

    public String getCameraType() {
        return mCameraType;
    }

    public double getPreviewRatio() {
        return mFeaturePreviewRatio;
    }

    public String getVideoHdrMode() {
        return mFeatureVideoHdrMode;
    }

    public String getStabilizationMode() {
        return mFeatureStabilizationMode;
    }

    public boolean isVideoAiNightOn() {
        return mbFeatureVideoAiNightOn;
    }

    public int getPreviewSurfaceType() {
        return mPreviewSurfaceType;
    }

    public String getVideoResolution() {
        return mFeatureVideoResolution;
    }

    public String getFlashMode() {
        return mFeatureFlashMode;
    }

    public boolean isFrontCameraVideoMirrorEnable() {
        return mbSettingFrontCameraVideoMirrorEnable;
    }

    public float getZoomLevel() {
        return mFeatureZoomLevel;
    }

    public PreviewInterface getPreviewHandle() {
        return mPreviewHandle;
    }

    public void setPreviewHandle(PreviewInterface previewHandle) {
        this.mPreviewHandle = previewHandle;
    }

    public void setPreviewSurfaceType(int previewSurfaceType) {
        this.mPreviewSurfaceType = previewSurfaceType;
    }

    public void setCameraMode(String cameraMode) {
        this.mCameraMode = cameraMode;
    }

    public void setCameraTypeDefaultValue(String cameraType) {
        this.mCameraType = cameraType;
        this.mFeatureFlashMode = Constant.FlashMode.FLASH_OFF;
        this.mFeatureZoomLevel = 1.0f;
        this.mFeatureExposureProgress = 0.5f;
        this.mbFeatureVideoAiNightOn = false;
        this.mFeatureAutoFocusRectF = null;
        this.mOrientation = Constant.Orientation.ORIENTATION_0;
        mFeatureVideoFps = new Range<>(FRAME_RATE_30, FRAME_RATE_30);

        if (Constant.CameraMode.VIDEO_MODE.equals(getCameraModeType())) {
            if (Constant.CameraType.REAR_SAT_CAMERA.equals(cameraType)
                    && mStabilizationList.contains(Constant.VideoStabilizationMode.VIDEO_STABILIZATION)) {
                setStabilizationMode(Constant.VideoStabilizationMode.VIDEO_STABILIZATION);
            } else {
                setStabilizationMode(Constant.CommonStateValue.OFF);
            }
        }
    }

    public void setVideoHdrMode(String videoHdrMode) {
        this.mFeatureVideoHdrMode = videoHdrMode;
    }

    public void setCaptureHdrMode(String captureHdrMode) {
        this.mFeatureCaptureHdrMode = captureHdrMode;
    }

    public void setVideoFps(Range<Integer> videoFps) {
        if (null == videoFps) {
            return;
        }

        this.mFeatureVideoFps = videoFps;
    }

    public void setVideoResolution(String videoResolution) {
        this.mFeatureVideoResolution = videoResolution;
    }

    public void setStabilizationMode(String stabilizationMode) {
        this.mFeatureStabilizationMode = stabilizationMode;
    }

    public void setFlashMode(String flashMode) {
        this.mFeatureFlashMode = flashMode;
    }

    public void setFrontCameraVideoMirrorEnable(boolean frontCameraVideoMirrorEnable) {
        this.mbSettingFrontCameraVideoMirrorEnable = frontCameraVideoMirrorEnable;
    }

    public void setPhotoRatio(double previewRatio) {
        this.mFeaturePreviewRatio = previewRatio;
    }

    public void setVideoAiNightOn(boolean isVideoAiNightOn) {
        this.mbFeatureVideoAiNightOn = isVideoAiNightOn;
    }

    public void setZoomLevel(float zoomLevel) {
        this.mFeatureZoomLevel = zoomLevel;
    }

    public void setExposureProgress(float exposureValue) {
        this.mFeatureExposureProgress = exposureValue;
    }

    public float getExposureProgress() {
        return mFeatureExposureProgress;
    }

    public void setBlurIndex(int blurIndex) {
        this.mFeaturePortraitBlurIndex = blurIndex;
    }

    public int getBlurIndex() {
        return mFeaturePortraitBlurIndex;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public RectF getAutoFocusRectF() {
        return mFeatureAutoFocusRectF;
    }

    public void setAutoFocusRectF(RectF mAutoFocusRectF) {
        this.mFeatureAutoFocusRectF = mAutoFocusRectF;
    }

    public boolean isNeedConfigureSession() {
        return mbConfigureSession;
    }

    public void setNeedConfigureSession(boolean mbConfigureSession) {
        this.mbConfigureSession = mbConfigureSession;
    }

    public boolean isReopenCamera() {
        return mbReopenCamera;
    }

    public void setNeedReopenCamera(boolean needReopenCamera) {
        this.mbReopenCamera = needReopenCamera;
    }

    public void setStabilizationList(List<String> mStabilizationList) {
        this.mStabilizationList = mStabilizationList;
    }

    public void setCameraModeDefaultValue(@NonNull String cameraMode) {
        this.mCameraMode = cameraMode;
        this.mFeatureFlashMode = Constant.FlashMode.FLASH_OFF;
        this.mFeatureZoomLevel = 1.0f;
        this.mFeatureExposureProgress = 0.5f;
        this.mFeatureAutoFocusRectF = null;
        this.mOrientation = Constant.Orientation.ORIENTATION_0;

        if (Constant.CameraMode.VIDEO_MODE.equals(mCameraMode)) {
            mFeatureVideoHdrMode = Constant.CommonStateValue.OFF;
            mFeatureVideoFps = new Range<>(FRAME_RATE_30, FRAME_RATE_30);
            mFeatureVideoResolution = Constant.DisplayResolution.DISPLAY_RESOLUTION_NORMAL;
            mFeaturePreviewRatio = Constant.PreviewRatio.RATIO_VALUE_16_9;
            mbFeatureVideoAiNightOn = false;

            if (Constant.CameraType.REAR_SAT_CAMERA.equals(mCameraType)) {
                mFeatureStabilizationMode = Constant.VideoStabilizationMode.VIDEO_STABILIZATION;
            } else {
                mFeatureStabilizationMode = Constant.CommonStateValue.OFF;
            }
        } else if (Constant.CameraMode.PHOTO_MODE.equals(mCameraMode)) {
            mFeatureCaptureHdrMode = Constant.CommonStateValue.OFF;
            mFeaturePreviewRatio = Constant.PreviewRatio.RATIO_VALUE_4_3;
        } else if (Constant.CameraMode.MULTI_CAMERA_MODE.equals(mCameraMode)) {
            mFeatureStabilizationMode = Constant.CommonStateValue.OFF;
            mFeatureCaptureHdrMode = Constant.CommonStateValue.OFF;
            mFeatureVideoHdrMode = Constant.CommonStateValue.OFF;
            mFeaturePreviewRatio = Constant.PreviewRatio.RATIO_VALUE_16_9;
        } else if (Constant.CameraMode.PORTRAIT_PHOTO_MODE.equals(mCameraMode)) {
            mFeatureCaptureHdrMode = Constant.CommonStateValue.OFF;
            mFeaturePreviewRatio = Constant.PreviewRatio.RATIO_VALUE_4_3;
        } else if (Constant.CameraMode.NIGHT_PHOTO_MODE.equals(mCameraMode)) {
            mFeatureCaptureHdrMode = Constant.CommonStateValue.OFF;
            mFeatureStabilizationMode = Constant.CommonStateValue.OFF;
            mFeatureCaptureHdrMode = Constant.CommonStateValue.OFF;
            mFeatureVideoHdrMode = Constant.CommonStateValue.OFF;
            mFeaturePreviewRatio = Constant.PreviewRatio.RATIO_VALUE_4_3;
        } else if (Constant.CameraMode.SLOW_VIDEO_MODE.equals(mCameraMode)) {
            mFeatureVideoFps = new Range<>(FRAME_RATE_240, FRAME_RATE_240);;
            mFeatureVideoResolution = Constant.DisplayResolution.DISPLAY_RESOLUTION_HIGH;
            mFeaturePreviewRatio = Constant.PreviewRatio.RATIO_VALUE_16_9;
            mFeatureStabilizationMode = Constant.CommonStateValue.OFF;
            mbFeatureVideoAiNightOn = false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "ConfigureBean{" +
                "mPreviewSurfaceType=" + mPreviewSurfaceType +
                ", mCameraMode='" + mCameraMode + '\'' +
                ", mCameraType='" + mCameraType + '\'' +
                ", mVideoHdrMode='" + mFeatureVideoHdrMode + '\'' +
                ", mCaptureHdrMode='" + mFeatureCaptureHdrMode + '\'' +
                ", mVideoFps='" + mFeatureVideoFps + '\'' +
                ", mVideoResolution='" + mFeatureVideoResolution + '\'' +
                ", mStabilizationMode='" + mFeatureStabilizationMode + '\'' +
                ", mFlashMode='" + mFeatureFlashMode + '\'' +
                ", mbFrontCameraVideoMirrorEnable=" + mbSettingFrontCameraVideoMirrorEnable +
                ", mPreviewRatio=" + mFeaturePreviewRatio +
                ", mbVideoAiNightOn=" + mbFeatureVideoAiNightOn +
                ", mZoomLevel=" + mFeatureZoomLevel +
                ", mExposureValue=" + mFeatureExposureProgress +
                ", mBlurIndex=" + mFeaturePortraitBlurIndex +
                ", mPreviewHandle=" + mPreviewHandle +
                ", mOrientation=" + mOrientation +
                ", mAutoFocusRectF=" + mFeatureAutoFocusRectF +
                '}';
    }
}