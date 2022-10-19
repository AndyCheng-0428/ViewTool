package com.machines0008.viewlibrary.barcodescanner.camera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/2
 * Usage:
 **/
public class CameraConfigurationUtils {
    private static final String TAG = CameraConfigurationUtils.class.getSimpleName();

    private static final Pattern SEMICOLON = Pattern.compile(";");

    private static final float MAX_EXPOSURE_COMPENSATION = 1.5f;
    private static final float MIN_EXPOSURE_COMPENSATION = 0.0f;
    private static final int MIN_FPS = 10;
    private static final int MAX_FPS = 20;
    private static final int AREA_PER_1000 = 400;

    private CameraConfigurationUtils() {

    }

    public static void setFocus(Camera.Parameters parameters, CameraSettings.FocusMode focusModeSetting, boolean safeMode) {
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        String focusMode = null;

        if (safeMode || focusModeSetting == CameraSettings.FocusMode.AUTO) {
            focusMode = findSettableValue("focus mode", supportedFocusModes, Camera.Parameters.FOCUS_MODE_AUTO);
        } else if (focusModeSetting == CameraSettings.FocusMode.CONTINUOUS) {
            focusMode = findSettableValue("focus mode", supportedFocusModes, Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, Camera.Parameters.FOCUS_MODE_AUTO);
        } else if (focusModeSetting == CameraSettings.FocusMode.INFINITY) {
            focusMode = findSettableValue("focus mode", supportedFocusModes, Camera.Parameters.FOCUS_MODE_INFINITY);
        } else if (focusModeSetting == CameraSettings.FocusMode.MACRO) {
            focusMode = findSettableValue("focus mode", supportedFocusModes, Camera.Parameters.FOCUS_MODE_MACRO);
        }
        if (null == focusMode) {
            if (!safeMode) {
                focusMode = findSettableValue("focus mode", supportedFocusModes, Camera.Parameters.FOCUS_MODE_MACRO, Camera.Parameters.FOCUS_MODE_EDOF);
            }
        }
        if (null == focusMode) {
            return;
        }
        if (focusMode.equals(parameters.getFocusMode())) {
            Log.i(TAG, "Focus mode already set to " + focusMode);
        } else {
            parameters.setFocusMode(focusMode);
        }
    }

    public static void setTorch(Camera.Parameters parameters, boolean on) {
        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        String flashMode;
        if (on) {
            flashMode = findSettableValue("flash mode", supportedFlashModes, Camera.Parameters.FLASH_MODE_TORCH, Camera.Parameters.FLASH_MODE_ON);
        } else {
            flashMode = findSettableValue("flash mode", supportedFlashModes, Camera.Parameters.FLASH_MODE_OFF);
        }
        if (null == flashMode) {
            return;
        }
        if (flashMode.equals(parameters.getFlashMode())) {
            Log.i(TAG, "Flash mode already set to " + flashMode);
        } else {
            Log.i(TAG, "Setting flash mode to " + flashMode);
            parameters.setFlashMode(flashMode);
        }
    }

    public static void setBestExposure(Camera.Parameters parameters, boolean lightOn) {
        int minExposure = parameters.getMinExposureCompensation();
        int maxExposure = parameters.getMaxExposureCompensation();
        float step = parameters.getExposureCompensationStep();
        if ((minExposure != 0 || maxExposure != 0) && step > 0.0f) {
            float targetCompensation = lightOn ? MIN_EXPOSURE_COMPENSATION : MAX_EXPOSURE_COMPENSATION;
            int compensationStep = Math.round(targetCompensation / step);
            float actualCompensation = step * compensationStep;
            compensationStep = Math.max(Math.min(compensationStep, maxExposure), minExposure);
            if (parameters.getExposureCompensation() == compensationStep) {
                Log.i(TAG, "Exposure compensation already set to " + compensationStep + " / " + actualCompensation);
            } else {
                Log.i(TAG, "Setting exposure compensation to " + compensationStep + " / " + actualCompensation);
                parameters.setExposureCompensation(compensationStep);
            }
        } else {
            Log.i(TAG, "Camera does not support exposure compensation");
        }
    }

    public static void setBestPreviewFPS(Camera.Parameters parameters) {
        setBestPreviewFPS(parameters, MIN_FPS, MAX_FPS);
    }

