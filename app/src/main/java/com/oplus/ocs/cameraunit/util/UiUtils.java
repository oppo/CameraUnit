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
 * File: - UiUtils.java
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Range;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.ocs.cameraunit.R;
import com.oplus.ocs.base.FeatureBean;
import com.oplus.ocs.cameraunit.ui.previewsurfaces.view.ImageReaderPreview;
import com.oplus.ocs.cameraunit.ui.previewsurfaces.view.SurfacePreview;
import com.oplus.ocs.cameraunit.ui.previewsurfaces.view.SurfaceTexturePreview;
import com.oplus.ocs.cameraunit.ui.previewsurfaces.view.TexturePreview;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.FeatureFactory;
import com.oplus.ocs.camerax.util.Constant.CameraMode;
import com.oplus.ocs.camerax.util.Constant.CameraType;
import com.oplus.ocs.camerax.util.Constant.CommonStateValue;
import com.oplus.ocs.camerax.util.Constant.VideoFpsRange;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static com.oplus.ocs.cameraunit.util.Constant.PreviewViewType.IMAGE_READER;
import static com.oplus.ocs.cameraunit.util.Constant.PreviewViewType.SURFACE_TEXTURE;
import static com.oplus.ocs.cameraunit.util.Constant.PreviewViewType.SURFACE_VIEW;
import static com.oplus.ocs.cameraunit.util.Constant.PreviewViewType.TEXTURE_VIEW;
import static com.oplus.ocs.camerax.features.FeatureFactory.FeatureIds.COMMON_SETTING;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_30;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_360;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_60;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_90;

public class UiUtils {
    private static final String TAG = "UiUtils";
    private static final String GALLERY_REVIEW = "com.android.camera.action.REVIEW";
    private static final String COMMA_SPLIT = ",";

    private static int sScreenHeight = 0;
    private static int sScreenWidth = 0;
    private static final int ORIENTATION_HYSTERESIS = 5;

    private UiUtils() {
        // do nothing for protected utils tools
    }

