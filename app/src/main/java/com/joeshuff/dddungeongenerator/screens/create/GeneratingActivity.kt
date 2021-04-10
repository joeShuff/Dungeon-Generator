package com.joeshuff.dddungeongenerator.screens.create

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.generator.DungeonProcessor
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.features.*
import com.joeshuff.dddungeongenerator.generator.models.RuntimeTypeAdapterFactory
import com.joeshuff.dddungeongenerator.generator.monsters.Bestiary
import com.joeshuff.dddungeongenerator.memory.MemoryController
import com.joeshuff.dddungeongenerator.memory.MemoryGeneration
import com.joeshuff.dddungeongenerator.screens.viewdungeon.ResultsActivity
import com.joeshuff.dddungeongenerator.util.Logs
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_generating.*

class GeneratingActivity : AppCompatActivity() {
    @Transient
    var leftMidGeneration = false

    var disposables = arrayListOf<Disposable>()

    @Transient
    var myTrace: Trace? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generating)
        myTrace = FirebasePerformance.getInstance().newTrace("generate_dungeon")
        startGenerating()
    }

    private fun startGenerating() {
        val instructions = Gson().fromJson(intent.getStringExtra("instructions"), MemoryGeneration::class.java)
        var newDungeon = Dungeon(0, 0, 800, 800, instructions.seed)

        Bestiary.launchBestiary(this)

        newDungeon?.let {
            it.setRoomSize(instructions.roomSize)
            it.setLongCorridors(instructions.longCorridors)
            it.setLinearProgression(instructions.isLoops)
            it.setUserModifier(instructions.userModifier)
        }

        myTrace?.start()

        DungeonProcessor(disposables, newDungeon)
                .beginProcessing({
                    setProgressText(it)
                }, {
                    onCompleted(it)
                }, {
                    Logs.e("GENERATE", "Error when generating a dungeon", it)
                })
    }

    fun onCompleted(dungeon: Dungeon) {
        val results = Intent(this, ResultsActivity::class.java)
        val gson = GsonBuilder().registerTypeAdapterFactory(roomFeatureAdapter).create()
        MemoryController.saveToSharedPreferences(applicationContext, "RECENT_DUNGEON", gson.toJson(dungeon))

        myTrace?.stop()
        startActivity(results)
        finish()
    }

    fun setProgressText(text: String) {
        Logs.i("GenerationUpdate", text, null)
        runOnUiThread { progressText.text = text }
    }

    override fun onPause() {
        super.onPause()
        leftMidGeneration = true
    }

    override fun onResume() {
        super.onResume()
        if (leftMidGeneration) {
            Toast.makeText(this, "You interrupted generation.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.forEach { it.dispose() }
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Generating, please don't exit the app", Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmField
        @Transient
        var roomFeatureAdapter = RuntimeTypeAdapterFactory.of(RoomFeature::class.java, "type")
                .registerSubtype(TreasureFeature::class.java, "Treasure")
                .registerSubtype(MonsterFeature::class.java, "Monster")
                .registerSubtype(StairsFeature::class.java, "Stairs")
                .registerSubtype(TrapFeature::class.java, "Trap")
    }
}