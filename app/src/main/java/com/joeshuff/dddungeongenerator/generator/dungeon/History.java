package com.joeshuff.dddungeongenerator.generator.dungeon;

import java.util.Random;

public class History {

    public enum HISTORY {
        ABANDONED("Abandoned by creators", new Modifier()),
        PLAGUE("Abandoned due to plague", new Modifier()),
        CONQUERED("Conquered by invaders", new Modifier()),
        DESTROYED_BY_ATTACKERS("Creators destroyed by attacking raiders", new Modifier()),
        DESTROYED_BY_DISCOVERY("Creators destroyed by discovery made within the site", new Modifier()),
        DESTROYED_BY_INTERNAL("Creators destroyed by internal conflict", new Modifier()),
        DESTROYED_BY_MAGIC("Creators destroyed by magical catastrophe", new Modifier()),
        DESTROYED_BY_NATURAL("Creators destroyed by natural disaster", new Modifier()),
        CURSED_BY_GODS("Location cursed by the gods and shunned", new Modifier()),
        STILL_IN_CONTROL("Original creator still in control", new Modifier()),
        OVERRUN("Overrun by planar creatures", new Modifier()),
        SITE_OF_MIRACLE("Site of a great miracle", new Modifier());

        String desc;
        Modifier modifier;

        HISTORY(String desc, Modifier modifier) {
            this.modifier = modifier;
            this.desc = desc;
        }

        public Modifier getModifier() {
            return modifier;
        }

        public String getDesc() {
            return desc;
        }
    }

    public static HISTORY getHistory(Random rnd) {
        int d20 = rnd.nextInt(20) + 1;

        if (d20 <= 3) return HISTORY.ABANDONED;
        if (d20 <= 4) return HISTORY.PLAGUE;
        if (d20 <= 8) return HISTORY.CONQUERED;
        if (d20 <= 10) return HISTORY.DESTROYED_BY_ATTACKERS;
        if (d20 <= 11) return HISTORY.DESTROYED_BY_DISCOVERY;
        if (d20 <= 12) return HISTORY.DESTROYED_BY_INTERNAL;
        if (d20 <= 13) return HISTORY.DESTROYED_BY_MAGIC;
        if (d20 <= 15) return HISTORY.DESTROYED_BY_NATURAL;
        if (d20 <= 16) return HISTORY.CURSED_BY_GODS;
        if (d20 <= 18) return HISTORY.STILL_IN_CONTROL;
        if (d20 <= 19) return HISTORY.OVERRUN;
        if (d20 <= 20) return HISTORY.SITE_OF_MIRACLE;

        return HISTORY.STILL_IN_CONTROL;
    }

}
