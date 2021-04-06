package com.joeshuff.dddungeongenerator.generator.monsters

class MonsterClass {
    enum class MONSTER_CLASS(var type: String) {
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
    }
}