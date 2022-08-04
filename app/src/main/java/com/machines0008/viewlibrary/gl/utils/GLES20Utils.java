package com.machines0008.viewlibrary.gl.utils;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/2
 * Usage:
 **/
public class GLES20Utils {
    private GLES20Utils() {

    }

    public static int genShader(int createShader, String shaderCode) {
        int shader = GLES20.glCreateShader(createShader);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static int createGlProgram(String vertexSourceCode, String fragmentSourceCode) {
        int vs = GLES20Utils.genShader(GLES20.GL_VERTEX_SHADER, vertexSourceCode);
        int fs = GLES20Utils.genShader(GLES20.GL_FRAGMENT_SHADER, fragmentSourceCode);
        return linkProgramShader(vs, fs);
    }

    public static int linkProgramShader(int vs, int fs) {
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vs);
        GLES20.glAttachShader(program, fs);
        GLES20.glLinkProgram(program);
        GLES20.glDeleteShader(vs);
        GLES20.glDeleteShader(fs);
        GLES20.glUseProgram(program);
        return program;
    }

    public static FloatBuffer genBuffer(@NonNull float[] source) {
        ByteBuffer bb = ByteBuffer.allocateDirect(source.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(source);
        fb.position(0);
        return fb;
    }

    public static int[] createOESTexture() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        return texture;
    }
}