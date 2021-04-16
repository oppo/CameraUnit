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
 * File: - CameraUnitFeatureManager.java
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

package com.oplus.ocs.camerax.features.cameraunit;

import android.content.Context;

import com.oplus.ocs.camera.CameraDeviceInfo;
import com.oplus.ocs.camera.CameraParameter;
import com.oplus.ocs.camera.CameraUnit;
import com.oplus.ocs.camerax.features.FeatureFactory;
import com.oplus.ocs.camerax.util.Constant;

import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.MULTI_CAMERA_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraType.REAR_MAIN;

public class CameraUnitFeatureManager {
    public static boolean isNeedReConfigureSession(int featureId) {
        return (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_HDR == featureId)
                || (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT == featureId)
                || (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION == featureId)
                || (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_RESOLUTION == featureId)
                || (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_FPS == featureId)
                || (FeatureFactory.FeatureIds.PHOTO_FEATURE_RATIO == featureId);
    }

    public static boolean isFeatureSupport(Context appContext, String cameraModeType, String cameraType, int featureId) {
        // 这里需要等CameraUnitClient init 执行完成才可以调用, 因此这里要么sdk里做同步，要么就需要在 open camera 后再执行
        CameraDeviceInfo cameraDeviceInfo = getCameraDeviceInfo(appContext, cameraModeType, cameraType);

        if (FeatureFactory.FeatureIds.COMMON_FEATURE_AE_AF == featureId) {
            return true;
        } else if (FeatureFactory.FeatureIds.COMMON_FEATURE_EXPOSURE_COMPENSATION == featureId) {
            return true;
        } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_HDR == featureId) {
            return cameraDeviceInfo.isSupportConfigureParameter(CameraParameter.VIDEO_3HDR_MODE);
        } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT == featureId) {
            return cameraDeviceInfo.isSupportConfigureParameter(CameraParameter.AI_NIGHT_VIDEO_MODE);
        } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION == featureId) {
            return cameraDeviceInfo.isSupportConfigureParameter(CameraParameter.VIDEO_STABILIZATION_MODE);
        } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_FPS == featureId) {
            return cameraDeviceInfo.isSupportConfigureParameter(CameraParameter.VIDEO_FPS);
        } else if (FeatureFactory.FeatureIds.COMMON_FEATURE_FLASH == featureId) {
            return cameraDeviceInfo.isSupportPreviewParameter(CameraParameter.FLASH_MODE);
        } else if (FeatureFactory.FeatureIds.PHOTO_FEATURE_CAPTURE_HDR == featureId) {
            return cameraDeviceInfo.isSupportPreviewParameter(CameraParameter.CAPTURE_HDR_MODE);
        } else if (FeatureFactory.FeatureIds.PHOTO_FEATURE_PORTRAIT_BLUR == featureId) {
            return cameraDeviceInfo.isSupportPreviewParameter(CameraParameter.KEY_BLUR_LEVEL_RANGE);
        } else if (FeatureFactory.FeatureIds.PHOTO_FEATURE_RATIO == featureId) {
            return Constant.CameraMode.PHOTO_MODE.equals(cameraModeType)
                    || Constant.CameraMode.NIGHT_PHOTO_MODE.equals(cameraModeType)
                    || Constant.CameraMode.PREVIEW_MODE.equals(cameraModeType);
        } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_RESOLUTION == featureId) {
            return Constant.CameraMode.VIDEO_MODE.equals(cameraModeType)
                    || Constant.CameraMode.SLOW_VIDEO_MODE.equals(cameraModeType);
        } else if (FeatureFactory.FeatureIds.COMMON_FEATURE_ZOOM == featureId) {
            return (Constant.CameraMode.VIDEO_MODE.equals(cameraModeType)
                    || Constant.CameraMode.PHOTO_MODE.equals(cameraModeType)
                    || Constant.CameraMode.PREVIEW_MODE.equals(cameraModeType)
                    || Constant.CameraMode.MULTI_CAMERA_MODE.equals(cameraModeType)
                    || Constant.CameraMode.NIGHT_PHOTO_MODE.equals(cameraModeType))
                    && !Constant.CameraType.FRONT_MAIN_CAMERA.equals(cameraType);
        }

        return false;
    }

    public static CameraDeviceInfo getCameraDeviceInfo(Context appContext, String modeType, String cameraType) {
        CameraDeviceInfo deviceInfo = CameraUnit.getCameraClient(appContext).getCameraDeviceInfo(cameraType, modeType);

        // if in multi-camera mode, will return more then one cameras, get the back camera device info.
        if (MULTI_CAMERA_MODE.equals(modeType) && (deviceInfo.getPhysicalCameraTypeList().size() > 1)) {
            deviceInfo = CameraUnit.getCameraClient(appContext).getCameraDeviceInfo(REAR_MAIN, MULTI_CAMERA_MODE);
        }

        return deviceInfo;
    }
}