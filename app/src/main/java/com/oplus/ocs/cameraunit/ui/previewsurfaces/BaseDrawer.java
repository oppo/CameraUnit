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
 * File: - BaseDrawer.java
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

package com.oplus.ocs.cameraunit.ui.previewsurfaces;

import android.graphics.Rect;
import android.opengl.GLES32;
import android.util.Log;

import androidx.annotation.CallSuper;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;

public abstract class BaseDrawer {
    protected int mShaderProgram = 0;
    protected int mvpMatrix = 0;
    protected static final int COORD_OF_EACH_VERTEX = 4;
    protected static final int NUMBER_OF_VERTEX = 4;
    protected static final int SIZE_OF_FLOAT = 4;

    private static final String VERTEX_SHADER =
            "#version 300 es                                              \n" +
            "uniform mat4 mvp_matrix;                                     \n" +
            "layout(location = 0) in vec4 a_position;                     \n" +
            "layout(location = 1) in vec4 a_texture_coord;                \n" +
            "out vec2 v_texture_coord;                                    \n" +
            "void main()                                                  \n" +
            "{                                                            \n" +
            "    gl_Position = a_position;                                \n" +
            "    v_texture_coord = (mvp_matrix * a_texture_coord).xy;     \n" +
            "}                                                            \n";

    private static final String TAG = "BaseDrawer";

    public static void checkError() {
        int error = GLES32.glGetError();

        if (error != 0) {
            Throwable t = new Throwable();

            Log.e(TAG, "checkGlError, error: " + error, t);
        }
    }

    protected int loadShader(int type, String shaderSource) {
        int shader = GLES32.glCreateShader(type);

        if (shader == 0) {
            throw new RuntimeException("loadShader Failed!" + GLES32.glGetError());
        }

        GLES32.glShaderSource(shader, shaderSource);
        GLES32.glCompileShader(shader);
        return shader;
    }

    protected int linkProgram(int verShader, int fragShader) {
        int program = GLES32.glCreateProgram();

        if (0 == program) {
            throw new RuntimeException("linkProgram Failed!" + GLES32.glGetError());
        }

        GLES32.glAttachShader(program, verShader);
        GLES32.glAttachShader(program, fragShader);
        GLES32.glLinkProgram(program);

        checkError();
        return program;
    }

    @CallSuper
    public void init() {
        int vertexShader = loadShader(GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GL_FRAGMENT_SHADER, getFragmentShader());
        mShaderProgram = linkProgram(vertexShader, fragmentShader);
        checkError();
        mvpMatrix = GLES32.glGetUniformLocation(mShaderProgram, "mvp_matrix");
        checkError();
    }

    protected abstract String getFragmentShader();

    public abstract void draw(int[] textures, float[][] matrix, Rect[] areas, FloatBuffer[] vertexes);
}
