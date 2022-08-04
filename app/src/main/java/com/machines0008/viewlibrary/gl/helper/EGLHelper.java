package com.machines0008.viewlibrary.gl.helper;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.util.Log;


/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/2
 * Usage:
 **/
public class EGLHelper {
    private static final String TAG = EGLHelper.class.getSimpleName();

    private EGLConfig eglConfig;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface;
    private EGLContext eglContext;

    private EGLSurface eglCopySurface;
    private EGLContext shareEGLContext = EGL14.EGL_NO_CONTEXT;

    private int eglSurfaceType = EGL14.EGL_WINDOW_BIT;
    private Object surface;
    private Object copySurface;

    /**
     * {@link EGL14#EGL_WINDOW_BIT}
     * {@link EGL14#EGL_PBUFFER_BIT}
     * {@link EGL14#EGL_PIXMAP_BIT}
     *
     * @param type
     */
    public void setEGLSurfaceType(int type) {
        this.eglSurfaceType = type;
    }

    public void setSurface(Object surface) {
        this.surface = surface;
    }

    public void setCopySurface(Object surface) {
        this.copySurface = surface;
    }

    public boolean createGLES(int eglWidth, int eglHeight) {
        int[] attributes = new int[]{
                EGL14.EGL_SURFACE_TYPE, eglSurfaceType, //渲染類型
                EGL14.EGL_RED_SIZE, 8, //指定RGB中的R大小(bits)
                EGL14.EGL_GREEN_SIZE, 8, //指定RGB中的G大小
                EGL14.EGL_BLUE_SIZE, 8, //指定RGB中的B大小
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_DEPTH_SIZE, 16,
                EGL14.EGL_RENDERABLE_TYPE, 4, //指定渲染api類別
                EGL14.EGL_NONE
        };
        int glAttrs[] = {
                EGL14.EGL_CONTEXT_CLIENT_TYPE, 2,
                EGL14.EGL_NONE
        };
        int bufferAttrs[] = {
                EGL14.EGL_WIDTH, eglWidth,
                EGL14.EGL_HEIGHT, eglHeight,
                EGL14.EGL_NONE
        };
        // 獲取預設顯示設備，通常為主螢幕
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        // 獲取版本號碼 [0]:版本號 [1]:子版本號
        int[] versions = new int[2];
        EGL14.eglInitialize(eglDisplay, versions, 0, versions, 1);
        Log.i(TAG, EGL14.eglQueryString(eglDisplay, EGL14.EGL_VENDOR));
        Log.i(TAG, EGL14.eglQueryString(eglDisplay, EGL14.EGL_VERSION));
        Log.i(TAG, EGL14.eglQueryString(eglDisplay, EGL14.EGL_EXTENSIONS));
        // 獲取EGL可用配置
        EGLConfig[] configs = new EGLConfig[1];
        int[] configNum = new int[1];
        EGL14.eglChooseConfig(eglDisplay, attributes, 0, configs, 0, 1, configNum, 0);
        if (null == configs[0]) {
            Log.i(TAG, String.format("EGLChooseConfig Error : %d", EGL14.eglGetError()));
            return false;
        }
        eglConfig = configs[0];
        // 創建EGLContext
        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, shareEGLContext, glAttrs, 0);
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            return false;
        }
        // 獲取創建後台繪製的Surface
        switch (eglSurfaceType) {
            case EGL14.EGL_WINDOW_BIT:
                eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, new int[]{EGL14.EGL_NONE}, 0);
                break;
            case EGL14.EGL_PIXMAP_BIT:
                break;
            case EGL14.EGL_PBUFFER_BIT:
                eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, bufferAttrs, 0);
                break;
        }
        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            Log.i(TAG, String.format("EGLCreateSurface Error : %d", EGL14.eglGetError()));
            return false;
        }
        if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            Log.i(TAG, String.format("EGLMakeCurrent Error : %s", EGL14.eglQueryString(eglDisplay, EGL14.eglGetError())));
            return false;
        }
        Log.i(TAG, "GL Environment Create Successfully!");
        return true;
    }

    public EGLSurface createEGLWindowSurface(Object object) {
        return EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, object, new int[]{EGL14.EGL_NONE}, 0);
    }

    public void setShareEGLContext(EGLContext context) {
        this.shareEGLContext = context;
    }

    public EGLContext getEglContext() {
        return eglContext;
    }

    public boolean makeCurrent() {
        return EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
    }

    public boolean makeCurrent(EGLSurface surface) {
        return EGL14.eglMakeCurrent(eglDisplay, surface, surface, eglContext);
    }

    public void destroyGLES() {
        EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
        EGL14.eglDestroySurface(eglDisplay, eglSurface);
        EGL14.eglDestroyContext(eglDisplay, eglContext);
        EGL14.eglTerminate(eglDisplay);
        Log.i(TAG, "GL Destroy GLES!");
    }
}
