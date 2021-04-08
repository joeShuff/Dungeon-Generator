package com.joeshuff.dddungeongenerator.screens.create

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.memory.MemoryController
import com.joeshuff.dddungeongenerator.memory.MemoryGeneration
import com.joeshuff.dddungeongenerator.util.FirebaseTracker
import com.warkiz.tickseekbar.TickSeekBar
import kotlinx.android.synthetic.main.activity_new.*
import org.json.JSONException
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class NewDungeonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Configure Dungeon"

        initialiseControls()
    }

    private fun initialiseControls() {
        scrollContent.setOnScrollChangeListener { v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val view = scrollContent.getChildAt(scrollContent.childCount - 1) as View
            val diff = view.bottom - (scrollContent.height + scrollY)
            if (diff == 0) {
                //HIDE TEXT
                scrollDownPrompt.animate().alpha(0f).setDuration(200).start()
            } else {
                scrollDownPrompt.animate().alpha(1f).setDuration(200).start()
            }
        }

        scrollContent.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val viewHeight = scrollContent.measuredHeight
                val contentHeight = scrollContent.getChildAt(0).height
                if (viewHeight - contentHeight < 0) {
                    // scrollable
                } else {
                    scrollDownPrompt.animate().alpha(0f).start()
                }
                scrollContent.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        longerCorridorsSwitch.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                longerCorridorsStatus.text = "The longest corridors will be prioritised"
            } else {
                longerCorridorsStatus.text = "The shortest corridors will be prioritised"
            }
        }

        generateButton.setOnClickListener { genButtonClicked() }
    }

    private fun genButtonClicked() {
        val generator = Intent(this, GeneratingActivity::class.java)
        var seed = seedInput.text.toString()

        if (seed.isEmpty()) {
            seed = System.currentTimeMillis().toString() + ""
            val list = seed.chars().mapToObj { c: Int -> c.toChar() }.collect(Collectors.toList())
            list.shuffle()
            val sb = StringBuilder()
            list.forEach(Consumer { c: Char? -> sb.append(c) })
            seed = sb.toString()
        }

        FirebaseTracker.EVENT(this, "DungeonCreate", "CreatedWithSeed:$seed")
        val memory = MemoryGeneration(seed,
                roomSizeBar.progress,
                longerCorridorsSwitch.isChecked,
                0.5 + monster_frequency.progressFloat / 100.0f * 2.5f,
                0.5 + trap_frequency.progressFloat / 100.0f * 2.5f,
                0.5 + treasure_frequency.progressFloat / 100.0f * 2.5f,
                (1 + Math.abs(dungeon_depth.progress - 50) / 50 * 2).toDouble(),
                dungeon_depth.progress
        )

        try {
            MemoryController.addToMemory(applicationContext, memory)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        generator.putExtra("instructions", Gson().toJson(memory))
        startActivity(generator)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return false
    }
}