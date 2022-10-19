package com.machines0008.viewlibrary.barcodescanner.camera;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/5
 * Usage:
 **/
public class CameraThread {
    private static final String TAG = CameraThread.class.getSimpleName();
    private static CameraThread instance;
    private Handler handler;
    private HandlerThread thread;
    private int openCount = 0;
    private final Object LOCK = new Object();

    public static CameraThread getInstance() {
        if (null == instance) {
            instance = new CameraThread();
        }
        return instance;
    }

    private CameraThread() {

    }

    protected void enqueue(Runnable runnable) {
        synchronized (LOCK) {
            checkRunning();
            handler.post(runnable);
        }
    }

    protected void enqueueDelayed(Runnable runnable, long delayMillis) {
        synchronized (LOCK) {
            checkRunning();
            handler.postDelayed(runnable, delayMillis);
        }
    }

    private void checkRunning() {
        synchronized (LOCK) {
            if (handler == null) {
                if (openCount <= 0) {
                    throw new IllegalStateException("CameraThread is not open");
                }
                thread = new HandlerThread("CameraThread");
                thread.start();
                this.handler = new Handler(thread.getLooper());
            }
        }
    }

    private void quit() {
        synchronized (LOCK) {
            thread.quitSafely();
            thread = null;
            handler = null;
        }
    }

    protected void decrementInstances() {
        synchronized (LOCK) {
            openCount -= 1;
            if (openCount == 0) {
                quit();
            }
        }
    }

    protected void incrementAndEnqueue(Runnable runnable) {
        synchronized (LOCK) {
            openCount += 1;
            enqueue(runnable);
        }
    }
}