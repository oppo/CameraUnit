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
 * File: - CameraButtonView.java
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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ocs.cameraunit.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CameraButtonView extends RelativeLayout implements View.OnClickListener {

    private boolean isRecording = false;
    private int mCurrentMode = Mode.VIDEO;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private CameraButtonEventListener cameraButtonEventListener;

    public CameraButtonView(Context context) {
        this(context, null);
    }

    public CameraButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraButtonView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackground(ContextCompat.getDrawable(context, R.drawable.camera_button_background));
        setOnClickListener(this);

        int cameraButtonSize = getResources().getDimensionPixelSize(R.dimen.camera_button_size);
        int videoStopSize = getResources().getDimensionPixelSize(R.dimen.video_stop_action_size);

        mImageView = new ImageView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoStopSize, videoStopSize);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(layoutParams);
        mImageView.setImageResource(R.drawable.video_stop_background);
        addView(mImageView);

        mProgressBar = new ProgressBar(context);
        RelativeLayout.LayoutParams progressLayoutParams = new RelativeLayout.LayoutParams(cameraButtonSize, cameraButtonSize);
        progressLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBar.setLayoutParams(progressLayoutParams);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.take_picture_progress_bg));
        mProgressBar.setVisibility(View.GONE);
        addView(mProgressBar);
    }

    public void setMode(int mode) {
        mCurrentMode = mode;
        mImageView.setVisibility(Mode.PHOTO == mCurrentMode ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (null == cameraButtonEventListener) {
            return;
        }

        if (Mode.PHOTO == mCurrentMode) {
            cameraButtonEventListener.takePicture();
        } else {
            if (isRecording) {
                cameraButtonEventListener.stopRecording();
            } else {
                cameraButtonEventListener.startRecording();
            }
        }
    }

    public void updateStartRecordView(){
        isRecording = true;
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
        int smallSize = getResources().getDimensionPixelSize(R.dimen.video_start_action_size);
        layoutParams.height = smallSize;
        layoutParams.width = smallSize;
        mImageView.setLayoutParams(layoutParams);
        mImageView.setImageResource(R.drawable.video_start_background);
    }

    public void resetVideoModeView() {
        isRecording = false;
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
        int bigSize = getResources().getDimensionPixelSize(R.dimen.video_stop_action_size);
        layoutParams.height = bigSize;
        layoutParams.width = bigSize;
        mImageView.setImageResource(R.drawable.video_stop_background);
    }

    public void updateViewTakingPicture(boolean isTakePicture) {
        setEnabled(!isTakePicture);
        mProgressBar.setVisibility(isTakePicture ? View.VISIBLE : View.GONE);
    }

    @IntDef({Mode.PHOTO, Mode.VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
        int PHOTO = 1;
        int VIDEO = 2;
    }

    public void setCameraButtonEventListener(CameraButtonEventListener cameraButtonEventListener) {
        this.cameraButtonEventListener = cameraButtonEventListener;
    }

    public interface CameraButtonEventListener {
        void takePicture();

        void startRecording();

        void stopRecording();
    }
}
