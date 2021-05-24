package com.joeshuff.dddungeongenerator.generator.features

import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier
import io.realm.annotations.Ignore
import java.util.*

class TrapFeature(
        @Transient var seed: String,
        @Transient var modifier: Modifier): RoomFeature() {

    enum class TRAP_TYPE {
        MECHANICAL, MAGICAL
    }

    private var trapType: TRAP_TYPE

    @Transient
    private var rnd: Random = Random(Dungeon.stringToSeed(seed))

    fun setTrapType(trapType: TRAP_TYPE) {
        this.trapType = trapType
    }

    init {
        trapType = if (rnd.nextDouble() <= modifier.getTrapMagicalChance()) {
            TRAP_TYPE.MAGICAL
        } else {
            TRAP_TYPE.MECHANICAL
        }
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