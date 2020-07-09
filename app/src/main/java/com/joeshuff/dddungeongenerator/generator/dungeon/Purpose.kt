package com.joeshuff.dddungeongenerator.generator.dungeon

import java.util.*

object Purpose {
    fun getPurpose(rnd: Random): PURPOSE {
        val d20 = rnd.nextInt(20) + 1
        if (d20 <= 1) return PURPOSE.DEATH_TRAP
        if (d20 <= 5) return PURPOSE.LAIR
        if (d20 <= 6) return PURPOSE.MAZE
        if (d20 <= 9) return PURPOSE.MINE
        if (d20 <= 10) return PURPOSE.PLANAR_GATE
        if (d20 <= 14) return PURPOSE.STRONGHOLD
        if (d20 <= 17) return PURPOSE.TEMPLE
        if (d20 <= 19) return PURPOSE.TOMB
        return if (d20 <= 20) PURPOSE.TREASURE_VAULT else PURPOSE.TREASURE_VAULT
    }

    enum class PURPOSE(var title: String, //MORE MONSTER AND TRAP CHANCE
                       var description: String, var modifier: Modifier) {
        DEATH_TRAP("Death trap", """
     This dungeon is built to eliminate any
     creature that dares to enter it. A death trap might guard
     the treasure of an insane wizard, or it might be designed
     to lure adventurers to their demise for some nefarious
     purpose.
     """.trimIndent(),
                Modifier().setTrapChanceModifier(2.0).setTreasureChanceModifier(2.0)),
        LAIR("Lair", """
     A lair is a place where monsters live. Typical
     lairs include ruins and caves.
     """.trimIndent(),
                Modifier().setMonsterChanceModifier(1.5).setMonsterAverageGroupSize(5)),  //MORE MONSTERS
        MAZE("Maze", """
     A maze is intended to deceive or confuse
     those who enter it. Some mazes are elaborate obstacles
     that protect treasure, while others are gauntlets for
     prisoners banished there to be hunted and devoured by
     the monsters within.
     """.trimIndent(),
                Modifier().setTriangulationAdditionChance(45).setTreasureChanceModifier(1.5).setStairChanceModifier(1.5)),  //WILL NEED TO MODIFY THE CHANCE OF TRIANGULATION PATHS ADDED, MORE MONSTERS and TREASURE
        MINE("Mine", """
     An abandoned mine can quickly become
     infested with monsters, while miners who delve too
     deep can break through into the underdark.
     """.trimIndent(),
                Modifier().setMonsterChanceModifier(2.0).setStairChanceModifier(1.5).setGrowthChanceUp(30)),  //MORE MONSTERS
        PLANAR_GATE("Planar gate", """
     Dungeons built around planar portals
     are often transformed by the planar energy seeping out
     through those portals.
     """.trimIndent(),
                Modifier().setBossChanceModifier(2.5)),  //WIZARDS
        STRONGHOLD("Stronghold", """
     A stronghold dungeon provides a secure
     base of operations for villains and monsters. It is
     usually ruled by a powerful individual, such as a wizard,
     vampire, or dragon, and it is larger and more complex
     than a simple lair.
     """.trimIndent(),
                Modifier().setBossChanceModifier(3.0).setMonsterChanceModifier(2.0)),  //larger coverage
        TEMPLE("Temple with a shrine", """
     This dungeon is consecrated to
     a deity or other planar entity. The entity's worshipers
     control the dungeon and conduct their rites there.
     """.trimIndent(),
                Modifier().setMonsterChanceModifier(1.5)),  //MORE MONSTERS
        TOMB("Tomb", """
     Tombs are magnets for treasure hunters, as
     well as monsters that hunger for the bones of the dead.
     """.trimIndent(),
                Modifier().setTreasureChanceModifier(2.0)),  //MORE TREASURE
        TREASURE_VAULT("Treasure vault", """
     Built to protect powerful magic items
     and great material wealth, treasure vault dungeons are
     heavily guarded by monsters and traps.
     """.trimIndent(),
                Modifier().setTreasureChanceModifier(5.0).setMonsterAverageGroupSize(5).setMonsterChanceModifier(1.5));

    }
}