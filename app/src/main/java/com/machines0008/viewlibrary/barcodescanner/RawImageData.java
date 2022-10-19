package com.machines0008.viewlibrary.barcodescanner;

import android.graphics.Rect;

import lombok.Getter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage:
 **/
@Getter
public class RawImageData {
    private byte[] data;
    private int width;
    private int height;

    public RawImageData(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public RawImageData cropAndScale(Rect cropRect, int scale) {
        int width = cropRect.width() / scale;
        int height = cropRect.height() / scale;
        int top = cropRect.top;
        int area = width * height;
        byte[] matrix = new byte[area];

        int inputOffset = top * this.width + cropRect.left;
        if (scale == 1) {
            for (int y = 0; y < height; y++) {
                int outputOffset = y * width;
                System.arraycopy(this.data, inputOffset, matrix, outputOffset, width);
                inputOffset += this.width;
            }
        } else {
            for (int y = 0; y < height; y++) {
                int outputOffset = y * width;
                int xOffset = inputOffset;
                for (int x = 0; x < width; x++) {
                    matrix[outputOffset] = this.data[xOffset];
                    xOffset += scale;
                    outputOffset += 1;
                }
                inputOffset += this.width * scale;
            }
        }
        return new RawImageData(matrix, width, height);
    }

    public RawImageData rotateCameraPreview(int cameraRotation) {
        switch (cameraRotation) {
            case 90:
                return new RawImageData(rotateCW(data, width, height), height, width);
            case 180:
                return new RawImageData(rotate180(data, width, height), width, height);
            case 270:
                return new RawImageData(rotateCCW(data, width, height), height, width);
            default:
                return this;
        }
    }

    public static byte[] rotateCW(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageHeight * imageWidth];
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        return yuv;
    }

    public static byte[] rotate180(byte[] data, int imageWidth, int imageHeight) {
        int n = imageWidth * imageHeight;
        byte[] yuv = new byte[n];
        int i = n - 1;
        for (int j = 0; j < n; j++) {
            yuv[i] = data[j];
            i--;
        }
        return yuv;
    }

    public static byte[] rotateCCW(byte[] data, int imageWidth, int imageHeight) {
        int n = imageWidth * imageHeight;
        byte[] yuv = new byte[n];
        int i = n - 1;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i--;
            }
        }
        return yuv;
    }
}