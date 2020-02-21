package com.joeshuff.dddungeongenerator.generator.features;

import com.joeshuff.dddungeongenerator.Logs;
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier;
import com.joeshuff.dddungeongenerator.generator.monsters.Bestiary;
import com.joeshuff.dddungeongenerator.generator.monsters.Monster;

import java.util.Random;

public class MonsterFeature implements RoomFeature {

    int size;
    Monster selectedMonster;

    private static transient double DEFAULT_BOSS_CHANCE = 0.3d;

    transient Modifier modifier;
    transient Random rnd;
    boolean isBoss = false;

    public MonsterFeature(String seed, Modifier m) {
        this.modifier = m;
        this.rnd = new Random(Dungeon.stringToSeed(seed));
        size = Math.max(1, (int) ((rnd.nextGaussian() * 2) + m.getMonsterAverageGroupSize()));
        generateMonster();
    }

    private void generateMonster() {
        if (size == 1) {
            if (rnd.nextDouble() <= (DEFAULT_BOSS_CHANCE * modifier.getBossChanceModifier())) {
                selectedMonster = Bestiary.getBoss(rnd, modifier, false);

                if (selectedMonster != null) {
                    isBoss = true;
                } else {
                    Logs.i("MonsterFeature", "EMPTY BOSS MONSTER", null);
                }
            }
        }

        if (selectedMonster != null) return;

        selectedMonster = Bestiary.getMonster(rnd, modifier, false);

        if (selectedMonster == null) {
            Logs.i("MonsterFeature", "EMPTY MONSTER", null);
        }
    }

    @Override
    public String getFeatureDescription() {
        if (isBoss) {
            return "there is a boss in this room. A " + selectedMonster.getName() + " resides here";
        }

        if (size > 1) {
            return "there are " + size + " " + selectedMonster.getName() + "'s in this room, be careful!";
        } else {
            return "there is a " + selectedMonster.getName() + " in this room. Be careful!";
        }
    }

    @Override
    public String getFeatureName() {
        return "Monster(s)";
    }

    @Override
    public int getPageForMoreInfo() {
        return 0;
    }

    public Monster getSelectedMonster() {
        return selectedMonster;
    }

    public int getSize() {
        return size;
    }

    public boolean isBoss() {
        return isBoss;
    }
}
