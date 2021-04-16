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
 * File: - PhotoPortraitBlur.java
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
import com.oplus.ocs.camera.CameraParameter;
import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeaturePhotoPortraitBlur;
import com.oplus.ocs.camerax.features.BaseFeaturePhotoRatio;

import java.math.BigDecimal;
import java.util.List;

public class PhotoPortraitBlur extends BaseFeaturePhotoPortraitBlur {

    private static final BigDecimal BLUR_VALUE_INDEX_TO_ALGORITHM = new BigDecimal("0.01");
    private static final float PROGRESS_SCALE = 100f;

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

        List<Integer> mBlurLevelRange = cameraDevice.getCameraDeviceInfo(modeType, cameraType)
                .getPreviewParameterRange(CameraParameter.KEY_BLUR_LEVEL_RANGE);

        if (null != mBlurLevelRange) {
            int max = mBlurLevelRange.get(mBlurLevelRange.size() - 1);
            int min = mBlurLevelRange.get(0);

            int mBlurIndex = (int) (min + (max - min) * (Integer) value / PROGRESS_SCALE);
            cameraDevice.setParameter(CameraParameter.KEY_BLUR_LEVEL, new float[]{getBlurValue(mBlurIndex)});
        }
    }

    @Override
    public List<String> getSupportFeatureSubValues(Context appContext, ConfigureBean configure) {
        return null;
    }

    private float getBlurValue(int mBlurIndex) {
        return new BigDecimal(mBlurIndex).multiply(BLUR_VALUE_INDEX_TO_ALGORITHM).floatValue();
    }

    @Override
    public void checkConflictFeature(Context appContext, ConfigureBean configure) {
        // do nothing in this feature
    }
}