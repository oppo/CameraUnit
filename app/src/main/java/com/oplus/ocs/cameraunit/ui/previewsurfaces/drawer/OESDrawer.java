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
 * File: - OESDrawer.java
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

package com.oplus.ocs.cameraunit.ui.previewsurfaces.drawer;

import android.graphics.Rect;
import android.opengl.GLES11Ext;
import android.opengl.GLES32;

import com.oplus.ocs.cameraunit.ui.previewsurfaces.BaseDrawer;

import java.nio.FloatBuffer;

import static android.opengl.GLES32.GL_FLOAT;

public class OESDrawer extends BaseDrawer {
    private static final String FRAGMENT_SHADER =
            "#version 300 es                                              \n" +
            "#extension GL_OES_EGL_image_external_essl3 : require         \n" +
            "precision mediump float;                                     \n" +
            "uniform samplerExternalOES uTextureSampler;                  \n" +
            "in vec2 v_texture_coord;                                     \n" +
            "layout(location = 0) out vec4 out_color;                     \n" +
            "void main()                                                  \n" +
            "{                                                            \n" +
            "    out_color = texture(uTextureSampler, v_texture_coord);   \n" +
            "}                                                            \n";

    private int uTextureSamplerLocation = 0;

    public void init() {
        super.init();
        uTextureSamplerLocation = GLES32.glGetUniformLocation(mShaderProgram, "uTextureSampler");
        checkError();
    }

    @Override
    protected String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    @Override
    public void draw(int[] oesId, float[][] matrix, Rect[] areas, FloatBuffer[] vertexBuffer) {
        int stride = SIZE_OF_FLOAT * COORD_OF_EACH_VERTEX;

        GLES32.glUseProgram(mShaderProgram);
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0);

        GLES32.glViewport(areas[0].left, areas[0].top, areas[0].width(), areas[0].height() + areas[0].top);
        GLES32.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesId[0]);
        GLES32.glUniform1i(uTextureSamplerLocation, 0);
        GLES32.glUniformMatrix4fv(mvpMatrix, 1, false, matrix[0], 0);

        vertexBuffer[0].position(0);
        GLES32.glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, vertexBuffer[0]);

        vertexBuffer[0].position(2);
        GLES32.glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, vertexBuffer[0]);

        GLES32.glEnableVertexAttribArray(0);
        GLES32.glEnableVertexAttribArray(1);

        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, 0, NUMBER_OF_VERTEX);

        GLES32.glDisableVertexAttribArray(0);
        GLES32.glDisableVertexAttribArray(1);

        if (areas.length > 1) {
            GLES32.glViewport(areas[1].left, areas[1].top, areas[1].width(), areas[1].height() + areas[1].top);
            GLES32.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesId[1]);
            GLES32.glUniform1i(uTextureSamplerLocation, 0);
            GLES32.glUniformMatrix4fv(mvpMatrix, 1, false, matrix[1], 0);

            vertexBuffer[1].position(0);
            GLES32.glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, vertexBuffer[1]);

            vertexBuffer[1].position(2);
            GLES32.glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, vertexBuffer[1]);

            GLES32.glEnableVertexAttribArray(0);
            GLES32.glEnableVertexAttribArray(1);

            GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, 0, NUMBER_OF_VERTEX);

            GLES32.glDisableVertexAttribArray(0);
            GLES32.glDisableVertexAttribArray(1);
        }

        GLES32.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

        GLES32.glUseProgram(0);
    }
}
