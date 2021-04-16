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
 * File: - CameraUtil.java
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

package com.oplus.ocs.camerax.util;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.oplus.ocs.camerax.util.Constant.VideoFpsValue.VIDEO_FPS_120;
import static com.oplus.ocs.camerax.util.Constant.VideoFpsValue.VIDEO_FPS_240;
import static com.oplus.ocs.camerax.util.Constant.VideoFpsValue.VIDEO_FPS_30;
import static com.oplus.ocs.camerax.util.Constant.VideoFpsValue.VIDEO_FPS_480;
import static com.oplus.ocs.camerax.util.Constant.VideoFpsValue.VIDEO_FPS_60;
import static com.oplus.ocs.camerax.util.Constant.VideoFpsValue.VIDEO_FPS_960;

public class CameraUtil {
    private static final String TAG = "CameraUtil";
    public static final double COMPARE_LIMIT = 0.01;

    public static List<Size> getSizeListByRatio(List<Size> sizes, double targetRatio) {
        Log.d(TAG, "getSizeListByRatio: " + sizes + ", currentRatio: " + targetRatio);

        if ((null == sizes) || (sizes.size() == 0)) {
            return null;
        }

        List<Size> optimalSizeList = new ArrayList<>();

        for (Size size : sizes) {
            if (null != size) {
                double ratio = (double) size.getWidth() / size.getHeight();

                if (Math.abs(ratio - targetRatio) <= COMPARE_LIMIT) {
                    optimalSizeList.add(size);
                }
            }
        }

        optimalSizeList.sort((sizeA, sizeB) -> sizeB.getWidth() - sizeA.getWidth());

        return optimalSizeList;
    }

    public static Size getOptimalPreviewSize(Context context, List<Size> sizes, double targetRatio) {
        if (null == sizes) {
            return null;
        }

        Size optimalSize = null;
        int minDiff = Integer.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of preview surface. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size.
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.getWidth() / size.getHeight();

            if (Math.abs(ratio - targetRatio) > COMPARE_LIMIT) {
                continue;
            }

            int diff = Math.abs(size.getHeight() - getScreenSize(context).getWidth());

            if (diff < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.getHeight() - getScreenSize(context).getWidth());
            } else if ((diff == minDiff) && (size.getHeight() > getScreenSize(context).getWidth())) {
                optimalSize = size;
            }
        }

        // Cannot find the one match the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (null == optimalSize) {
            minDiff = Integer.MAX_VALUE;

            for (Size size : sizes) {
                if (Math.abs(size.getHeight() - getScreenSize(context).getWidth()) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.getHeight() - getScreenSize(context).getWidth());
                }
            }
        }

        return optimalSize;
    }

    private static Size getScreenSize(@NonNull Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        Point point = new Point();
        d.getRealSize(point);
        int screenHeight = Math.max(point.x, point.y);
        int screenWidth = Math.min(point.x, point.y);

        return new Size(screenWidth, screenHeight);
    }

    public static String convertFpsRangeValue(Range<Integer> fps) {
        if (fps.getUpper() <= 30) {
            return VIDEO_FPS_30;
        } else if (fps.getUpper() <= 60) {
            return VIDEO_FPS_60;
        } else if (fps.getUpper() <= 120) {
            return VIDEO_FPS_120;
        } else if (fps.getUpper() <= 240) {
            return VIDEO_FPS_240;
        } else if (fps.getUpper() <= 480) {
            return VIDEO_FPS_480;
        } else if (fps.getUpper() <= 960) {
            return VIDEO_FPS_960;
        }

        return VIDEO_FPS_30;
    }
}