package com.machines0008.viewlibrary.barcodescanner.camera;

import android.hardware.Camera;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/2
 * Usage:
 **/
public interface CameraParametersCallback {

    Camera.Parameters changeCameraParameters(Camera.Parameters parameters);
}
