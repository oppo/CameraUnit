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
 * File: - SettingMenuPanel.java
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

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oplus.ocs.base.ConfigDialog;
import com.oplus.ocs.base.FeatureBean;
import com.oplus.ocs.base.OnConfigChangeListener;

import java.util.ArrayList;
import java.util.List;

public class SettingMenuPanel extends LinearLayout {
    private Context mActivityContext;
    private ConfigDialog mDialog;
    private OnConfigChangeListener onConfigChangeListener;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private List<FeatureBean> mFeatureLists;
    private boolean mbClickable = true;

    public SettingMenuPanel(Context context) {
        this(context, null);
    }

    public SettingMenuPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingMenuPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.setting_menu_panel_view, this);
        mFeatureLists = new ArrayList<>();

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(mFeatureLists);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(myRecyclerViewAdapter);

        myRecyclerViewAdapter.setListener((view, position, str) -> {
            if (!mbClickable) {
                return;
            }

            configurationOptions(position);
        });

    }

    public void updateFeature(Activity activity, List<FeatureBean> featureLists, OnConfigChangeListener onConfigChangeListener) {
        this.mActivityContext = activity;
        this.mFeatureLists = featureLists;
        this.onConfigChangeListener = onConfigChangeListener;
        myRecyclerViewAdapter.updateLists(mFeatureLists);
    }

    public void updateList(List<FeatureBean> featureLists) {
        this.mFeatureLists = featureLists;
        myRecyclerViewAdapter.updateLists(mFeatureLists);
    }

    public void setItemClickable(boolean mbClickable) {
        this.mbClickable = mbClickable;
    }

    private void configurationOptions(int position) {
        FeatureBean featureBean = mFeatureLists.get(position);

        if (mActivityContext.getString(R.string.config_name_setting).equals(featureBean.getFeatureName())) {
            onConfigChangeListener.onConfigViewShow(featureBean.getFeatureId());
        } else {
            showDialog(mFeatureLists.get(position), (featureId, display, tag) -> {
                if (!featureBean.getSelectValue().equals(tag)) {
                    onConfigChangeListener.onConfigChange(featureId, tag);
                }
            });
        }
    }

    private void showDialog(FeatureBean featureBean, ConfigDialog.ItemClickListener listener) {
        if ((null != mDialog) && mDialog.isShowing()) {
            mDialog.dismiss();

            return;
        }

        mDialog = new ConfigDialog.Builder(mActivityContext)
                .setConfigType(featureBean.getFeatureId())
                .setTitle(featureBean.getFeatureName())
                .setDisplay(featureBean.getFeatureDisplayNameLists())
                .setTag(featureBean.getFeatureSubValues())
                .setItemClickListener(listener)
                .setSelectItem(featureBean.getSelectValue())
                .show();
    }
}

