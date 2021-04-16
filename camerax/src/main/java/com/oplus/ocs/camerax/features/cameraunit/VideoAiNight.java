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
 * File: - VideoAiNight.java
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
import com.oplus.ocs.camera.CameraUnit;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeatureVideoAiNight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.oplus.ocs.camera.CameraParameter.AI_NIGHT_VIDEO_MODE;
import static com.oplus.ocs.camera.CameraParameter.AiNightVideoValues.AI_NIGHT_VIDEO_OFF;
import static com.oplus.ocs.camera.CameraParameter.AiNightVideoValues.AI_NIGHT_VIDEO_ON;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_DYNAMIC_FPS;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_STABILIZATION_MODE;
import static com.oplus.ocs.camera.CameraParameter.VideoFps.FPS_30;
import static com.oplus.ocs.camera.CameraParameter.VideoFps.FPS_60;
import static com.oplus.ocs.camera.CameraParameter.VideoFpsValue.VIDEO_FPS_30;
import static com.oplus.ocs.camera.CameraParameter.VideoFpsValue.VIDEO_FPS_60;
import static com.oplus.ocs.camerax.util.Constant.VideoStabilizationMode.VIDEO_STABILIZATION_OFF;

public class VideoAiNight extends BaseFeatureVideoAiNight {

    @Override
    public <T> void setConfigureParameter(Object configureBuilderHolder, T value) {
        CameraDeviceConfig.Builder builder = (CameraDeviceConfig.Builder) configureBuilderHolder;

        // update ai night value, when ai night is off, needn't set parameter
        if ((Boolean) value) {
            builder.setParameter(AI_NIGHT_VIDEO_MODE, 1);
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

        CameraDeviceInfo cameraDeviceInfo = CameraUnit.getCameraClient(appContext).getCameraDeviceInfo(cameraType, modeType);
        List<Integer> aiNightValues = cameraDeviceInfo.getConfigureParameterRange(AI_NIGHT_VIDEO_MODE);
        List<String> result = new ArrayList<>();

        for (Integer value : aiNightValues) {
            if (1 == value) {
                result.add("on");
            } else {
                result.add("off");
            }
        }

        return result;
    }

    @Override
    public void checkConflictFeature(Context appContext, ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        CameraDeviceInfo cameraDeviceInfo = CameraUnit.getCameraClient(appContext).getCameraDeviceInfo(cameraType, modeType);
        Map<String, List<String>> conflictMap = cameraDeviceInfo.getConflictParameter(AI_NIGHT_VIDEO_MODE.getKeyName(),
                configure.isVideoAiNightOn() ? String.valueOf(AI_NIGHT_VIDEO_ON) : String.valueOf(AI_NIGHT_VIDEO_OFF));

        if (null != conflictMap) {
            List<String> fpsLists = conflictMap.get(VIDEO_DYNAMIC_FPS.getKeyName());

            if (null != fpsLists) {
                if ((fpsLists.contains(VIDEO_FPS_60)) && (!fpsLists.contains(VIDEO_FPS_30))) {
                    configure.setVideoFps(new Range<>(FPS_30, FPS_30));
                } else if ((fpsLists.contains(VIDEO_FPS_30)) && (!fpsLists.contains(VIDEO_FPS_60))) {
                    configure.setVideoFps(new Range<>(FPS_60, FPS_60));
                }
            }

            List<String> stabilizationLists = conflictMap.get(VIDEO_STABILIZATION_MODE.getKeyName());

            if (null != stabilizationLists) {
                configure.setStabilizationMode(VIDEO_STABILIZATION_OFF);
            }
        }
    }
}