    public static void setBestPreviewFPS(Camera.Parameters parameters, int minFPS, int maxFPS) {
        List<int[]> supportedPreviewFpsRanges = parameters.getSupportedPreviewFpsRange();
        Log.i(TAG, "Supported FPS ranges: " + toString(supportedPreviewFpsRanges));
        if (supportedPreviewFpsRanges == null || supportedPreviewFpsRanges.isEmpty()) {
            return;
        }
        int[] suitableFPSRange = null;
        for (int[] fpsRange : supportedPreviewFpsRanges) {
            int thisMin = fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
            int thisMax = fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            if (thisMin >= minFPS * 1000 && thisMax <= maxFPS * 1000) {
                suitableFPSRange = fpsRange;
                break;
            }
        }
        if (null == suitableFPSRange) {
            Log.i(TAG, "No suitable FPS range");
            return;
        }
        int[] currentFpsRange = new int[2];
        parameters.getPreviewFpsRange(currentFpsRange);
        if (Arrays.equals(currentFpsRange, suitableFPSRange)) {
            Log.i(TAG, "FPS range already set to " + Arrays.toString(suitableFPSRange));
            return;
        }
        Log.i(TAG, "Setting FPS range to " + Arrays.toString(suitableFPSRange));
        parameters.setPreviewFpsRange(suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], suitableFPSRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
    }

    public static void setFocusArea(Camera.Parameters parameters) {
        if (parameters.getMaxNumFocusAreas() <= 0) {
            Log.i(TAG, "Device does not support focus areas");
            return;
        }
        Log.i(TAG, "Old focus area: " + toString(parameters.getFocusAreas()));
        List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
        Log.i(TAG, "Setting focus area to : " + toString(middleArea));
        parameters.setFocusAreas(middleArea);
    }

    public static void setMetering(Camera.Parameters parameters) {
        if (parameters.getMaxNumMeteringAreas() <= 0) {
            Log.i(TAG, "Device does not support metering areas");
            return;
        }
        Log.i(TAG, "Old metering area: " + parameters.getMeteringAreas());
        List<Camera.Area> middleArea = buildMiddleArea(AREA_PER_1000);
        Log.i(TAG, "Setting metering area to : " + toString(middleArea));
        parameters.setMeteringAreas(middleArea);
    }

    private static List<Camera.Area> buildMiddleArea(int areaPer1000) {
        return Collections.singletonList(new Camera.Area(new Rect(-areaPer1000, -areaPer1000, areaPer1000, areaPer1000), 1));
    }

    public static void setVideoStabilization(Camera.Parameters parameters) {
        if (!parameters.isVideoStabilizationSupported()) {
            Log.i(TAG, "This device does not support video stabilization");
            return;
        }
        if (parameters.getVideoStabilization()) {
            Log.i(TAG, "Video stabilization already enabled");
            return;
        }
        Log.i(TAG, "Enabling video stabilization...");
        parameters.setVideoStabilization(true);
    }

    public static void setBarcodeSceneMode(Camera.Parameters parameters) {
        if (Camera.Parameters.SCENE_MODE_BARCODE.equals(parameters.getSceneMode())) {
            Log.i(TAG, "Barcode scene scene mode already set");
            return;
        }
        String sceneMode = findSettableValue("scene mode", parameters.getSupportedSceneModes(), Camera.Parameters.SCENE_MODE_BARCODE);
        if (null == sceneMode) {
            return;
        }
        parameters.setSceneMode(sceneMode);
    }

    public static void setZoom(Camera.Parameters parameters, double targetZoomRatio) {
        if (!parameters.isZoomSupported()) {
            Log.i(TAG, "Zoom is not supported");
            return;
        }
        Integer zoom = indexOfClosestZoom(parameters, targetZoomRatio);
        if (null == zoom) {
            return;
        }
        if (parameters.getZoom() == zoom) {
            Log.i(TAG, "Zoom is already set to " + zoom);
            return;
        }
        Log.i(TAG, "Setting zoom to " + zoom);
        parameters.setZoom(zoom);
    }

