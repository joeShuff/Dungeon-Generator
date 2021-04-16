package com.joeshuff.dddungeongenerator.generator.features

import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import java.util.*

class TreasureFeature(seed: String) : RoomFeature() {
    var description: String

    override fun getPageForMoreInfo(): Int {
        return 0
    }

    override fun getFeatureName(): String {
        return "Treasure"
    }

    override fun getFeatureDescription(): String {
        return description
    }

    companion object {
        @Transient
        var treasureDescriptions = Arrays.asList("There is an abundance of treasure in this room.",
                "There is a shimmering chest in the corner of the room.",
                "There appears to be gold coins pouring out of a statues mouth in the centre of the room.",
                "There is a larger fountain in the centre which appeared to be a wishing fountain."
        )
    }

    init {
        description = treasureDescriptions[Random(Dungeon.stringToSeed(seed)).nextInt(treasureDescriptions.size)]
    }
}