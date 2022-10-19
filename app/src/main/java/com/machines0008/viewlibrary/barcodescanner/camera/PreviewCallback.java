package com.machines0008.viewlibrary.barcodescanner.camera;

import com.machines0008.viewlibrary.barcodescanner.SourceData;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage:
 **/
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
    void onPreviewError(Exception e);
}
