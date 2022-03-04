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
 * File: - VideoTimeView.java
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ocs.cameraunit.R;

import java.util.Timer;
import java.util.TimerTask;

public class VideoTimeView extends LinearLayout {
    public static final int ONE_SECOND = 1000;

    private final LinearLayout mTimeView;
    private final TextView mTvTime;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int count = 0;

    public VideoTimeView(Context context) {
        this(context, null);
    }

    public VideoTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTimeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.video_time_view, this);

        mTimeView = findViewById(R.id.time_view);
        mTvTime = findViewById(R.id.time);
        mTvTime.setText(formatTime(0));
    }

    public synchronized void startTimer() {
        mTimeView.setVisibility(VISIBLE);

        if (null == mTimer) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    count++;
                    VideoTimeView.this.post(() -> mTvTime.setText(formatTime(count)));
                }
            };
        }

        mTimer.schedule(mTimerTask, ONE_SECOND, ONE_SECOND);
    }

    public synchronized void stopTimer(boolean isPause) {
        if (!isPause) {
            mTimeView.setVisibility(GONE);
            count = 0;
            mTvTime.setText(formatTime(0));
        }

        if (null != mTimer) {
            mTimerTask.cancel();
            mTimerTask = null;

            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    @SuppressLint("DefaultLocale")
    public String formatTime(int count) {
        int seconds = count % 60;
        int minutes = (count / 60) % 60;
        int hours = count / 3600;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else if (seconds > 0) {
            return "00:" + String.format("%02d", seconds);
        } else {
            return "00:00";
        }
    }
}
