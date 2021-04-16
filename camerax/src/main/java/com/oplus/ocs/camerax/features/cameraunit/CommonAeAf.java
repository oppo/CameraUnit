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
 * File: - CommonAeAf.java
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
import android.graphics.RectF;
import android.hardware.camera2.CaptureRequest;

import com.oplus.ocs.camera.CameraDevice;
import com.oplus.ocs.camera.CameraParameter;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeatureAeAf;

import java.util.List;

public class CommonAeAf extends BaseFeatureAeAf {

    @Override
    public <T> void setConfigureParameter(Object configureBuilderHolder, T value) {
        // do nothing in aeaf feature
    }

    @Override
    public <T> void setPreviewParameter(Object cameraDeviceHolder, String modeType, String cameraType, T value) {
        CameraDevice cameraDevice = (CameraDevice) cameraDeviceHolder;

        if (null == cameraDevice) {
            return;
        }

        List<Integer> focusModeList = cameraDevice.getCameraDeviceInfo(modeType, cameraType)
                .getPreviewParameterRange(CameraParameter.FOCUS_MODE);

        if (null != value) {
            if ((null != focusModeList) && focusModeList.contains(CameraParameter.FocusMode.AF_MODE_AUTO)) {
                cameraDevice.setParameter(CameraParameter.FOCUS_MODE, CameraParameter.FocusMode.AF_MODE_AUTO);
                cameraDevice.setParameter(CameraParameter.AF_REGIONS, (RectF) value);
                cameraDevice.setParameter(CameraParameter.AE_REGIONS, (RectF) value);
            } else {
                cameraDevice.setParameter(CameraParameter.AE_REGIONS, (RectF) value);
                cameraDevice.setParameter(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
                cameraDevice.setParameter(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            }
        } else {
            if (null != focusModeList) {
                if (focusModeList.contains(CameraParameter.FocusMode.AF_MODE_CONTINUOUS_VIDEO)) {
                    cameraDevice.setParameter(CameraParameter.FOCUS_MODE, CameraParameter.FocusMode.AF_MODE_CONTINUOUS_VIDEO);
                }

                if (focusModeList.contains(CameraParameter.FocusMode.AF_MODE_CONTINUOUS_PICTURE)) {
                    cameraDevice.setParameter(CameraParameter.FOCUS_MODE, CameraParameter.FocusMode.AF_MODE_CONTINUOUS_PICTURE);
                }
            }
        }
    }

    @Override
    public List<String> getSupportFeatureSubValues(Context appContext, ConfigureBean configureBean) {
        return null;
    }

    @Override
    public void checkConflictFeature(Context appContext, ConfigureBean configure) {
        // do nothing in this feature
    }
}