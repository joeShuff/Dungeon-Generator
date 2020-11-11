package com.joeshuff.dddungeongenerator.util;

import android.content.Context;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class FirebaseTracker {

    public static void EVENT(Context c, String name, String desc) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("desc", desc);
            FirebaseAnalytics.getInstance(c).logEvent(name, bundle);
        } catch (Exception e) {
            try {
                FirebaseCrashlytics.getInstance().recordException(e);
            } catch (Exception e2){e2.printStackTrace();}
        }
    }
}
