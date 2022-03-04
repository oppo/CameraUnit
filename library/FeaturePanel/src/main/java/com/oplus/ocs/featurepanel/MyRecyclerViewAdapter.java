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
 * File: - MyRecyclerViewAdapter.java
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

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oplus.ocs.base.FeatureBean;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private List<FeatureBean> mFeatureLists;
    private MyRecyclerViewAdapter.OnItemClickListener mListener;

    public MyRecyclerViewAdapter(List<FeatureBean> lists) {
        mFeatureLists = lists;
    }

    public void setListener(MyRecyclerViewAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public void updateLists(List<FeatureBean> lists) {
        mFeatureLists = lists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_panel_item, parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.MyViewHolder holder, int position) {
        FeatureBean selectedFeature = mFeatureLists.get(position);

        if ((null == selectedFeature)) {
            return;
        }

        String selectedValue = selectedFeature.getSelectValue();

        if (null == selectedValue) {
            holder.mTextView.setText(selectedFeature.getFeatureName());
            holder.mImageView.setImageResource(selectedFeature.getFeatureIcon());

            return;
        }

        int itemIndex = selectedFeature.getFeatureSubValues().indexOf(selectedValue);
        List<Integer> iconLists = selectedFeature.getFeatureDisplayIconLists();

        if ((null != iconLists) && !iconLists.isEmpty() && (0 <= itemIndex)) {
            int selectedFeatureIcon = iconLists.get(itemIndex);

            if (0 != selectedFeatureIcon) {
                holder.mImageView.setImageResource(selectedFeatureIcon);
            } else {
                holder.mImageView.setImageResource(selectedFeature.getFeatureIcon());
            }
        } else {
            holder.mImageView.setImageResource(selectedFeature.getFeatureIcon());
        }

        List<String> featureNames = selectedFeature.getFeatureDisplayNameLists();

        if ((null != featureNames) && !featureNames.isEmpty() && (0 <= itemIndex)) {
            String selectedFeatureName =  featureNames.get(itemIndex);

            if (null != selectedFeatureName) {
                holder.mTextView.setText(selectedFeature.getFeatureName() + "-" + selectedFeatureName);
            } else {
                holder.mTextView.setText(selectedFeature.getFeatureName());
            }
        } else {
            holder.mTextView.setText(selectedFeature.getFeatureName());
        }
    }

    @Override
    public int getItemCount() {
        return mFeatureLists.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;
        LinearLayout mContent;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.img_icon);
            mTextView = itemView.findViewById(R.id.tv_item);
            mContent = itemView.findViewById(R.id.content);
            mContent.setOnClickListener(view -> {
                if (mListener != null) {
                    mListener.onClick(view, getLayoutPosition(), mFeatureLists.get(getLayoutPosition()).getFeatureName());
                }
            });
        }
    }

    interface OnItemClickListener {
        void onClick(View view, int position, String str);
    }
}
