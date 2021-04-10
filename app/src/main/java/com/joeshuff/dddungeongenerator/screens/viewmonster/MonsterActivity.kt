package com.joeshuff.dddungeongenerator.screens.viewmonster

import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.joeshuff.dddungeongenerator.R
import com.joeshuff.dddungeongenerator.RecyclerViewEmptySupport
import com.joeshuff.dddungeongenerator.generator.monsters.Monster
import com.joeshuff.dddungeongenerator.util.FirebaseTracker
import kotlinx.android.synthetic.main.activity_monster_display.*
import kotlinx.android.synthetic.main.monster_stat_view.*

class MonsterActivity : AppCompatActivity() {

    var displayingMonster: Monster? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monster_display)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        try {
            displayingMonster = Gson().fromJson(intent.getStringExtra("monster"), Monster::class.java)
        } catch (e: Exception) {
        }

        displayingMonster?.let {
            title = it.name
            loadMonsterDetails(it)
        }?: run {
            Toast.makeText(this, "No Monster Found", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun loadMonsterDetails(monster: Monster) {
        topDesc.text = monster.size + " " + monster.type + ", " + monster.alignment
        armorClass.text = Html.fromHtml("<b>Armor Class</b>    " + monster.armorClass)
        hitpoints.text = Html.fromHtml("<b>Hit Points</b>    " + monster.hitPoints)
        speed.text = Html.fromHtml("<b>Speed</b>    " + monster.speed)
        strengthValue.text = "${monster.strength} " + monster.getModifierFor(monster.strength.toLong())
        dexValue.text = "${monster.dexterity} ${monster.getModifierFor(monster.dexterity.toLong())}"
        consValue.text = "${monster.constitution} " + monster.getModifierFor(monster.constitution.toLong())
        intelligenceValue.text = "${monster.intelligence} " + monster.getModifierFor(monster.intelligence.toLong())
        wisdomValue.text = "${monster.wisdom} " + monster.getModifierFor(monster.wisdom.toLong())
        charismaValue.text = "${monster.charisma} " + monster.getModifierFor(monster.charisma.toLong())
        savingThrows.text = Html.fromHtml("<b>Saving Throws</b>    " + monster.getSavingThrows())
        senses.text = Html.fromHtml("<b>Senses</b>    " + monster.senses)
        languages.text = Html.fromHtml("<b>Languages</b>    " + monster.languages)
        challenge.text = Html.fromHtml("<b>Challenge</b>    " + monster.challengeRating + " (" + monster.getChallengeXp() + " XP)")

        specialActionsList.setEmptyView(findViewById(R.id.noSpecialActions))
        specialActionsList.layoutManager = LinearLayoutManager(this)
        specialActionsList.adapter = ActionAdapter(monster.specialAbilities?: emptyList())

        actionsList.setEmptyView(findViewById(R.id.noActions))
        actionsList.layoutManager = LinearLayoutManager(this)
        actionsList.adapter = ActionAdapter(monster.actions?: emptyList())

        legendaryActionsList.setEmptyView(findViewById(R.id.noLegendaryActions))
        legendaryActionsList.layoutManager = LinearLayoutManager(this)
        legendaryActionsList.adapter = ActionAdapter(monster.legendaryActions?: emptyList())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.monster_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
            R.id.monster_external -> {
                displayingMonster?.let {
                    FirebaseTracker.EVENT(this, "EXTERNAL MONSTER", "Showing Monster: " + it.name)
                    val builder = CustomTabsIntent.Builder()
                    builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(this, Uri.parse("https://www.dndbeyond.com/monsters/" + (it.name?: "").toLowerCase().replace(" ", "-")))
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}