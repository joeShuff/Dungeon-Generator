package com.joeshuff.dddungeongenerator.generator.dungeon

import java.util.*

object Environment {
    fun generateEnvironmentType(rnd: Random): ENVIRONMENT_TYPE {
        val d100Roll = rnd.nextInt(99) + 1

        if (d100Roll <= 4) return ENVIRONMENT_TYPE.CITY
        if (d100Roll <= 8) return ENVIRONMENT_TYPE.SEWERS
        if (d100Roll <= 12) return ENVIRONMENT_TYPE.FARMYARD
        if (d100Roll <= 16) return ENVIRONMENT_TYPE.GRAVEYARD
        if (d100Roll <= 22) return ENVIRONMENT_TYPE.RUINED_CASTLE
        if (d100Roll <= 26) return ENVIRONMENT_TYPE.RUINED_CITY
        if (d100Roll <= 30) return ENVIRONMENT_TYPE.TEMPLE
        if (d100Roll <= 34) return ENVIRONMENT_TYPE.CHASM
        if (d100Roll <= 38) return ENVIRONMENT_TYPE.CLIFF_FACE
        if (d100Roll <= 42) return ENVIRONMENT_TYPE.DESERT
        if (d100Roll <= 46) return ENVIRONMENT_TYPE.FOREST
        if (d100Roll <= 50) return ENVIRONMENT_TYPE.GLACIER
        if (d100Roll <= 54) return ENVIRONMENT_TYPE.GORGE
        if (d100Roll <= 58) return ENVIRONMENT_TYPE.JUNGLE
        if (d100Roll <= 62) return ENVIRONMENT_TYPE.MOUNTAIN_PASS
        if (d100Roll <= 66) return ENVIRONMENT_TYPE.SWAMP
        if (d100Roll <= 70) return ENVIRONMENT_TYPE.MESA
        if (d100Roll <= 74) return ENVIRONMENT_TYPE.SEA_CAVES
        if (d100Roll <= 78) return ENVIRONMENT_TYPE.CONNECTED_MESAS
        if (d100Roll <= 82) return ENVIRONMENT_TYPE.MOUNTAIN
        if (d100Roll <= 86) return ENVIRONMENT_TYPE.PROMONTORY
        if (d100Roll <= 90) return ENVIRONMENT_TYPE.ISLAND
        return if (d100Roll <= 95) ENVIRONMENT_TYPE.UNDERWATER else ENVIRONMENT_TYPE.CASTLE

        //TODO: IMPLEMENT THE `EXOTIC LOCATION` TABLE
    }

    enum class ENVIRONMENT_TYPE(var description: String, var modifier: Modifier) {
        CITY("A building in a city", Modifier().setTrapMagicalChance(20)),

        SEWERS("Catacombs or sewers beneath a city", Modifier().setTrapMagicalChance(20).setStairChanceModifier(1.5).setGrowthChanceUp(30)),

        FARMYARD("Beneath a farmhouse", Modifier().setTrapMagicalChance(50)),

        GRAVEYARD("Beneath a graveyard", Modifier().setTrapMagicalChance(70)),

        RUINED_CASTLE("Beneath a ruined castle", Modifier().setTrapMagicalChance(60).setStairChanceModifier(1.5).setGrowthChanceUp(70)),
        RUINED_CITY("Beneath a ruined city", Modifier().setTrapMagicalChance(50)),
        TEMPLE("Beneath a temple", Modifier().setTrapMagicalChance(70)),
        CHASM("In a chasm", Modifier().setTrapMagicalChance(20).setStairChanceModifier(1.2).setGrowthChanceUp(30)),
        CLIFF_FACE("In a cliff face", Modifier().setTrapMagicalChance(20)),
        DESERT("In a desert", Modifier().setTrapMagicalChance(50)),
        FOREST("In a forest", Modifier().setTrapMagicalChance(60)),
        GLACIER("In a glacier", Modifier().setTrapMagicalChance(60)),
        GORGE("In a gorge", Modifier().setTrapMagicalChance(50).setStairChanceModifier(1.2).setGrowthChanceUp(30)),
        JUNGLE("In a jungle", Modifier().setTrapMagicalChance(60)),
        MOUNTAIN_PASS("In a mountain pass", Modifier().setTrapMagicalChance(50)),
        SWAMP("In a swamp", Modifier().setTrapMagicalChance(70)),
        MESA("Beneath or on top of a mesa", Modifier().setTrapMagicalChance(40)),
        SEA_CAVES("In sea caves", Modifier().setTrapMagicalChance(50).setStairChanceModifier(1.3).setGrowthChanceUp(20)),
        CONNECTED_MESAS("In several connected mesas", Modifier().setTrapMagicalChance(40)),
        MOUNTAIN("On a mountain peak", Modifier().setTrapMagicalChance(20).setStairChanceModifier(1.3).setGrowthChanceUp(20)),
        PROMONTORY("On a promontory", Modifier().setTrapMagicalChance(20)),
        ISLAND("On an island", Modifier().setTrapMagicalChance(50)),
        UNDERWATER("Underwater", Modifier().setTrapMagicalChance(60)),
        CASTLE("Exploring a castle", Modifier().setTrapMagicalChance(70).setStairChanceModifier(1.4).setGrowthChanceUp(80));
    }
}