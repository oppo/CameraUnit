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
 * File: - CameraStatusCallback.java
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

package com.oplus.ocs.camerax;

import android.graphics.Bitmap;

import androidx.annotation.IntDef;

import com.oplus.ocs.camerax.adapter.BaseAdapter;
import com.oplus.ocs.camerax.util.Constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface CameraStatusCallback {
    void onCameraReady();
    void onCameraOpened(@Constant.CameraType String cameraType);
    void onCameraClosed();
    void onCameraConfigured();
    void onCameraError(@BaseAdapter.CameraStatusListener.ErrorCode int errorCode, String errorMsg);
    void onFlashModeSupportListChanged(List<String> list);
    void onFlashModeChanged(String flashMode);
    void onPreviewStart();
    void onPreviewStop();
    void onRecordingStart();
    void onRecordingResume();
    void onRecordingPaused();
    void onRecordingStop();
    void onRecordingFinish(Bitmap bitmap, String videoPath);
    void onShutterCallback(long timestamp);
    void onPictureTaken(byte[] picture, long timeStamp);
    void onPictureTakenFailed();
    void onBokehStateChanged(int state);
}
