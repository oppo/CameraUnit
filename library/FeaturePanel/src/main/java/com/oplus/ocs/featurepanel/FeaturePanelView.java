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
 * File: - FeaturePanelView.java
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

package com.oplus.ocs.featurepanel;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oplus.ocs.base.ConfigDialog;
import com.oplus.ocs.base.FeatureBean;
import com.oplus.ocs.base.OnConfigChangeListener;
import com.oplus.ocs.base.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FeaturePanelView extends LinearLayout {
    private final static int FIX_FEATURE_NUM = 3;

    private Context mActivityContext;
    private ConfigDialog mDialog;
    private RelativeLayout mRLArrow;
    private ImageView mIvExpand;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private OnConfigChangeListener mOnConfigChangeListener;
    private List<FeatureBean> mFeatureLists;
    private boolean mbIsExpand = false;
    private boolean mbClickable = true;

    public FeaturePanelView(Context context) {
        this(context, null);
    }

    public FeaturePanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FeaturePanelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setItemClickable(boolean mbClickable) {
        this.mbClickable = mbClickable;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.feature_panel_view, this);
        mFeatureLists = new ArrayList<>();

        mRLArrow = findViewById(R.id.rl_arrow);
        mIvExpand = findViewById(R.id.iv_expand);
        mRecyclerView = findViewById(R.id.recyclerView);

        myRecyclerViewAdapter = new MyRecyclerViewAdapter(mFeatureLists);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerViewAdapter.setListener((view, position, str) -> {
            if (!mbClickable) {
                return;
            }

            configurationOptions(position);
        });

        mRLArrow.setOnClickListener(view -> {
            if (mbIsExpand) {
                mbIsExpand = false;
                mIvExpand.setRotation(180f);
                LinearLayout.LayoutParams layoutParams = (LayoutParams) mRecyclerView.getLayoutParams();
                layoutParams.height = LayoutParams.WRAP_CONTENT;
                mRecyclerView.setLayoutParams(layoutParams);
                myRecyclerViewAdapter.updateLists(mFeatureLists.subList(0, FIX_FEATURE_NUM));
            } else {
                mbIsExpand = true;
                mIvExpand.setRotation(0f);
                LinearLayout.LayoutParams layoutParams = (LayoutParams) mRecyclerView.getLayoutParams();
                layoutParams.height = Util.dip2px(context, 300);
                mRecyclerView.setLayoutParams(layoutParams);
                myRecyclerViewAdapter.updateLists(mFeatureLists);
            }
        });
    }

    public void updateFeature(Activity activity, List<FeatureBean> featureLists, OnConfigChangeListener listener) {
        this.mActivityContext = activity;
        this.mOnConfigChangeListener = listener;
        this.mFeatureLists = featureLists;
        LinearLayout.LayoutParams layoutParams = (LayoutParams) mRecyclerView.getLayoutParams();

        if (mFeatureLists.size() > FIX_FEATURE_NUM) {
            mRLArrow.setVisibility(View.VISIBLE);

            if (mbIsExpand) {
                mIvExpand.setRotation(0f);
                layoutParams.height = Util.dip2px(activity, 300);
                mRecyclerView.setLayoutParams(layoutParams);
                myRecyclerViewAdapter.updateLists(mFeatureLists);
            } else {
                mIvExpand.setRotation(180f);
                layoutParams.height = LayoutParams.WRAP_CONTENT;
                mRecyclerView.setLayoutParams(layoutParams);
                myRecyclerViewAdapter.updateLists(mFeatureLists.subList(0, FIX_FEATURE_NUM));
            }
        } else {
            mRLArrow.setVisibility(View.GONE);
            layoutParams.height = LayoutParams.WRAP_CONTENT;
            mRecyclerView.setLayoutParams(layoutParams);
            myRecyclerViewAdapter.updateLists(mFeatureLists);
        }
    }

    private void configurationOptions(int position) {
        FeatureBean featureBean = mFeatureLists.get(position);

        if (mActivityContext.getString(R.string.config_name_blur).equals(featureBean.getFeatureName())) {
            mOnConfigChangeListener.onConfigViewShow(featureBean.getFeatureId());
        } else {
            showDialog(mFeatureLists.get(position), (featureId, display, tag) -> {
                if (!featureBean.getSelectValue().equals(display)) {
                    mOnConfigChangeListener.onConfigChange(featureId, tag);
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
