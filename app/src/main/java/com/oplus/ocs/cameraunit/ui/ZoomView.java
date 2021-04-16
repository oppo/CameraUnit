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
 * File: - ZoomView.java
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

package com.oplus.ocs.cameraunit.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ocs.cameraunit.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ZoomView extends LinearLayout {
    private static final String STRING_X = "X";
    private static final String FORMAT_FLOAT = "#.#";
    private static final int TEXT_OFFSET_X = 2;

    private final DecimalFormat mFloatDecimalFormat = new DecimalFormat(FORMAT_FLOAT);
    private final List<TextView> mTvs = new ArrayList<>();
    private ZoomClickListener mListener = null;
    private float mCurrentZoomValue = 1.0f;
    private final List<Float> mPoints = new ArrayList<>();
    private boolean isSupportZoom = false;
    private boolean mbClickable = true;

    public ZoomView(Context context) {
        this(context, null);
    }

    public ZoomView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(ZoomClickListener listener) {
        this.mListener = listener;
    }

    public void setSupportZoom(boolean notSupportZoom) {
        isSupportZoom = notSupportZoom;
    }

    public void initZoomValues(float currentZoomValue, List<String> clickPoints) {
        this.mCurrentZoomValue = currentZoomValue;
        mTvs.clear();
        mPoints.clear();
        removeAllViews();

        if (null != clickPoints) {
            for (String zoomValue : clickPoints) {
                mPoints.add(Float.parseFloat(zoomValue));
            }

            for (float point : mPoints) {
                TextView tv = new TextView(getContext());
                LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.zoom_mark_button_margin);
                layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.zoom_mark_button_margin);
                tv.setLayoutParams(layoutParams);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.zoom_mark_button_text_size));
                tv.setTextColor(Color.WHITE);
                tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.zoom_mark_button_background));
                tv.setText(getDisplayValue(point));
                tv.setPadding(0, 0, TEXT_OFFSET_X, 0);

                tv.setOnClickListener(v -> {
                    if (!mbClickable || !isSupportZoom || (0 == Float.compare(mCurrentZoomValue, point))) {
                        return;
                    }

                    mCurrentZoomValue = point;
                    updateClickZoom();
                    Optional.ofNullable(mListener).ifPresent(li -> li.zoomClick(point));
                });

                addView(tv);
                mTvs.add(tv);
            }

            updateClickZoom();
        }
    }

    public void setClickable(boolean clickable) {
        this.mbClickable = clickable;
    }

    public String getDisplayValue(float value) {
        return mFloatDecimalFormat.format(value) + STRING_X;
    }

    public void onZoomChange(float value) {
        mCurrentZoomValue = value;

        for (int i = 0; i < mPoints.size(); i++) {
            if ((i == (mPoints.size() - 1)) && (mCurrentZoomValue >= mPoints.get(i))) {
                mTvs.get(i).setSelected(true);
            } else {
                mTvs.get(i).setSelected((mCurrentZoomValue >= mPoints.get(i)) && (mCurrentZoomValue < mPoints.get(i + 1)));
            }
        }

        for (int i = 0; i < mTvs.size(); i++) {
            if (mTvs.get(i).isSelected()) {
                mTvs.get(i).setText(getDisplayValue(mCurrentZoomValue));
            } else {
                mTvs.get(i).setText(getDisplayValue(mPoints.get(i)));
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        for (int i = 0; i < mPoints.size(); i++) {
            mTvs.get(i).setEnabled(enabled);
        }
    }

    private void updateClickZoom() {
        for (int i = 0; i < mPoints.size(); i++) {
            mTvs.get(i).setText(getDisplayValue(mPoints.get(i)));

            if ((i == (mPoints.size() - 1)) && (mCurrentZoomValue >= mPoints.get(i))) {
                mTvs.get(i).setSelected(true);
            } else {
                mTvs.get(i).setSelected((mCurrentZoomValue >= mPoints.get(i)) && (mCurrentZoomValue < mPoints.get(i + 1)));
            }
        }
    }

    public interface ZoomClickListener {
        void zoomClick(float zoomValue);
    }
}
