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
 * File: - PhotoRatio.java
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

import com.oplus.ocs.camerax.ConfigureBean;
import com.oplus.ocs.camerax.features.BaseFeaturePhotoRatio;

import java.util.ArrayList;
import java.util.List;

import static com.oplus.ocs.camerax.util.Constant.PreviewRatioType.RATIO_TYPE_16_9;
import static com.oplus.ocs.camerax.util.Constant.PreviewRatioType.RATIO_TYPE_1_1;
import static com.oplus.ocs.camerax.util.Constant.PreviewRatioType.RATIO_TYPE_4_3;
import static com.oplus.ocs.camerax.util.Constant.PreviewRatioType.RATIO_TYPE_FULL;

public class PhotoRatio extends BaseFeaturePhotoRatio {
    @Override
    public <T> void setConfigureParameter(Object configureBuilderHolder, T value) {
        // do nothing in this feature
    }

    @Override
    public <T> void setPreviewParameter(Object cameraDeviceHolder, String modeType, String cameraType, T value) {
        // do nothing in this feature
    }

    @Override
    public List<String> getSupportFeatureSubValues(Context appContext, ConfigureBean configure) {
        List<String> ratioLists = new ArrayList<>();
        ratioLists.add(RATIO_TYPE_4_3);
        ratioLists.add(RATIO_TYPE_1_1);
        ratioLists.add(RATIO_TYPE_16_9);
        ratioLists.add(RATIO_TYPE_FULL);
        return ratioLists;
    }

    @Override
    public void checkConflictFeature(Context appContext, ConfigureBean configure) {
        // do nothing in this feature
    }
}