package com.april.utils;

import android.util.Log;

public class Utils {

    private static final boolean DEBUG = true;

    private static final String TAG = "UubDemos";

    public static void log(String str) {
        if (DEBUG) {
            Log.d(TAG, str);
        }
    }
}
