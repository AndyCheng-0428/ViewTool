package com.machines0008.viewlibrary.barcodescanner;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;
import android.view.WindowManager;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/5
 * Usage:
 **/
public class RotationListener {
    private int lastRotation;

    private WindowManager windowManager;
    private OrientationEventListener orientationEventListener;
    private RotationCallback callback;


    public void listen(Context context, RotationCallback callback) {
        stop();

        context = context.getApplicationContext();
        this.callback = callback;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        orientationEventListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                WindowManager localWindowManager = windowManager;
                if (null == windowManager || callback == null) {
                    return;
                }
                int newRotation = localWindowManager.getDefaultDisplay().getRotation();
                if (newRotation == lastRotation) {
                    return;
                }
                lastRotation = newRotation;
                callback.onRotationChanged(newRotation);
            }
        };
        orientationEventListener.enable();
        lastRotation = windowManager.getDefaultDisplay().getRotation();
    }

    public void stop() {
        if (null != orientationEventListener) {
            orientationEventListener.disable();
        }
        orientationEventListener = null;
        windowManager = null;
        callback = null;
    }
}
