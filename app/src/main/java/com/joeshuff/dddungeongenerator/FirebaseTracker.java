package com.joeshuff.dddungeongenerator;

import android.content.Context;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseTracker {

    public static void EVENT(Context c, String id, String name) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
            FirebaseAnalytics.getInstance(c).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        } catch (Exception e) {
            try {
                Crashlytics.logException(e);
            } catch (Exception e2){e2.printStackTrace();}
        }
    }
}
