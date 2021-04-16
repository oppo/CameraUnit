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
 * File: - SettingActivity.java
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

package com.oplus.ocs.cameraunit.component;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ocs.cameraunit.R;
import com.ocs.cameraunit.databinding.ActivitySettingBinding;
import com.oplus.ocs.cameraunit.util.UiUtils;

public class SettingActivity extends AppCompatActivity {
    private final View.OnClickListener onClickListener = v -> {
        if (R.id.tv_title == v.getId()) {
            finish();
        } else if (R.id.rl_open_source_licenses == v.getId()) {
            new OpenSourceLicensesDialog().showLicenses(getSupportFragmentManager());
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UiUtils.updateSettingActivityWindow(this);
        ActivitySettingBinding mRootView = ActivitySettingBinding.inflate(getLayoutInflater());

        setContentView(mRootView.getRoot());

        mRootView.tvTitle.setOnClickListener(onClickListener);
        mRootView.tvVersionNum.setText(UiUtils.getVersionName(this));
        mRootView.rlOpenSourceLicenses.setOnClickListener(onClickListener);
    }
}
