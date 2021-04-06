package com.joeshuff.dddungeongenerator.generator.monsters

import com.google.gson.annotations.SerializedName

class LegendaryAction {
    var name: String? = null
    var desc: String? = null

    @SerializedName("attack_bonus")
    var attackBonus = 0

}