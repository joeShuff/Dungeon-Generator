package com.joeshuff.dddungeongenerator

import android.os.Bundle
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.joeshuff.dddungeongenerator.util.AppPreferences
import com.joeshuff.dddungeongenerator.util.openUrl

import kotlinx.android.synthetic.main.item_settings_build_info.*
import kotlinx.android.synthetic.main.item_settings_opensource.*
import kotlinx.android.synthetic.main.item_settings_srd.*
import kotlinx.android.synthetic.main.item_settings_theme.*
import android.os.Build
import androidx.appcompat.app.AlertDialog
import com.joeshuff.dddungeongenerator.pdfviewing.PDFActivity
import com.joeshuff.dddungeongenerator.pdfviewing.PaperActivity
import com.joeshuff.dddungeongenerator.pdfviewing.SRDActivity
import com.joeshuff.dddungeongenerator.util.Logs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.item_settings_aboutcard.*
import java.lang.Exception


class SettingsActivity : AppCompatActivity() {

    val disposables = arrayListOf<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        AppPreferences.init(applicationContext)
        AppCompatDelegate.setDefaultNightMode(AppPreferences.darkThemeMode)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        buildInfo_buildDateValue.text = BuildConfig.buildTime
        buildInfo_versionValue.text = BuildConfig.VERSION_NAME
        buildInfo_buildNumberValue.text = BuildConfig.buildNumber
        buildInfo_installationValue.text = AppPreferences.installReference

        theme_chosenTheme.text = getString(THEME_TYPE.values().first { it.systemId == AppPreferences.darkThemeMode }.textResourceId)

        setupClickListeners()

        subscribe()
    }

    private fun subscribe() {
        disposables.add(AppPreferences.themeChangeLiveData
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {newValue ->
                try {
                    theme_chosenTheme.text = getString(THEME_TYPE.values().firstOrNull { it.systemId == newValue }?.textResourceId ?: R.string.theme_not_found)
                } catch (e: Exception) {
                    Logs.e("SettingsActivity", "Unable to update the theme textview", e)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach { it.dispose() }
    }

    private fun setupClickListeners() {
        openSource_viewCode.setOnClickListener {
            "https://github.com/joeShuff/Dungeon-Generator".openUrl(this)
        }

        ddSrd_viewDocument.setOnClickListener {
            val srdAct = Intent(this, SRDActivity::class.java)
            startActivity(srdAct)
        }

        aboutApp_viewPaper.setOnClickListener {
            val paperActivity = Intent(this, PaperActivity::class.java)
            startActivity(paperActivity)
        }

        buildInfo_installationItem.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Install Reference", AppPreferences.installReference)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Copied Install Reference", Toast.LENGTH_LONG).show()
        }

        theme_chooseTheme.setOnClickListener {
            showThemePickerDialog()
        }
    }

    private fun showThemePickerDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Choose Theme")
        val items = arrayListOf(THEME_TYPE.LIGHT, THEME_TYPE.DARK)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            items.add(THEME_TYPE.BATTERY_SAVER)
        } else {
            items.add(THEME_TYPE.SYSTEM_DEFAULT)
        }

        var checkedItem = items.indexOfFirst { it.systemId == AppPreferences.darkThemeMode }

        if (checkedItem < 0) checkedItem = 2

        var alert: AlertDialog? = null

        alertDialog.setSingleChoiceItems(items.map { getString(it.textResourceId) }.toTypedArray(), checkedItem) { dialog, which ->
            AppPreferences.darkThemeMode = items[which].systemId
            alert?.dismiss()
        }

        alert = alertDialog.create()
        alert.show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        return false
    }

    enum class THEME_TYPE(val systemId: Int, val textResourceId: Int) {
        LIGHT(AppCompatDelegate.MODE_NIGHT_NO, R.string.theme_light_value),
        DARK(AppCompatDelegate.MODE_NIGHT_YES, R.string.theme_dark_value),
        SYSTEM_DEFAULT(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.theme_follow_system_value),
        BATTERY_SAVER(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, R.string.theme_battery_saver);
    }
}
