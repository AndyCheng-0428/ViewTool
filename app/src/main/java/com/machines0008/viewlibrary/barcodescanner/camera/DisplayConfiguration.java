package com.machines0008.viewlibrary.barcodescanner.camera;



import android.graphics.Rect;

import com.machines0008.viewlibrary.barcodescanner.Size;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage:
 **/
public class DisplayConfiguration {
    private Size viewfinderSize;
    @Getter
    private int rotation;
    private boolean center = false;
    @Setter
    @Getter
    private PreviewScalingStrategy previewScalingStrategy = new FitXYStrategy();

    public DisplayConfiguration(int rotation) {
        this.rotation = rotation;
    }

    public DisplayConfiguration(int rotation, Size viewfinderSize) {
        this.rotation = rotation;
        this.viewfinderSize = viewfinderSize;
    }

    public Size getDesiredPreviewSize(boolean rotate) {
        if (null == viewfinderSize) {
            return null;
        }
        if (rotate) {
            return viewfinderSize.rotate();
        }
        return viewfinderSize;
    }

    public Size getBestPreviewSize(List<Size> sizes, boolean isRotated) {
        return previewScalingStrategy.getBestPreviewSize(sizes);
    }

    public Rect scalePreview(Size previewSize) {
        return previewScalingStrategy.scalePreview(previewSize, viewfinderSize);
    }
}