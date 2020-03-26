package com.joeshuff.dddungeongenerator.util

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.joeshuff.dddungeongenerator.R

fun String.openUrl(activity: Activity) {
    val builder = CustomTabsIntent.Builder()
    builder.setToolbarColor(activity.resources.getColor(R.color.colorPrimary))
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(activity, Uri.parse(this))
}