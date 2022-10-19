package com.machines0008.viewlibrary.barcodescanner.camera;

import static com.machines0008.viewlibrary.barcodescanner.Utils.validateMainThread;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import com.machines0008.viewlibrary.barcodescanner.Size;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/5
 * Usage:
 **/
public class CameraInstance {
    private static final String TAG = CameraInstance.class.getSimpleName();

    private CameraThread cameraThread;
    private CameraSurface surface;
    private CameraManager cameraManager;
    private Handler readyHandler;
    private DisplayConfiguration displayConfiguration;
    private boolean open = false;
    private boolean cameraClosed = true;
    private Handler mainHandler;
    private CameraSettings cameraSettings = new CameraSettings();

    public CameraInstance(Context context) {
        validateMainThread();
        cameraThread = CameraThread.getInstance();
        cameraManager = new CameraManager(context);
        cameraManager.setCameraSettings(cameraSettings);
        this.mainHandler = new Handler();
    }

    public CameraInstance(CameraManager cameraManager) {
        validateMainThread();
        this.cameraManager = cameraManager;
    }

    public void setDisplayConfiguration(DisplayConfiguration configuration) {
        this.displayConfiguration = configuration;
        cameraManager.setDisplayConfiguration(configuration);
    }

    public DisplayConfiguration getDisplayConfiguration() {
        return displayConfiguration;
    }

    public void setReadyHandler(Handler readyHandler) {
        this.readyHandler = readyHandler;
    }

    public void setSurfaceHolder(SurfaceHolder holder) {
        setSurface(new CameraSurface(holder));
    }

    public void setSurface(CameraSurface cameraSurface) {
        this.surface = cameraSurface;
    }

    public CameraSettings getCameraSettings() {
        return cameraSettings;
    }

    public void setCameraSettings(CameraSettings cameraSettings) {
        if (open) {
            return;
        }
        this.cameraSettings = cameraSettings;
        this.cameraManager.setCameraSettings(cameraSettings);
    }

    public void requestPreview(final PreviewCallback callback) {
        mainHandler.post(()-> {
            if (!open) {
                Log.d(TAG, "Camera is closed, not requesting preview");
                return;
            }
            cameraThread.enqueue(()-> cameraManager.requestPreviewFrame(callback));
        });
    }

    private Size getPreviewSize() {
        return cameraManager.getPreviewSize();
    }

    public int getCameraRotation() {
        return cameraManager.getCameraRotation();
    }

    public void open() {
        validateMainThread();
        open = true;
        cameraClosed = false;
        cameraThread.incrementAndEnqueue(opener);
    }

    public void configureCamera() {
        validateMainThread();
        validateOpen();
        cameraThread.incrementAndEnqueue(configure);
    }

    public void startPreview() {
        validateMainThread();
        validateOpen();
        cameraThread.enqueue(previewStarter);
    }

    public void setTorch(final boolean on) {
        validateMainThread();
        if (!open) {
            return;
        }
        cameraThread.enqueue(() -> cameraManager.setTorch(on));
    }

    public void changeCameraParameters(final CameraParametersCallback callback) {
        validateMainThread();
        if (!open) {
            return;
        }
        cameraThread.enqueue(() -> cameraManager.changeCameraParameters(callback));
    }

    public void close() {
        validateMainThread();
        if (open) {
            cameraThread.enqueue(closer);
        } else {
            cameraClosed = true;
        }
        open = false;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isCameraClosed() {
        return cameraClosed;
    }

    private Runnable opener = () -> {
        try {
            Log.d(TAG, "Opening camera");
            cameraManager.open();
        } catch (Exception e) {
            Log.e(TAG, "Failed to open camera", e);
            notifyError(e);
        }
    };

    private Runnable configure = () -> {
        try {
            Log.d(TAG, "Configuring camera");
            cameraManager.configure();
            if (null == readyHandler) {
                return;
            }
            readyHandler.obtainMessage(MSG_READY, getPreviewSize()).sendToTarget();
        } catch (Exception e) {
            Log.e(TAG, "Failed to configure camera", e);
            notifyError(e);
        }
    };

    private Runnable previewStarter = () -> {
        try {
            Log.d(TAG, "Starting preview");
            cameraManager.setPreviewDisplay(surface);
            cameraManager.startPreview();
        } catch (Exception e) {
            notifyError(e);
            Log.e(TAG, "Failed to start preview", e);

        }
    };
    private Runnable closer = () -> {
        try {
            Log.d(TAG, "Closing camera");
            cameraManager.stopPreview();
            cameraManager.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to close camera", e);
        }
        cameraClosed = true;
        readyHandler.sendEmptyMessage(MSG_CLOSE);
        cameraThread.decrementInstances();
    };

    private void validateOpen() {
        if (open) {
            return;
        }
        throw new IllegalStateException("CameraInstance is not open");
    }

    private void notifyError(Exception error) {
        if (readyHandler == null) {
            return;
        }
        readyHandler.obtainMessage(MSG_ERROR, error).sendToTarget();
    }

    protected CameraManager getCameraManager() {
        return cameraManager;
    }

    protected CameraThread getCameraThread() {
        return cameraThread;
    }

    protected CameraSurface getSurface() {
        return surface;
    }

    @IntDef(value = {MSG_READY, MSG_ERROR, MSG_CLOSE})
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Retention(RetentionPolicy.SOURCE)
    public @interface CameraMessage {

    }

    public static final int MSG_READY = 0;
    public static final int MSG_ERROR = 1;
    public static final int MSG_CLOSE = 2;
}