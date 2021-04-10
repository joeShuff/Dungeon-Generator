package com.joeshuff.dddungeongenerator.memory

import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier
import java.text.SimpleDateFormat
import java.util.Date

class MemoryGeneration(val seed: String,
                       val roomSize: Int,
                       val longCorridors: Boolean,
                       val monsterFrequency: Double,
                       val trapFrequency: Double,
                       val treasureFrequency: Double,
                       val stairsFrequency: Double,
                       val depth: Int) : Comparable<MemoryGeneration> {
    var epoch: Int
    val generatedAt: String
    val isLoops = false

    val userModifier: Modifier
        get() = Modifier()
                .setMonsterChanceModifier(monsterFrequency)
                .setTrapChanceModifier(trapFrequency)
                .setTreasureChanceModifier(treasureFrequency)
                .setStairChanceModifier(stairsFrequency)
                .setGrowthChanceUp(depth)

    override fun equals(obj: Any?): Boolean {
        return if (obj !is MemoryGeneration) false else obj.epoch == epoch
    }

    override fun compareTo(o: MemoryGeneration): Int {
        val x = o.epoch
        val y = epoch
        return if (x < y) -1 else if (x == y) 0 else 1
    }

    fun workOutDungeonName(): String {
        val test = Dungeon()
        test.setSeed(seed)
        test.generateAttributes()
        return test.getName()
    }

    init {
        val sdf = SimpleDateFormat("dd MMM yyyy | HH:mm:ss")
        epoch = (System.currentTimeMillis() / 1000.0).toInt()
        generatedAt = sdf.format(Date())
    }
}