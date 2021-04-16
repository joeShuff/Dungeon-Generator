package com.joeshuff.dddungeongenerator.screens.create

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.google.gson.Gson
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.db.dungeonDao
import com.joeshuff.dddungeongenerator.generator.DungeonProcessor
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.monsters.Bestiary
import com.joeshuff.dddungeongenerator.memory.MemoryController
import com.joeshuff.dddungeongenerator.memory.MemoryGeneration
import com.joeshuff.dddungeongenerator.screens.viewdungeon.ResultsActivity
import com.joeshuff.dddungeongenerator.util.Logs
import io.reactivex.disposables.Disposable
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_generating.*

class GeneratingActivity : AppCompatActivity() {

    companion object {
        const val GENERATION_INSTRUCTIONS = "gen_instructions"
    }

    @Transient
    var leftMidGeneration = false

    var disposables = arrayListOf<Disposable>()

    @Transient
    var myTrace: Trace? = null

    var instructions: MemoryGeneration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generating)
        myTrace = FirebasePerformance.getInstance().newTrace("generate_dungeon")
        startGenerating()
    }

    private fun startGenerating() {
        val foundInstructions = Gson().fromJson(intent.getStringExtra(GENERATION_INSTRUCTIONS), MemoryGeneration::class.java)
        var newDungeon = Dungeon(0, 0, 800, 800, foundInstructions.seed)

        newDungeon?.let {
            it.setRoomSize(foundInstructions.roomSize)
            it.setLongCorridors(foundInstructions.longCorridors)
            it.setLinearProgression(foundInstructions.isLoops)
            it.setUserModifier(foundInstructions.userModifier)
        }

        instructions = foundInstructions

        myTrace?.start()

        DungeonProcessor(disposables, newDungeon)
                .beginProcessing({
                    setProgressText(it)
                }, {
                    onCompleted(it)
                }, {
                    Logs.e("GENERATE", "Error when generating a dungeon ${newDungeon.getSeed()}", it)
                    Toast.makeText(this, "Something went wrong generating this dungeon. This has been reported.", Toast.LENGTH_LONG).show()
                    finish()
                })
    }

    fun onCompleted(dungeon: Dungeon) {
        myTrace?.stop()

        instructions?.let {
            MemoryController.removeFromMemory(applicationContext, it)
        }

        Realm.getDefaultInstance().dungeonDao().addDungeon(dungeon)
                .subscribe({
                    val results = Intent(this, ResultsActivity::class.java)
                    results.putExtra(ResultsActivity.DUNGEON_ID_EXTRA, dungeon.id)
                    startActivity(results)
                    finish()
                }, {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                    finish()
                })
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
}