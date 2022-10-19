package com.machines0008.viewlibrary.barcodescanner.camera;


import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.machines0008.viewlibrary.barcodescanner.Size;
import com.machines0008.viewlibrary.barcodescanner.SourceData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Setter;


/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage: 因應許多手機無法適用Camera2 API ex.Vivo手機、Oppo舊版手機、紅米機等，故還是需要以Camera類別為主
 **/
@SuppressWarnings("deprecation")
public final class CameraManager {
    private static final String TAG = CameraManager.class.getSimpleName();
    private Camera camera;
    private Camera.CameraInfo cameraInfo;

    private AutoFocusManager autoFocusManager;
    private AmbientLightManager ambientLightManager;

    private boolean previewing;
    private String defaultParameters;

    private CameraSettings settings = new CameraSettings();
    private DisplayConfiguration displayConfiguration;

    private Size requestedPreviewSize;
    private Size previewSize;

    private int rotationDegrees = -1;

    private Context context;


    private final class CameraPreviewCallback implements Camera.PreviewCallback {
        @Setter
        private PreviewCallback callback;
        @Setter
        private Size resolution; //解析度

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Size cameraResolution = this.resolution;
            if (callback == null) {
                return;
            }
            if (cameraResolution == null) {
                callback.onPreviewError(new Exception("No resolution available"));
                return;
            }
            if (null == data) {
                callback.onPreviewError(new NullPointerException("No preview data received"));
                return;
            }
            try {
                int format = camera.getParameters().getPreviewFormat();
                SourceData source = new SourceData(data, cameraResolution.width, cameraResolution.height, format, getCameraRotation());
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    source.setPreviewMirrored(true);
                }
                callback.onPreview(source);
            } catch (RuntimeException e) {
                callback.onPreviewError(e);
            }
        }
    }

    private final CameraPreviewCallback cameraPreviewCallback;

    public CameraManager(Context context) {
        this.context = context;
        cameraPreviewCallback = new CameraPreviewCallback();
    }

    public void open() {
        camera = OpenCameraInterface.open(settings.getRequestedCameraId());
        if (null == camera) {
            throw new RuntimeException("Failed to open camera");
        }
        int cameraId = OpenCameraInterface.getCameraId(settings.getRequestedCameraId());
        cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
    }

    public void configure() {
        if (null == camera) {
            throw new RuntimeException("Camera not open");
        }
        setParameters();
    }

    public void setPreviewDisplay(SurfaceHolder holder) throws IOException {
        setPreviewDisplay(new CameraSurface(holder));
    }

    public void setPreviewDisplay(CameraSurface cameraSurface) throws IOException {
        cameraSurface.setPreview(camera);
    }

    public void setCameraSettings(CameraSettings settings) {
        this.settings = settings;
    }

    public void startPreview() {
        Camera theCamera = camera;
        if (null == theCamera) {
            return;
        }
        if (previewing) {
            return;
        }
        theCamera.startPreview();
        previewing = true;
        autoFocusManager = new AutoFocusManager(camera, settings);
        ambientLightManager = new AmbientLightManager(context, this, settings);
        ambientLightManager.start();
    }

    public void stopPreview() {
        if (autoFocusManager != null) {
            autoFocusManager.stop();
            autoFocusManager = null;
        }
        if (ambientLightManager != null) {
            ambientLightManager.stop();
            ambientLightManager = null;
        }
        if (null != camera && previewing) {
            camera.stopPreview();
            cameraPreviewCallback.setCallback(null);
            previewing = false;
        }
    }

    public void close() {
        if (null == camera) {
            return;
        }
        camera.release();
        camera = null;
    }

    public boolean isCameraRotated() {
        if (rotationDegrees == -1) {
            throw new IllegalStateException("Rotation not calculated yet. Call configure() first.");
        }
        return rotationDegrees % 180 != 0;
    }

    public int getCameraRotation() {
        return rotationDegrees;
    }

    private Camera.Parameters getDefaultCameraParameters() {
        Camera.Parameters parameters = camera.getParameters();
        if (null == defaultParameters) {
            defaultParameters = parameters.flatten();
        } else {
            parameters.unflatten(defaultParameters);
        }
        return parameters;
    }

    private void setDesiredParameters(boolean safeMode) {
        Camera.Parameters parameters = getDefaultCameraParameters();

        if (null == parameters) {
            Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }

        Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

        if (safeMode) {
            Log.w(TAG, "In camera config safe mode -- most settings will not be honored.");
        }
        CameraConfigurationUtils.setFocus(parameters, settings.getFocusMode(), safeMode);

        if (!safeMode) {
            CameraConfigurationUtils.setTorch(parameters, false);

            if (settings.isScanInverted()) {
                CameraConfigurationUtils.setInvertColor(parameters);
            }

            if (settings.isBarcodeSceneModeEnabled()) {
                CameraConfigurationUtils.setBarcodeSceneMode(parameters);
            }

            if (settings.isMeteringEnabled()) {
                CameraConfigurationUtils.setVideoStabilization(parameters);
                CameraConfigurationUtils.setFocusArea(parameters);
                CameraConfigurationUtils.setMetering(parameters);
            }
        }

        List<Size> previewSizes = getPreviewSize(parameters);
        if (previewSizes.size() == 0) {
            requestedPreviewSize = null;
        } else {
            requestedPreviewSize = displayConfiguration.getBestPreviewSize(previewSizes, isCameraRotated());
            parameters.setPreviewSize(requestedPreviewSize.width, requestedPreviewSize.height);
        }

        if (Build.DEVICE.equals("glass-1")) {
            CameraConfigurationUtils.setBestPreviewFPS(parameters);
        }

        Log.i(TAG, "Final camera parameters: " + parameters.flatten());

        camera.setParameters(parameters);
    }

    private static List<Size> getPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        List<Size> previewSizes = new ArrayList<>();
        if (rawSupportedSizes == null) {
            Camera.Size defaultSize = parameters.getPreviewSize();
            if (null != defaultSize) {
                Size previewSize = new Size(defaultSize.width, defaultSize.height);
                previewSizes.add(previewSize);
            }
            return previewSizes;
        }
        for (Camera.Size size : rawSupportedSizes) {
            previewSizes.add(new Size(size.width, size.height));
        }
        return previewSizes;
    }

    private void setParameters() {
        try {
            this.rotationDegrees = calculateDisplayRotation();
            setCameraDisplayOrientation(rotationDegrees);
        } catch (Exception e) {
            Log.w(TAG, "Failed to set rotation.");
        }
        try {
            setDesiredParameters(false);
        } catch (Exception e) {
            try {
                setDesiredParameters(true);
            } catch (Exception e2) {
                Log.w(TAG, "Camera rejected even safe-mode parameters! No configuration.");
            }
        }
        Camera.Size realPreviewSize = camera.getParameters().getPreviewSize();
        previewSize = realPreviewSize == null ? requestedPreviewSize : new Size(realPreviewSize.width, realPreviewSize.height);
        cameraPreviewCallback.setResolution(previewSize);
    }

    private int calculateDisplayRotation() {
        int rotation = displayConfiguration.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private void setCameraDisplayOrientation(int rotation) {
        camera.setDisplayOrientation(rotation);
    }

    public boolean isOpen() {
        return null != camera;
    }

    public Size getNaturalPreviewSize() {
        return previewSize;
    }

    public Size getPreviewSize() {
        if (null == previewSize) {
            return null;
        }
        if (isCameraRotated()) {
            return previewSize.rotate();
        }
        return previewSize;
    }

    public void requestPreviewFrame(PreviewCallback callback) {
        Camera theCamera = camera;
        if (null == theCamera || !previewing) {
            return;
        }
        cameraPreviewCallback.setCallback(callback);
        theCamera.setOneShotPreviewCallback(cameraPreviewCallback);
    }

    public CameraSettings getCameraSettings() {
        return settings;
    }

    public DisplayConfiguration getDisplayConfiguration() {
        return displayConfiguration;
    }

    public void setDisplayConfiguration(DisplayConfiguration displayConfiguration) {
        this.displayConfiguration = displayConfiguration;
    }

    public void setTorch(boolean on) {
        if (null == camera) {
            return;
        }
        boolean isOn = isTorchOn();
        if (on == isOn) {
            return;
        }
        try {
            if (null != autoFocusManager) {
                autoFocusManager.stop();
            }
            Camera.Parameters parameters = camera.getParameters();
            CameraConfigurationUtils.setTorch(parameters, on);
            if (settings.isExposureEnabled()) {
                CameraConfigurationUtils.setBestExposure(parameters, on);
            }
            camera.setParameters(parameters);
            if (null != autoFocusManager) {
                autoFocusManager.start();
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to set torch", e);
        }
    }

    public boolean isTorchOn() {
        Camera.Parameters parameters = camera.getParameters();
        if (null == parameters) {
            return false;
        }
        String flashMode = parameters.getFlashMode();
        return flashMode != null && (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) || Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode));
    }

    public void changeCameraParameters(CameraParametersCallback callback) {
        if (null == camera) {
            return;
        }
        try {
            camera.setParameters(callback.changeCameraParameters(camera.getParameters()));
        } catch (RuntimeException e) {
            Log.e(TAG, "Failed to change camera parameters", e);
        }
    }

    public Camera getCamera() {
        return camera;
    }
}