package com.joeshuff.dddungeongenerator.generator.dungeon

import com.joeshuff.dddungeongenerator.generator.monsters.MonsterClass.MONSTER_CLASS
import com.joeshuff.dddungeongenerator.util.ifNotEmpty
import io.realm.RealmList
import io.realm.RealmObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.ceil

open class Modifier: RealmObject() {
    var monsterChanceModifier = 0.0
        private set

    var monsterAverageGroupSize = 0 //default 3
        private set

    var trapChanceModifier = 0.0
        private set

    var trapMagicalChance = 0 //default 50
        private set

    var treasureChanceModifier = 0.0
        private set

    var mapCoveragePercentage = 0 //default 65
        private set

    var triangulationAdditionChance = 0 //default 15
        private set

    var bossChanceModifier = 0.0
        private set

    /**
     * This is the chance of, when a stair feature is generated, those stairs going up. The change of them going down
     * is 100 - this
     */
    var growthChanceUp = 0.0 //default 0.5
        private set

    var stairChanceModifier = 0.0
        private set

    private var internalPreferredMonsters: RealmList<String> = RealmList()
    private var preferredMonsters: ArrayList<MONSTER_CLASS>
        get() { return ArrayList(internalPreferredMonsters.map { MONSTER_CLASS.valueOf(it) }) }
        set(value) {
            internalPreferredMonsters.clear()
            internalPreferredMonsters.addAll(value.map { it.name })
        }

    private var internalBlockMonsters: RealmList<String> = RealmList()
    private var blockedMonsters: ArrayList<MONSTER_CLASS>
        get() { return ArrayList(internalBlockMonsters.map { MONSTER_CLASS.valueOf(it) }) }
        set(value) {
            internalBlockMonsters.clear()
            internalBlockMonsters.addAll(value.map { it.name })
        }


    fun loadDefaults() {
        monsterChanceModifier = 1.0
        monsterAverageGroupSize = 3
        trapChanceModifier = 1.0
        trapMagicalChance = 50
        treasureChanceModifier = 1.0
        mapCoveragePercentage = 65
        triangulationAdditionChance = 15
        bossChanceModifier = 1.0
        growthChanceUp = 0.5
        stairChanceModifier = 1.0
    }

    fun setMonsterChanceModifier(monsterChanceModifier: Double): Modifier {
        this.monsterChanceModifier = monsterChanceModifier
        return this
    }

    fun setTrapChanceModifier(trapChanceModifier: Double): Modifier {
        this.trapChanceModifier = trapChanceModifier
        return this
    }

    fun setTrapMagicalChance(trapMagicalChance: Int): Modifier {
        this.trapMagicalChance = trapMagicalChance
        return this
    }

    fun setTreasureChanceModifier(treasureChanceModifier: Double): Modifier {
        this.treasureChanceModifier = treasureChanceModifier
        return this
    }

    fun setMapCoveragePercentage(mapCoveragePercentage: Int): Modifier {
        this.mapCoveragePercentage = mapCoveragePercentage
        return this
    }

    fun setTriangulationAdditionChance(triangulationAdditionChance: Int): Modifier {
        this.triangulationAdditionChance = triangulationAdditionChance
        return this
    }

    fun setBossChanceModifier(bossChanceModifier: Double): Modifier {
        this.bossChanceModifier = bossChanceModifier
        return this
    }

    fun setMonsterAverageGroupSize(monsterAverageGroupSize: Int): Modifier {
        this.monsterAverageGroupSize = monsterAverageGroupSize
        return this
    }

    fun setBlockedMonsters(blockedMonsters: List<MONSTER_CLASS>): Modifier {
        this.blockedMonsters.clear()
        this.blockedMonsters.addAll(blockedMonsters)
        return this
    }

    fun setPreferredMonsters(preferredMonsters: List<MONSTER_CLASS>): Modifier {
        this.preferredMonsters.clear()
        this.preferredMonsters.addAll(preferredMonsters)
        return this
    }

    fun setGrowthChanceUp(growthChanceUp: Int): Modifier {
        this.growthChanceUp = growthChanceUp / 100.0f.toDouble()
        return this
    }

    fun setStairChanceModifier(stairChanceModifier: Double): Modifier {
        this.stairChanceModifier = stairChanceModifier
        return this
    }

    fun getMapCoveragePercentage(): Double {
        return mapCoveragePercentage / 100.0
    }

    fun getTriangulationAdditionChange(): Double {
        return triangulationAdditionChance / 100.0
    }

    fun getTrapMagicalChance(): Double {
        return trapMagicalChance / 100.0
    }

    fun getPreferredMonsters(): List<MONSTER_CLASS> {
        return preferredMonsters
    }

    fun getBlockedMonsters(): List<MONSTER_CLASS> {
        return blockedMonsters
    }

    companion object {
        fun combineModifiers(modifiers: List<Modifier>, defaults: Boolean): Modifier {
            val finalModifier = Modifier()
            if (defaults) finalModifier.loadDefaults()
            val monsterChances = modifiers.filter { it.monsterChanceModifier > 0.0 }.map { it.monsterChanceModifier }
            val monsterAmounts = modifiers.filter { it.monsterAverageGroupSize > 0 }.map { it.monsterAverageGroupSize }
            val trapChances = modifiers.filter { it.trapChanceModifier > 0 }.map { it.trapChanceModifier }
            val trapMagicalChances = modifiers.filter { it.trapMagicalChance > 0 }.map { it.trapMagicalChance }
            val treasureChances = modifiers.filter { it.treasureChanceModifier > 0 }.map { it.treasureChanceModifier }
            val mapCoverages = modifiers.filter { it.mapCoveragePercentage > 0 }.map { it.mapCoveragePercentage }
            val triangulationAdditions = modifiers.filter { it.triangulationAdditionChance > 0 }.map { it.triangulationAdditionChance }
            val bossChances = modifiers.filter { it.bossChanceModifier > 0 }.map { it.bossChanceModifier }
            val growthChances = modifiers.filter { it.growthChanceUp > 0 }.map { it.growthChanceUp }
            val stairChances = modifiers.filter { it.stairChanceModifier > 0 }.map { it.stairChanceModifier }

            monsterChances.ifNotEmpty { finalModifier.monsterChanceModifier = it.average() }
            monsterAmounts.ifNotEmpty { finalModifier.monsterAverageGroupSize = ceil(it.average()).toInt() }
            trapChances.ifNotEmpty { finalModifier.trapChanceModifier = it.average() }
            trapMagicalChances.ifNotEmpty { finalModifier.trapMagicalChance = ceil(it.average()).toInt() }
            treasureChances.ifNotEmpty { finalModifier.treasureChanceModifier = it.average() }
            mapCoverages.ifNotEmpty { finalModifier.mapCoveragePercentage = ceil(it.average()).toInt() }
            triangulationAdditions.ifNotEmpty { finalModifier.triangulationAdditionChance = ceil(it.average()).toInt() }
            bossChances.ifNotEmpty { finalModifier.bossChanceModifier = it.average() }
            growthChances.ifNotEmpty { finalModifier.growthChanceUp = it.average() }
            stairChances.ifNotEmpty { finalModifier.stairChanceModifier = it.average() }

            modifiers.forEach { modifier: Modifier -> finalModifier.preferredMonsters.addAll(modifier.getPreferredMonsters()) }
            modifiers.forEach { modifier: Modifier -> finalModifier.blockedMonsters.addAll(modifier.getBlockedMonsters()) }
            return finalModifier
        }
    }
}