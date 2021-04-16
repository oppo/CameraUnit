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
 * File: - VideoControl.java
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

package com.oplus.ocs.camerax.component.video;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaCodecInfo;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;

import com.oplus.ocs.camerax.util.Constant;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_0;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_180;
import static com.oplus.ocs.camerax.util.Constant.Orientation.ORIENTATION_360;

public class VideoControl {
    private static final String TAG = "VideoControl";

    private static final int VIDEO_1080P_BIT_RATE = 17000000;
    private static final int VIDEO_4K_BIT_RATE = 100000000;

    private static final String VIDEO_PREFIX = ".mp4";
    private static final String MIME_TYPE = "video/mp4";
    private static final String DEBUG_SAVE_PATH = Environment.getExternalStorageDirectory() + "/DCIM/Camera/";

    private static final int STATE_STOPPED = 0;
    private static final int STATE_RECORDING = 1;
    private static final int STATE_STARTING = 2;
    private static final int STATE_STOPPING = 3;
    private static final int STATE_PAUSE = 4;
    private static final long AT_LEAST_TIME = 1000;

    private MediaRecorder mRecorder = null;
    private int mState = STATE_STOPPED;
    private String mCurrentPath = null;
    private String mCurrentName = null;
    private long mStartTime = 0;
    private Handler mHandler = null;
    private Runnable mStartTask = null;

    public VideoControl() {
        HandlerThread thread = new HandlerThread("Record Thread");
        thread.start();
        mHandler = new Handler(thread.getLooper());
    }

    private boolean stopRecording() {
        boolean stopFailed = false;

        if (null != mRecorder) {
            setRecordState(STATE_STOPPING);

            try {
                mRecorder.stop();
            } catch (Exception e) {
                stopFailed = true;

                Log.e(TAG, "stop recording failed", e);
            } finally {
                setRecordState(STATE_STOPPED);
                mRecorder.release();
                mRecorder = null;
            }
        }

        return stopFailed;
    }

    public void release() {
        if (null != mStartTask) {
            mHandler.removeCallbacks(mStartTask);
        }
    }

    public void resetState() {
        setRecordState(STATE_STOPPED);
    }

