package com.machines0008.viewlibrary.camera_view;

import android.Manifest;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import androidx.annotation.RequiresPermission;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/1
 * Usage:
 **/
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private CameraRender cameraRender;

    @RequiresPermission(Manifest.permission.CAMERA)
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        cameraRender = new CameraRender(getContext());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        cameraRender.onSurfaceCreated(gl, config);
        cameraRender.getSurfaceTexture().setOnFrameAvailableListener((listener) -> requestRender());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        cameraRender.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        cameraRender.onDrawFrame(gl);
    }
    @Override
    public void onPause() {
        super.onPause();
        cameraRender.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}