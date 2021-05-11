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
 * File: - MainActivity.java
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

package com.oplus.ocs.cameraunit.component;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.ocs.cameraunit.R;
import com.ocs.cameraunit.databinding.ActivityMainBinding;
import com.oplus.ocs.base.FeatureBean;
import com.oplus.ocs.base.OnConfigChangeListener;
import com.oplus.ocs.cameraunit.ui.BlurProgressView;
import com.oplus.ocs.cameraunit.ui.CameraButtonView;
import com.oplus.ocs.cameraunit.util.CameraUtil;
import com.oplus.ocs.cameraunit.util.Constant;
import com.oplus.ocs.cameraunit.util.GestureUtil;
import com.oplus.ocs.cameraunit.util.ToastUtil;
import com.oplus.ocs.cameraunit.util.UiUtils;
import com.oplus.ocs.camerax.CameraController;
import com.oplus.ocs.camerax.CameraStatusCallback;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.adapter.BaseAdapter;
import com.oplus.ocs.camerax.component.preview.PreviewInterface;
import com.oplus.ocs.camerax.features.BaseFeatureFlash;
import com.oplus.ocs.camerax.features.FeatureFactory;
import com.oplus.ocs.camerax.util.Constant.CameraMode;
import com.oplus.ocs.camerax.util.Constant.CameraType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.github.clans.fab.FloatingActionButton.SIZE_MINI;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String VIEW_TYPE = "view_type";
    private static final int MSG_HIDE_SEEK_BAR_VIEW = 1000;
    private static final int MSG_SWITCH_CAMERA_MODE = 1001;
    private static final int HIDE_SEEK_BAR_VIEW_DELAY = 3000;
    private static final int SWITCH_CAMERA_MODE_DELAY = 600;

    // view binding
    private ActivityMainBinding mRootView = null;
    private CameraStatusActionImpl mCameraStatusActionImpl = null;
    private ConfigureBean mConfigureBean = null;
    private String mViewType;
    private View mPreviewView = null;
    private int mCurrentBokehState = -1;
    private MyOrientationEventListener mOrientationListener = null;
    private GestureUtil mGestureUtil;
    private SoundPool mSoundPool = null;
    private int mVideoSoundId = 0;
    private int mCaptureSoundId = 0;
    private Handler mMainThreadHandler;
    private List<FeatureBean> mTopPanelFeatureList;
    private List<FeatureBean> mRightPanelFeatureList;

    /**
     * 相机镜头切换监听器
     */
    private final View.OnClickListener mOnCameraTypeFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRootView.controller.fab.close(true);

            if (!(v instanceof FloatingActionButton)) {
                return;
            }

            FloatingActionButton currentType = (FloatingActionButton) v;
            String cameraType = (String) currentType.getTag();

            if (mConfigureBean.getCameraType().equals(cameraType)) {
                return;
            }

            Log.d(TAG, "onClick: OnCameraTypeFabClick: " + cameraType);

            mConfigureBean.setCameraTypeDefaultValue(cameraType);

            // disable all view when changing camera
            setAllViewClickable(false);
            CameraController.getInstance().onConfigureChange(mConfigureBean);
        }
    };

    /**
     * 相机模式切换监听器，设置了delay，防止在滑动过程中连续切换
     */
    private final TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int modeIndex = tab.getPosition();
            String selectedCameraMode = (String) tab.getTag();

            Log.d(TAG, "onTabSelected, tab position: " + modeIndex + ", selectedCameraMode: " + selectedCameraMode);

            mMainThreadHandler.removeMessages(MSG_SWITCH_CAMERA_MODE);
            Message message = Message.obtain();
            message.what = MSG_SWITCH_CAMERA_MODE;
            message.obj = selectedCameraMode;
            mMainThreadHandler.sendMessageDelayed(message, SWITCH_CAMERA_MODE_DELAY);

            setAllViewClickable(false);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            // do nothing
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            // do nothing
        }
    };

    /**
     * 功能开关状态改变监听器，监听顶部和右侧功能菜单的状态变化。
     */
    private final OnConfigChangeListener featureChangeListener = new OnConfigChangeListener() {
        @Override
        public void onConfigChange(int featureId, String configValue) {
            Log.d(TAG, "onConfigChange: featureId: " + featureId + ", configValue: " + configValue);

            String featureConflictString = UiUtils.getFeatureConflictString(MainActivity.this, mConfigureBean,
                    featureId, configValue);

            if (!TextUtils.isEmpty(featureConflictString)) {
                ToastUtil.showToast(MainActivity.this, featureConflictString);

                return;
            }

            // 部分功能的改变需要重新configure, 此处针对这些feature进行判断。
            if (FeatureFactory.isNeedReConfigureSession(featureId)) {
                mConfigureBean.setNeedConfigureSession(true);
            }

            if (FeatureFactory.FeatureIds.COMMON_FEATURE_FLASH == featureId) {
                mConfigureBean.setFlashMode(configValue);
            } else if (FeatureFactory.FeatureIds.PHOTO_FEATURE_RATIO == featureId) {
                mConfigureBean.setPhotoRatio(UiUtils.translateDisplayToRatio(configValue));
            } else if (FeatureFactory.FeatureIds.PHOTO_FEATURE_CAPTURE_HDR == featureId) {
                mConfigureBean.setCaptureHdrMode(configValue);
            } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_RESOLUTION == featureId) {
                mConfigureBean.setVideoResolution(configValue);
            } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_FPS == featureId) {
                mConfigureBean.setVideoFps(UiUtils.stringToRange(configValue));
            } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_HDR == featureId) {
                mConfigureBean.setVideoHdrMode(configValue);
            } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION == featureId) {
                mConfigureBean.setStabilizationMode(configValue);
            } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT == featureId) {
                mConfigureBean.setVideoAiNightOn(UiUtils.translateDisplayToBoolean(configValue));
            }

            // 检查功能互斥
            FeatureFactory.getInstance().getFeature(featureId).checkConflictFeature(MainActivity.this, mConfigureBean);

            // 当配置发生变化时，需要更新所有feature列表，因为存在功能互斥的情况。
            updateFeature();
            CameraController.getInstance().onConfigureChange(mConfigureBean);
        }

        @Override
        public void onConfigViewShow(int featureId) {
            if (FeatureFactory.FeatureIds.PHOTO_FEATURE_PORTRAIT_BLUR == featureId) {
                mRootView.controller.blurProgressView.setProgress(mConfigureBean.getBlurIndex());
                mRootView.controller.blurProgressView.setVisibility(View.VISIBLE);
                mMainThreadHandler.removeMessages(MSG_HIDE_SEEK_BAR_VIEW);
                mMainThreadHandler.sendEmptyMessageDelayed(MSG_HIDE_SEEK_BAR_VIEW, HIDE_SEEK_BAR_VIEW_DELAY);
            } else if (FeatureFactory.FeatureIds.COMMON_SETTING == featureId) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        }
    };

    /**
     * 预览界面各种滑动手势监听器
     */
    private final GestureUtil.GestureConfigListener gestureConfigListener = new GestureUtil.GestureConfigListener() {
        @Override
        public void autoFocus(RectF rectF) {
            mConfigureBean.setAutoFocusRectF(rectF);
            CameraController.getInstance().onConfigureChange(mConfigureBean);
        }

        @Override
        public void cancelAutoFocus() {
            mConfigureBean.setAutoFocusRectF(null);
            CameraController.getInstance().onConfigureChange(mConfigureBean);
        }

        @Override
        public void exposureCompensation(float value) {
            mConfigureBean.setExposureProgress(value);
            CameraController.getInstance().onConfigureChange(mConfigureBean);
        }

        @Override
        public void zoomChange(float value) {
            mConfigureBean.setZoomLevel(value);
            CameraController.getInstance().onConfigureChange(mConfigureBean);
        }

        @Override
        public void switchToNextRight() {
            int selectedTabPosition = mRootView.controller.modeTab.getSelectedTabPosition();
            int tabCount = mRootView.controller.modeTab.getTabCount();

            if (selectedTabPosition < tabCount - 1) {
                mRootView.controller.modeTab.selectTab(mRootView.controller.modeTab.getTabAt(selectedTabPosition + 1));
            }
        }

        @Override
        public void switchToNextLeft() {
            int selectedTabPosition = mRootView.controller.modeTab.getSelectedTabPosition();

            if (selectedTabPosition > 0) {
                mRootView.controller.modeTab.selectTab(mRootView.controller.modeTab.getTabAt(selectedTabPosition - 1));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiUtils.updateActivityWindow(MainActivity.this);
        UiUtils.initScreenHeightWidth(this);
        mMainThreadHandler = new MainHandler(getMainLooper());

        mRootView = ActivityMainBinding.inflate(getLayoutInflater());

        // 根据 short cuts 传回的 intent type 选择合适的预览方式
        mViewType = getIntent().getStringExtra(VIEW_TYPE);
        mPreviewView = UiUtils.getPreviewSurfaceView(MainActivity.this, mViewType);
        mRootView.previewRoot.addView(mPreviewView);

        setContentView(mRootView.getRoot());

        mSoundPool = new SoundPool(1, AudioManager.STREAM_RING, 100);
        mVideoSoundId = mSoundPool.load(this, R.raw.camera_video, 1);
        mCaptureSoundId = mSoundPool.load(this, R.raw.camera_shutter, 1);

        initListener();

        mTopPanelFeatureList = new ArrayList<>();
        mRightPanelFeatureList = new ArrayList<>();

        // 预览界面触摸手势监听
        mGestureUtil = new GestureUtil(this);
        mGestureUtil.setGestureConfigListener(gestureConfigListener);

        // 注册相机状态的监听器，用于监听相机状态的改变
        mCameraStatusActionImpl = new CameraStatusActionImpl();
        CameraController.getInstance().setCameraStatusCallback(mCameraStatusActionImpl);

        // 使能方向监听
        mOrientationListener = new MyOrientationEventListener(MainActivity.this);
        mOrientationListener.enable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: start");

        // 1. 权限检查，如果通过，则会调用到 init()
        MainActivityPermissionsDispatcher.initWithPermissionCheck(MainActivity.this);
        // 2. call controller resume
        CameraController.getInstance().resume();
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void init() {
        Log.d(TAG, "init: start");

        if (null == mConfigureBean) {
            mConfigureBean = new ConfigureBean();
        }

        mConfigureBean.setPreviewHandle((PreviewInterface) mPreviewView);
        mConfigureBean.setPreviewSurfaceType(((PreviewInterface) mPreviewView).getSurfaceType());
        CameraController.getInstance().init(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: start");

        CameraController.getInstance().pause(mConfigureBean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Optional.ofNullable(mOrientationListener).ifPresent(OrientationEventListener::disable);
        CameraController.getInstance().setCameraStatusCallback(null);
        CameraController.getInstance().deInit();
        mCameraStatusActionImpl = null;
        mOrientationListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void showRationaleForPermission(PermissionRequest request) {
        showRationaleDialog(R.string.permission_granded_tips, request);
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onPermissionDenied() {
        Toast.makeText(this, R.string.permission_some_been_refused, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain({Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onPermissionNeverAskAgain() {
        Toast.makeText(this, R.string.permission_granded_tips, Toast.LENGTH_SHORT).show();

        finish();
    }

    private void showRationaleDialog(@StringRes int messageResId, PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(messageResId)
                .setPositiveButton(R.string.permission_proceed, (dialog, which) -> {
                    request.proceed();
                })
                .setNegativeButton(R.string.permission_refuse, (dialog, which) -> {
                    request.cancel();
                })
                .setCancelable(false)
                .show();
    }

    private void updateRecordingViewState(boolean isRecording) {
        runOnUiThread(() -> {
            mRootView.controller.topFeatureView.setVisibility(isRecording ? View.GONE : View.VISIBLE);
            mRootView.controller.rightFeatureView.setVisibility(isRecording ? View.GONE : View.VISIBLE);
            mRootView.controller.thumbnail.setVisibility(isRecording ? View.GONE : View.VISIBLE);
            mRootView.controller.fab.setVisibility(isRecording ? View.GONE : View.VISIBLE);
            mRootView.controller.modeTab.setVisibility(isRecording ? View.GONE : View.VISIBLE);
            mRootView.controller.videoControl.setVisibility(isRecording ? View.VISIBLE : View.GONE);
            mGestureUtil.setFlingAble(!isRecording);

            if (isRecording) {
                mRootView.controller.videoTimeView.startTimer();
            } else {
                mRootView.controller.videoTimeView.stopTimer(false);
                mRootView.controller.cameraButton.resetVideoModeView();
            }
        });
    }

    private void updateTakePictureViewState(boolean isTakingPicture) {
        runOnUiThread(() -> {
            updateCameraModeSwitchState(!isTakingPicture);
            setAllViewClickable(!isTakingPicture);
            mRootView.controller.cameraButton.updateViewTakingPicture(isTakingPicture);
        });
    }

    public void updateCameraModeSwitchState(boolean canSwitch) {
        LinearLayout tabStrip = (LinearLayout) mRootView.controller.modeTab.getChildAt(0);

        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(canSwitch);
        }

        mGestureUtil.setFlingAble(canSwitch);
    }

    private void updateCameraTypeFab(String modeType) {
        mRootView.controller.fab.removeAllMenuButtons();

        List<String> cameraList = CameraController.getInstance().getSupportCameraType(modeType);

        Log.d(TAG, "updateFab: allSupportCameraList is: " + cameraList);

        if (!cameraList.contains(mConfigureBean.getCameraType())) {
            if (CameraMode.VIDEO_MODE.equals(mConfigureBean.getCameraModeType())
                    || CameraMode.PHOTO_MODE.equals(mConfigureBean.getCameraModeType())) {
                for (String cameraType : cameraList) {
                    if (CameraType.REAR_MAIN_CAMERA.equals(cameraType)) {
                        mConfigureBean.setCameraTypeDefaultValue(cameraType);
                        break;
                    }
                }
            } else {
                mConfigureBean.setCameraTypeDefaultValue(cameraList.get(0));
            }
        }

        for (String cameraType : cameraList) {
            FloatingActionButton fab = new FloatingActionButton(getBaseContext());
            fab.setLabelText(UiUtils.getCameraNameWithCameraType(getBaseContext(), cameraType));
            fab.setButtonSize(SIZE_MINI);
            fab.setOnClickListener(mOnCameraTypeFabClickListener);
            fab.setTag(cameraType);

            if (mConfigureBean.getCameraType().equals(cameraType)) {
                fab.setSelected(true);
            }

            mRootView.controller.fab.addMenuButton(fab);
        }

        mConfigureBean.setCameraModeDefaultValue(mConfigureBean.getCameraModeType());
        CameraController.getInstance().onConfigureChange(mConfigureBean);
    }

    private void updateModeTab() {
        mRootView.controller.modeTab.removeAllTabs();
        // remove 掉监听器，防止 add tab 的时候，收到 onTabSelected 的回调
        mRootView.controller.modeTab.clearOnTabSelectedListeners();
        List<String> modeList = CameraController.getInstance().getSupportModeType();

        // ImageReaderPreview and TexturePreview are not supported in multi_camera_mode,
        // because there has not init two surfaces
        if ((Constant.PreviewViewType.IMAGE_READER.equals(mViewType))
                || (Constant.PreviewViewType.TEXTURE_VIEW.equals(mViewType))) {
            modeList.remove(CameraMode.MULTI_CAMERA_MODE);
        }

        Log.d(TAG, "onCameraReady: allSupportModeList is: " + modeList);

        for (String mode : modeList) {
            TabLayout.Tab item = mRootView.controller.modeTab.newTab();
            item.setText(UiUtils.getModeTabStringWithModeName(getBaseContext(), mode));
            item.setTag(mode);
            mRootView.controller.modeTab.addTab(item, false);
        }

        int modeIndex = modeList.indexOf(mConfigureBean.getCameraModeType());
        mRootView.controller.modeTab.selectTab(mRootView.controller.modeTab.getTabAt(modeIndex));
        mRootView.controller.modeTab.addOnTabSelectedListener(mOnTabSelectedListener);
        setCameraButtonMode(mConfigureBean.getCameraModeType());
        updateCameraTypeFab(mConfigureBean.getCameraModeType());
    }

    private void updateFeature() {
        mTopPanelFeatureList.clear();
        mRightPanelFeatureList.clear();
        String cameraMode = mConfigureBean.getCameraModeType();
        String cameraType = mConfigureBean.getCameraType();

        // reset some config
        boolean supportZoom = false;
        List<String> zoomRange = null;

        List<Integer> featureList = CameraController.getInstance().getSupportFeatures(cameraMode, cameraType);

        Log.d(TAG, "updateFeature: " + featureList);

        for (Integer featureId : featureList) {
            List<String> supportFeatureSubValues = FeatureFactory.getInstance().getFeature(featureId)
                    .getSupportFeatureSubValues(this, mConfigureBean);
            List<String> displayLists = FeatureFactory.getInstance().getFeature(featureId)
                    .getSupportFeatureDisplayValues(this, mConfigureBean);
            List<Integer> displayIconLists = FeatureFactory.getInstance().getFeature(featureId)
                    .getSupportFeatureDisplayIcons(this, mConfigureBean);

            FeatureBean featureBean = new FeatureBean(featureId);
            featureBean.setFeatureName(FeatureFactory.getInstance().getFeature(featureId).getFeatureName(this));
            featureBean.setFeatureIcon(FeatureFactory.getInstance().getFeature(featureId).getFeatureIconId());
            featureBean.setFeatureSubValues(supportFeatureSubValues);
            featureBean.setFeatureDisplayNameLists(displayLists);
            featureBean.setFeatureDisplayIconLists(displayIconLists);

            if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_STABILIZATION == featureId) {
                mConfigureBean.setStabilizationList(supportFeatureSubValues);
                featureBean.setSelectValue(mConfigureBean.getStabilizationMode());
            } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_AI_NIGHT == featureId) {
                featureBean.setSelectValue(UiUtils.translateBooleanToDisplay(mConfigureBean.isVideoAiNightOn()));
            } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_FPS == featureId) {
                featureBean.setSelectValue(mConfigureBean.getVideoFps().toString());
            } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_HDR == featureId) {
                featureBean.setSelectValue(mConfigureBean.getVideoHdrMode());
            } else if (FeatureFactory.FeatureIds.VIDEO_FEATURE_VIDEO_RESOLUTION == featureId) {
                if (supportFeatureSubValues.contains(mConfigureBean.getVideoResolution())) {
                    featureBean.setSelectValue(mConfigureBean.getVideoResolution());
                } else {
                    if (supportFeatureSubValues.size() > 0) {
                        String defaultValue = supportFeatureSubValues.get(0);
                        mConfigureBean.setVideoResolution(defaultValue);
                        featureBean.setSelectValue(defaultValue);
                    }
                }
            } else if (FeatureFactory.FeatureIds.COMMON_FEATURE_FLASH == featureId) {
                featureBean.setSelectValue(mConfigureBean.getFlashMode());
            } else if (FeatureFactory.FeatureIds.PHOTO_FEATURE_CAPTURE_HDR == featureId) {
                featureBean.setSelectValue(mConfigureBean.getCaptureHdrMode());
            } else if (FeatureFactory.FeatureIds.PHOTO_FEATURE_RATIO == featureId) {
                featureBean.setSelectValue(UiUtils.translateRatioToDisplay(mConfigureBean.getPreviewRatio()));
            }

            if (Constant.FeatureType.TOP_COMMON_SETTINGS == UiUtils.getFeatureType(featureId)) {
                mTopPanelFeatureList.add(featureBean);
            } else if (Constant.FeatureType.RIGHT_PANEL_SETTINGS == UiUtils.getFeatureType(featureId)) {
                mRightPanelFeatureList.add(featureBean);
            }

            if (FeatureFactory.FeatureIds.COMMON_FEATURE_ZOOM == featureId) {
                supportZoom = true;
                zoomRange = supportFeatureSubValues;
            }
        }

        mTopPanelFeatureList.add(UiUtils.getSettingFeature(this));

        mRootView.controller.topFeatureView.updateFeature(this, mTopPanelFeatureList, featureChangeListener);
        mRootView.controller.rightFeatureView.updateFeature(this, mRightPanelFeatureList, featureChangeListener);

        // 根据获取到的zoom范围更新zoom的值
        mGestureUtil.initZoomValue(mConfigureBean.getZoomLevel(), supportZoom, zoomRange);
    }

    private void updateThumbnail(Bitmap bitmap, String mediaPath) {
        if (null == mediaPath) {
            Log.e(TAG, "updateThumbnail: media path is null");

            return;
        }

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(mediaPath))));

        Log.d(TAG, "updateThumbnail: save to: " + mediaPath);

        runOnUiThread(() -> Glide.with(getBaseContext())
                .load(bitmap)
                .centerCrop()
                .dontAnimate() // 防止设置placeholder导致第一次不显示网络图片,只显示默认图片的问题
                .error(R.drawable.thumbnail)
                .into(mRootView.controller.thumbnail));
    }

    public void setAllViewClickable(boolean clickable) {
        mRootView.controller.cameraButton.setClickable(clickable);
        mRootView.controller.topFeatureView.setItemClickable(clickable);
        mRootView.controller.rightFeatureView.setItemClickable(clickable);
        mRootView.controller.thumbnail.setClickable(clickable);
        mGestureUtil.setClickable(clickable);
        mRootView.controller.fab.close(true);
        int childCount = mRootView.controller.fab.getChildCount();

        for (int i = 0; i < childCount; i++) {
            if (mRootView.controller.fab.getChildAt(i) instanceof FloatingActionButton) {
                mRootView.controller.fab.getChildAt(i).setClickable(clickable);
            }
        }
    }

    private void setCameraButtonMode(String cameraMode) {
        if (CameraMode.VIDEO_MODE.equals(cameraMode) || CameraMode.SLOW_VIDEO_MODE.equals(cameraMode)) {
            mRootView.controller.cameraButton.setMode(CameraButtonView.Mode.VIDEO);
        } else {
            mRootView.controller.cameraButton.setMode(CameraButtonView.Mode.PHOTO);
        }

        mRootView.controller.cameraButton.setEnabled(!CameraMode.MULTI_CAMERA_MODE.equals(cameraMode));
    }

    private void initListener() {
        mRootView.controller.fab.setClosedOnTouchOutside(true);

        mRootView.controller.cameraButton.setCameraButtonEventListener(new CameraButtonView.CameraButtonEventListener() {
            @Override
            public void takePicture() {
                updateTakePictureViewState(true);
                CameraController.getInstance().takePicture(mConfigureBean);
            }

            @Override
            public void startRecording() {
                CameraController.getInstance().startRecording(mConfigureBean);
            }

            @Override
            public void stopRecording() {
                CameraController.getInstance().stopRecording();
            }
        });

        mRootView.controller.videoControl.setOnClickListener(v -> {
            if (CameraController.getInstance().getVideoControl().isPaused()) {
                mRootView.controller.videoControl.setImageResource(R.drawable.pic_video_record_pause);
                CameraController.getInstance().resumeRecording();
            } else {
                mRootView.controller.videoControl.setImageResource(R.drawable.pic_video_record_resume);
                CameraController.getInstance().pauseRecording();
            }
        });

        mRootView.controller.thumbnail.setOnClickListener(v -> UiUtils.openGallery(MainActivity.this));

        mRootView.controller.blurProgressView.setSeekBarChangeListener(new BlurProgressView.SeekBarChangeListener() {
            @Override
            public void onProgressChange(int progress) {
                mConfigureBean.setBlurIndex(progress);
                CameraController.getInstance().onConfigureChange(mConfigureBean);
            }

            @Override
            public void onStartTrackingTouch() {
                mMainThreadHandler.removeMessages(MSG_HIDE_SEEK_BAR_VIEW);
            }

            @Override
            public void onStopTrackingTouch() {
                mMainThreadHandler.removeMessages(MSG_HIDE_SEEK_BAR_VIEW);
                mMainThreadHandler.sendEmptyMessageDelayed(MSG_HIDE_SEEK_BAR_VIEW, HIDE_SEEK_BAR_VIEW_DELAY);
            }
        });

        ((PreviewInterface) mPreviewView).setOnPreviewChangeListener((size, topBlank) -> {
            mGestureUtil.setPreviewSize(size, topBlank);
        });
    }

    // inner classes
    private class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (MSG_HIDE_SEEK_BAR_VIEW == msg.what) {
                mRootView.controller.blurProgressView.setVisibility(View.GONE);
            } else if (MSG_SWITCH_CAMERA_MODE == msg.what) {
                String cameraMode = (String) msg.obj;

                if (!cameraMode.equals(mConfigureBean.getCameraModeType())) {
                    updateCameraModeSwitchState(false);
                    mConfigureBean.setCameraMode(cameraMode);
                    setCameraButtonMode(cameraMode);
                    updateCameraTypeFab(cameraMode);
                } else {
                    setAllViewClickable(true);
                }
            }
        }
    }

    private class MyOrientationEventListener extends OrientationEventListener {
        public MyOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) {
                return;
            }

            if (null != mConfigureBean) {
                int configuredOrientation = mConfigureBean.getOrientation();
                int orientationCompensation = UiUtils.roundOrientation(orientation, configuredOrientation);

                if (configuredOrientation != orientationCompensation) {
                    Log.w(TAG, "onOrientationChanged, mOrientation: " + configuredOrientation
                            + " -> " + orientationCompensation);

                    mConfigureBean.setOrientation(orientationCompensation);
                }
            }
        }
    }

    private class CameraStatusActionImpl implements CameraStatusCallback {

        @Override
        public void onCameraReady() {
            Log.d(TAG, "onCameraReady: start");

            runOnUiThread(MainActivity.this::updateModeTab);
        }

        @Override
        public void onCameraOpened(@CameraType String cameraType) {
            Log.d(TAG, "onCameraOpened: " + cameraType);

            CameraController.getInstance().onConfigureChange(mConfigureBean);
            runOnUiThread(MainActivity.this::updateFeature);
        }

        @Override
        public void onCameraClosed() {
            Log.d(TAG, "onCameraClosed: start");

            if (mConfigureBean.isReopenCamera()) {
                mConfigureBean.setNeedReopenCamera(false);
                mConfigureBean.getPreviewHandle().destroyPreviewBuffer();
                CameraController.getInstance().onConfigureChange(mConfigureBean);
            }
        }

        @Override
        public void onCameraConfigured() {
            Log.d(TAG, "onCameraConfigured: start");

            CameraController.getInstance().onConfigureChange(mConfigureBean);
        }

        @Override
        public void onCameraError(@BaseAdapter.CameraStatusListener.ErrorCode int errorCode, String errorMsg) {
            Log.d(TAG, "onCameraError, errorCode: " + errorCode + ", errorMsg: " + errorMsg);

            String str = "Camera has some error! ErrorCode: " + errorCode + "; ErrorMessage: " + errorMsg;
            Snackbar.make(mRootView.controller.bottomActionBar, str , Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            runOnUiThread(() -> setAllViewClickable(true));
        }

        @Override
        public void onFlashModeSupportListChanged(List<String> list) {
            runOnUiThread(() -> {
                for (FeatureBean featureBean : mTopPanelFeatureList) {
                    if (FeatureFactory.FeatureIds.COMMON_FEATURE_FLASH == featureBean.getFeatureId()) {
                        BaseFeatureFlash flashFeature = (BaseFeatureFlash) FeatureFactory.getInstance()
                                .getFeature(featureBean.getFeatureId());
                        featureBean.setFeatureSubValues(list);
                        featureBean.setFeatureDisplayNameLists(flashFeature
                                .getFlashChangeDisplayValues(MainActivity.this, list));
                        featureBean.setFeatureDisplayIconLists(flashFeature.getFlashChangeDisplayIcons(list));

                        break;
                    }
                }
            });
        }

        @Override
        public void onFlashModeChanged(String flashMode) {
            runOnUiThread(() -> {
                for (FeatureBean featureBean : mTopPanelFeatureList) {
                    if (FeatureFactory.FeatureIds.COMMON_FEATURE_FLASH == featureBean.getFeatureId()) {
                        featureBean.setSelectValue(flashMode);

                        break;
                    }
                }

                mRootView.controller.topFeatureView.updateList(mTopPanelFeatureList);
            });
        }

        @Override
        public void onPreviewStart() {
            Log.d(TAG, "onPreviewStart: start");

            runOnUiThread(() -> {
                setAllViewClickable(true);
                updateCameraModeSwitchState(true);
            });
        }

        @Override
        public void onPreviewStop() {
            // do nothing
        }

        @Override
        public void onRecordingStart() {
            updateRecordingViewState(true);
            mSoundPool.play(mVideoSoundId, 1f, 1f, 1, 0, 1);
        }

        @Override
        public void onRecordingResume() {
            runOnUiThread(mRootView.controller.videoTimeView::startTimer);
        }

        @Override
        public void onRecordingPaused() {
            runOnUiThread(() -> mRootView.controller.videoTimeView.stopTimer(true));
        }

        @Override
        public void onRecordingStop() {
            updateRecordingViewState(false);
            mSoundPool.play(mVideoSoundId, 1f, 1f, 1, 0, 1);
        }

        @Override
        public void onRecordingFinish(Bitmap bitmap, String videoPath) {
            updateThumbnail(bitmap, videoPath);
        }

        @Override
        public void onShutterCallback(long timestamp) {
            mSoundPool.play(mCaptureSoundId, 1f, 1f, 1, 0, 1);
        }

        @Override
        public void onPictureTaken(byte[] picture, long timeStamp) {
            String fileName = "" + timeStamp + Constant.PICTURE_PREFIX;
            String path = CameraUtil.saveBytesToFile(picture, fileName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
            updateThumbnail(bitmap, path);
            updateTakePictureViewState(false);
        }

        @Override
        public void onPictureTakenFailed() {
            updateTakePictureViewState(false);
        }

        @Override
        public void onBokehStateChanged(int state) {
            if (mCurrentBokehState != state) {
                mCurrentBokehState = state;
                String tips = UiUtils.getBokehStateTips(MainActivity.this, mCurrentBokehState);

                if (null != tips) {
                    ToastUtil.showToast(MainActivity.this, tips);
                } else {
                    ToastUtil.hideToast();
                }
            }
        }
    }
}

