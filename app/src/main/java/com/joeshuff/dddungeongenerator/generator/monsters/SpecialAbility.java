package com.joeshuff.dddungeongenerator.generator.monsters;

import com.google.gson.annotations.SerializedName;

public class SpecialAbility {
    String name;
    String desc;

    @SerializedName("attack_bonus")
    int attackBonus;

    @SerializedName("damage_dice")
    String damageDice;

    @SerializedName("damage_bonus")
    int damageBonus;

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public String getDamageDice() {
        return damageDice;
    }

    public int getDamageBonus() {
        return damageBonus;
    }
}
