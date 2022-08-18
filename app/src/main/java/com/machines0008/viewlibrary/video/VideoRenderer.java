package com.machines0008.viewlibrary.video;

import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.Surface;

import com.machines0008.viewlibrary.gl.utils.GLES20Utils;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/4
 * Usage:
 **/
public class VideoRenderer implements GLSurfaceView.Renderer, MediaPlayer.OnVideoSizeChangedListener {
    private static final String vertexShaderCode = "" +
            "attribute vec4 vPosition;" +
            "attribute vec2 vCoordinate;" +
            "varying vec2 textureCoordinate;" +
            "uniform mat4 vMatrix;" +
            "void main() {" +
            "   textureCoordinate = vCoordinate;" +
            "   gl_Position = vMatrix * vPosition;" +
            "}";
    private static final String fragmentShaderCode = "" +
            "#extension GL_OES_EGL_image_external : require \r\n" +
            "precision mediump float;" +
            "varying vec2 textureCoordinate;" +
            "uniform samplerExternalOES vTexture;" +
            "void main() {" +
            "   gl_FragColor = texture2D(vTexture, textureCoordinate);" +
            "}";

    private FloatBuffer fbVertex;
    private FloatBuffer fbTexture;
    private final float[] vertexPosition = {
            -1.0f, 1.0f,
            1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f
    };
    private final float[] texturePosition = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    private int screenWidth;
    private int screenHeight;
    private final float[] matrix = new float[16];
    private int[] texture;
    private int program;
    private MediaPlayer mediaPlayer;
    private VideoBean videoBean = new VideoBean();
    private SurfaceTexture surfaceTexture;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        program = GLES20Utils.createGlProgram(vertexShaderCode, fragmentShaderCode);
        texture = GLES20Utils.createOESTexture();
        fbVertex = GLES20Utils.genBuffer(vertexPosition);
        fbTexture = GLES20Utils.genBuffer(texturePosition);
        surfaceTexture = new SurfaceTexture(texture[0]);
        try {
            initMediaPlayer(new Surface(surfaceTexture));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    private void initMediaPlayer(Surface surface) throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(videoBean.getUrl());
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
        mediaPlayer.setSurface(surface);
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        if (null != surfaceTexture) {
            surfaceTexture.updateTexImage();
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        int mHTexture = GLES20.glGetUniformLocation(program, "vTexture");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
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

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        updateProjection(width, height);
    }

    private void updateProjection(int videoWidth, int videoHeight) {
        float screenRatio = (float) screenWidth / screenHeight;
        float videoRatio = (float) videoWidth / videoHeight;
        if (videoRatio > screenRatio) {
            Matrix.orthoM(matrix, 0, -1f, 1f, -videoRatio / screenRatio, videoRatio / screenRatio, -1f, 1f);
        } else {
            Matrix.orthoM(matrix, 0, -screenRatio / videoRatio, screenRatio / videoRatio, -1f, 1f, -1f, 1f);
        }
        Matrix.rotateM(matrix, 0, 180, 0, 0, 1); //上下顛倒
        Matrix.scaleM(matrix, 0, -1, 1, 1); //水平反轉
    }
}
