package com.machines0008.viewlibrary.barcodescanner.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.io.IOException;

import lombok.Getter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/1
 * Usage:
 **/
@Getter
public class CameraSurface {

    private SurfaceHolder surfaceHolder;
    private SurfaceTexture surfaceTexture;

    public CameraSurface(@NonNull SurfaceHolder holder) {
        if (null == holder) {
            throw new IllegalArgumentException("SurfaceHolder must not be null.");
        }
        this.surfaceHolder = holder;
    }

    public CameraSurface(SurfaceTexture surfaceTexture) {
        if (null == surfaceTexture) {
            throw new IllegalArgumentException("SurfaceTexture must not be null.");
        }
        this.surfaceTexture = surfaceTexture;
    }

    public void setPreview(Camera camera) throws IOException {
        if (null != surfaceHolder) {
            camera.setPreviewDisplay(surfaceHolder);
        } else {
            camera.setPreviewTexture(surfaceTexture);
        }
    }
}