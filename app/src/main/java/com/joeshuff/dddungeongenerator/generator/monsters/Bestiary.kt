package com.joeshuff.dddungeongenerator.generator.monsters

import android.content.Context
import com.google.gson.Gson
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier
import com.joeshuff.dddungeongenerator.generator.monsters.MonsterClass.MONSTER_CLASS
import org.json.JSONArray
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

object Bestiary {
    private val monsterList: ArrayList<Monster> = ArrayList()

    var bossSize = listOf("large", "gargantuan", "huge")

    private fun loadJSONFromAsset(c: Context): String? {
        return try {
            val inputstream = c.assets.open("monsters.json")
            val size = inputstream.available()
            val buffer = ByteArray(size)
            inputstream.read(buffer)
            inputstream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    fun launchBestiary(c: Context) {
        try {
            val jsonObject = JSONArray(loadJSONFromAsset(c)?: "[]")
            for (i in 0 until jsonObject.length()) {
                monsterList.add(Gson().fromJson(jsonObject.getJSONObject(i).toString(), Monster::class.java))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTypes(): List<String?>? {
        val res: MutableList<String?> = ArrayList()
        for (m in monsterList) {
            if (res.contains(m.type)) continue
            res.add(m.type)
        }
        return res
    }

    fun getMonster(rnd: Random, mod: Modifier, ignorePrefs: Boolean, boss: Boolean = false): Monster {
        //A boss is defined as a monster of size
        val preferredTypes: ArrayList<String> = ArrayList(mod.getPreferredMonsters().map { it.type })

        if (preferredTypes.isEmpty() || ignorePrefs) {
            preferredTypes.clear()
            preferredTypes.addAll(MONSTER_CLASS.values().map { it.type })
        }

        val ignoreTypes = mod.getBlockedMonsters().map { it.type }

        val preferred = monsterList.filter {
            ((boss && bossSize.contains(it.size?.toLowerCase())) || (!boss && !bossSize.contains(it.size?.toLowerCase()))) &&
                    preferredTypes.contains(it.type) &&
                    !ignoreTypes.contains(it.type)
        }

        return if (preferred.isEmpty()) getMonster(rnd, mod, true, boss) else preferred.random()
    }

    fun getBoss(rnd: Random, mod: Modifier, ignorePrefs: Boolean): Monster {
        return getMonster(rnd, mod, ignorePrefs, true)
    }
}