    public void setVideoMirror(MediaRecorder recorder, int mirrorType) {
        Log.v(TAG, "setVideoMirror, mirrorType: " + mirrorType);

        try {
            Method method = MediaRecorder.class.getDeclaredMethod("setParameter", String.class);
            method.setAccessible(true);
            method.invoke(recorder, "set-video-mirror=" + mirrorType);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, "set video mirror failed", e);
        }
    }

    public void start(final Surface surface, final int sensorOrientation, final Size videoSize, final int fps,
            boolean isVideoHdrMode, boolean isSlowVideoMode, boolean mirror, int rotation,
            final VideoStateCallback callback, double capturerate) {
        mStartTask = () -> {
            setRecordState(STATE_STARTING);
            mRecorder = new MediaRecorder();
            mRecorder.setInputSurface(surface);

            if (!isSlowVideoMode) {
                // setAudioSource and setAudioEncoder have order requirements and cannot be called consecutively.
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            }

            mRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            // NOTICE: find x pro should set profile level because it has hardware hdr.
            if (isVideoHdrMode && Build.BOARD.equals("kona") && Build.DEVICE.equals("OP4A7A")) {
                mRecorder.setVideoEncodingBitRate(VIDEO_4K_BIT_RATE);
                mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.HEVC);
                mRecorder.setVideoEncodingProfileLevel(MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10,
                        MediaCodecInfo.CodecProfileLevel.AVCLevel13);
            } else {
                mRecorder.setVideoEncodingBitRate(VIDEO_1080P_BIT_RATE);
                mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            }

            int mirrorType = 0;

            if (mirror) {
                if ((ORIENTATION_0 == rotation) || (ORIENTATION_180 == rotation)) {
                    mirrorType = Constant.MirrorType.MIRROR_TYPE_HORIZONTAL;
                } else {
                    mirrorType = Constant.MirrorType.MIRROR_TYPE_VERTICAL;
                }
            }

            // for qcom platform.
            if (0 != mirrorType) {
                setVideoMirror(mRecorder, mirrorType);
            }

            mRecorder.setVideoFrameRate(fps);
            mRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());

            if (!isSlowVideoMode) {
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            }

            mRecorder.setOutputFile(genVideoPath());
            int degrees = getJpegOrientation(0 != mirrorType, rotation, sensorOrientation);
            mRecorder.setOrientationHint(degrees);

            // slow video mode must set capture rate corresponding to fps.
            mRecorder.setCaptureRate(capturerate);

            try {
                mRecorder.prepare();
                mRecorder.start();
                mStartTime = System.currentTimeMillis();
                setRecordState(STATE_RECORDING);
                callback.onStartFinish();
                mStartTask = null;
            } catch (Exception e) {
                Log.e(TAG, "start: start recording failed!", e);
                stopRecording();
            }
        };

        mHandler.post(mStartTask);
    }

    public void stop(final VideoStateCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "stop, before recorder stop");

                boolean stopFailed = stopRecording();
                callback.onRecorderStopFinish();

                Log.i(TAG, "stop, after recorder stop, stopFailed: " + stopFailed
                        + ", record time: " + (System.currentTimeMillis() - mStartTime));

                if (stopFailed || ((System.currentTimeMillis() - mStartTime) < AT_LEAST_TIME)) {
                    //removeVideo(activity, null);
                    callback.onUpdateUI(null);
                } else {
                    Bitmap bitmap = createVideoThumbnailBitmap(mCurrentPath);
                    callback.onUpdateUI(bitmap);
                }
            }
        });
    }

    private void setRecordState(int state) {
        Log.i(TAG, "setRecordState, mState: " + mState + " -> " + state);

        this.mState = state;
    }

    public boolean isRecording() {
        return STATE_RECORDING == mState;
    }

    public boolean isStoppable() {
        return ((STATE_RECORDING == mState) || isPaused()) && (System.currentTimeMillis() - mStartTime > AT_LEAST_TIME);
    }

    public boolean isStopped() {
        return STATE_STOPPED == mState;
    }

    public boolean isStopping() {
        return STATE_STOPPING == mState;
    }

    public boolean isPaused() {
        return STATE_PAUSE == mState;
    }

    public void resume() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null != mRecorder) {
                    setRecordState(STATE_RECORDING);
                    mRecorder.resume();
                }
            }
        });
    }

    public void pause() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null != mRecorder) {
                    setRecordState(STATE_PAUSE);
                    mRecorder.pause();
                }
            }
        });
    }

    private Bitmap saveVideo(Activity activity, Uri currentUri) {
        Log.v(TAG, "saveVideo, uri: " + currentUri);

        activity.sendBroadcast(new Intent(android.hardware.Camera.ACTION_NEW_VIDEO, currentUri));

        return createVideoThumbnailBitmap(mCurrentPath);
    }

    private void removeVideo(Activity activity, Uri currentUri) {
        if (null != mCurrentPath) {
            File f = new File(mCurrentPath);

            if (f.exists() && !f.delete()) {
                Log.v(TAG, "deleteVideoFile, Could not delete: " + mCurrentPath);
            }
        }

        if (null != currentUri) {
            Log.v(TAG, "deleteVideoFile, delete uri: " + currentUri);

            activity.getContentResolver().delete(currentUri, null, null);
        }
    }

    private static Bitmap createVideoThumbnailBitmap(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            if (null != filePath) {
                retriever.setDataSource(filePath);
            }

            bitmap = retriever.getFrameAtTime(-1);
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }

        return bitmap;
    }

    private String genVideoPath() {
        File savePath = new File(DEBUG_SAVE_PATH);

        if (!savePath.exists()) {
            savePath.mkdirs();
        }

        mCurrentName = System.currentTimeMillis() + VIDEO_PREFIX;
        mCurrentPath = DEBUG_SAVE_PATH + mCurrentName;
        return mCurrentPath;
    }

    public String getCurrentPath() {
        return mCurrentPath;
    }

    private static int getJpegOrientation(boolean front, int orientation, int sensorOrientation) {
        int rotation = 0;

        if (orientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
            if (front) {
                rotation = (sensorOrientation - orientation + ORIENTATION_360) % ORIENTATION_360;
            } else {
                rotation = (sensorOrientation + orientation) % ORIENTATION_360;
            }
        } else {
            rotation = sensorOrientation;
        }

        Log.v(TAG, "getJpegOrientation, cameraId: " + ", orientation: " + orientation + ", rotation: " + rotation);

        return rotation;
    }
}
