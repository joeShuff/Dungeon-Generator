package com.joeshuff.dddungeongenerator.generator.monsters;

import com.google.gson.annotations.SerializedName;

public class LegendaryAction {
    String name;
    String desc;

    @SerializedName("attack_bonus")
    int attackBonus;

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}
