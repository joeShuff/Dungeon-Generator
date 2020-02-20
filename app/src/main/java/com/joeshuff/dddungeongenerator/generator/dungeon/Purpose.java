package com.joeshuff.dddungeongenerator.generator.dungeon;

import java.util.Random;

public class Purpose {

    public enum PURPOSE  {
        DEATH_TRAP("Death trap", "This dungeon is built to eliminate any\n" +
                "creature that dares to enter it. A death trap might guard\n" +
                "the treasure of an insane wizard, or it might be designed\n" +
                "to lure adventurers to their demise for some nefarious\n" +
                "purpose.",
                new Modifier().setTrapChanceModifier(2d).setTreasureChanceModifier(2d)),

        LAIR("Lair", "A lair is a place where monsters live. Typical\n" +
                "lairs include ruins and caves.",
                new Modifier().setMonsterChanceModifier(1.5d).setMonsterAverageGroupSize(5)), //MORE MONSTERS

        MAZE("Maze", "A maze is intended to deceive or confuse\n" +
                "those who enter it. Some mazes are elaborate obstacles\n" +
                "that protect treasure, while others are gauntlets for\n" +
                "prisoners banished there to be hunted and devoured by\n" +
                "the monsters within.",
                new Modifier().setTriangulationAdditionChance(45).setTreasureChanceModifier(1.5d).setStairChanceModifier(1.5d)), //WILL NEED TO MODIFY THE CHANCE OF TRIANGULATION PATHS ADDED, MORE MONSTERS and TREASURE

        MINE("Mine", "An abandoned mine can quickly become\n" +
                "infested with monsters, while miners who delve too\n" +
                "deep can break through into the underdark.",
                new Modifier().setMonsterChanceModifier(2d).setStairChanceModifier(1.5d).setGrowthChanceUp(30)), //MORE MONSTERS

        PLANAR_GATE("Planar gate", "Dungeons built around planar portals\n" +
                "are often transformed by the planar energy seeping out\n" +
                "through those portals.",
                new Modifier().setBossChanceModifier(2.5d)), //WIZARDS

        STRONGHOLD("Stronghold", "A stronghold dungeon provides a secure\n" +
                "base of operations for villains and monsters. It is\n" +
                "usually ruled by a powerful individual, such as a wizard,\n" +
                "vampire, or dragon, and it is larger and more complex\n" +
                "than a simple lair.",
                new Modifier().setBossChanceModifier(3d).setMonsterChanceModifier(2d)), //larger coverage

        TEMPLE("Temple with a shrine", "This dungeon is consecrated to\n" +
                "a deity or other planar entity. The entity's worshipers\n" +
                "control the dungeon and conduct their rites there.",
                new Modifier().setMonsterChanceModifier(1.5d)), //MORE MONSTERS

        TOMB("Tomb", "Tombs are magnets for treasure hunters, as\n" +
                "well as monsters that hunger for the bones of the dead.",
                new Modifier().setTreasureChanceModifier(2d)), //MORE TREASURE

        TREASURE_VAULT("Treasure vault", "Built to protect powerful magic items\n" +
                "and great material wealth, treasure vault dungeons are\n" +
                "heavily guarded by monsters and traps.",
                new Modifier().setTreasureChanceModifier(5d).setMonsterAverageGroupSize(5).setMonsterChanceModifier(1.5d)); //MORE MONSTER AND TRAP CHANCE

        String description;
        String title;

        Modifier modifier;

        PURPOSE(String title, String description, Modifier m) {
            this.title = title;
            this.description = description;
            this.modifier = m;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public Modifier getModifier() {
            return modifier;
        }
    }

    public static PURPOSE getPurpose(Random rnd) {
        int d20 = rnd.nextInt(20) + 1;

        if (d20 <= 1) return PURPOSE.DEATH_TRAP;
        if (d20 <= 5) return PURPOSE.LAIR;
        if (d20 <= 6) return PURPOSE.MAZE;
        if (d20 <= 9) return PURPOSE.MINE;
        if (d20 <= 10) return PURPOSE.PLANAR_GATE;
        if (d20 <= 14) return PURPOSE.STRONGHOLD;
        if (d20 <= 17) return PURPOSE.TEMPLE;
        if (d20 <= 19) return PURPOSE.TOMB;
        if (d20 <= 20) return PURPOSE.TREASURE_VAULT;

        return PURPOSE.TREASURE_VAULT;
    }

}
