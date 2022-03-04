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
 * File: - FeatureFactory.java
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

package com.oplus.ocs.camerax.features;

import android.content.Context;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.oplus.ocs.camerax.features.cameraunit.CameraUnitFeatureManager;
import com.oplus.ocs.camerax.features.cameraunit.CommonAeAf;
import com.oplus.ocs.camerax.features.cameraunit.CommonExposureCompensation;
import com.oplus.ocs.camerax.features.cameraunit.CommonFlash;
import com.oplus.ocs.camerax.features.cameraunit.CommonZoom;
import com.oplus.ocs.camerax.features.cameraunit.PhotoHdr;
import com.oplus.ocs.camerax.features.cameraunit.PhotoPortraitBlur;
import com.oplus.ocs.camerax.features.cameraunit.PhotoRatio;
import com.oplus.ocs.camerax.features.cameraunit.VideoAiNight;
import com.oplus.ocs.camerax.features.cameraunit.VideoFps;
import com.oplus.ocs.camerax.features.cameraunit.VideoHdr;
import com.oplus.ocs.camerax.features.cameraunit.VideoResolution;
import com.oplus.ocs.camerax.features.cameraunit.VideoStabilization;
import com.oplus.ocs.camerax.util.Constant;
import com.oplus.ocs.camerax.util.RomUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureFactory {
    private final Map<Integer, IFeature> mFeatureMap = new HashMap<>(10);

    private FeatureFactory() {
    }

    @IntDef({FeatureIds.COMMON_SETTING, FeatureIds.VIDEO_FEATURE_NORMAL, FeatureIds.VIDEO_FEATURE_VIDEO_SLOW_MOTION,
            FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT, FeatureIds.VIDEO_FEATURE_VIDEO_HDR,
            FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION, FeatureIds.VIDEO_FEATURE_VIDEO_FPS,
            FeatureIds.VIDEO_FEATURE_VIDEO_RESOLUTION, FeatureIds.COMMON_FEATURE_FLASH,
            FeatureIds.COMMON_FEATURE_ZOOM, FeatureIds.COMMON_FEATURE_AE_AF,
            FeatureIds.COMMON_FEATURE_EXPOSURE_COMPENSATION, FeatureIds.PHOTO_FEATURE_CAPTURE_HDR,
            FeatureIds.PHOTO_FEATURE_RATIO, FeatureIds.PHOTO_FEATURE_PORTRAIT_BLUR
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FeatureIds {
        int COMMON_SETTING = -1;
        int VIDEO_FEATURE_NORMAL = 0;
        int VIDEO_FEATURE_VIDEO_SLOW_MOTION = 1;
        int VIDEO_FEATURE_VIDEO_AI_NIGHT = 2;
        int VIDEO_FEATURE_VIDEO_STABILIZATION = 3;
        int VIDEO_FEATURE_VIDEO_HDR = 4;
        int VIDEO_FEATURE_VIDEO_FPS = 5;
        int VIDEO_FEATURE_VIDEO_RESOLUTION = 6;
        int COMMON_FEATURE_FLASH = 7;
        int COMMON_FEATURE_ZOOM = 8;
        int COMMON_FEATURE_AE_AF = 9;
        int COMMON_FEATURE_EXPOSURE_COMPENSATION = 10;
        int PHOTO_FEATURE_CAPTURE_HDR = 11;
        int PHOTO_FEATURE_RATIO = 12;
        int PHOTO_FEATURE_PORTRAIT_BLUR = 13;
    }

    private static class SingletonHolder {
        private static final FeatureFactory sInstance = new FeatureFactory();
    }

    public static FeatureFactory getInstance() {
        return SingletonHolder.sInstance;
    }

    public List<Integer> getSupportFeatureList(Context appContext, String cameraModeType, String cameraType) {
        List<Integer> featureList = new ArrayList<>();

        if (RomUtils.isOppo()) {
            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.COMMON_FEATURE_AE_AF)) {
                featureList.add(FeatureIds.COMMON_FEATURE_AE_AF);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.COMMON_FEATURE_EXPOSURE_COMPENSATION)) {
                featureList.add(FeatureIds.COMMON_FEATURE_EXPOSURE_COMPENSATION);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.COMMON_FEATURE_FLASH)) {
                featureList.add(FeatureIds.COMMON_FEATURE_FLASH);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT)) {
                featureList.add(FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION)) {
                featureList.add(FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.VIDEO_FEATURE_VIDEO_HDR)) {
                featureList.add(FeatureIds.VIDEO_FEATURE_VIDEO_HDR);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.VIDEO_FEATURE_VIDEO_FPS)) {
                featureList.add(FeatureIds.VIDEO_FEATURE_VIDEO_FPS);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.PHOTO_FEATURE_CAPTURE_HDR)) {
                featureList.add(FeatureIds.PHOTO_FEATURE_CAPTURE_HDR);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.PHOTO_FEATURE_PORTRAIT_BLUR)) {
                featureList.add(FeatureIds.PHOTO_FEATURE_PORTRAIT_BLUR);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.PHOTO_FEATURE_RATIO)) {
                featureList.add(FeatureIds.PHOTO_FEATURE_RATIO);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.VIDEO_FEATURE_VIDEO_RESOLUTION)) {
                featureList.add(FeatureIds.VIDEO_FEATURE_VIDEO_RESOLUTION);
            }

            if (CameraUnitFeatureManager.isFeatureSupport(appContext, cameraModeType, cameraType,
                    FeatureIds.COMMON_FEATURE_ZOOM)) {
                featureList.add(FeatureIds.COMMON_FEATURE_ZOOM);
            }
        }

        return featureList;
    }

    public static boolean isNeedReConfigureSession(int featureId) {
        if (RomUtils.isOppo()) {
            return CameraUnitFeatureManager.isNeedReConfigureSession(featureId);
        }

        return false;
    }

    public IFeature getFeature(@FeatureIds int featureId) {
        if (mFeatureMap.containsKey(featureId)) {
            return mFeatureMap.get(featureId);
        }

        if (RomUtils.isOppo()) {
            if (FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT == featureId) {
                IFeature aiNight = new VideoAiNight();
                mFeatureMap.put(featureId, aiNight);
                return aiNight;
            } else if (FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION == featureId) {
                IFeature stabilization = new VideoStabilization();
                mFeatureMap.put(featureId, stabilization);
                return stabilization;
            } else if (FeatureIds.VIDEO_FEATURE_VIDEO_HDR == featureId) {
                IFeature videoHdr = new VideoHdr();
                mFeatureMap.put(featureId, videoHdr);
                return videoHdr;
            } else if (FeatureIds.VIDEO_FEATURE_VIDEO_FPS == featureId) {
                IFeature videoFps = new VideoFps();
                mFeatureMap.put(featureId, videoFps);
                return videoFps;
            } else if (FeatureIds.COMMON_FEATURE_FLASH == featureId) {
                IFeature flashMode = new CommonFlash();
                mFeatureMap.put(featureId, flashMode);
                return flashMode;
            } else if (FeatureIds.COMMON_FEATURE_ZOOM == featureId) {
                IFeature commonZoom = new CommonZoom();
                mFeatureMap.put(featureId, commonZoom);
                return commonZoom;
            } else if (FeatureIds.COMMON_FEATURE_AE_AF == featureId) {
                IFeature commonAeAfEv = new CommonAeAf();
                mFeatureMap.put(featureId, commonAeAfEv);
                return commonAeAfEv;
            } else if (FeatureIds.COMMON_FEATURE_EXPOSURE_COMPENSATION == featureId) {
                IFeature commonAeEv = new CommonExposureCompensation();
                mFeatureMap.put(featureId, commonAeEv);
                return commonAeEv;
            } else if (FeatureIds.PHOTO_FEATURE_CAPTURE_HDR == featureId) {
                IFeature photoHdr = new PhotoHdr();
                mFeatureMap.put(featureId, photoHdr);
                return photoHdr;
            } else if (FeatureIds.PHOTO_FEATURE_RATIO == featureId) {
                IFeature photoRatio = new PhotoRatio();
                mFeatureMap.put(featureId, photoRatio);
                return photoRatio;
            } else if (FeatureIds.VIDEO_FEATURE_VIDEO_RESOLUTION == featureId) {
                IFeature videoResolution = new VideoResolution();
                mFeatureMap.put(featureId, videoResolution);
                return videoResolution;
            } else if (FeatureIds.PHOTO_FEATURE_PORTRAIT_BLUR == featureId) {
                IFeature photoPortraitBlur = new PhotoPortraitBlur();
                mFeatureMap.put(featureId, photoPortraitBlur);
                return photoPortraitBlur;
            }
        }

        throw new RuntimeException("not found feature by feature id: " + featureId);
    }
}