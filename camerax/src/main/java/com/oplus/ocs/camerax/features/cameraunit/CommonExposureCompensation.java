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
 * File: - CommonExposureCompensation.java
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
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.util.Range;

import com.oplus.ocs.camera.CameraDevice;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeatureExposureCompensation;

import java.util.List;

public class CommonExposureCompensation extends BaseFeatureExposureCompensation {

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

        Range<Integer> mAECompensationRange = cameraDevice.getCameraDeviceInfo(modeType, cameraType)
                .get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);

        if (null != mAECompensationRange) {
            int max = mAECompensationRange.getUpper();
            int min = mAECompensationRange.getLower();
            int mExposureValue = (int) (min + (max - min) * (Float) value);
            cameraDevice.setParameter(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, mExposureValue);
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
