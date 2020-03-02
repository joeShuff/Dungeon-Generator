package com.joeshuff.dddungeongenerator.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import java.util.*

object AppPreferences {

    private const val dark_theme_type_storage = "dark_theme_key"
    private const val installReferenceKey = "install_reference_key"
    private const val pdfIsDarkKey = "pdf_dark_mode_key"

    private lateinit var preferences: SharedPreferences
    private lateinit var gson: Gson

    fun init(context: Context) {
        preferences = context.getSharedPreferences("dungeongeneratorpreferences", Context.MODE_PRIVATE)
        gson = Gson()
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var pdfIsDark: Boolean
        get() = preferences.getBoolean(pdfIsDarkKey, false)
        set(value) = preferences.edit { it.putBoolean(pdfIsDarkKey, value) }

    var darkThemeMode: Int
        get() = preferences.getInt(dark_theme_type_storage, if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) {
            preferences.edit { it.putInt(dark_theme_type_storage, value) }
            AppCompatDelegate.setDefaultNightMode(value)
        }

    var installReference: String
        get() {
            val existing = preferences.getString(installReferenceKey, "")?: ""
            if (existing.isEmpty()) installReference = UUID.randomUUID().toString()
            return preferences.getString(installReferenceKey, "")?: ""
        }
        set(value) = preferences.edit { it.putString(installReferenceKey, value) }
}

