package com.machines0008.viewlibrary.camera_view;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import java.util.Arrays;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/1
 * Usage:
 **/
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class KitkatCamera {
    private static final String TAG = KitkatCamera.class.getSimpleName();
    private HandlerThread cameraThread;
    private Handler cameraHandler;

    private SurfaceTexture previewTexture;
    private final CameraManager manager;
    private CameraDevice device;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder builder;
    private Size previewSize;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private ImageReader imageReader;
    private ImageReader.OnImageAvailableListener imageHandler;

    public KitkatCamera(@NonNull Context context) {
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            device = camera;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            device = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            String errorMsg = "";
            switch (error) {
                case CameraDevice.StateCallback.ERROR_CAMERA_IN_USE:
                    errorMsg = "CAMERA_IN_USE";
                    break;
                case CameraDevice.StateCallback.ERROR_MAX_CAMERAS_IN_USE:
                    errorMsg = "MAX_CAMERAS_IN_USE";
                    break;
                case CameraDevice.StateCallback.ERROR_CAMERA_DISABLED:
                    errorMsg = "CAMERA_DISABLED";
                    break;
                case CameraDevice.StateCallback.ERROR_CAMERA_DEVICE:
                    errorMsg = "CAMERA_DEVICE";
                    break;
                case CameraDevice.StateCallback.ERROR_CAMERA_SERVICE:
                    errorMsg = "CAMERA_SERVICE";
                    break;
            }
            Log.e(TAG, String.format("Error Code = %d, Message = %s", error, errorMsg));
        }
    };

    private final CameraCaptureSession.StateCallback sessionPreviewCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            cameraCaptureSession = session;
            try {
                cameraCaptureSession.setRepeatingRequest(builder.build(), captureCallback, cameraHandler);
            } catch (CameraAccessException | IllegalStateException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        }
    };

    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            cameraCaptureSession = session;
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            cameraCaptureSession = session;
            super.onCaptureCompleted(session, request, result);
        }
    };


    @RequiresPermission(Manifest.permission.CAMERA)
    public void open(int cameraId) {
        createCameraThread();
        try {
            manager.openCamera(String.valueOf(cameraId), stateCallback, cameraHandler); //打開相機
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(String.valueOf(cameraId));
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] size = map.getOutputSizes(SurfaceHolder.class);
            previewSize = size[0];
            imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YUV_420_888, 2);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public void preview() {
        try {
            createCameraCaptureSession();
        } catch (CameraAccessException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public void close() {
        if (null != device) {
            device.close();
            device = null;
        }
        if (null != cameraCaptureSession) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
        stopCameraThread();
    }

    public void setPreviewTexture(SurfaceTexture previewTexture) {
        this.previewTexture = previewTexture;
    }

    private void createCameraCaptureSession() throws CameraAccessException {
        if (null == previewTexture || null == device) {
            return;
        }
        builder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        Surface surface = new Surface(previewTexture);
        builder.addTarget(surface);
        device.createCaptureSession(Arrays.asList(surface), sessionPreviewCallback, cameraHandler);
    }

    private void createCameraThread() {
        if (cameraThread != null && cameraThread.isAlive()) {
            return;
        }
        cameraThread = new HandlerThread(TAG);
        cameraThread.start();
        cameraHandler = new Handler(cameraThread.getLooper());
    }

    private void stopCameraThread() {
        if (null != cameraThread && cameraThread.isAlive()) {
            cameraThread.quitSafely();
        }
        cameraHandler = null;
    }

    public Size getPreviewSize() {
        return previewSize;
    }
}
