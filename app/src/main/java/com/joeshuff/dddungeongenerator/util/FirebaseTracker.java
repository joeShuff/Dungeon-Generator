package com.joeshuff.dddungeongenerator.util;

import android.content.Context;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseTracker {

    public static void EVENT(Context c, String name, String desc) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("desc", desc);
            FirebaseAnalytics.getInstance(c).logEvent(name, bundle);
        } catch (Exception e) {
            try {
                Crashlytics.logException(e);
            } catch (Exception e2){e2.printStackTrace();}
        }
    }
}
