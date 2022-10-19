package com.machines0008.viewlibrary.barcodescanner.camera;

import android.graphics.Rect;

import com.machines0008.viewlibrary.barcodescanner.Size;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage:
 **/
public abstract class PreviewScalingStrategy {
    public Size getBestPreviewSize(List<Size> sizes) {
        return CollectionUtils.get(sizes, 0);
    }

    public abstract Rect scalePreview(Size previewSize, Size viewfinderSize);
}
