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
 * File: - BlurProgressView.java
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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ocs.cameraunit.R;

import java.util.Optional;

public class BlurProgressView extends FrameLayout {
    private SeekBarChangeListener mSeekBarChangeListener;
    private final TextView mTvBlurValue;
    private final SeekBar mBlurSeekBar;

    public BlurProgressView(Context context) {
        this(context, null);
    }

    public BlurProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.blur_progress_view, this);

        mTvBlurValue = findViewById(R.id.tv_blur_value);
        mBlurSeekBar = findViewById(R.id.blur_seek_bar);
        mBlurSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTvBlurValue.setText(String.format(getContext().getString(R.string.blur_progress_value), progress));
                Optional.ofNullable(mSeekBarChangeListener).ifPresent(li -> li.onProgressChange(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Optional.ofNullable(mSeekBarChangeListener).ifPresent(SeekBarChangeListener::onStartTrackingTouch);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Optional.ofNullable(mSeekBarChangeListener).ifPresent(SeekBarChangeListener::onStopTrackingTouch);
            }
        });
    }

    public void setProgress(int progress) {
        mTvBlurValue.setText(String.format(getContext().getString(R.string.blur_progress_value), progress));
        mBlurSeekBar.setProgress(progress);
    }

    public void setSeekBarChangeListener(SeekBarChangeListener seekBarChangeListener) {
        this.mSeekBarChangeListener = seekBarChangeListener;
    }

    public interface SeekBarChangeListener {
        void onProgressChange(int progress);

        void onStartTrackingTouch();

        void onStopTrackingTouch();
    }
}