    private static Integer indexOfClosestZoom(Camera.Parameters parameters, double targetZoomRatio) {
        List<Integer> ratios = parameters.getZoomRatios();
        Log.i(TAG, "Zoom ratios: " + ratios);
        int maxZoom = parameters.getMaxZoom();
        if (null == ratios || ratios.isEmpty() || ratios.size() != maxZoom + 1) {
            Log.w(TAG, "Invalid zoom ratios!");
            return null;
        }
        double target100 = 100.0 * targetZoomRatio;
        double smallestDiff = Double.POSITIVE_INFINITY;
        int closestIndex = 0;
        for (int i = 0, size = ratios.size(); i < size; i++) {
            double diff = Math.abs(ratios.get(i) - target100);
            if (diff < smallestDiff) {
                smallestDiff = diff;
                closestIndex = i;
            }
        }
        Log.i(TAG, "Chose zoom ratio of " + (ratios.get(closestIndex) / 100.0));
        return closestIndex;
    }

    public static void setInvertColor(Camera.Parameters parameters) {
        if (Camera.Parameters.EFFECT_NEGATIVE.equals(parameters.getColorEffect())) {
            Log.i(TAG, "Negative effect already set");
            return;
        }
        String colorMode = findSettableValue("color effect", parameters.getSupportedColorEffects(), Camera.Parameters.EFFECT_NEGATIVE);
        if (null == colorMode) {
            return;
        }
        parameters.setColorEffect(colorMode);
    }

    private static String findSettableValue(String name, Collection<String> supportedValues, String... desiredValues) {
        Log.i(TAG, "Requesting " + name + " value form among: " + Arrays.toString(desiredValues));
        Log.i(TAG, "Supported " + name + " values: " + supportedValues);
        if (null != supportedValues) {
            for (String desiredValue : desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    Log.i(TAG, "Can set " + name + " to: " + desiredValue);
                    return desiredValue;
                }
            }
        }
        Log.i(TAG, "No supported values match");
        return null;
    }

    private static String toString(Collection<int[]> arrays) {
        if (CollectionUtils.isEmpty(arrays)) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        Iterator<int[]> it = arrays.iterator();
        while (it.hasNext()) {
            builder.append(Arrays.toString(it.next()));
            if (it.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append(']');
        return builder.toString();
    }

    private static String toString(Iterable<Camera.Area> areas) {
        if (null == areas) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Camera.Area area : areas) {
            builder.append(area.rect).append(':').append(area.weight).append(' ');
        }
        return builder.toString();
    }

    public static String collectStats(Camera.Parameters parameters) {
        return collectStats(parameters.flatten());
    }

    public static String collectStats(CharSequence flattenedParams) {
        StringBuilder result = new StringBuilder(1000);

        result.append("BOARD=").append(Build.BOARD).append('\n');
        result.append("BRAND=").append(Build.BRAND).append('\n');
        result.append("CPU_ABI=").append(Build.CPU_ABI).append('\n');
        result.append("DEVICE=").append(Build.DEVICE).append('\n');
        result.append("DISPLAY=").append(Build.DISPLAY).append('\n');
        result.append("FINGERPRINT=").append(Build.FINGERPRINT).append('\n');
        result.append("HOST=").append(Build.HOST).append('\n');
        result.append("ID=").append(Build.ID).append('\n');
        result.append("MANUFACTURER=").append(Build.MANUFACTURER).append('\n');
        result.append("MODEL=").append(Build.MODEL).append('\n');
        result.append("PRODUCT=").append(Build.PRODUCT).append('\n');
        result.append("TAGS=").append(Build.TAGS).append('\n');
        result.append("TIME=").append(Build.TIME).append('\n');
        result.append("TYPE=").append(Build.TYPE).append('\n');
        result.append("USER=").append(Build.USER).append('\n');
        result.append("VERSION.CODENAME=").append(Build.VERSION.CODENAME).append('\n');
        result.append("VERSION.INCREMENTAL=").append(Build.VERSION.INCREMENTAL).append('\n');
        result.append("VERSION.RELEASE=").append(Build.VERSION.RELEASE).append('\n');
        result.append("VERSION.SDK_INT=").append(Build.VERSION.SDK_INT).append('\n');

        if (flattenedParams != null) {
            String[] params = SEMICOLON.split(flattenedParams);
            Arrays.sort(params);
            for (String param : params) {
                result.append(param).append('\n');
            }
        }

        return result.toString();
    }
}