package com.machines0008.viewlibrary.barcodescanner;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/7
 * Usage:
 **/
public class Decoder implements ResultPointCallback {
    private final List<ResultPoint> possibleResultPoints = new ArrayList<>();
    private final Reader reader;

    public Decoder(Reader reader) {
        this.reader = reader;
    }

    protected Reader getReader() {
        return reader;
    }

    public Result decode(LuminanceSource source) {
        return decode(toBitmap(source));
    }

    private BinaryBitmap toBitmap(LuminanceSource source) {
        return new BinaryBitmap(new HybridBinarizer(source));
    }

    private Result decode(BinaryBitmap bitmap) {
        possibleResultPoints.clear();
        try {
            if (reader instanceof MultiFormatReader) {
                return ((MultiFormatReader) reader).decodeWithState(bitmap);
            } else {
                return reader.decode(bitmap);
            }
        } catch (Exception e) {
            return null;
        } finally {
            reader.reset();
        }
    }

    public List<ResultPoint> getPossibleResultPoints() {
        return new ArrayList<>(possibleResultPoints);
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }
}