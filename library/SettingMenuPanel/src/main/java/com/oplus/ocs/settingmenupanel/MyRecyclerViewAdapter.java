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

package com.oplus.ocs.settingmenupanel;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oplus.ocs.base.FeatureBean;
import com.oplus.ocs.base.util.Util;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private List<FeatureBean> mFeatureLists;
    private OnItemClickListener mListener;

    public MyRecyclerViewAdapter(List<FeatureBean> lists) {
        mFeatureLists = lists;
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void updateLists(List<FeatureBean> lists) {
        mFeatureLists = lists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_menu_panel_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mMenuItemView.setIconAndText(mFeatureLists.get(position));

        ViewGroup.LayoutParams layoutParams = holder.mContent.getLayoutParams();

        if (mFeatureLists.size() == 1) {
            layoutParams.width = Util.getScreenWidth(holder.itemView.getContext());
            holder.mContent.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            holder.mContent.setPadding(0, 0, Util.dip2px(holder.itemView.getContext(), 25), 0);
        } else if (mFeatureLists.size() == 2) {
            layoutParams.width = Util.getScreenWidth(holder.itemView.getContext()) / 2;

            if (position == 0) {
                holder.mContent.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                holder.mContent.setPadding(Util.dip2px(holder.itemView.getContext(), 25), 0, 0, 0);
            } else {
                holder.mContent.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                holder.mContent.setPadding(0, 0, Util.dip2px(holder.itemView.getContext(), 25), 0);
            }
        } else if (mFeatureLists.size() == 3) {
            layoutParams.width = Util.getScreenWidth(holder.itemView.getContext()) / 3;

            if (position == 0) {
                holder.mContent.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                holder.mContent.setPadding(Util.dip2px(holder.itemView.getContext(), 25), 0, 0, 0);
            } else if (position == 1) {
                holder.mContent.setGravity(Gravity.CENTER);
                holder.mContent.setPadding(0, 0, 0, 0);
            } else {
                holder.mContent.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                holder.mContent.setPadding(0, 0, Util.dip2px(holder.itemView.getContext(), 25), 0);
            }
        } else if (mFeatureLists.size() == 4) {
            layoutParams.width = Util.getScreenWidth(holder.itemView.getContext()) / 4;

            if (position == 0) {
                holder.mContent.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                holder.mContent.setPadding(Util.dip2px(holder.itemView.getContext(), 25), 0, 0, 0);
            } else if (position == 3) {
                holder.mContent.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
                holder.mContent.setPadding(0, 0, Util.dip2px(holder.itemView.getContext(), 25), 0);
            } else {
                holder.mContent.setGravity(Gravity.CENTER);
                holder.mContent.setPadding(0, 0, 0, 0);
            }
        } else {
            layoutParams.width = Util.getScreenWidth(holder.itemView.getContext()) / 5;
            holder.mContent.setGravity(Gravity.CENTER);
            holder.mContent.setPadding(0, 0, 0, 0);
        }

        holder.mContent.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mFeatureLists.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout mContent;
        MenuItemView mMenuItemView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mContent = itemView.findViewById(R.id.content);
            mMenuItemView = itemView.findViewById(R.id.menu_item);

            mMenuItemView.setOnClickListener(view -> {
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
