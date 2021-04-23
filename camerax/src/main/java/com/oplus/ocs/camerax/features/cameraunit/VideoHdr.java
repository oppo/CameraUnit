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
 * File: - VideoHdr.java
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
import android.util.Log;
import android.util.Range;

import com.oplus.ocs.camera.CameraDeviceConfig;
import com.oplus.ocs.camera.CameraDeviceInfo;
import com.oplus.ocs.camera.CameraParameter;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeatureVideoHdr;
import com.oplus.ocs.camerax.util.Constant;

import java.util.List;
import java.util.Map;

import static com.oplus.ocs.camera.CameraParameter.FLASH_MODE;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_3HDR_MODE;
import static com.oplus.ocs.camerax.util.Constant.VideoFps.FRAME_RATE_30;
import static com.oplus.ocs.camerax.util.Constant.VideoStabilizationMode.VIDEO_STABILIZATION_OFF;

public class VideoHdr extends BaseFeatureVideoHdr {
    private static final String TAG = "VideoHdr";

    @Override
    public <T> void setConfigureParameter(Object configureBuilderHolder, T value) {
        CameraDeviceConfig.Builder builder = (CameraDeviceConfig.Builder) configureBuilderHolder;

        if (Constant.CommonStateValue.ON.equals(value)) {
            builder.setParameter(VIDEO_3HDR_MODE, Constant.CommonStateValue.ON);
        }
    }

    @Override
    public <T> void setPreviewParameter(Object cameraDeviceHolder, String modeType, String cameraType, T value) {
        // do nothing in this feature
    }

    @Override
    public List<String> getSupportFeatureSubValues(Context appContext, ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        CameraDeviceInfo cameraDeviceInfo = CameraUnitFeatureManager.getCameraDeviceInfo(appContext, modeType, cameraType);

        return cameraDeviceInfo.getConfigureParameterRange(VIDEO_3HDR_MODE);
    }

    @Override
    public void checkConflictFeature(Context appContext, ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        CameraDeviceInfo cameraDeviceInfo = CameraUnitFeatureManager.getCameraDeviceInfo(appContext, modeType, cameraType);
        Map<String, List<String>> conflictMap
                = cameraDeviceInfo.getConflictParameter(VIDEO_3HDR_MODE.getKeyName(), configure.getVideoHdrMode());

        Log.d(TAG, "checkConflictFeature: VIDEO_3HDR_MODE conflict with: " + conflictMap);

        // 如果存在互斥表，则通过互斥表判断是否和其他功能互斥，需要注意的是，有时互斥结果会在其他功能的互斥结果中和HDR功能互斥。。。
        // 部分老机型，存在互斥表接口未实现的情况，此情况下，需要按照默认值设置。
        if (null != conflictMap) {
            // 通常情况下，视频 HDR 会和 Flash、 超级防抖、 Ai视频、高帧率视频互斥。
            List<String> flashModeList = conflictMap.get(FLASH_MODE.getKeyName());

            if (null != flashModeList && flashModeList.contains(configure.getFlashMode())) {
                configure.setFlashMode(CameraParameter.FlashMode.FLASH_OFF);
            }
        } else {
            // conflict with Flash on/auto by default.
            if (!CameraParameter.CommonStateValue.OFF.equals(configure.getVideoHdrMode())
                    && !CameraParameter.FlashMode.FLASH_OFF.equals(configure.getStabilizationMode())) {
                configure.setFlashMode(CameraParameter.FlashMode.FLASH_OFF);
            }

            // conflict with super stabilization by default.
            if (!CameraParameter.CommonStateValue.OFF.equals(configure.getVideoHdrMode())
                    && CameraParameter.VideoStabilizationMode.SUPER_STABILIZATION.equals(configure.getStabilizationMode())) {
                configure.setStabilizationMode(VIDEO_STABILIZATION_OFF);
            }

            // conflict with ai night by default.
            if (!CameraParameter.CommonStateValue.OFF.equals(configure.getVideoHdrMode()) && configure.isVideoAiNightOn()) {
                configure.setVideoAiNightOn(false);
            }

            // conflict with high frame rate by default.
            if (!CameraParameter.CommonStateValue.OFF.equals(configure.getVideoHdrMode())
                    && CameraParameter.VideoFps.FPS_60 == configure.getVideoFps().getUpper()) {
                configure.setVideoFps(new Range<>(FRAME_RATE_30, FRAME_RATE_30));
            }
        }
    }
}