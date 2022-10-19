package com.machines0008.viewlibrary.barcodescanner.camera;

import android.graphics.Rect;

import com.machines0008.viewlibrary.barcodescanner.Size;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/6
 * Usage:
 **/
public class FitXYStrategy extends PreviewScalingStrategy {

    @Override
    public Rect scalePreview(Size previewSize, Size viewfinderSize) {
        return new Rect(0, 0, viewfinderSize.width, viewfinderSize.height);
    }
}
