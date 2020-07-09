package com.joeshuff.dddungeongenerator.generator.features

import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier
import com.joeshuff.dddungeongenerator.generator.monsters.Bestiary
import com.joeshuff.dddungeongenerator.generator.monsters.Monster
import com.joeshuff.dddungeongenerator.util.Logs
import java.util.*
import kotlin.math.max

class MonsterFeature(seed: String?, @field:Transient var modifier: Modifier) : RoomFeature {
    var size: Int
    var selectedMonster: Monster? = null

    @Transient
    var rnd: Random = Random(Dungeon.stringToSeed(seed))

    companion object {
        @Transient
        private val DEFAULT_BOSS_CHANCE = 0.3
    }

    init {
        size = max(1, (rnd.nextGaussian() * 2 + modifier.monsterAverageGroupSize).toInt())
        generateMonster()
    }

    var isBoss = false
    private fun generateMonster() {
        if (size == 1) {
            if (rnd.nextDouble() <= (DEFAULT_BOSS_CHANCE * modifier.bossChanceModifier)) {
                selectedMonster = Bestiary.getBoss(rnd, modifier, false)
                if (selectedMonster != null) {
                    isBoss = true
                } else {
                    Logs.i("MonsterFeature", "EMPTY BOSS MONSTER", null)
                }
            }
        }

        if (selectedMonster != null) return
        selectedMonster = Bestiary.getMonster(rnd, modifier, false)

        if (selectedMonster == null) {
            Logs.i("MonsterFeature", "EMPTY MONSTER", null)
        }
    }

    override fun getFeatureDescription(): String {
        if (isBoss) {
            return "there is a boss in this room. A " + selectedMonster!!.name + " resides here"
        }
        
        return if (size > 1) {
            "there are " + size + " " + selectedMonster!!.name + "'s in this room, be careful!"
        } else {
            "there is a " + selectedMonster!!.name + " in this room. Be careful!"
        }
    }

    override fun getFeatureName(): String {
        return "Monster(s)"
    }

    override fun getPageForMoreInfo(): Int {
        return 0
    }
}