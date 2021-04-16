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
 * File: - BaseFeatureFlash.java
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

public abstract class BaseFeatureFlash implements IFeature {
    @Override
    public String getFeatureName(Context appContext) {
        return appContext.getString(R.string.config_name_flash);
    }

    @Override
    public int getFeatureIconId() {
        return R.drawable.menu_flash_normal;
    }

    @Override
    public List<String> getSupportFeatureDisplayValues(Context appContext, ConfigureBean configure) {
        List<String> subValues = getSupportFeatureSubValues(appContext, configure);

        return getDisplayValuesBySubValues(appContext, subValues);
    }

    @Override
    public List<Integer> getSupportFeatureDisplayIcons(Context appContext, ConfigureBean configure) {
        List<String> subValues = getSupportFeatureSubValues(appContext, configure);

        return getDisplayIconsBySubValues(subValues);
    }

    public List<String> getFlashChangeDisplayValues(Context appContext, List<String> subValues) {
        return getDisplayValuesBySubValues(appContext, subValues);
    }

    public List<Integer> getFlashChangeDisplayIcons(List<String> subValues) {
        return getDisplayIconsBySubValues(subValues);
    }

    private List<String> getDisplayValuesBySubValues(Context appContext, List<String> subValues) {
        List<String> displayLists = new ArrayList<>();

        if (null == subValues) {
            return displayLists;
        }

        for (String supportFeatureSubValue : subValues) {
            if (Constant.FlashMode.FLASH_AUTO.equals(supportFeatureSubValue)) {
                displayLists.add(appContext.getString(R.string.display_flash_auto));
            } else if (Constant.FlashMode.FLASH_OFF.equals(supportFeatureSubValue)) {
                displayLists.add(appContext.getString(R.string.display_flash_off));
            } else if (Constant.FlashMode.FLASH_ON.equals(supportFeatureSubValue)) {
                displayLists.add(appContext.getString(R.string.display_flash_on));
            } else if (Constant.FlashMode.FLASH_TORCH.equals(supportFeatureSubValue)) {
                displayLists.add(appContext.getString(R.string.display_flash_torch));
            }
        }

        return displayLists;
    }

    private List<Integer> getDisplayIconsBySubValues(List<String> subValues) {
        List<Integer> displayIconLists = new ArrayList<>();

        if (null == subValues) {
            return displayIconLists;
        }

        for (String supportFeatureSubValue : subValues) {
            if (Constant.FlashMode.FLASH_AUTO.equals(supportFeatureSubValue)) {
                displayIconLists.add(R.drawable.menu_flash_auto_dark);
            } else if (Constant.FlashMode.FLASH_OFF.equals(supportFeatureSubValue)) {
                displayIconLists.add(R.drawable.menu_flash_off_dark);
            } else if (Constant.FlashMode.FLASH_ON.equals(supportFeatureSubValue)) {
                displayIconLists.add(R.drawable.menu_flash_on_light);
            } else if (Constant.FlashMode.FLASH_TORCH.equals(supportFeatureSubValue)) {
                displayIconLists.add(R.drawable.menu_flash_torch_light);
            }
        }

        return displayIconLists;
    }
}