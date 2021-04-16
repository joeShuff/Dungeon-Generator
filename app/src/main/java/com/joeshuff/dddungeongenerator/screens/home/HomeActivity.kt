package com.joeshuff.dddungeongenerator.screens.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    val sorters = arrayListOf(
            HomeSortable(0, "Newest") { ArrayList(it.sortedBy { it.createdAt }.reversed()) },
            HomeSortable(1, "Oldest") { ArrayList(it.sortedBy { it.createdAt }) },
            HomeSortable(2, "Name A-Z") { ArrayList(it.sortedBy { it.dungeonName }) },
            HomeSortable(3, "Name Z-A") { ArrayList(it.sortedBy { it.dungeonName }.reversed()) }
    )

    var chosenSorter: HomeSortable? = null

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
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Toast.makeText(applicationContext, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                Toast.makeText(applicationContext, "on Swiped $position", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

        homeHistoryList.let {
            it.setLayoutManager(LinearLayoutManager(this))
            it.setOnEmptyViewCreatedListener(this)
//            itemTouchHelper.attachToRecyclerView(it.getRecyclerView())
            it.setAdapter(DungeonHistoryAdapter(arrayListOf(), sorters[0]), true)
        }

        val facetAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sorters.map { it.displayName })
        facetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        homeScreenSorter.apply {
            adapter = facetAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    changeSort(position)
                }
            }
            setSelection(0)
        }
    }

    fun changeSort(position: Int) {
        val newFilter = sorters.firstOrNull { it.id == position }

        newFilter?.let {newSort ->
            chosenSorter = newSort

            homeHistoryList.getRecyclerView()?.adapter?.let {
                if (it is DungeonHistoryAdapter) it.updateSort(newSort)
            }
        }?: run {
            Toast.makeText(applicationContext, "Can't apply that filter", Toast.LENGTH_LONG).show()
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