package com.joeshuff.dddungeongenerator.screens.viewdungeon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.joeshuff.chatalyser.db.RealmHelper
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.db.dungeonDao
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.screens.home.HomeActivity
import com.joeshuff.dddungeongenerator.screens.viewdungeon.ResultsFragment.ResultFragmentPagerAdapter
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_results.*

class ResultsActivity : AppCompatActivity() {

    companion object {
        const val DUNGEON_ID_EXTRA = "dungeon_id_extra"
    }

    var generatedDungeon: Dungeon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        RealmHelper.build(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchDungeon(intent.getIntExtra(DUNGEON_ID_EXTRA, -1))
    }

    fun fetchDungeon(id: Int) {
        val request = Realm.getDefaultInstance().dungeonDao().getDungeonById(id)
        request.observe(this, Observer {results ->
            request.removeObservers(this)

            results.firstOrNull()?.let {
                title = it.getName()
                loadUI(it)
            }?: run {
                Toast.makeText(this, "Unable to find dungeon", Toast.LENGTH_LONG).show()
            }
        })
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
        resultsPager.adapter = ResultFragmentPagerAdapter(this, supportFragmentManager, dungeon)
        resultsTab.setupWithViewPager(resultsPager)
    }
}