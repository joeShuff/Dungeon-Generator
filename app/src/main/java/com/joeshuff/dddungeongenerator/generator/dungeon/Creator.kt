package com.joeshuff.dddungeongenerator.generator.dungeon

import com.joeshuff.dddungeongenerator.generator.monsters.MonsterClass
import java.util.*

class Creator {
    enum class CREATOR(description: String, modifier: Modifier, names: List<String>?) {
        BEHOLDER("Beholder", Modifier(), null),
        CULT("A Cult or Religious Group", Modifier(), null),
        DWARVES("Dwarves", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.FEY)), listOf<String>("Dwarves", "Dwarf")),
        ELVES("Elves (including drow)", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.FEY)), listOf<String>("Sprite", "Sprites", "Faun", "Fauns", "Leprechauns", "Leprechaun")),
        GIANTS("Giants", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.GIANT)), listOf<String>("Storm Giant", "Hill Giant", "Cyclops")),
        HOBGOBLINS("Hobgoblins", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.HUMANOID)), listOf<String>("Hobgoblins")),
        HUMANS("Humans", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.HUMANOID)), null),
        KUO_TOA("Kuo-toa", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.HUMANOID)), null),
        LICH("Lich", Modifier(), null), MIND_FLAYERS("Mind flayers", Modifier(), null),
        YUAN_TI("Yuan-ti", Modifier(), null), NO_CREATOR("No creator (natural caverns)", Modifier(), null);

        var description: String
        var modifier: Modifier
        var livingNames = ArrayList<String>()

        init {
            if (names != null) livingNames.addAll(names)
            this.description = description
            this.modifier = modifier
        }
    }

    var creatorType: CREATOR? = null

    var cultType: CULT_TYPE? = null

    var extraDesc = ""
    var humanClass = ""

    fun getDescription(): String {
        return (creatorType?.description?:"") + " $extraDesc"
    }

    enum class CULT_TYPE(val cultName: String, val modifier: Modifier) {
        EMPTY("Empty", Modifier()),
        DEMON_WORSHIP("Demon-worshiping cult", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.BEAST))),
        DEVIL_WORSHIP("Devil-worshiping cult", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.BEAST))),
        ELEMENTAL_AIR("Elemental air cult", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.ELEMENTAL, MonsterClass.MONSTER_CLASS.FEY))),
        ELEMENTAL_EARTH("Elemental earth cult", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.ELEMENTAL))),
        ELEMENTAL_FIRE("Elemental fire cult", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.ELEMENTAL))),
        ELEMENTAL_WATER("Elemental water cult", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.ELEMENTAL))),
        WORSHIPERS_OF_EVIL("Worshipers of an evil deity", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.MONSTROSITY))),
        WORSHIPERS_OF_GOOD("Worshipers of a good deity", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.PLANT))),
        WORSHIPERS_OF_NEUTRAL("Worshipers of a neutral deity", Modifier().setPreferredMonsters(listOf(MonsterClass.MONSTER_CLASS.HUMANOID)));
    }

    companion object {
        fun generateCreator(rnd: Random): Creator {
            val d20 = rnd.nextInt(20) + 1
            val newcreator = Creator()
            var selected = CREATOR.NO_CREATOR
            if (d20 <= 20) selected = CREATOR.NO_CREATOR
            if (d20 <= 19) selected = CREATOR.YUAN_TI
            if (d20 <= 18) selected = CREATOR.MIND_FLAYERS
            if (d20 <= 17) selected = CREATOR.LICH
            if (d20 <= 16) selected = CREATOR.KUO_TOA
            if (d20 <= 15) selected = CREATOR.HUMANS
            if (d20 <= 11) selected = CREATOR.HOBGOBLINS
            if (d20 <= 10) selected = CREATOR.GIANTS
            if (d20 <= 9) selected = CREATOR.ELVES
            if (d20 <= 8) selected = CREATOR.DWARVES
            if (d20 <= 4) selected = CREATOR.CULT
            if (d20 <= 1) selected = CREATOR.BEHOLDER
            newcreator.creatorType = selected
            var cult = CULT_TYPE.EMPTY
            if (selected == CREATOR.HUMANS) {
                newcreator.extraDesc = "(" + getHumanDetails(newcreator, rnd) + ")"
            } else if (selected == CREATOR.CULT) {
                cult = getCultType(rnd)
                newcreator.extraDesc = "(" + cult.cultName + ")"
            }
            newcreator.cultType = cult
            selected.modifier = Modifier.combineModifiers(listOf(selected.modifier, cult.modifier), false)
            return newcreator
        }

        private fun getHumanDetails(selected: Creator, rnd: Random): String {
            val d20_1 = rnd.nextInt(20) + 1
            val d20_2 = rnd.nextInt(20) + 1
            var selectedAlignment = "Chaotic Good"
            var selectedClass = "Barbarian"
            if (d20_1 <= 20) selectedAlignment = "Chaotic Evil"
            if (d20_1 <= 18) selectedAlignment = "Neutral Evil"
            if (d20_1 <= 15) selectedAlignment = "Lawful Evil"
            if (d20_1 <= 12) selectedAlignment = "Chaotic Neutral"
            if (d20_1 <= 11) selectedAlignment = "Neutral"
            if (d20_1 <= 9) selectedAlignment = "Lawful Neutral"
            if (d20_1 <= 6) selectedAlignment = "Chaotic Good"
            if (d20_1 <= 4) selectedAlignment = "Neutral Good"
            if (d20_1 <= 2) selectedAlignment = "Lawful Good"
            if (d20_2 <= 20) selectedClass = "Wizard"
            if (d20_2 <= 16) selectedClass = "Warlock"
            if (d20_2 <= 15) selectedClass = "Sorcerer"
            if (d20_2 <= 14) selectedClass = "Rogue"
            if (d20_2 <= 10) selectedClass = "Ranger"
            if (d20_2 <= 9) selectedClass = "Paladin"
            if (d20_2 <= 8) selectedClass = "Monk"
            if (d20_2 <= 7) selectedClass = "Fighter"
            if (d20_2 <= 5) selectedClass = "Druid"
            if (d20_2 <= 4) selectedClass = "Cleric"
            if (d20_2 <= 2) selectedClass = "Bard"
            if (d20_2 <= 1) selectedClass = "Barbarian"
            selected.humanClass = selectedClass
            return "$selectedAlignment $selectedClass"
        }

        private fun getCultType(rnd: Random): CULT_TYPE {
            val d20 = rnd.nextInt(20) + 1

            return when (d20) {
                1 -> CULT_TYPE.DEMON_WORSHIP
                2 -> CULT_TYPE.DEVIL_WORSHIP
                3,4 -> CULT_TYPE.ELEMENTAL_AIR
                5,6 -> CULT_TYPE.ELEMENTAL_EARTH
                7,8 -> CULT_TYPE.ELEMENTAL_FIRE
                9,10 -> CULT_TYPE.ELEMENTAL_WATER
                in 11..15 -> CULT_TYPE.WORSHIPERS_OF_EVIL
                16,17 -> CULT_TYPE.WORSHIPERS_OF_GOOD
                in 18..20 -> CULT_TYPE.WORSHIPERS_OF_NEUTRAL
                else -> CULT_TYPE.WORSHIPERS_OF_NEUTRAL
            }
        }
    }
}