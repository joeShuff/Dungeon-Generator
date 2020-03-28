package com.joeshuff.dddungeongenerator.generator.dungeon;

import androidx.annotation.Nullable;
import com.joeshuff.dddungeongenerator.generator.monsters.MonsterClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Creator {

    public enum CREATOR  {

        BEHOLDER("Beholder", new Modifier(), null),
        CULT("A Cult or Religious Group", new Modifier(), null),
        DWARVES("Dwarves", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.FEY)), Arrays.asList("Dwarves", "Dwarf")),
        ELVES("Elves (including drow)", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.FEY)), Arrays.asList("Sprite", "Sprites", "Faun", "Fauns", "Leprechauns", "Leprechaun")),
        GIANTS("Giants", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.GIANT)), Arrays.asList("Storm Giant", "Hill Giant", "Cyclops")),
        HOBGOBLINS("Hobgoblins", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.HUMANOID)), Arrays.asList("Hobgoblins")),
        HUMANS("Humans", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.HUMANOID)), null),
        KUO_TOA("Kuo-toa", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.HUMANOID)), null),
        LICH("Lich", new Modifier(), null),
        MIND_FLAYERS("Mind flayers", new Modifier(), null),
        YUAN_TI("Yuan-ti", new Modifier(), null),
        NO_CREATOR("No creator (natural caverns)", new Modifier(), null);

        String description;

        Modifier modifier;

        ArrayList<String> livingNames = new ArrayList<>();

        CREATOR(String description, Modifier modifier, @Nullable List<String> names) {
            if (names != null) livingNames.addAll(names);
            this.description = description;
            this.modifier = modifier;
        }

        public Modifier getModifier() {
            return modifier;
        }
    }

    public CREATOR creatorType;

    public CULT_TYPE cultType;

    String extraDesc = "";

    String humanClass = "";

    public String getDescription() {
        return creatorType.description  + " " + extraDesc;
    }

    public static Creator generateCreator(Random rnd) {
        int d20 = rnd.nextInt(20) + 1;

        Creator newcreator = new Creator();

        CREATOR selected = CREATOR.NO_CREATOR;

        if (d20 <= 20) selected = CREATOR.NO_CREATOR;
        if (d20 <= 19) selected = CREATOR.YUAN_TI;
        if (d20 <= 18) selected = CREATOR.MIND_FLAYERS;
        if (d20 <= 17) selected = CREATOR.LICH;
        if (d20 <= 16) selected = CREATOR.KUO_TOA;
        if (d20 <= 15) selected = CREATOR.HUMANS;
        if (d20 <= 11) selected = CREATOR.HOBGOBLINS;
        if (d20 <= 10) selected = CREATOR.GIANTS;
        if (d20 <= 9) selected = CREATOR.ELVES;
        if (d20 <= 8) selected = CREATOR.DWARVES;
        if (d20 <= 4) selected = CREATOR.CULT;
        if (d20 <= 1) selected = CREATOR.BEHOLDER;

        newcreator.creatorType = selected;

        CULT_TYPE cult = CULT_TYPE.EMPTY;

        if (selected == CREATOR.HUMANS) {
            newcreator.extraDesc = "(" + getHumanDetails(newcreator, rnd) + ")";
        } else if (selected == CREATOR.CULT) {
            cult = getCultType(rnd);
            newcreator.extraDesc = "(" + cult.name + ")";
        }

        newcreator.cultType = cult;
        selected.modifier = Modifier.combineModifiers(Arrays.asList(selected.modifier, cult.modifier), false);
        
        return newcreator;
    }
    
    private static String getHumanDetails(Creator selected, Random rnd) {
        int d20_1 = rnd.nextInt(20) + 1;
        int d20_2 = rnd.nextInt(20) + 1;
        
        String selectedAlignment = "Chaotic Good";
        String selectedClass = "Barbarian";

        if (d20_1 <= 20) selectedAlignment = "Chaotic Evil";
        if (d20_1 <= 18) selectedAlignment = "Neutral Evil";
        if (d20_1 <= 15) selectedAlignment = "Lawful Evil";
        if (d20_1 <= 12) selectedAlignment = "Chaotic Neutral";
        if (d20_1 <= 11) selectedAlignment = "Neutral";
        if (d20_1 <= 9) selectedAlignment = "Lawful Neutral";
        if (d20_1 <= 6) selectedAlignment = "Chaotic Good";
        if (d20_1 <= 4) selectedAlignment = "Neutral Good";
        if (d20_1 <= 2) selectedAlignment = "Lawful Good";

        if (d20_2 <= 20) selectedClass = "Wizard";
        if (d20_2 <= 16) selectedClass = "Warlock";
        if (d20_2 <= 15) selectedClass = "Sorcerer";
        if (d20_2 <= 14) selectedClass = "Rogue";
        if (d20_2 <= 10) selectedClass = "Ranger";
        if (d20_2 <= 9) selectedClass = "Paladin";
        if (d20_2 <= 8) selectedClass = "Monk";
        if (d20_2 <= 7) selectedClass = "Fighter";
        if (d20_2 <= 5) selectedClass = "Druid";
        if (d20_2 <= 4) selectedClass = "Cleric";
        if (d20_2 <= 2) selectedClass = "Bard";
        if (d20_2 <= 1) selectedClass = "Barbarian";

        selected.humanClass = selectedClass;

        return selectedAlignment + " " + selectedClass;
    }

    private static CULT_TYPE getCultType(Random rnd) {
        int d20 = rnd.nextInt(20) + 1;

        if (d20 <= 1) return CULT_TYPE.DEMON_WORSHIP;
        if (d20 <= 2) return CULT_TYPE.DEVIL_WORSHIP;
        if (d20 <= 4) return CULT_TYPE.ELEMENTAL_AIR;
        if (d20 <= 6) return CULT_TYPE.ELEMENTAL_EARTH;
        if (d20 <= 8) return CULT_TYPE.ELEMENTAL_FIRE;
        if (d20 <= 10) return CULT_TYPE.ELEMENTAL_WATER;
        if (d20 <= 15) return CULT_TYPE.WORSHIPERS_OF_EVIL;
        if (d20 <= 17) return CULT_TYPE.WORSHIPERS_OF_GOOD;
        if (d20 <= 20) return CULT_TYPE.WORSHIPERS_OF_NEUTRAL;

        return CULT_TYPE.WORSHIPERS_OF_NEUTRAL;
    }

    enum CULT_TYPE  {
        EMPTY("Empty", new Modifier()),
        DEMON_WORSHIP("Demon-worshiping cult", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.BEAST))),
        DEVIL_WORSHIP("Devil-worshiping cult", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.BEAST))),
        ELEMENTAL_AIR("Elemental air cult", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.ELEMENTAL, MonsterClass.MONSTER_CLASS.FEY))),
        ELEMENTAL_EARTH("Elemental earth cult", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.ELEMENTAL))),
        ELEMENTAL_FIRE("Elemental fire cult", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.ELEMENTAL))),
        ELEMENTAL_WATER("Elemental water cult", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.ELEMENTAL))),
        WORSHIPERS_OF_EVIL("Worshipers of an evil deity", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.MONSTROSITY))),
        WORSHIPERS_OF_GOOD("Worshipers of a good deity", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.PLANT))),
        WORSHIPERS_OF_NEUTRAL("Worshipers of a neutral deity", new Modifier().setPreferredMonsters(Arrays.asList(MonsterClass.MONSTER_CLASS.HUMANOID)))
        ;

        final String name;
        final Modifier modifier;

        CULT_TYPE(String name, Modifier modifier) {
            this.name = name;
            this.modifier = modifier;
        }
    }

}

