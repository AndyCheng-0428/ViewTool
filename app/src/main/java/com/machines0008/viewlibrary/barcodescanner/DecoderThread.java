package com.machines0008.viewlibrary.barcodescanner;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.machines0008.viewlibrary.barcodescanner.camera.CameraInstance;
import com.machines0008.viewlibrary.barcodescanner.camera.PreviewCallback;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/7
 * Usage:
 **/
public class DecoderThread {
    private static final String TAG = DecoderThread.class.getSimpleName();
    private CameraInstance instance;
    private HandlerThread thread;
    private Handler handler;
    private Decoder decoder;
    private Handler resultHandler;
    private Rect cropRect;
    private boolean running = false;
    private final Object LOCK = new Object();

    private final Handler.Callback callback = msg -> {
        if (msg.what == MSG_DECODE) {
            decode((SourceData) msg.obj);
        } else if (msg.what == MSG_DECODE_FAILED) {
            requestNextPreview();
        }
        return true;
    };

    private final PreviewCallback previewCallback = new PreviewCallback() {

        @Override
        public void onPreview(SourceData sourceData) {
            synchronized (LOCK) {
                if (running) {
                    return;
                }
                handler.obtainMessage(MSG_DECODE, sourceData).sendToTarget();
            }
        }

        @Override
        public void onPreviewError(Exception e) {
            synchronized (LOCK) {
                if (running) {
                    return;
                }
                handler.obtainMessage(MSG_DECODE_FAILED).sendToTarget();
            }
        }
    };

    private void requestNextPreview() {
        instance.requestPreview(previewCallback);
    }

    private void decode(SourceData sourceData) {
        long start = System.currentTimeMillis();
        Result rawResult = null;
        sourceData.setCropRect(cropRect);
        LuminanceSource source = createSource(sourceData);
        if (null != source) {
            rawResult = decoder.decode(source);
        }
        if (null == resultHandler) {
            requestNextPreview();
            return;
        }
        if (null != rawResult) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");
            BarcodeResult barcodeResult = new BarcodeResult(rawResult, sourceData);
            Message message = Message.obtain(resultHandler, MSG_DECODE_SUCCESS, barcodeResult);
            Bundle bundle = new Bundle();
            message.setData(bundle);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(resultHandler, MSG_DECODE_FAILED);
            message.sendToTarget();
        }
        requestNextPreview();
    }

    public DecoderThread(CameraInstance cameraInstance, Decoder decoder, Handler resultHandler) {
        Utils.validateMainThread();
        this.instance = cameraInstance;
        this.decoder = decoder;
        this.resultHandler = resultHandler;
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public Rect getCropRect() {
        return cropRect;
    }

    public void setCropRect(Rect cropRect) {
        this.cropRect = cropRect;
    }

    public void start() {
        Utils.validateMainThread();
        thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper(), callback);
        running = true;
        requestNextPreview();
    }

    public void stop() {
        Utils.validateMainThread();
        synchronized (LOCK) {
            running = false;
            handler.removeCallbacksAndMessages(null);
            thread.quit();
        }
    }

    private LuminanceSource createSource(SourceData sourceData) {
        return cropRect == null ? null : sourceData.createSource();
    }

    @IntDef(value = {MSG_DECODE, MSG_DECODE_FAILED, MSG_DECODE_SUCCESS})
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @Retention(RetentionPolicy.SOURCE)
    public @interface DecodeMessage {

    }

    public static final int MSG_DECODE = 0;
    public static final int MSG_DECODE_SUCCESS = 1;
    public static final int MSG_DECODE_FAILED = 2;
}