package com.machines0008.viewlibrary.barcodescanner;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/7
 * Usage:
 **/
public class BarcodeResult {
    private static final float PREVIEW_LINE_WIDTH = 4.0f;
    private final float PREVIEW_DOT_WIDTH = 10.0f;

    protected Result result;
    protected SourceData sourceData;
    private final int scaleFactor = 2;

    public BarcodeResult(Result result, SourceData sourceData) {
        this.result = result;
        this.sourceData = sourceData;
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, int scaleFactor) {
        if (null == a || null == b) {
            return;
        }
        canvas.drawLine(a.getX() / scaleFactor,
                a.getY() / scaleFactor,
                b.getX() / scaleFactor,
                b.getY() / scaleFactor,
                paint);
    }

    public Bitmap getBitmapWithResultPoints(int color) {
        Bitmap bitmap = getBitmap();
        Bitmap barcode = bitmap;
        List<ResultPoint> points = getTransformedResultPoints();
        if (points.isEmpty() || null == bitmap) {
            return barcode;
        }
        barcode = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(barcode);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(color);
        if (points.size() == 2) {
            paint.setStrokeWidth(PREVIEW_LINE_WIDTH);
            drawLine(canvas, paint, points.get(0), points.get(1), scaleFactor);
        } else if (points.size() == 4 && (result.getBarcodeFormat() == BarcodeFormat.UPC_A || result.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
            drawLine(canvas, paint, points.get(0), points.get(1), scaleFactor);
            drawLine(canvas, paint, points.get(2), points.get(3), scaleFactor);
        } else {
            paint.setStrokeWidth(PREVIEW_DOT_WIDTH);
            for (ResultPoint point : points) {
                if (null == point) {
                    continue;
                }
                canvas.drawPoint(point.getX() / scaleFactor, point.getY() / scaleFactor, paint);
            }
        }
        return barcode;
    }

    public Result getResult() {
        return result;
    }

    public int getBitmapScaleFactor() {
        return scaleFactor;
    }

    public String getText() {
        return result.getText();
    }

    public Bitmap getBitmap() {
        return sourceData.getBitmap(null, scaleFactor);
    }

    public ResultPoint[] getResultPoints() {
        return result.getResultPoints();
    }

    public BarcodeFormat getBarcodeFormat() {
        return result.getBarcodeFormat();
    }

    public byte[] getRawBytes() {
        return result.getRawBytes();
    }

    public List<ResultPoint> getTransformedResultPoints() {
        if (null == result.getResultPoints()) {
            return Collections.emptyList();
        }
        return transformResultPoints(Arrays.asList(result.getResultPoints()), sourceData);
    }

    public long getTimestamp() {
        return result.getTimestamp();
    }

    @Override
    public String toString() {
        return result.getText();
    }

    private List<ResultPoint> transformResultPoints(List<ResultPoint> resultPoints, SourceData sourceData) {
        List<ResultPoint> scaledPoints = new ArrayList<>(resultPoints.size());
        for (ResultPoint point : resultPoints) {
            scaledPoints.add(sourceData.translateResultPoint(point));
        }
        return scaledPoints;
    }
}