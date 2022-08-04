package com.machines0008.viewlibrary.video;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/4
 * Usage:
 **/
public class VideoView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private VideoRenderer renderer;
    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        renderer = new VideoRenderer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        renderer.onSurfaceCreated(gl, config);
        renderer.getSurfaceTexture().setOnFrameAvailableListener((listener) -> requestRender());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        renderer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        renderer.onDrawFrame(gl);
    }
}
