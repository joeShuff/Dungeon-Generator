package com.joeshuff.dddungeongenerator

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.joeshuff.dddungeongenerator.util.openUrl

import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.item_settings_build_info.*
import kotlinx.android.synthetic.main.item_settings_opensource.*
import kotlinx.android.synthetic.main.item_settings_srd.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        buildInfo_buildDateValue.text = BuildConfig.buildTime
        buildInfo_versionValue.text = BuildConfig.VERSION_NAME
        buildInfo_buildNumberValue.text = BuildConfig.buildNumber

        openSource_viewCode.setOnClickListener {
            "https://github.com/joeShuff/Dungeon-Generator".openUrl(this)
        }

        ddSrd_viewDocument.setOnClickListener {
            startActivity(Intent(this, SRDActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }

        return false
    }

}
