package com.joeshuff.dddungeongenerator.screens.viewdungeon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.memory.MemoryController
import com.joeshuff.dddungeongenerator.screens.create.GeneratingActivity
import com.joeshuff.dddungeongenerator.screens.home.HomeActivity
import com.joeshuff.dddungeongenerator.screens.viewdungeon.ResultsFragment.ResultFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_results.*

class ResultsActivity : AppCompatActivity() {

    var generatedDungeon: Dungeon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val gson = GsonBuilder().registerTypeAdapterFactory(GeneratingActivity.roomFeatureAdapter).create()
        generatedDungeon = gson.fromJson(MemoryController.getFromSharedPreferences(applicationContext, "RECENT_DUNGEON"), Dungeon::class.java)

        generatedDungeon?.let {
            title = it.name
            loadUI(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.results_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeIcon || item.itemId == android.R.id.home) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return true
        }

        return false
    }

    private fun loadUI(dungeon: Dungeon) {
        resultsTab.setBackgroundColor(ContextCompat.getColor(this, R.color.tabLayoutBackgroundColor))
        resultsPager.adapter = ResultFragmentPagerAdapter(this, dungeon)
        resultsTab.setupWithViewPager(resultsPager)
    }
}