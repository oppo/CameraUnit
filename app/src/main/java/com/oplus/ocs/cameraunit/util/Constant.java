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
 * File: - Constant.java
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

package com.oplus.ocs.cameraunit.util;

import android.os.Environment;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Constant {
    public static final String DEBUG_SAVE_PATH = Environment.getExternalStorageDirectory() + "/DCIM/Camera/";
    public static final String PICTURE_PREFIX = ".jpg";

    private Constant() {
        // do nothing
    }

    public @interface PreviewRatio {
        double RATIO_VALUE_4_3 = (double) 4 / 3;
        double RATIO_VALUE_16_9 = (double) 16 / 9;
        double RATIO_VALUE_1_1 = (double) 1 / 1;
    }

    @IntDef({CameraSurfaceType.SURFACE_VIEW, CameraSurfaceType.SURFACE_TEXTURE, CameraSurfaceType.IMAGE_READER,
            CameraSurfaceType.RECORDING_SURFACE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CameraSurfaceType {
        int SURFACE_VIEW = 1;
        int SURFACE_TEXTURE = 2;
        int IMAGE_READER = 3;
        int RECORDING_SURFACE = 4;
    }

    @StringDef({PreviewViewType.SURFACE_VIEW, PreviewViewType.SURFACE_TEXTURE,
            PreviewViewType.TEXTURE_VIEW, PreviewViewType.IMAGE_READER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PreviewViewType {
        String SURFACE_VIEW = "surface_view";
        String SURFACE_TEXTURE = "surface_texture";
        String TEXTURE_VIEW = "texture_view";
        String IMAGE_READER = "image_reader";
    }

    @StringDef({VideoStabilizationMode.VIDEO_STABILIZATION, VideoStabilizationMode.SUPER_STABILIZATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoStabilizationMode {
        String VIDEO_STABILIZATION = "video_stabilization";
        String SUPER_STABILIZATION = "super_stabilization";
    }


    @IntDef({FeatureType.TOP_COMMON_SETTINGS, FeatureType.RIGHT_PANEL_SETTINGS, FeatureType.PREVIEW_OVERRIDE_SETTINGS,
            FeatureType.BOTTOM_PANEL_SETTINGS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FeatureType {
        int UNKNOWN = 0;
        int TOP_COMMON_SETTINGS = 1;
        int RIGHT_PANEL_SETTINGS = 2;
        int PREVIEW_OVERRIDE_SETTINGS = 3;
        int BOTTOM_PANEL_SETTINGS = 4;
    }

    @IntDef({BokehState.INVALID, BokehState.NO_DEPTH_EFFECT, BokehState.DEPTH_EFFECT_SUCCESS,
            BokehState.TOO_NEAR, BokehState.TOO_FAR, BokehState.LOW_LIGHT,
            BokehState.SUBJECT_NOT_FOUND, BokehState.TOUCH_TO_FOCUS, BokehState.CAMERA_COVERGED_SUB,
            BokehState.CAMERA_CALIBRATION, BokehState.CAMERA_SINGLE, BokehState.CAMERA_COVERGED_MAIN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BokehState {
        int INVALID = -1;
        int NO_DEPTH_EFFECT = 0;
        int DEPTH_EFFECT_SUCCESS = 1;
        int TOO_NEAR = 2;
        int TOO_FAR = 3;
        int LOW_LIGHT = 4;
        int SUBJECT_NOT_FOUND = 5;
        int TOUCH_TO_FOCUS = 6;
        int CAMERA_COVERGED_SUB = 7;
        int CAMERA_CALIBRATION = 8;
        int CAMERA_SINGLE = 9;
        int CAMERA_COVERGED_MAIN = 10;
    }

    @StringDef({PHOTO_RATIO_TYPE.RATIO_TYPE_4_3, PHOTO_RATIO_TYPE.RATIO_TYPE_1_1,
            PHOTO_RATIO_TYPE.RATIO_TYPE_16_9, PHOTO_RATIO_TYPE.RATIO_TYPE_FULL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PHOTO_RATIO_TYPE {
        String RATIO_TYPE_4_3 = "4:3";
        String RATIO_TYPE_1_1 = "1:1";
        String RATIO_TYPE_16_9 = "16:9";
        String RATIO_TYPE_FULL = "Full";
    }
}