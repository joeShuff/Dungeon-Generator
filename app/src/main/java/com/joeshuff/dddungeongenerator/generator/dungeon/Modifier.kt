package com.joeshuff.dddungeongenerator.generator.dungeon

import com.joeshuff.dddungeongenerator.generator.monsters.MonsterClass.MONSTER_CLASS
import java.util.*
import kotlin.math.ceil

class Modifier {
    var monsterChanceModifier = 0.0

    var monsterAverageGroupSize = 0 //default 3

    var trapChanceModifier = 0.0

    var trapMagicalChance = 0 //default 50

    var treasureChanceModifier = 0.0

    var mapCoveragePercentage = 0 //default 65

    var triangulationAdditionChance = 0 //default 15

    var bossChanceModifier = 0.0

    /**
     * This is the chance of, when a stair feature is generated, those stairs going up. The change of them going down
     * is 100 - this
     */
    var growthChanceUp = 0.0 //default 0.5

    var stairChanceModifier = 0.0

    private var preferredMonsters: MutableList<MONSTER_CLASS> = ArrayList()

    private var blockedMonsters: MutableList<MONSTER_CLASS> = ArrayList()

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

    val triangulationAdditionChange: Double
        get() = triangulationAdditionChance / 100.0

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
            val averageMonsterChance = modifiers.filter { it.monsterChanceModifier > 0.0 }.map { it.monsterChanceModifier }.average()
            val averageMonsterAmount = modifiers.filter { it.monsterAverageGroupSize > 0 }.map { it.monsterAverageGroupSize }.average()
            val averageTrapModifier = modifiers.filter { it.trapChanceModifier > 0 }.map { it.trapChanceModifier }.average()
            val averageTrapMagicalChance = modifiers.filter { it.trapMagicalChance > 0 }.map { it.trapMagicalChance }.average()
            val averageTreasureChanceModifier = modifiers.filter { it.treasureChanceModifier > 0 }.map { it.treasureChanceModifier }.average()
            val averageMapCoveragePercentage = modifiers.filter { it.mapCoveragePercentage > 0 }.map { it.mapCoveragePercentage }.average()
            val averageTriangulationPercentage = modifiers.filter { it.triangulationAdditionChance > 0 }.map { it.triangulationAdditionChance }.average()
            val averageBossChanceModifier = modifiers.filter { it.bossChanceModifier > 0 }.map { it.bossChanceModifier }.average()
            val averageGrowthUpChance = modifiers.filter { it.growthChanceUp > 0 }.map { it.growthChanceUp }.average()
            val averageStairsChangeModifier = modifiers.filter { it.stairChanceModifier > 0 }.map { it.stairChanceModifier }.average()

            finalModifier.monsterChanceModifier = averageMonsterChance
            finalModifier.monsterAverageGroupSize = ceil(averageMonsterAmount).toInt()
            finalModifier.trapChanceModifier = averageTrapModifier
            finalModifier.trapMagicalChance = ceil(averageTrapMagicalChance).toInt()
            finalModifier.treasureChanceModifier = averageTreasureChanceModifier
            finalModifier.mapCoveragePercentage = ceil(averageMapCoveragePercentage).toInt()
            finalModifier.triangulationAdditionChance = ceil(averageTriangulationPercentage).toInt()
            finalModifier.bossChanceModifier = averageBossChanceModifier
            finalModifier.growthChanceUp = averageGrowthUpChance
            finalModifier.stairChanceModifier = averageStairsChangeModifier

            modifiers.forEach { modifier: Modifier -> finalModifier.preferredMonsters.addAll(modifier.getPreferredMonsters()) }
            modifiers.forEach { modifier: Modifier -> finalModifier.blockedMonsters.addAll(modifier.getBlockedMonsters()) }
            return finalModifier
        }
    }
}