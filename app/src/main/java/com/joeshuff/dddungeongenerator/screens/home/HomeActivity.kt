package com.joeshuff.dddungeongenerator.screens.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.RecyclerViewEmptySupport
import com.joeshuff.dddungeongenerator.memory.MemoryController
import com.joeshuff.dddungeongenerator.screens.create.NewDungeonActivity
import com.joeshuff.dddungeongenerator.screens.settings.SettingsActivity
import com.joeshuff.dddungeongenerator.util.AppPreferences.darkThemeMode
import com.joeshuff.dddungeongenerator.util.AppPreferences.init
import com.joeshuff.dddungeongenerator.util.AppPreferences.installReference
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        init(applicationContext)

        generateNewDungeonButton.setOnClickListener { genNewClicked() }

        AppCompatDelegate.setDefaultNightMode(darkThemeMode)

        FirebaseCrashlytics.getInstance().setUserId(installReference)

        doUpdate()
    }

    fun genNewClicked() {
        startActivity(Intent(this, NewDungeonActivity::class.java))
    }

    fun doUpdate() {
        homeHistoryList.let {
            it.layoutManager = LinearLayoutManager(this)
            it.setEmptyView(findViewById(R.id.emptyHomeView))
            it.adapter = HomeListAdapter(MemoryController.getMemory(applicationContext))
        }
    }

    override fun onResume() {
        super.onResume()
        doUpdate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.newDungeon) {
            genNewClicked()
            return true
        }
        if (item.itemId == R.id.settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        return false
    }
}