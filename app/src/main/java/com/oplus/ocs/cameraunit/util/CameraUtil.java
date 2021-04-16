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

package com.oplus.ocs.cameraunit.util;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.oplus.ocs.cameraunit.util.Constant.DEBUG_SAVE_PATH;

public class CameraUtil {
    private static final String TAG = "CameraUtil";

    private CameraUtil() {
        // do nothing for protect util tools
    }

    public static String saveBytesToFile(@NonNull byte[] bytes, String fileName) {
        if ((null == bytes) || (0 == bytes.length)) {
            return null;
        }

        File dir = new File(DEBUG_SAVE_PATH);

        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(DEBUG_SAVE_PATH, fileName);
        String filePath = file.getAbsolutePath();

        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "saveBytesToFile: write output stream failed!", e);
        }

        return filePath;
    }
}