    public static void updateActivityWindow(@NonNull Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    public static void updateSettingActivityWindow(@NonNull Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.white));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static void initScreenHeightWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point point = new Point();
        d.getRealSize(point);
        sScreenHeight = Math.max(point.x, point.y);
        sScreenWidth = Math.min(point.x, point.y);
    }

    public static String getVersionName(Context context) {
        String verName = "";

        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionName: get sdk version name failed!", e);
        }

        return verName;
    }


    public static double getFullPictureSizeRatio() {
        return (double) sScreenHeight / sScreenWidth;
    }

    public static String getModeTabStringWithModeName(Context activityContext, String modeName) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            switch (modeName) {
                case CameraMode.PHOTO_MODE:
                    return activityContext.getString(R.string.mode_normal_capture);
                case CameraMode.VIDEO_MODE:
                    return activityContext.getString(R.string.mode_normal_video);
                case CameraMode.MULTI_CAMERA_MODE:
                    return activityContext.getString(R.string.mode_multi_camera);
                case CameraMode.SLOW_VIDEO_MODE:
                    return activityContext.getString(R.string.mode_slowmotion_video);
                default:
                    return "";
            }
        } else {
            switch (modeName) {
                case CameraMode.NIGHT_PHOTO_MODE:
                    return activityContext.getString(R.string.mode_night_capture);
                case CameraMode.PHOTO_MODE:
                    return activityContext.getString(R.string.mode_normal_capture);
                case CameraMode.VIDEO_MODE:
                    return activityContext.getString(R.string.mode_normal_video);
                case CameraMode.PORTRAIT_PHOTO_MODE:
                    return activityContext.getString(R.string.mode_portrait_capture);
                case CameraMode.MULTI_CAMERA_MODE:
                    return activityContext.getString(R.string.mode_multi_camera);
                case CameraMode.SLOW_VIDEO_MODE:
                    return activityContext.getString(R.string.mode_slowmotion_video);
                default:
                    return "";
            }
        }
    }

    public static String getCameraNameWithCameraType(Context activityContext, String cameraName) {
        switch (cameraName) {
            case CameraType.FRONT_MAIN_CAMERA:
                return activityContext.getString(R.string.camera_name_front_main);
            case CameraType.REAR_MAIN_CAMERA:
                return activityContext.getString(R.string.camera_name_rear_main);
            case CameraType.REAR_MAIN_FRONT_MAIN_CAMERA:
                return activityContext.getString(R.string.camera_name_rear_main_front_main);
            case CameraType.REAR_MAIN_REAR_TELE_CAMERA:
                return activityContext.getString(R.string.camera_name_rear_main_rear_tele);
            case CameraType.REAR_MAIN_REAR_WIDE_CAMERA:
                return activityContext.getString(R.string.camera_name_rear_main_rear_wide);
            case CameraType.REAR_PORTRAIT_CAMERA:
                return activityContext.getString(R.string.camera_name_rear_portrait);
            case CameraType.REAR_SAT_CAMERA:
                return activityContext.getString(R.string.camera_name_rear_sat);
            case CameraType.REAR_TELE_CAMERA:
                return activityContext.getString(R.string.camera_name_rear_tele);
            case CameraType.REAR_WIDE_REAR_TELE_CAMERA:
                return activityContext.getString(R.string.camera_name_rear_wide_rear_tele);
            case CameraType.REAR_WIDE_CAMERA:
                return activityContext.getString(R.string.camera_name_rear_wide);
            default:
                return "";
        }
    }

    @Constant.FeatureType
    public static int getFeatureType(Integer featureId) {
        if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT == featureId
                || FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_SLOW_MOTION == featureId
                || FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION == featureId
                || FeatureFactory.FeatureIds.PHOTO_FEATURE_PORTRAIT_BLUR == featureId) {
            return Constant.FeatureType.RIGHT_PANEL_SETTINGS;
        } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_FPS == featureId
                || FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_HDR == featureId
                || FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_RESOLUTION == featureId
                || FeatureFactory.FeatureIds.COMMON_FEATURE_FLASH == featureId
                || FeatureFactory.FeatureIds.PHOTO_FEATURE_CAPTURE_HDR == featureId
                || FeatureFactory.FeatureIds.PHOTO_FEATURE_RATIO == featureId) {
            return Constant.FeatureType.TOP_COMMON_SETTINGS;
        } else {
            return Constant.FeatureType.UNKNOWN;
        }
    }

    public static int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation = false;
        int result = orientationHistory;

        if (orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist, ORIENTATION_360 - dist);
            changeOrientation = (dist >= ORIENTATION_60 + ORIENTATION_HYSTERESIS);
        }

        if (changeOrientation) {
            result = ((orientation + ORIENTATION_30) / ORIENTATION_90 * ORIENTATION_90) % ORIENTATION_360;
        }

        return result;
    }

    public static String getBokehStateTips(Activity activity, int state) {
        switch (state) {
            case Constant.BokehState.TOO_NEAR:
                return activity.getString(R.string.camera_bokeh_move_farther_away);
            case Constant.BokehState.TOO_FAR:
                return activity.getString(R.string.camera_bokeh_move_closer);
            case Constant.BokehState.LOW_LIGHT:
                return activity.getString(R.string.camera_bokeh_need_more_light);
            case Constant.BokehState.SUBJECT_NOT_FOUND:
                return activity.getString(R.string.camera_bokeh_place_subject_not_found);
            case Constant.BokehState.CAMERA_COVERGED_MAIN:
            case Constant.BokehState.CAMERA_COVERGED_SUB:
                return activity.getString(R.string.camera_bokeh_camera_occlusion);
            case Constant.BokehState.CAMERA_SINGLE:
                return activity.getString(R.string.camera_bokeh_single);
            default:
                return null;
        }
    }

    public static String translateRatioToDisplay(double ratio) {
        if (Constant.PreviewRatio.RATIO_VALUE_1_1 == ratio) {
            return Constant.PHOTO_RATIO_TYPE.RATIO_TYPE_1_1;
        } else if (Constant.PreviewRatio.RATIO_VALUE_4_3 == ratio) {
            return Constant.PHOTO_RATIO_TYPE.RATIO_TYPE_4_3;
        } else if (Constant.PreviewRatio.RATIO_VALUE_16_9 == ratio) {
            return Constant.PHOTO_RATIO_TYPE.RATIO_TYPE_16_9;
        } else {
            return Constant.PHOTO_RATIO_TYPE.RATIO_TYPE_FULL;
        }
    }

    public static double translateDisplayToRatio(String display) {
        if (Constant.PHOTO_RATIO_TYPE.RATIO_TYPE_1_1.equals(display)) {
            return Constant.PreviewRatio.RATIO_VALUE_1_1;
        } else if (Constant.PHOTO_RATIO_TYPE.RATIO_TYPE_4_3.equals(display)) {
            return Constant.PreviewRatio.RATIO_VALUE_4_3;
        } else if (Constant.PHOTO_RATIO_TYPE.RATIO_TYPE_16_9.equals(display)) {
            return Constant.PreviewRatio.RATIO_VALUE_16_9;
        } else {
            return getFullPictureSizeRatio();
        }
    }

    public static String translateBooleanToDisplay(boolean value) {
        if (value) {
            return "on";
        } else {
            return "off";
        }
    }

    public static boolean translateDisplayToBoolean(String value) {
        return CommonStateValue.ON.equals(value);
    }

    public static void openGallery(Activity activity) {
        Intent intent = new Intent(GALLERY_REVIEW);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
        activity.startActivity(intent);
    }

    public static View getPreviewSurfaceView(Context activityContext, String type) {
        View previewSurfaceView = null;

        if (IMAGE_READER.equals(type)) {
            previewSurfaceView = new ImageReaderPreview(activityContext);
        } else if (SURFACE_TEXTURE.equals(type)) {
            previewSurfaceView = new SurfaceTexturePreview(activityContext);
        } else if (TEXTURE_VIEW.equals(type)) {
            previewSurfaceView = new TexturePreview(activityContext);
        } else if (SURFACE_VIEW.equals(type)) {
            previewSurfaceView = new SurfacePreview(activityContext);
        } else {
            previewSurfaceView = new SurfaceTexturePreview(activityContext);
        }

        FrameLayout.LayoutParams vlp = new FrameLayout.LayoutParams(FILL_PARENT, FILL_PARENT);
        previewSurfaceView.setLayoutParams(vlp);
        previewSurfaceView.setId(R.id.preview);
        return previewSurfaceView;
    }

    public static Range<Integer> stringToRange(String fpsString) {
        String[] split = fpsString.split(COMMA_SPLIT);

        try {
            int lower = Integer.parseInt(split[0].substring(1));
            int upper = Integer.parseInt(split[1].trim().substring(0, split[1].trim().length() - 1));

            return new Range<>(lower, upper);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getFeatureConflictString(Context context, ConfigureBean configureBean, Integer featureId,
            String featureValue) {
        if (isVideoSATFpsConflict(configureBean, featureId, featureValue)) {
            return context.getString(R.string.video_60fps_ai_and_sat_exclusive);
        }

        if (isVideoSatStabConflict(configureBean, featureId, featureValue)) {
            return context.getString(R.string.video_sat_and_stablizition);
        }

        return "";
    }

    public static boolean isVideoSATFpsConflict(ConfigureBean configureBean, Integer featureId, String featureValue) {
        return FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_FPS == featureId
                && TextUtils.equals(CameraType.REAR_SAT_CAMERA, configureBean.getCameraType())
                && TextUtils.equals(CameraMode.VIDEO_MODE, configureBean.getCameraModeType())
                && TextUtils.equals(VideoFpsRange.VIDEO_FPS_RANGE_60, featureValue);
    }

    public static boolean isVideoSatStabConflict(ConfigureBean configureBean, Integer featureId, String featureValue) {
        return FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION == featureId
                && TextUtils.equals(CameraType.REAR_SAT_CAMERA, configureBean.getCameraType())
                && TextUtils.equals(CameraMode.VIDEO_MODE, configureBean.getCameraModeType())
                && TextUtils.equals(CommonStateValue.OFF, featureValue);
    }

    public static FeatureBean getSettingFeature(Context context) {
        FeatureBean featureBean = new FeatureBean(COMMON_SETTING);
        featureBean.setFeatureName(context.getString(R.string.config_name_setting));
        featureBean.setFeatureIcon(R.drawable.menu_camera_setting);
        featureBean.setFeatureSubValues(null);
        featureBean.setFeatureDisplayNameLists(null);
        featureBean.setFeatureDisplayIconLists(null);

        return featureBean;
    }
}