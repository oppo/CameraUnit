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
 * File: - BaseFeatureVideoStabilization.java
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
import com.oplus.ocs.camerax.util.Constant;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFeatureVideoStabilization implements IFeature {
    @Override
    public String getFeatureName(Context appContext) {
        return appContext.getString(R.string.config_name_stabilization);
    }

    @Override
    public int getFeatureIconId() {
        return R.drawable.menu_super_eis_light;
    }

    @Override
    public List<String> getSupportFeatureDisplayValues(Context appContext, ConfigureBean configure) {
        List<String> subValues = getSupportFeatureSubValues(appContext, configure);
        List<String> displayLists = new ArrayList<>();

        if (null == subValues) {
            return displayLists;
        }

        for (String supportFeatureSubValue : subValues) {
            if (Constant.VideoStabilizationMode.VIDEO_STABILIZATION_OFF.equals(supportFeatureSubValue)) {
                displayLists.add(appContext.getString(R.string.display_no_stabilization));
            } else if (Constant.VideoStabilizationMode.VIDEO_STABILIZATION.equals(supportFeatureSubValue)) {
                displayLists.add(appContext.getString(R.string.display_video_stabilization));
            } else if (Constant.VideoStabilizationMode.SUPER_STABILIZATION.equals(supportFeatureSubValue)) {
                displayLists.add(appContext.getString(R.string.display_super_stabilization));
            }
        }

        return displayLists;
    }

    @Override
    public List<Integer> getSupportFeatureDisplayIcons(Context appContext, ConfigureBean configure) {
        List<String> subValues = getSupportFeatureSubValues(appContext, configure);
        List<Integer> displayIconLists = new ArrayList<>();

        if (null == subValues) {
            return displayIconLists;
        }

        for (String supportFeatureSubValue : subValues) {
            if (Constant.VideoStabilizationMode.VIDEO_STABILIZATION_OFF.equals(supportFeatureSubValue)) {
                displayIconLists.add(R.drawable.menu_super_eis_light);
            } else if (Constant.VideoStabilizationMode.VIDEO_STABILIZATION.equals(supportFeatureSubValue)) {
                displayIconLists.add(R.drawable.menu_super_eis_light);
            } else if (Constant.VideoStabilizationMode.SUPER_STABILIZATION.equals(supportFeatureSubValue)) {
                displayIconLists.add(R.drawable.menu_super_eis_light);
            }
        }

        return displayIconLists;
    }
}