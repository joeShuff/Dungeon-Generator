package com.joeshuff.dddungeongenerator.generator.dungeon

import java.util.*

object History {
    fun getHistory(rnd: Random): HISTORY {
        val d20 = rnd.nextInt(20) + 1
        if (d20 <= 3) return HISTORY.ABANDONED
        if (d20 <= 4) return HISTORY.PLAGUE
        if (d20 <= 8) return HISTORY.CONQUERED
        if (d20 <= 10) return HISTORY.DESTROYED_BY_ATTACKERS
        if (d20 <= 11) return HISTORY.DESTROYED_BY_DISCOVERY
        if (d20 <= 12) return HISTORY.DESTROYED_BY_INTERNAL
        if (d20 <= 13) return HISTORY.DESTROYED_BY_MAGIC
        if (d20 <= 15) return HISTORY.DESTROYED_BY_NATURAL
        if (d20 <= 16) return HISTORY.CURSED_BY_GODS
        if (d20 <= 18) return HISTORY.STILL_IN_CONTROL
        if (d20 <= 19) return HISTORY.OVERRUN
        if (d20 <= 20) return HISTORY.SITE_OF_MIRACLE
        return HISTORY.STILL_IN_CONTROL
    }

    enum class HISTORY(var desc: String, var modifier: Modifier) {
        ABANDONED("Abandoned by creators", Modifier()),
        PLAGUE("Abandoned due to plague", Modifier()),
        CONQUERED("Conquered by invaders", Modifier()),
        DESTROYED_BY_ATTACKERS("Creators destroyed by attacking raiders", Modifier()),
        DESTROYED_BY_DISCOVERY("Creators destroyed by discovery made within the site", Modifier()),
        DESTROYED_BY_INTERNAL("Creators destroyed by internal conflict", Modifier()),
        DESTROYED_BY_MAGIC("Creators destroyed by magical catastrophe", Modifier()),
        DESTROYED_BY_NATURAL("Creators destroyed by natural disaster", Modifier()),
        CURSED_BY_GODS("Location cursed by the gods and shunned", Modifier()),
        STILL_IN_CONTROL("Original creator still in control", Modifier()),
        OVERRUN("Overrun by planar creatures", Modifier()),
        SITE_OF_MIRACLE("Site of a great miracle", Modifier());
    }
}