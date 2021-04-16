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
 * File: - AdapterFactory.java
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

package com.oplus.ocs.camerax.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.oplus.ocs.camerax.adapter.camera2.Camera2ApiAdapter;
import com.oplus.ocs.camerax.adapter.cameraunit.CameraUnitAdapter;
import com.oplus.ocs.camerax.util.RomUtils;

public class AdapterFactory {
    private static BaseAdapter sBaseAdapter = null;

    @NonNull
    public static BaseAdapter getDeviceAdapter(@NonNull Context appContext) {
        if (null != sBaseAdapter) {
            return sBaseAdapter;
        }

        if (RomUtils.isOppo()) {
            sBaseAdapter = new CameraUnitAdapter(appContext);
        } else {
            sBaseAdapter = new Camera2ApiAdapter(appContext);
        }

        return sBaseAdapter;
    }
}