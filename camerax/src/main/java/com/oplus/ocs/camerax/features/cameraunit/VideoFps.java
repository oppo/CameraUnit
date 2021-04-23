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
 * File: - VideoFps.java
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
import android.util.Range;

import com.oplus.ocs.camera.CameraDeviceConfig;
import com.oplus.ocs.camera.CameraDeviceInfo;
import com.oplus.ocs.camera.CameraParameter;
import com.oplus.ocs.camera.CameraUnit;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeatureVideoFps;
import com.oplus.ocs.camerax.util.CameraUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.oplus.ocs.camera.CameraParameter.AI_NIGHT_VIDEO_MODE;
import static com.oplus.ocs.camera.CameraParameter.AiNightVideoValues.AI_NIGHT_VIDEO_OFF;
import static com.oplus.ocs.camera.CameraParameter.AiNightVideoValues.AI_NIGHT_VIDEO_ON;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_DYNAMIC_FPS;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_STABILIZATION_MODE;
import static com.oplus.ocs.camerax.util.Constant.VideoStabilizationMode.VIDEO_STABILIZATION_OFF;

public class VideoFps extends BaseFeatureVideoFps {

    @Override
    public <T> void setConfigureParameter(Object configureBuilderHolder, T value) {
        CameraDeviceConfig.Builder builder = (CameraDeviceConfig.Builder) configureBuilderHolder;

        builder.setParameter(VIDEO_DYNAMIC_FPS, (Range) value);
    }

    @Override
    public <T> void setPreviewParameter(Object cameraDeviceHolder, String modeType, String cameraType, T value) {
        // do nothing in this feature
    }

    @Override
    public List<String> getSupportFeatureSubValues(Context appContext, ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        CameraDeviceInfo cameraDeviceInfo = CameraUnit.getCameraClient(appContext).getCameraDeviceInfo(cameraType, modeType);
        List<Range> fpsRangeList = cameraDeviceInfo.getConfigureParameterRange(VIDEO_DYNAMIC_FPS);
        List<String> fpsList = new ArrayList<>();

        for (Range range : fpsRangeList) {
            fpsList.add(range.toString());
        }

        return fpsList;
    }

    @Override
    public void checkConflictFeature(Context appContext, ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        CameraDeviceInfo cameraDeviceInfo = CameraUnit.getCameraClient(appContext).getCameraDeviceInfo(cameraType, modeType);
        Map<String, List<String>> stabilizationConflictMap = cameraDeviceInfo.
                getConflictParameter(VIDEO_STABILIZATION_MODE.getKeyName(), configure.getStabilizationMode());
        Map<String, List<String>> aiNightConflictMap = cameraDeviceInfo.getConflictParameter(AI_NIGHT_VIDEO_MODE.getKeyName(),
                configure.isVideoAiNightOn() ? String.valueOf(AI_NIGHT_VIDEO_ON) : String.valueOf(AI_NIGHT_VIDEO_OFF));

        if (null != stabilizationConflictMap) {
            List<String> fpsLists = stabilizationConflictMap.get(VIDEO_DYNAMIC_FPS.getKeyName());

            if (null != fpsLists && fpsLists.contains(CameraUtil.convertFpsRangeValue(configure.getVideoFps()))) {
                configure.setStabilizationMode(VIDEO_STABILIZATION_OFF);
            }
        }

        if (null != aiNightConflictMap) {
            List<String> fpsLists = aiNightConflictMap.get(VIDEO_DYNAMIC_FPS.getKeyName());

            if (null != fpsLists && fpsLists.contains(CameraUtil.convertFpsRangeValue(configure.getVideoFps()))) {
                configure.setVideoAiNightOn(false);
            }
        }

        // 部分老机型，存在互斥表接口未实现的情况，此情况下，需要按照默认值设置。
        if ((null == stabilizationConflictMap) || (null == aiNightConflictMap)) {
            if (CameraParameter.VideoFps.FPS_60 == configure.getVideoFps().getUpper()) {
                // conflict with video hdr on by in 60fps.
                configure.setVideoHdrMode(CameraParameter.CommonStateValue.OFF);
                configure.setVideoAiNightOn(false);
            } else {
                // conflict with super stabilization in 30fps.
                configure.setStabilizationMode(VIDEO_STABILIZATION_OFF);
            }
        }
    }
}