package com.joeshuff.dddungeongenerator.generator.monsters

import com.google.gson.annotations.SerializedName

class Action {
    var name: String? = null
      get

    var desc: String? = null
      get

    @SerializedName("attack_bonus")
    var attackBonus = 0

    @SerializedName("damage_dice")
    var damageDice: String? = null

    @SerializedName("damage_bonus")
    var damageBonus = 0

}