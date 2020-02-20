package com.joeshuff.dddungeongenerator.generator.monsters;

public class MonsterClass {

    public enum MONSTER_CLASS {
        ABERRATION("aberration"),
        BEAST("beast"),
        CELESTIAL("celestial"),
        CONSTRUCT("construct"),
        DRAGON("dragon"),
        ELEMENTAL("elemental"),
        FEY("fey"),
        FIEND("fiend"),
        GIANT("giant"),
        HUMANOID("humanoid"),
        MONSTROSITY("monstrosity"),
        OOZE("ooze"),
        PLANT("plant"),
        UNDEAD("undead"),
        SWARM("swarm of Tiny beasts");

        String type;

        MONSTER_CLASS(String type) {
            this.type = type;
        }
    }

}
