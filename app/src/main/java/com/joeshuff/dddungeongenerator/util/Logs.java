package com.joeshuff.dddungeongenerator.util;

import android.util.Log;
import com.crashlytics.android.Crashlytics;

public class Logs {
    public static void i(String tag, String message, Throwable error) {
        Crashlytics.log(Log.INFO, tag, message);
        Log.i(tag, message);
        if (error != null) Crashlytics.logException(error);
    }

    public static void e(String tag, String message, Throwable error) {
        Crashlytics.log(Log.ERROR, tag, message);
        Log.e(tag, message);
        if (error != null) Crashlytics.logException(error);
    }
}
