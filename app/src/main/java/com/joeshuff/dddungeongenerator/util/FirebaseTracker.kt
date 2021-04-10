package com.joeshuff.dddungeongenerator.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

object FirebaseTracker {
    fun EVENT(c: Context, name: String, desc: String) {
        try {
            val bundle = Bundle()
            bundle.putString("desc", desc)
            FirebaseAnalytics.getInstance(c).logEvent(name, bundle)
        } catch (e: Exception) {
            try {
                FirebaseCrashlytics.getInstance().recordException(e)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }
}