package com.joeshuff.dddungeongenerator.screens.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeshuff.chatalyser.db.RealmHelper
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.db.copyFromRealm
import com.joeshuff.dddungeongenerator.db.dungeonDao
import com.joeshuff.dddungeongenerator.generator.monsters.Bestiary
import com.joeshuff.dddungeongenerator.memory.MemoryController
import com.joeshuff.dddungeongenerator.screens.create.NewDungeonActivity
import com.joeshuff.dddungeongenerator.screens.settings.SettingsActivity
import com.joeshuff.dddungeongenerator.util.AppPreferences.darkThemeMode
import com.joeshuff.dddungeongenerator.util.AppPreferences.init
import com.joeshuff.dddungeongenerator.util.AppPreferences.installReference
import com.joeshuff.emptyrecyclerview.EmptyViewCreatedListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.empty_home_list.view.*

class HomeActivity : AppCompatActivity(), EmptyViewCreatedListener {

    val disposables = arrayListOf<Disposable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        RealmHelper.build(this)

        Bestiary.launchBestiary(this)

        init(applicationContext)

        AppCompatDelegate.setDefaultNightMode(darkThemeMode)

        FirebaseCrashlytics.getInstance().setUserId(installReference)

        initUi()
        subscribe()
    }

    fun initUi() {
        homeHistoryList.let {
            it.setLayoutManager(LinearLayoutManager(this))
            it.setOnEmptyViewCreatedListener(this)
            it.setAdapter(DungeonHistoryAdapter(arrayListOf()), true)
        }
    }

    fun genNewClicked() {
        startActivity(Intent(this, NewDungeonActivity::class.java))
    }

    fun subscribe() {
        disposables.add(MemoryController.memoryHistory
                .observeOn(AndroidSchedulers.mainThread())
                .map { ArrayList(it.map { it.getMemoryItem() }) }
                .subscribe {memory ->
                    val fromDb = ArrayList(Realm.getDefaultInstance().dungeonDao().getMemory().map { it.getMemoryItem() })
                    memory.addAll(fromDb)

                    homeHistoryList.getRecyclerView()?.adapter?.let {
                        if (it is DungeonHistoryAdapter) {
                            it.update(memory)
                        }
                    }
                }
        )

        Realm.getDefaultInstance().dungeonDao().getLiveMemory().observe(this, Observer {results ->
            homeHistoryList.getRecyclerView()?.adapter?.let {
               if (it is DungeonHistoryAdapter) {
                   val memory = ArrayList(MemoryController.getMemory(applicationContext).map { it.getMemoryItem() })
                   val fromDb = ArrayList(results.map { it.copyFromRealm().getMemoryItem() })
                   memory.addAll(fromDb)
                   it.update(memory)
               }
            }
        })
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

    override fun onCreated(view: View) {
        view.generateNewDungeonButton.setOnClickListener { genNewClicked() }
    }

    override fun onShown(view: View?) {}

    override fun onResume() {
        super.onResume()
        subscribe()
    }

    override fun onPause() {
        super.onPause()
        disposables.forEach { it.dispose() }
    }
}