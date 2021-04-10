package com.joeshuff.dddungeongenerator.generator.features

import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier
import java.util.*

class TrapFeature : RoomFeature {
    enum class TRAP_TYPE {
        MECHANICAL, MAGICAL
    }

    private lateinit var trapType: TRAP_TYPE
    private lateinit var rnd: Random

    fun setTrapType(trapType: TRAP_TYPE) {
        this.trapType = trapType
    }

    constructor(seed: String?, modifier: Modifier) {
        rnd = Random(Dungeon.stringToSeed(seed))
        trapType = if (rnd.nextDouble() <= modifier.getTrapMagicalChance()) {
            TRAP_TYPE.MAGICAL
        } else {
            TRAP_TYPE.MECHANICAL
        }
    }

    constructor(type: TRAP_TYPE) {
        trapType = type
    }

    override fun getFeatureName(): String {
        return "Trap"
    }

    override fun getFeatureDescription(): String {
        return "There is a " + trapType.toString().toLowerCase() + " trap somewhere in this room, tread with caution. Read more about traps on pages 120-123 in the DM guide"
    }

    override fun getPageForMoreInfo(): Int {
        return 120
    }
}