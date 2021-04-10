package com.joeshuff.dddungeongenerator.generator.monsters

import com.google.gson.annotations.SerializedName
import com.joeshuff.dddungeongenerator.util.tryOrNull
import org.json.JSONObject
import java.util.*

class Monster {

    companion object {
        @Transient
        private val modifierAmount = mutableMapOf(
                Pair("1", -5),
                Pair("2", -4),
                Pair("4", -3),
                Pair("6", -2),
                Pair("8", -1),
                Pair("10", 0),
                Pair("12", 1),
                Pair("14", 2),
                Pair("16", 3),
                Pair("18", 4),
                Pair("20", 5),
                Pair("22", 6),
                Pair("24", 7),
                Pair("26", 8),
                Pair("28", 9),
                Pair("30", 10)
        )
    }

    var name: String? = null
    var size: String? = null
    var type: String? = null
    var subtype: String? = null
    var alignment: String? = null

    @SerializedName("armor_class")
    var armorClass = 0

    @SerializedName("hit_points")
    var hitPoints = 0

    var speed: String? = null
    var strength = 0
    var dexterity = 0
    var constitution = 0
    var intelligence = 0
    var wisdom = 0
    var charisma = 0

    @SerializedName("constitution_save")
    var constitutionSave = -1

    @SerializedName("intelligence_save")
    var intelligenceSave = -1

    @SerializedName("wisdom_save")
    var wisdomSave = -1

    @SerializedName("dexterity_save")
    var dexteritySave = -1

    @SerializedName("charisma_save")
    var charismaSave = -1

    @SerializedName("strength_save")
    var strengthSave = -1

    @SerializedName("damage_vulnerabilities")
    var damageVulnerabilities: String? = null

    @SerializedName("damage_resistances")
    var damageResistances: String? = null

    @SerializedName("damage_immumities")
    var damageImmunities: String? = null

    @SerializedName("condition_immunities")
    var conditionImmunities: String? = null

    var senses: String? = null
    var languages: String? = null

    @SerializedName("challenge_rating")
    var challengeRating: String? = null

    var actions: List<Action>? = emptyList()

    @SerializedName("special_abilities")
    var specialAbilities: List<Action>? = emptyList()

    @SerializedName("legendary_actions")
    var legendaryActions: List<Action>? = emptyList()

    constructor(json: JSONObject) {
        name = tryOrNull { json.getString("name") }?: ""
        size = tryOrNull { json.getString("size") }?: ""
        type = tryOrNull { json.getString("type") }?: ""
        subtype = tryOrNull { json.getString("subtype") }?: ""
        alignment = tryOrNull { json.getString("alignment") }?: ""
        armorClass = tryOrNull { json.getInt("armor_class") }?: 0
        hitPoints = tryOrNull { json.getInt("hit_points") }?: 0
        speed = tryOrNull { json.getString("speed") }?: ""
        strength = tryOrNull { json.getInt("strength") }?: 0
        dexterity = tryOrNull { json.getInt("dexterity") }?: 0
        constitution = tryOrNull { json.getInt("constitution") }?: 0
        intelligence = tryOrNull { json.getInt("intelligence") }?: 0
        wisdom = tryOrNull { json.getInt("wisdom") }?: 0
        charisma = tryOrNull { json.getInt("charisma") }?: 0
        damageVulnerabilities = tryOrNull { json.getString("damage_vulnerabilities") }?: ""
        damageResistances = tryOrNull { json.getString("damage_resistances") }?: ""
        damageImmunities = tryOrNull { json.getString("damage_immunities") }?: ""
        conditionImmunities = tryOrNull { json.getString("condition_immunities") }?: ""
        senses = tryOrNull { json.getString("senses") }?: ""
        languages = tryOrNull { json.getString("languages") }?: ""
        challengeRating = tryOrNull { json.getString("challenge_rating") }?: ""
    }

    fun getSavingThrows(): String? {
        var savingThrows = ""
        if (constitutionSave > 0) {
            savingThrows = savingThrows + "CON +" + constitutionSave + ", "
        }
        if (charismaSave > 0) {
            savingThrows = savingThrows + "CHA +" + charismaSave + ", "
        }
        if (intelligenceSave > 0) {
            savingThrows = savingThrows + "INT +" + intelligenceSave + ", "
        }
        if (wisdomSave > 0) {
            savingThrows = savingThrows + "WIS +" + wisdomSave + ", "
        }
        if (dexteritySave > 0) {
            savingThrows = savingThrows + "DEX +" + dexteritySave + ", "
        }
        if (strengthSave > 0) {
            savingThrows = savingThrows + "STR +" + strengthSave + ", "
        }
        return if (savingThrows.length <= 2) "None" else savingThrows.substring(0, savingThrows.length - 2)
    }

    fun getChallengeXp(): Int? {
        val xpForChallenge = HashMap<String, Int>()
        xpForChallenge["0"] = 10
        xpForChallenge["1/8"] = 25
        xpForChallenge["1/4"] = 50
        xpForChallenge["1/2"] = 100
        xpForChallenge["1"] = 200
        xpForChallenge["2"] = 450
        xpForChallenge["3"] = 700
        xpForChallenge["4"] = 1100
        xpForChallenge["5"] = 1800
        xpForChallenge["6"] = 2300
        xpForChallenge["7"] = 2900
        xpForChallenge["8"] = 3900
        xpForChallenge["14"] = 11500
        xpForChallenge["15"] = 13000
        xpForChallenge["16"] = 15000
        xpForChallenge["17"] = 18000
        xpForChallenge["18"] = 20000
        xpForChallenge["19"] = 22000
        xpForChallenge["20"] = 25000
        xpForChallenge["21"] = 33000
        xpForChallenge["22"] = 41000
        xpForChallenge["23"] = 50000
        xpForChallenge["24"] = 62000
        xpForChallenge["25"] = 75000
        return xpForChallenge[challengeRating]
    }

    fun getModifierFor(amount: Long): String? {
        var key = amount.toInt()
        while (!modifierAmount.containsKey("" + key)) {
            key--
        }
        val modifier = modifierAmount[key.toString() + ""]?: 0
        return if (modifier >= 0) "(+$modifier)" else "(-$modifier)"
    }
}