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
 * File: - BaseFeatureAeAf.java
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
import com.oplus.ocs.camerax.R;

import java.util.List;

public abstract class BaseFeatureAeAf implements IFeature {

    @Override
    public String getFeatureName(Context appContext) {
        return appContext.getString(R.string.config_name_ae_af);
    }

    @Override
    public int getFeatureIconId() {
        return 0;
    }

    @Override
    public List<String> getSupportFeatureDisplayValues(Context appContext, ConfigureBean configure) {
        return null;
    }

    @Override
    public List<Integer> getSupportFeatureDisplayIcons(Context appContext, ConfigureBean configure) {
        return null;
    }
}