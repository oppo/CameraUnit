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
 * File: - FocusIndicatorRotateLayout.java
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
package com.oplus.ocs.cameraunit.ui.focus;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

// A view that indicates the focus area or the metering area.
public class FocusIndicatorRotateLayout extends View implements FocusIndicator {
    private static final int STATE_IDLE = 0;
    private static final int STATE_FOCUSING = 1;
    private static final int STATE_FINISHING = 2;
    private static final int SCALING_UP_TIME = 160;
    private static final int SCALING_DOWN_TIME = 100;
    private static final int DISAPPEAR_TIMEOUT = 200;
    private static final float SCALE_X_VALUE = 0.55f;
    private static final float SCALE_Y_VALUE = 0.55f;
    private static final float SCALE_NONE = 1.0f;
    private int mState;
    private final Runnable mDisappear = new Disappear();
    private final Runnable mEndAction = new EndAction();

    private boolean mbForceDisableFocusIndicator = false;

    public FocusIndicatorRotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void showStart() {
        if (mbForceDisableFocusIndicator) {
            return;
        }

        clear();

        if (mState != STATE_IDLE) {
            return;
        }

        setVisibility(View.VISIBLE);
        animate().cancel();
        animate().withLayer().setDuration(SCALING_UP_TIME).scaleX(SCALE_X_VALUE).scaleY(SCALE_Y_VALUE);
        mState = STATE_FOCUSING;
    }

    @Override
    public void showSuccess(boolean timeout, boolean isExposureAdjusting) {
        showSuccess(timeout, false, isExposureAdjusting);
    }

    public void showSuccess(boolean timeout, boolean aEAFLocked , boolean isExposureAdjusting) {
        if (mbForceDisableFocusIndicator || isExposureAdjusting) {
            return;
        }

        if (!aEAFLocked && (mState != STATE_FOCUSING)) {
            return;
        }

        setVisibility(View.VISIBLE);
        animate().cancel();
        animate().withLayer().setDuration(SCALING_DOWN_TIME).scaleX(SCALE_X_VALUE).scaleY(SCALE_Y_VALUE)
                .withEndAction(timeout ? mEndAction : null);
        mState = STATE_FINISHING;
    }

    @Override
    public void showFail(boolean timeout, boolean isExposureAdjusting) {
        if (mbForceDisableFocusIndicator || isExposureAdjusting) {
            return;
        }

        if (mState != STATE_FOCUSING) {
            return;
        }

        setVisibility(View.VISIBLE);
        animate().cancel();
        animate().withLayer().setDuration(SCALING_DOWN_TIME).scaleX(SCALE_X_VALUE).scaleY(SCALE_Y_VALUE)
                .withEndAction(timeout ? mEndAction : null);
        mState = STATE_FINISHING;
    }

    public boolean stateIsIdle() {
        return mState == STATE_IDLE;
    }

    @Override
    public void clear() {
        animate().cancel();
        removeCallbacks(mDisappear);
        mDisappear.run();
        setScaleX(SCALE_NONE);
        setScaleY(SCALE_NONE);
    }

    public void resetScale() {
        setVisibility(INVISIBLE);
        setScaleX(SCALE_X_VALUE);
        setScaleY(SCALE_Y_VALUE);
    }

    public void disableFocusIndicator(boolean disable) {
        mbForceDisableFocusIndicator = disable;
    }

    private class EndAction implements Runnable {
        @Override
        public void run() {
            // Keep the focus indicator for some time.
            postDelayed(mDisappear, DISAPPEAR_TIMEOUT);
        }
    }

    private class Disappear implements Runnable {
        @Override
        public void run() {
            setVisibility(INVISIBLE);
            mState = STATE_IDLE;
        }
    }
}

