package com.machines0008.viewlibrary.barcodescanner.camera;

import android.hardware.Camera;
import android.util.Log;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/1
 * Usage:
 **/
public final class OpenCameraInterface {
    private static final String TAG = OpenCameraInterface.class.getSimpleName();

    private OpenCameraInterface() {

    }

    public static final int NO_REQUESTED_CAMERA = -1;

    public static int getCameraId(int requestedId) {
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            Log.w(TAG, "No cameras!");
            return -1;
        }

        int cameraId = requestedId;

        boolean explicitRequest = cameraId >= 0;

        if (!explicitRequest) {
            int index = 0;
            while (index < numCameras) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(index, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    break;
                }
                index++;
            }
            cameraId = index;
        }
        if (cameraId < numCameras) {
            return cameraId;
        }
        return explicitRequest ? -1 : 0;
    }

    public static Camera open(int requestedId) {
        int cameraId = getCameraId(requestedId);
        return cameraId == -1 ? null : Camera.open(cameraId);
    }
}
