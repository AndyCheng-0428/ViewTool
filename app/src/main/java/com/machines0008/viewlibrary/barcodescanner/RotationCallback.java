package com.machines0008.viewlibrary.barcodescanner;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/5
 * Usage:
 **/
public interface RotationCallback {
    /**
     * Rotation changed.
     * @param rotation the current value of windowManager.getDefaultDisplay().getRotation()
     */
    void onRotationChanged(int rotation);
}
