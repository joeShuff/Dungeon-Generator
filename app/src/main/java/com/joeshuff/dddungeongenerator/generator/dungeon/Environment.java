package com.joeshuff.dddungeongenerator.generator.dungeon;

import java.io.Serializable;
import java.util.Random;

public class Environment  {

    public enum ENVIRONMENT_TYPE  {

        CITY("A building in a city", new Modifier().setTrapMagicalChance(20)),

        SEWERS("Catacombs or sewers beneath a city", new Modifier().setTrapMagicalChance(20).setStairChanceModifier(1.5d).setGrowthChanceUp(30)),

        FARMYARD("Beneath a farmhouse", new Modifier().setTrapMagicalChance(50)),
        GRAVEYARD("Beneath a graveyard", new Modifier().setTrapMagicalChance(70)),
        RUINED_CASTLE("Beneath a ruined castle", new Modifier().setTrapMagicalChance(60).setStairChanceModifier(1.5d).setGrowthChanceUp(70)),
        RUINED_CITY("Beneath a ruined city", new Modifier().setTrapMagicalChance(50)),
        TEMPLE("Beneath a temple", new Modifier().setTrapMagicalChance(70)),
        CHASM("In a chasm", new Modifier().setTrapMagicalChance(20).setStairChanceModifier(1.2d).setGrowthChanceUp(30)),
        CLIFF_FACE("In a cliff face", new Modifier().setTrapMagicalChance(20)),
        DESERT("In a desert", new Modifier().setTrapMagicalChance(50)),
        FOREST("In a forest", new Modifier().setTrapMagicalChance(60)),
        GLACIER("In a glacier", new Modifier().setTrapMagicalChance(60)),
        GORGE("In a gorge", new Modifier().setTrapMagicalChance(50).setStairChanceModifier(1.2d).setGrowthChanceUp(30)),
        JUNGLE("In a jungle", new Modifier().setTrapMagicalChance(60)),
        MOUNTAIN_PASS("In a mountain pass", new Modifier().setTrapMagicalChance(50)),
        SWAMP("In a swamp", new Modifier().setTrapMagicalChance(70)),
        MESA("Beneath or on top of a mesa", new Modifier().setTrapMagicalChance(40)),
        SEA_CAVES("In sea caves", new Modifier().setTrapMagicalChance(50).setStairChanceModifier(1.3d).setGrowthChanceUp(20)),
        CONNECTED_MESAS("In several connected mesas", new Modifier().setTrapMagicalChance(40)),
        MOUNTAIN("On a mountain peak", new Modifier().setTrapMagicalChance(20).setStairChanceModifier(1.3d).setGrowthChanceUp(20)),
        PROMONTORY("On a promontory", new Modifier().setTrapMagicalChance(20)),
        ISLAND("On an island", new Modifier().setTrapMagicalChance(50)),
        UNDERWATER("Underwater", new Modifier().setTrapMagicalChance(60)),
        CASTLE("Exploring a castle", new Modifier().setTrapMagicalChance(70).setStairChanceModifier(1.4d).setGrowthChanceUp(80));

        String description;

        Modifier modifier;

        ENVIRONMENT_TYPE(String description, Modifier modifier) {
            this.description = description;
            this.modifier = modifier;
        }

        public String getDescription() {
            return description;
        }

        public Modifier getModifier() {
            return modifier;
        }
    }

    public static ENVIRONMENT_TYPE generateEnvironmentType(Random rnd) {
        int d100Roll = rnd.nextInt(99) + 1;

        if (d100Roll <= 4) return ENVIRONMENT_TYPE.CITY;
        if (d100Roll <= 8) return ENVIRONMENT_TYPE.SEWERS;
        if (d100Roll <= 12) return ENVIRONMENT_TYPE.FARMYARD;
        if (d100Roll <= 16) return ENVIRONMENT_TYPE.GRAVEYARD;
        if (d100Roll <= 22) return ENVIRONMENT_TYPE.RUINED_CASTLE;
        if (d100Roll <= 26) return ENVIRONMENT_TYPE.RUINED_CITY;
        if (d100Roll <= 30) return ENVIRONMENT_TYPE.TEMPLE;
        if (d100Roll <= 34) return ENVIRONMENT_TYPE.CHASM;
        if (d100Roll <= 38) return ENVIRONMENT_TYPE.CLIFF_FACE;
        if (d100Roll <= 42) return ENVIRONMENT_TYPE.DESERT;
        if (d100Roll <= 46) return ENVIRONMENT_TYPE.FOREST;
        if (d100Roll <= 50) return ENVIRONMENT_TYPE.GLACIER;
        if (d100Roll <= 54) return ENVIRONMENT_TYPE.GORGE;
        if (d100Roll <= 58) return ENVIRONMENT_TYPE.JUNGLE;
        if (d100Roll <= 62) return ENVIRONMENT_TYPE.MOUNTAIN_PASS;
        if (d100Roll <= 66) return ENVIRONMENT_TYPE.SWAMP;
        if (d100Roll <= 70) return ENVIRONMENT_TYPE.MESA;
        if (d100Roll <= 74) return ENVIRONMENT_TYPE.SEA_CAVES;
        if (d100Roll <= 78) return ENVIRONMENT_TYPE.CONNECTED_MESAS;
        if (d100Roll <= 82) return ENVIRONMENT_TYPE.MOUNTAIN;
        if (d100Roll <= 86) return ENVIRONMENT_TYPE.PROMONTORY;
        if (d100Roll <= 90) return ENVIRONMENT_TYPE.ISLAND;
        if (d100Roll <= 95) return ENVIRONMENT_TYPE.UNDERWATER;

        return ENVIRONMENT_TYPE.CASTLE;

        //TODO: IMPLEMENT THE `EXOTIC LOCATION` TABLE
    }
}
