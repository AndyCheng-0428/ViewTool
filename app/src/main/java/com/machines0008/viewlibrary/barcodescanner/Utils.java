package com.machines0008.viewlibrary.barcodescanner;

import android.os.Looper;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/9/5
 * Usage:
 **/
public class Utils {
    public static void validateMainThread() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Must be called from the main thread.");
        }
    }
}
