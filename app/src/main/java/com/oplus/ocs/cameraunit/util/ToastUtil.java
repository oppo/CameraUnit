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
 * File: - ToastUtil.java
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

package com.oplus.ocs.cameraunit.util;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ocs.cameraunit.R;
import com.oplus.ocs.base.util.Util;

public class ToastUtil {
    private static final String TAG = "ToastUtil";
    private static Toast mToast;

    private ToastUtil() {
        // do nothing for protect util tools
    }

    public static void showToast(Context context, String text) {
        showToast(context, text, 0);
    }

    public static void showToast(Context context, String text, int drawableId) {
        showToast(context, text, drawableId, 0);
    }

    public static void showToast(Context context, String text, int drawableId, int bgColorId) {
        try {
            hideToast();
            mToast = new Toast(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.toast_layout, null);
            LinearLayout toastLayout = layout.findViewById(R.id.toast_layout);
            ImageView toastIcon = layout.findViewById(R.id.toast_icon);
            TextView toastText = layout.findViewById(R.id.toast_text);

            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setView(layout);
            mToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, Util.dip2px(context, 70));

            if (drawableId == 0) {
                toastIcon.setVisibility(View.GONE);
            } else {
                toastIcon.setVisibility(View.VISIBLE);
                toastIcon.setImageResource(drawableId);
            }

            GradientDrawable gradientDrawable = (GradientDrawable) toastLayout.getBackground();

            if (bgColorId == 0) {
                gradientDrawable.setColor(context.getResources().getColor(R.color.default_toast_color));
            } else {
                gradientDrawable.setColor(context.getResources().getColor(bgColorId));
            }

            toastText.setText(text);
            mToast.show();
        } catch (Exception e) {
            Log.e(TAG, "showToast failed!", e);
        }
    }

    public static void hideToast() {
        if (null != mToast) {
            mToast.cancel();
        }

        mToast = null;
    }
}

