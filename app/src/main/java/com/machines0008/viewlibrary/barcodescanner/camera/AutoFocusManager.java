package com.machines0008.viewlibrary.barcodescanner.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage:
 **/
@SuppressWarnings("deprecation")
public class AutoFocusManager {
    private static final long AUTO_FOCUS_INTERVAL_MS = 2000L; //自動對焦區間(毫秒)

    private boolean stopped;
    private boolean focusing;
    private final boolean useAutoFocus;
    private final Camera camera;
    private Handler handler;

    private int MESSAGE_FOCUS = 1;
    private static final Collection<String> FOCUS_MODE_CALLING_AF;

    static {
        FOCUS_MODE_CALLING_AF = new ArrayList<>(2);
        FOCUS_MODE_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO);
        FOCUS_MODE_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO);
    }

    private final Handler.Callback focusHandlerCallback = msg -> {
        if (msg.what == MESSAGE_FOCUS) {
            focus();
            return true;
        }
        return false;
    };

    private final Camera.AutoFocusCallback autoFocusCallback = (success, camera) -> handler.post(() -> {
        focusing = false;
        autoFocusAgainLater();
    });

    public AutoFocusManager(Camera camera, CameraSettings settings) {
        this.handler = new Handler(focusHandlerCallback);
        this.camera = camera;
        String currentFocusMode = camera.getParameters().getFocusMode();
        useAutoFocus = settings.isAutoFocusEnabled() && FOCUS_MODE_CALLING_AF.contains(currentFocusMode);
        start();
    }

    private synchronized void autoFocusAgainLater() {
        if (!stopped && !handler.hasMessages(MESSAGE_FOCUS)) {
            handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_FOCUS), AUTO_FOCUS_INTERVAL_MS);
        }
    }

    public void start() {
        stopped = false;
        focus();
    }

    private void focus() {
        if (!useAutoFocus) {
            return;
        }
        if (stopped || focusing) {
            return;
        }
        try {
            camera.autoFocus(autoFocusCallback);
            focusing = true;
        } catch (RuntimeException e) {
            Log.w(AutoFocusManager.class.getSimpleName(), Log.getStackTraceString(e));
            autoFocusAgainLater();
        }
    }

    private void cancelOutstandingTask() {
        handler.removeMessages(MESSAGE_FOCUS);
    }

    public void stop() {
        stopped = true;
        focusing = false;
        cancelOutstandingTask();
        if (!useAutoFocus) {
            return;
        }
        try {
            camera.cancelAutoFocus();
        } catch (RuntimeException e) {
            Log.w(AutoFocusManager.class.getSimpleName(), "Unexpected exception while canceling focusing", e);
        }
    }
}