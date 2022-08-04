package com.machines0008.viewlibrary.camera_view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.machines0008.viewlibrary.gl.utils.GLES20Utils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/1
 * Usage:
 **/
public class CameraRender implements GLSurfaceView.Renderer {
    private static final String vertexShaderCode = "" +
            "attribute vec4 vPosition;" +
            "attribute vec2 vCoordinate;" +
            "uniform mat4 vMatrix;" +
            "varying vec2 textureCoordinate;" +
            "void main() {" +
            "   gl_Position = vMatrix * vPosition;" +
            "   textureCoordinate = vCoordinate;" +
            "}";
    private static final String fragmentShaderCode = "" +
            "#extension GL_OES_EGL_image_external : require \r\n" +
            "precision mediump float;" +
            "varying vec2 textureCoordinate;" +
            "uniform samplerExternalOES vTexture;" +
            "void main() {" +
            "   gl_FragColor = texture2D(vTexture, textureCoordinate);" +
            "}";
    private static final float[] vertexPosition = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f, -1.0f
    };
    private static final float[] texturePosition = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    private final KitkatCamera kitkatCamera;
    private FloatBuffer fbVertex;
    private FloatBuffer fbTexture;
    private int cameraId = 0; //決定前後鏡頭
    private int[] texture;
    private int program;
    private SurfaceTexture surfaceTexture;
    private final float[] matrix = new float[16];
    private int dataWidth, dataHeight;
    private int width, height;

    @RequiresPermission(Manifest.permission.CAMERA)
    public CameraRender(@NonNull Context context) {
        this.kitkatCamera = new KitkatCamera(context);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        program = GLES20Utils.createGlProgram(vertexShaderCode, fragmentShaderCode);
        texture = GLES20Utils.createOESTexture();
        surfaceTexture = new SurfaceTexture(texture[0]);
        kitkatCamera.setPreviewTexture(surfaceTexture);
        kitkatCamera.open(cameraId);
        Size previewSize = kitkatCamera.getPreviewSize();
        dataWidth = previewSize.getWidth();
        dataHeight = previewSize.getHeight();
        surfaceTexture.setDefaultBufferSize(dataWidth, dataHeight);
        kitkatCamera.preview();
        fbVertex = GLES20Utils.genBuffer(vertexPosition);
        fbTexture = GLES20Utils.genBuffer(texturePosition);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        calculateMatrix();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (null != surfaceTexture) {
            surfaceTexture.updateTexImage();
        }
        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        int mHTexture = GLES20.glGetUniformLocation(program, "vTexture");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUniform1i(mHTexture, 0);

        int mHPosition = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition, 2, GLES20.GL_FLOAT, false, 0, fbVertex);

        int mHCoord = GLES20.glGetAttribLocation(program, "vCoordinate");
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, fbTexture);

        int mHMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        GLES20.glUniformMatrix4fv(mHMatrix, 1, false, matrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
        GLES20.glDeleteTextures(1, texture, 0);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    private void calculateMatrix() {
        float dataRatio = (float) dataHeight / dataWidth;
        float viewRatio = (float) width / height;
        float[] viewMatrix = new float[16];
        float[] projectionMatrix = new float[16];
        if (dataRatio > viewRatio) {
            Matrix.orthoM(projectionMatrix, 0, -viewRatio / dataRatio, viewRatio / dataRatio, -1, 1, 1, 3);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1, 1, -dataRatio / viewRatio, dataRatio / viewRatio, 1, 3);
        }
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(matrix, 0, projectionMatrix, 0, viewMatrix, 0);
        if (cameraId == 1) {
            Matrix.rotateM(matrix, 0, 90, 0, 0, 1);
        } else {
            Matrix.scaleM(matrix, 0, -1, 1, 1);
            Matrix.rotateM(matrix, 0, 270, 0, 0, 1);
        }
    }

    private void createTextureId() {
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
    }

    public void onResume() {
//        kitkatCamera.preview();
    }

    public void onPause() {
        kitkatCamera.close();
    }
}