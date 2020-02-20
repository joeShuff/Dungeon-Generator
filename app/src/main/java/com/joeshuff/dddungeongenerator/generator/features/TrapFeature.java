package com.joeshuff.dddungeongenerator.generator.features;

import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier;

import java.util.Random;

public class TrapFeature implements RoomFeature {

    public enum TRAP_TYPE {
        MECHANICAL, MAGICAL
    }

    private TRAP_TYPE trapType;

    private Random rnd;

    public void setTrapType(TRAP_TYPE trapType) {
        this.trapType = trapType;
    }

    public TrapFeature(String seed, Modifier modifier) {
        rnd = new Random(Dungeon.stringToSeed(seed));

        if (rnd.nextDouble() <= modifier.getTrapMagicalChance()) {
            trapType = TrapFeature.TRAP_TYPE.MAGICAL;
        } else {
            trapType = TrapFeature.TRAP_TYPE.MECHANICAL;
        }
    }

    public TrapFeature(TRAP_TYPE type) {
        this.trapType = type;
    }

    @Override
    public String getFeatureName() {
        return "Trap";
    }

    @Override
    public String getFeatureDescription() {
        return "There is a " + trapType.toString().toLowerCase() + " trap somewhere in this room, tread with caution. Read more about traps on pages 120-123 in the DM guide";
    }

    @Override
    public int getPageForMoreInfo() {
        return 120;
    }
}
