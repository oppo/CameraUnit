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
 * File: - CommonZoom.java
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
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeatureZoom;
import com.oplus.ocs.camerax.util.Constant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.oplus.ocs.camera.CameraParameter.ZOOM_RATIO;

public class CommonZoom extends BaseFeatureZoom {
    private static final String FORMAT_FLOAT = "#.#";
    private final DecimalFormat mFloatDecimalFormat = new DecimalFormat(FORMAT_FLOAT);

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

        cameraDevice.setParameter(ZOOM_RATIO, (Float) value);
    }

    @Override
    public List<String> getSupportFeatureSubValues(Context appContext, ConfigureBean configureBean) {
        String modeType = configureBean.getCameraModeType();
        String cameraType = configureBean.getCameraType();
        CameraDeviceInfo cameraDeviceInfo = CameraUnitFeatureManager.getCameraDeviceInfo(appContext, modeType, cameraType);
        List<Float> zoomPoints = cameraDeviceInfo.getPreviewParameterRange(ZOOM_RATIO);

        Float[] zoomValues = new Float[]{1.0f};

        if (zoomPoints.get(zoomPoints.size() - 1) <= 4f) {
            zoomValues = new Float[]{1.0f, 2.0f, 4.0f};
        } else if (zoomPoints.get(zoomPoints.size() - 1) <= 10f) {
            zoomValues = new Float[]{1.0f, 2.0f, 5.0f, 10.0f};
        } else if (zoomPoints.get(zoomPoints.size() - 1) <= 20f) {
            zoomValues = new Float[]{1.0f, 5.0f, 10.0f, 20.0f};
        } else if (zoomPoints.get(zoomPoints.size() - 1) <= 60f) {
            zoomValues = new Float[]{1.0f, 5.0f, 10.0f, 20.0f, 60.0f};
        }

        List<Float> zoomFloats = Arrays.asList(zoomValues);
        zoomFloats = new ArrayList<>(zoomFloats);

        if (Constant.CameraType.REAR_SAT_CAMERA.equals(cameraType)) {
            zoomFloats.add(0, 0.6f);
        }

        List<String> result = new ArrayList<>();

        if (null != zoomFloats) {
            for (Float ratio : zoomFloats) {
                result.add(mFloatDecimalFormat.format(ratio));
            }
        }

        return result;
    }

    @Override
    public void checkConflictFeature(Context appContext, ConfigureBean configure) {
        // do nothing in this feature
    }
}