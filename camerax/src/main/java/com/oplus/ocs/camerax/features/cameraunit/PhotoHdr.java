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
 * File: - PhotoHdr.java
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

import com.oplus.ocs.camera.CameraDevice;
import com.oplus.ocs.camera.CameraDeviceInfo;
import com.oplus.ocs.camera.CameraParameter;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeaturePhotoHdr;

import java.util.List;
import java.util.Map;

import static com.oplus.ocs.camera.CameraParameter.CAPTURE_HDR_MODE;
import static com.oplus.ocs.camera.CameraParameter.FLASH_MODE;

public class PhotoHdr extends BaseFeaturePhotoHdr {

    @Override
    public <T> void setConfigureParameter(Object configureBuilderHolder, T value) {
        // do nothing in this feature
    }

    @Override
    public <T> void setPreviewParameter(Object cameraDeviceHolder, String modeType, String cameraType, T value) {
        CameraDevice cameraDevice = (CameraDevice) cameraDeviceHolder;

        if (null == cameraDevice) {
            return;
        }

        cameraDevice.setParameter(CAPTURE_HDR_MODE, (String) value);
    }

    @Override
    public List<String> getSupportFeatureSubValues(Context appContext, ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        CameraDeviceInfo cameraDeviceInfo = CameraUnitFeatureManager.getCameraDeviceInfo(appContext, modeType, cameraType);

        return cameraDeviceInfo.getPreviewParameterRange(CAPTURE_HDR_MODE);
    }

    @Override
    public void checkConflictFeature(Context appContext, ConfigureBean configure) {
        String modeType = configure.getCameraModeType();
        String cameraType = configure.getCameraType();
        CameraDeviceInfo cameraDeviceInfo = CameraUnitFeatureManager.getCameraDeviceInfo(appContext, modeType, cameraType);
        Map<String, List<String>> conflictMap
                = cameraDeviceInfo.getConflictParameter(CAPTURE_HDR_MODE.getKeyName(), configure.getCaptureHdrMode());

        if (null != conflictMap) {
            List<String> flashModeList = conflictMap.get(FLASH_MODE.getKeyName());

            if (null != flashModeList && flashModeList.contains(configure.getFlashMode())) {
                configure.setFlashMode(CameraParameter.FlashMode.FLASH_OFF);
            }
        }
    }
}