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
 * File: - OpenSourceLicensesDialog.java
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

import android.app.Dialog;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ocs.cameraunit.R;

public class OpenSourceLicensesDialog extends DialogFragment {
    public static final String DIALOG_LICENSES = "dialog_licenses";
    public static final String DIALOG_LICENSES_PATH = "file:///android_asset/open_source_licenses.html";

    public void showLicenses(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment previousFragment = fragmentManager.findFragmentByTag(DIALOG_LICENSES);

        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment);
        }

        fragmentTransaction.addToBackStack(null);
        show(fragmentTransaction, DIALOG_LICENSES);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        WebView webView = new WebView(requireActivity());
        webView.loadUrl(DIALOG_LICENSES_PATH);

        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.setting_open_source_licenses_title)
                .setView(webView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dismiss())
                .create();
    }
}
