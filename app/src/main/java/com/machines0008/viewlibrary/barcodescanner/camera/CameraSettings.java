package com.machines0008.viewlibrary.barcodescanner.camera;

import lombok.Data;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage:
 **/
@Data
public class CameraSettings {
    private int requestedCameraId = OpenCameraInterface.NO_REQUESTED_CAMERA;
    private boolean scanInverted = false;
    private boolean barcodeSceneModeEnabled = false;
    private boolean meteringEnabled = false;
    private boolean autoFocusEnabled = true;
    private boolean continuousFocusEnabled = false;
    private boolean exposureEnabled = false;
    private boolean autoTorchEnabled = false;
    private FocusMode focusMode = FocusMode.AUTO;

    public enum FocusMode {
        AUTO,
        CONTINUOUS,
        INFINITY,
        MACRO
    }

    public void setAutoFocusEnabled(boolean autoFocusEnabled) {
        this.autoFocusEnabled = autoFocusEnabled;
        if (autoFocusEnabled && continuousFocusEnabled) {
            focusMode = FocusMode.CONTINUOUS;
        } else if (autoFocusEnabled) {
            focusMode = FocusMode.AUTO;
        } else {
            focusMode = null;
        }
    }
}
