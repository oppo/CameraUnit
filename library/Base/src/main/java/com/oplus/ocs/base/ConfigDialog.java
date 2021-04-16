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
 * File: - ConfigDialog.java
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

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ConfigDialog extends AlertDialog {
    private static final String TAG = "ConfigDialog";

    private ConfigDialog(Context context) {
        super(context);
    }

    public static class Builder {
        private List<String> mTags = new ArrayList<>();
        private List<String> mDisplays = new ArrayList<>();
        private int mSelectPosition = -1;
        private int mConfigType = 1;
        private ItemClickListener mItemClickListener = null;
        private String mTitle = "";
        private Context mContext = null;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setTag(List<String> tags) {
            mTags = tags;
            return this;
        }

        public Builder setDisplay(List<String> displays) {
            mDisplays = displays;
            return this;
        }

        public Builder setSelectItem(String display) {
            if ((-1 == mConfigType) || mDisplays.isEmpty()) {
                return this;
            }

            mSelectPosition = mDisplays.indexOf(display);

            if (-1 == mSelectPosition) {
                mSelectPosition = mTags.indexOf(display);
            }

            return this;
        }

        public Builder setConfigType(int configType) {
            mConfigType = configType;
            return this;
        }

        public Builder setTitle(String mTitle) {
            this.mTitle = mTitle;
            return this;
        }

        public Builder setItemClickListener(ItemClickListener mItemClickListener) {
            this.mItemClickListener = mItemClickListener;
            return this;
        }

        public String getTitle() {
            return mTitle;
        }

        private int getConfigType() {
            return mConfigType;
        }

        private int getSelectPosition() {
            return mSelectPosition;
        }

        private List<String> getTags() {
            return mTags;
        }

        private List<String> getDisplays() {
            return mDisplays;
        }

        private ItemClickListener getItemClickListener() {
            return mItemClickListener;
        }

        public ConfigDialog show() {
            ConfigDialog dialog = new ConfigDialog(mContext);

            View view = View.inflate(mContext, R.layout.config_dialog_main, null);
            TextView tvTitle = view.findViewById(R.id.config_dialog_title);
            RecyclerView recyclerView = view.findViewById(R.id.config_dialog_recyclerView);

            tvTitle.setText(getTitle());
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.config_dialog_item_layout, parent, false);
                    return new ViewHolder(view);
                }

                @Override
                public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
                    holder.mDisplay.setText(getDisplays().get(position));
                    holder.mChooseView.setVisibility((position == getSelectPosition()) ? View.VISIBLE : View.GONE);

                    holder.itemView.setOnClickListener(v -> {
                        if (null != getItemClickListener()) {
                            int displaysSize = getDisplays().size();
                            int tagsSize = getTags().size();

                            Log.d(TAG, "onClick displaysSize: " + displaysSize + ", tagsSize: " + tagsSize);

                            if ((displaysSize > position) && (tagsSize > position)) {
                                getItemClickListener().onItemClick(getConfigType(), getDisplays().get(position),
                                        getTags().get(position));
                            }
                        }

                        dialog.dismiss();
                    });
                }

                @Override
                public int getItemCount() {
                    return getDisplays().size();
                }
            });

            dialog.setView(view);
            dialog.show();

            return dialog;
        }
    }

    public interface ItemClickListener {
        void onItemClick(int featureId, String display, String tag);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mDisplay;
        View mChooseView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mDisplay = itemView.findViewById(R.id.item_des);
            mChooseView = itemView.findViewById(R.id.item_choose);
        }
    }
}
