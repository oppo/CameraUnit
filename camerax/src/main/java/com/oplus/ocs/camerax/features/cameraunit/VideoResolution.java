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
 * File: - VideoResolution.java
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
import android.util.Size;

import com.oplus.ocs.camera.CameraUnitClient;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeatureVideoResolution;
import com.oplus.ocs.camerax.util.CameraUtil;
import com.oplus.ocs.camerax.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.oplus.ocs.camera.CameraParameter.AI_NIGHT_VIDEO_MODE;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_3HDR_MODE;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_DYNAMIC_FPS;
import static com.oplus.ocs.camera.CameraParameter.VIDEO_STABILIZATION_MODE;
import static com.oplus.ocs.camera.CameraParameter.VideoStabilizationMode.SUPER_STABILIZATION;
import static com.oplus.ocs.camera.CameraParameter.VideoStabilizationMode.VIDEO_STABILIZATION;
import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.SLOW_VIDEO_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraMode.VIDEO_MODE;
import static com.oplus.ocs.camera.CameraUnitClient.CameraType.FRONT_MAIN;

public class VideoResolution extends BaseFeatureVideoResolution {
    @Override
    public <T> void setConfigureParameter(Object configureBuilderHolder, T value) {
        // do nothing in this feature
    }

    @Override
    public <T> void setPreviewParameter(Object cameraDeviceHolder, String modeType, String cameraType, T value) {
        // do nothing in this feature
    }

    @Override
    public List<String> getSupportFeatureSubValues(Context appContext, ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        double currentRatio = configure.getPreviewRatio();
        // 根据ui feature的配置，将对应的feature存入map，用于获取feature 组合对应的功能支持情况。
        Map<String, String> configFeatures = getConfiguredFeaturesByConfigure(configure);
        List<String> targetRatioLists = new ArrayList<>();

        if (VIDEO_MODE.equals(modeType) || SLOW_VIDEO_MODE.equals(modeType)) {
            List<Size> supportVideoSize = CameraUnitFeatureManager.getCameraDeviceInfo(appContext, modeType, cameraType)
                    .getSupportVideoSize(configFeatures);
            List<Size> copyOnWriteArrayList = new CopyOnWriteArrayList<>(supportVideoSize);
            List<Size> finalSize = CameraUtil.getSizeListByRatio(copyOnWriteArrayList, currentRatio);

            if (null != finalSize && finalSize.size() > 0) {
                Optional.ofNullable(finalSize.get(0)).ifPresent(size -> targetRatioLists.add(size.toString()));

                if (finalSize.size() > 1) {
                    Optional.ofNullable(finalSize.get(1)).ifPresent(size -> targetRatioLists.add(size.toString()));
                }
            }
        }

        return targetRatioLists;
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

    @Override
    public void checkConflictFeature(Context appContext, ConfigureBean configure) {
        // 通常情况下，720P 分辨率下，不支持超级防抖，超级防抖只在 1080P 下支持。
        if (Constant.DisplayResolution.DISPLAY_RESOLUTION_NORMAL.equals(configure.getVideoResolution())
                && SUPER_STABILIZATION.equals(configure.getStabilizationMode())) {
            configure.setStabilizationMode(VIDEO_STABILIZATION);
        }
    }
}