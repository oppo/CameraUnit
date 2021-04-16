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
 * File: - MenuItemView.java
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

package com.oplus.ocs.settingmenupanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oplus.ocs.base.FeatureBean;

import java.util.List;

public class MenuItemView extends LinearLayout {
    private ImageView mIcon;
    private TextView mText;

    public MenuItemView(Context context) {
        this(context, null);
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.menu_item_view, this);

        mIcon = findViewById(R.id.icon);
        mText = findViewById(R.id.text);
    }

    @SuppressLint("SetTextI18n")
    public void setIconAndText(FeatureBean featureBean) {
        if (0 == featureBean.getFeatureIcon()) {
            mText.setVisibility(View.VISIBLE);
            mIcon.setVisibility(View.GONE);
        } else {
            mText.setVisibility(View.GONE);
            mIcon.setVisibility(View.VISIBLE);
            mIcon.setImageResource(featureBean.getFeatureIcon());
        }

        List<String> featureSubValues = featureBean.getFeatureSubValues();
        String selectValue = featureBean.getSelectValue();

        if ((null != featureSubValues) && !featureSubValues.isEmpty()) {
            List<String> featureSubDisplays = featureBean.getFeatureDisplayNameLists();
            int nameIndex = featureSubValues.indexOf(selectValue);

            if ((nameIndex >= 0) && (nameIndex < featureSubDisplays.size())) {
                mText.setText(featureBean.getFeatureName() + "-" + featureSubDisplays.get(nameIndex));
            }

            List<Integer> featureSubDisplayIcons = featureBean.getFeatureDisplayIconLists();
            int iconIndex = featureSubValues.indexOf(selectValue);

            if ((iconIndex >= 0) && (iconIndex < featureSubDisplayIcons.size())) {
                mIcon.setImageResource(featureSubDisplayIcons.get(iconIndex));
            }
        } else {
            mText.setText(featureBean.getFeatureName());
        }
    }
}
