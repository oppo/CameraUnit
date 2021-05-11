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
 * File: - Camera2ApiAdapter.java
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
package com.oplus.ocs.camerax.adapter.camera2;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;


import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.adapter.BaseAdapter;
import com.oplus.ocs.camerax.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class Camera2ApiAdapter extends BaseAdapter {
    public Camera2ApiAdapter(Context mAppContext) {
        super(mAppContext);
    }

    @Override
    public boolean isPlatformSupported() {
        return true;
    }

    @Override
    public void init() {
        List<String> modeTypeList = new ArrayList<>();
        modeTypeList.add(Constant.CameraMode.VIDEO_MODE);
        modeTypeList.add(Constant.CameraMode.PHOTO_MODE);
    }

    @Override
    public List<String> getSupportModeType() {
        return null;
    }

    @Override
    public List<String> getSupportCameraType(String cameraModeType) {
        return null;
    }

    @Override
    public List<Integer> getSupportFeatures(String cameraModeType, String cameraType) {
        return null;
    }

    @Override
    public void openCamera(@Constant.CameraMode String cameraModeType, String cameraType,
            @NonNull CameraStatusListener listener, Handler handler) {
        // implement camera api2 logic
    }

    @Override
    public void closeCamera(String cameraModeType, String cameraType, @NonNull CameraStatusListener listener, Handler handler) {
        // implement camera api2 logic
    }

    @Override
    public void configureSession(ConfigureBean configure, Handler mCameraHandler) {
        // implement camera api2 logic
    }

    @Override
    public void startPreview(ConfigureBean configure, PreviewCallback callback, Handler handler) {
        // implement camera api2 logic
    }

    @Override
    public void stopPreview() {
        // implement camera api2 logic
    }

    @Override
    public void startRecording(ConfigureBean configure, RecordingStatusCallback callback, Handler handler) {
        // implement camera api2 logic
    }

    @Override
    public void resumeRecording() {
        // implement camera api2 logic
    }

    @Override
    public void pauseRecording() {
        // implement camera api2 logic
    }

    @Override
    public void stopRecording(RecordingStatusCallback callback) {
        // implement camera api2 logic
    }

    @Override
    public void takePicture(ConfigureBean configure, PictureCallback callback, Handler pictureCallback) {
        // implement camera api2 logic
    }
}