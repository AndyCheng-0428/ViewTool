package com.machines0008.viewlibrary.barcodescanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ResultPoint;

import java.io.ByteArrayOutputStream;

import lombok.Getter;
import lombok.Setter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage:
 **/
@Getter
@Setter
public class SourceData {
    private RawImageData data;
    private int imageFormat;
    private int rotation;
    private Rect cropRect;
    private int scalingFactor = 1;
    private boolean previewMirrored;

    public SourceData(byte[] data, int dataWidth, int dataHeight, int imageFormat, int rotation) {
        this.data = new RawImageData(data, dataWidth, dataHeight);
        this.rotation = rotation;
        this.imageFormat = imageFormat;
        if (dataWidth * dataHeight > data.length) {
            throw new IllegalArgumentException("Image data does not match the resolution. " + dataWidth + "x" + dataHeight + ">" + data.length);
        }
    }

    public byte[] getData() {
        return data.getData();
    }

    public int getDataWidth() {
        return data.getWidth();
    }

    public int getDataHeight() {
        return data.getHeight();
    }

    public ResultPoint translateResultPoint(ResultPoint point) {
        float x = point.getX() * scalingFactor + cropRect.left;
        float y = point.getY() * scalingFactor + cropRect.top;
        if (previewMirrored) {
            x = data.getWidth() - x;
        }
        return new ResultPoint(x, y);
    }

    public boolean isRotated() {
        return rotation % 180 != 0;
    }

    public PlanarYUVLuminanceSource createSource() {
        RawImageData rotated = data.rotateCameraPreview(rotation);
        RawImageData scaled = rotated.cropAndScale(cropRect, scalingFactor);
        return new PlanarYUVLuminanceSource(scaled.getData(), scaled.getWidth(), scaled.getHeight(), 0, 0, scaled.getWidth(), scaled.getHeight(), false);
    }

    public Bitmap getBitmap() {
        return getBitmap(1);
    }

    private Bitmap getBitmap(int scaleFactor) {
        return getBitmap(cropRect, scaleFactor);
    }

    public Bitmap getBitmap(Rect cropRect, int scaleFactor) {
        if (null == cropRect) {
            cropRect = new Rect(0, 0, data.getWidth(), data.getHeight());
        } else if (isRotated()) {
            cropRect = new Rect(cropRect.top, cropRect.left, cropRect.bottom, cropRect.right);
        }
        YuvImage img = new YuvImage(data.getData(), imageFormat, data.getWidth(), data.getHeight(), null);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        img.compressToJpeg(cropRect, 90, buffer);
        byte[] jpegData = buffer.toByteArray();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
        if (rotation != 0) {
            Matrix imageMatrix = new Matrix();
            imageMatrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), imageMatrix, false);
        }
        return bitmap;
    }
}