package com.joeshuff.dddungeongenerator.util;

import android.util.Log;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class Logs {
    public static void i(String tag, String message, Throwable error) {
        FirebaseCrashlytics.getInstance().log("I/" + tag + ": " + message);
        Log.i(tag, message);
        if (error != null) FirebaseCrashlytics.getInstance().recordException(error);
    }

    public static void e(String tag, String message, Throwable error) {
        FirebaseCrashlytics.getInstance().log("E/" + tag + ": " + message);
        Log.e(tag, message);
        if (error != null) FirebaseCrashlytics.getInstance().recordException(error);
    }
}
