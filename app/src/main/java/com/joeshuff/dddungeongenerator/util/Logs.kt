package com.joeshuff.dddungeongenerator.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

object Logs {
    fun i(tag: String, message: String, error: Throwable? = null) {
        FirebaseCrashlytics.getInstance().log("I/$tag: $message")
        Log.i(tag, message)
        if (error != null) FirebaseCrashlytics.getInstance().recordException(error)
    }

    fun e(tag: String, message: String, error: Throwable? = null) {
        FirebaseCrashlytics.getInstance().log("E/$tag: $message")
        Log.e(tag, message)
        if (error != null) FirebaseCrashlytics.getInstance().recordException(error)
    }
}