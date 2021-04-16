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
 * File: - YuvDrawer.java
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
import android.opengl.GLES32;

import com.oplus.ocs.cameraunit.ui.previewsurfaces.BaseDrawer;

import java.nio.FloatBuffer;

import static android.opengl.GLES32.GL_FLOAT;

public class YuvDrawer extends BaseDrawer {
    private static final String TAG = "YuvDrawer";

    private static final String FRAGMENT_SHADER =
            "#version 300 es                                              \n" +
            "precision mediump float;                                     \n" +
            "in vec2 v_texture_coord;                                     \n" +
            "uniform sampler2D y_texture;                                 \n" +
            "uniform sampler2D uv_texture;                                \n" +
            "layout(location = 0) out vec4 out_color;                     \n" +
            "void main()                                                  \n" +
            "{                                                            \n" +
            "    vec3 yuv;                                                \n" +
            "    yuv.x = texture(y_texture, v_texture_coord).r;           \n" +
            "    yuv.y = texture(uv_texture, v_texture_coord).a - 0.5;    \n" +
            "    yuv.z = texture(uv_texture, v_texture_coord).r - 0.5;    \n" +
            "    highp vec3 rgb = mat3(1.0,    1.0,   1.0,                \n" +
            "                          0.0, -0.344, 1.770,                \n" +
            "                        1.403, -0.714,   0.0) * yuv;         \n" +
            "    out_color = vec4(rgb, 1);                                \n" +
            "}                                                            \n";

    private int yTextureLocation = 0;
    private int uvTextureLocation = 0;

    @Override
    protected String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    public void init() {
        super.init();
        yTextureLocation = GLES32.glGetUniformLocation(mShaderProgram, "y_texture");BaseDrawer.checkError();
        uvTextureLocation = GLES32.glGetUniformLocation(mShaderProgram, "uv_texture");BaseDrawer.checkError();
    }

    @Override
    public void draw(int[] texId, float[][] matrix, Rect[] areas, FloatBuffer[] vertexBuffer) {
        GLES32.glUseProgram(mShaderProgram);
        int stride = SIZE_OF_FLOAT * COORD_OF_EACH_VERTEX;

        GLES32.glViewport(areas[0].left, areas[0].top, areas[0].width(), areas[0].height() + areas[0].top);
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texId[0]);
        GLES32.glUniform1i(yTextureLocation, 0);

        GLES32.glActiveTexture(GLES32.GL_TEXTURE1);
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texId[1]);
        GLES32.glUniform1i(uvTextureLocation, 1);

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
            GLES32.glActiveTexture(GLES32.GL_TEXTURE2);
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texId[2]);
            GLES32.glUniform1i(yTextureLocation, 2);

            GLES32.glActiveTexture(GLES32.GL_TEXTURE3);
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texId[3]);
            GLES32.glUniform1i(uvTextureLocation, 3);

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

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0);
        GLES32.glUseProgram(0);
    }
}
