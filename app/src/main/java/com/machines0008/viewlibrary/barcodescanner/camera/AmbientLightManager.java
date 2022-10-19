package com.machines0008.viewlibrary.barcodescanner.camera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/30
 * Usage:
 **/
public class AmbientLightManager implements SensorEventListener {

    private static final float TOO_DARK_LUX = 45.0f;
    private static final float BRIGHT_ENOUGH_LUX = 450.0f;

    private CameraManager cameraManager;
    private CameraSettings cameraSettings;
    private Sensor lightSensor;
    private Context context;

    private Handler handler;

    public AmbientLightManager(Context context, CameraManager cameraManager, CameraSettings settings) {
        this.context = context;
        this.cameraManager = cameraManager;
        this.cameraSettings = settings;
        this.handler = new Handler();
    }

    public void start() {
        if (!cameraSettings.isAutoTorchEnabled()) {
            return;
        }
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (null == lightSensor) {
            return;
        }
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        if (null == lightSensor) {
            return;
        }
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
        lightSensor = null;
    }

    private void setTorch(final boolean on) {
        handler.post(() -> cameraManager.setTorch(on));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float ambientLightLux = event.values[0];
        if (null == cameraManager) {
            return;
        }
        if (ambientLightLux <= TOO_DARK_LUX) {
            setTorch(true);
        } else if (ambientLightLux >= BRIGHT_ENOUGH_LUX) {
            setTorch(false);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}