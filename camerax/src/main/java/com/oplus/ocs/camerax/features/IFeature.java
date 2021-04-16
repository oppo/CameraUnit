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
 * File: - IFeature.java
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

package com.oplus.ocs.camerax.features;

import android.content.Context;

import com.oplus.ocs.camerax.ConfigureBean;

import java.util.List;

public interface IFeature {
    <T> void setConfigureParameter(Object configureBuilderHolder, T value);

    <T> void setPreviewParameter(Object cameraDeviceHolder, String modeType, String cameraType, T value);

    String getFeatureName(Context appContext);

    int getFeatureIconId();

    List<String> getSupportFeatureSubValues(Context appContext, ConfigureBean configure);

    List<String> getSupportFeatureDisplayValues(Context appContext, ConfigureBean configure);

    List<Integer> getSupportFeatureDisplayIcons(Context appContext, ConfigureBean configure);

    void checkConflictFeature(Context appContext, ConfigureBean configure);
}