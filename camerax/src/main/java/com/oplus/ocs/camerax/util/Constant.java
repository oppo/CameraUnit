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

package com.oplus.ocs.camerax.util;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Constant {

    private Constant() {
        // do nothing
    }

    public @interface PreviewRatio {
        double RATIO_VALUE_1_1 = (double) 1 / 1;
        double RATIO_VALUE_4_3 = (double) 4 / 3;
        double RATIO_VALUE_16_9 = (double) 16 / 9;
    }

    @StringDef({PreviewRatioType.RATIO_TYPE_4_3, PreviewRatioType.RATIO_TYPE_1_1,
            PreviewRatioType.RATIO_TYPE_16_9, PreviewRatioType.RATIO_TYPE_FULL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PreviewRatioType {
        String RATIO_TYPE_1_1 = "1:1";
        String RATIO_TYPE_4_3 = "4:3";
        String RATIO_TYPE_16_9 = "16:9";
        String RATIO_TYPE_FULL = "Full";
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

    @IntDef({Orientation.ORIENTATION_UNKNOWN, Orientation.ORIENTATION_0, Orientation.ORIENTATION_30,
            Orientation.ORIENTATION_45, Orientation.ORIENTATION_60, Orientation.ORIENTATION_90, Orientation.ORIENTATION_180,
            Orientation.ORIENTATION_270, Orientation.ORIENTATION_360})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
        int ORIENTATION_UNKNOWN = -1;
        int ORIENTATION_0 = 0;
        int ORIENTATION_30 = 30;
        int ORIENTATION_60 = 60;
        int ORIENTATION_45 = 45;
        int ORIENTATION_90 = 90;
        int ORIENTATION_180 = 180;
        int ORIENTATION_270 = 270;
        int ORIENTATION_360 = 360;
    }

    @StringDef({CameraMode.VIDEO_MODE, CameraMode.PHOTO_MODE, CameraMode.MULTI_CAMERA_MODE, CameraMode.NIGHT_PHOTO_MODE,
            CameraMode.PORTRAIT_PHOTO_MODE, CameraMode.SLOW_VIDEO_MODE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface CameraMode {
        String VIDEO_MODE = "video_mode";
        String PHOTO_MODE = "photo_mode";
        String PREVIEW_MODE = "preview_mode";
        String MULTI_CAMERA_MODE = "multi_camera_mode";
        String NIGHT_PHOTO_MODE = "night_mode";
        String PORTRAIT_PHOTO_MODE = "portrait_mode";
        String SLOW_VIDEO_MODE = "slowvideo_mode";
    }

    @StringDef({CameraType.REAR_MAIN_CAMERA, CameraType.FRONT_MAIN_CAMERA, CameraType.REAR_WIDE_CAMERA,
            CameraType.REAR_TELE_CAMERA, CameraType.REAR_PORTRAIT_CAMERA, CameraType.REAR_SAT_CAMERA,
            CameraType.REAR_PORTRAIT_MONO_1, CameraType.REAR_PORTRAIT_MONO_2, CameraType.REAR_MACRO_CAMERA,
            CameraType.REAR_MAIN_REAR_WIDE_CAMERA, CameraType.REAR_MAIN_REAR_TELE_CAMERA,
            CameraType.REAR_WIDE_REAR_TELE_CAMERA, CameraType.REAR_MAIN_FRONT_MAIN_CAMERA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CameraType {
        String REAR_MAIN_CAMERA = "rear_main";
        String FRONT_MAIN_CAMERA = "front_main";
        String REAR_WIDE_CAMERA = "rear_wide";
        String REAR_TELE_CAMERA = "rear_tele";
        String REAR_PORTRAIT_CAMERA = "rear_portrait";
        String REAR_PORTRAIT_MONO_1 = "rear_mono_1";
        String REAR_PORTRAIT_MONO_2 = "rear_mono_2";
        String REAR_SAT_CAMERA = "rear_sat";
        String REAR_MACRO_CAMERA = "rear_macro";
        String REAR_MAIN_REAR_WIDE_CAMERA = "rear_main_rear_wide";
        String REAR_MAIN_REAR_TELE_CAMERA = "rear_main_rear_tele";
        String REAR_WIDE_REAR_TELE_CAMERA = "rear_wide_rear_tele";
        String REAR_MAIN_FRONT_MAIN_CAMERA = "rear_main_front_main";
    }

    @StringDef({CommonStateValue.ON, CommonStateValue.OFF, CommonStateValue.AUTO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CommonStateValue {
        String ON = "on";
        String OFF = "off";
        String AUTO = "auto";
    }

    @StringDef({VideoFpsValue.VIDEO_FPS_30, VideoFpsValue.VIDEO_FPS_60, VideoFpsValue.VIDEO_FPS_120,
            VideoFpsValue.VIDEO_FPS_240, VideoFpsValue.VIDEO_FPS_480, VideoFpsValue.VIDEO_FPS_960})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoFpsValue {
        String VIDEO_FPS_30 = "video_30fps";
        String VIDEO_FPS_60 = "video_60fps";
        String VIDEO_FPS_120 = "video_120fps";
        String VIDEO_FPS_240 = "video_240fps";
        String VIDEO_FPS_480 = "video_480fps";
        String VIDEO_FPS_960 = "video_960fps";
    }

    @IntDef({VideoFps.FRAME_RATE_30, VideoFps.FRAME_RATE_60, VideoFps.FRAME_RATE_120,
            VideoFps.FRAME_RATE_240, VideoFps.FRAME_RATE_480, VideoFps.FRAME_RATE_960})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoFps {
        int FRAME_RATE_30 = 30;
        int FRAME_RATE_60 = 60;
        int FRAME_RATE_120 = 120;
        int FRAME_RATE_240 = 240;
        int FRAME_RATE_480 = 480;
        int FRAME_RATE_960 = 960;
    }

    @StringDef({VideoFpsRange.VIDEO_FPS_RANGE_30, VideoFpsRange.VIDEO_FPS_RANGE_60, VideoFpsRange.VIDEO_FPS_RANGE_120,
            VideoFpsRange.VIDEO_FPS_RANGE_240, VideoFpsRange.VIDEO_FPS_RANGE_480, VideoFpsRange.VIDEO_FPS_RANGE_960})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoFpsRange {
        String VIDEO_FPS_RANGE_30 = "[30, 30]";
        String VIDEO_FPS_RANGE_60 = "[60, 60]";
        String VIDEO_FPS_RANGE_120 = "[120, 120]";
        String VIDEO_FPS_RANGE_240 = "[240, 240]";
        String VIDEO_FPS_RANGE_480 = "[480, 480]";
        String VIDEO_FPS_RANGE_960 = "[960, 960]";
    }

    @StringDef({VideoStabilizationMode.VIDEO_STABILIZATION, VideoStabilizationMode.SUPER_STABILIZATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoStabilizationMode {
        String VIDEO_STABILIZATION_OFF = "off";
        String VIDEO_STABILIZATION = "video_stabilization";
        String SUPER_STABILIZATION = "super_stabilization";
    }

    @StringDef({VideoResolution.VIDEO_RESOLUTION_720P, VideoResolution.VIDEO_RESOLUTION_1080P,
            VideoResolution.VIDEO_RESOLUTION_2K, VideoResolution.VIDEO_RESOLUTION_4K, VideoResolution.VIDEO_RESOLUTION_8K})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoResolution {
        String VIDEO_RESOLUTION_720P = "720P";
        String VIDEO_RESOLUTION_1080P = "1080P";
        String VIDEO_RESOLUTION_2K = "2K";
        String VIDEO_RESOLUTION_4K = "4K";
        String VIDEO_RESOLUTION_8K = "8K";
    }

    @StringDef({DisplayResolution.DISPLAY_RESOLUTION_HIGH, DisplayResolution.DISPLAY_RESOLUTION_NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayResolution {
        String DISPLAY_RESOLUTION_HIGH = "1920x1080";
        String DISPLAY_RESOLUTION_NORMAL = "1280x720";
    }

    @StringDef({FlashMode.FLASH_AUTO, FlashMode.FLASH_OFF, FlashMode.FLASH_ON, FlashMode.FLASH_TORCH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FlashMode {
        String FLASH_AUTO = "auto";
        String FLASH_ON = "on";
        String FLASH_OFF = "off";
        String FLASH_TORCH = "torch";
    }

    @StringDef({HdrMode.HDR_ON, HdrMode.HDR_OFF, HdrMode.HDR_AUTO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HdrMode {
        String HDR_ON = "on";
        String HDR_OFF = "off";
        String HDR_AUTO = "auto";
    }

    @IntDef({MirrorType.MIRROR_TYPE_VERTICAL, MirrorType.MIRROR_TYPE_HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MirrorType {
        int MIRROR_TYPE_VERTICAL = 1;
        int MIRROR_TYPE_HORIZONTAL = 2;
    }

    @IntDef({RecordingState.RECORDING_START, RecordingState.RECORDING_PAUSE, RecordingState.RECORDING_RESUME,
            RecordingState.RECORDING_STOP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RecordingState {
        int RECORDING_START = 1;
        int RECORDING_PAUSE = 2;
        int RECORDING_RESUME = 3;
        int RECORDING_STOP = 4;
    }

    @StringDef({NightVideoMode.NIGHT_VIDEO_ON, NightVideoMode.NIGHT_VIDEO_OFF})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NightVideoMode {
        String NIGHT_VIDEO_ON = "on";
        String NIGHT_VIDEO_OFF = "off";
    }
}