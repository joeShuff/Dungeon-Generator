package com.joeshuff.dddungeongenerator.util

import android.app.Activity
import android.graphics.Point
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.generator.models.Corridor

fun String.openUrl(activity: Activity) {
    val builder = CustomTabsIntent.Builder()
    builder.setToolbarColor(activity.resources.getColor(R.color.colorPrimary))
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(activity, Uri.parse(this))
}

inline fun <T> tryOrNull(f: () -> T) =
        try {
            f()
        } catch (_: Exception) {
            null
        }

fun <T> List<T>.ifNotEmpty(f: (List<T>) -> Unit) {
    if (isNotEmpty()) f.invoke(this)
}