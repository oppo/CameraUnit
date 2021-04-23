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
 * File: - FeatureBean.java
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

package com.oplus.ocs.base;

import androidx.annotation.NonNull;

import java.util.List;

public class FeatureBean {
    private final int mFeatureId;
    private int mFeatureIcon;
    private String mFeatureName;
    private String selectValue;
    private List<String> mFeatureSubValues;
    private List<String> mFeatureDisplayNameLists;
    private List<Integer> mFeatureDisplayIconLists;

    public FeatureBean(int featureId) {
        this.mFeatureId = featureId;
    }

    public int getFeatureId() {
        return mFeatureId;
    }

    public String getFeatureName() {
        return mFeatureName;
    }

    public void setFeatureName(String mFeatureName) {
        this.mFeatureName = mFeatureName;
    }

    public int getFeatureIcon() {
        return mFeatureIcon;
    }

    public void setFeatureIcon(int mFeatureIcon) {
        this.mFeatureIcon = mFeatureIcon;
    }

    public List<String> getFeatureSubValues() {
        return mFeatureSubValues;
    }

    public void setFeatureSubValues(List<String> mFeatureSubValues) {
        this.mFeatureSubValues = mFeatureSubValues;
    }

    public List<String> getFeatureDisplayNameLists() {
        return mFeatureDisplayNameLists;
    }

    public void setFeatureDisplayNameLists(List<String> mFeatureDisplayNameLists) {
        this.mFeatureDisplayNameLists = mFeatureDisplayNameLists;
    }

    public String getSelectValue() {
        return selectValue;
    }

    public void setSelectValue(String selectValue) {
        this.selectValue = selectValue;
    }

    public List<Integer> getFeatureDisplayIconLists() {
        return mFeatureDisplayIconLists;
    }

    public void setFeatureDisplayIconLists(List<Integer> mFeatureDisplayIconLists) {
        this.mFeatureDisplayIconLists = mFeatureDisplayIconLists;
    }

    @NonNull
    @Override
    public String toString() {
        return "FeatureBean{" +
                "mFeatureId=" + mFeatureId +
                ", mFeatureIcon=" + mFeatureIcon +
                ", mFeatureName='" + mFeatureName + '\'' +
                ", selectValue='" + selectValue + '\'' +
                ", mFeatureSubValues=" + mFeatureSubValues +
                ", mFeatureDisplayNameLists=" + mFeatureDisplayNameLists +
                ", mFeatureDisplayIconLists=" + mFeatureDisplayIconLists +
                '}';
    }
}
