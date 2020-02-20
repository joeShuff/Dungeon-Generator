package com.joeshuff.dddungeongenerator.generator.monsters;

import com.google.gson.annotations.SerializedName;
import com.joeshuff.dddungeongenerator.Logs;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Monster {

	String name;
	String size;
	String type;
	String subtype;
	String alignment;

	@SerializedName("armor_class")
	int armorClass;

	@SerializedName("hit_points")
	int hitPoints;

	String speed;
	int strength;
	int dexterity;
	int constitution;
	int intelligence;
	int wisdom;
	int charisma;

	@SerializedName("constitution_save")
	int constitutionSave = -1;

	@SerializedName("intelligence_save")
	int intelligenceSave = -1;

	@SerializedName("wisdom_save")
	int wisdomSave = -1;

	@SerializedName("dexterity_save")
	int dexteritySave = -1;

	@SerializedName("charisma_save")
	int charismaSave = -1;

	@SerializedName("strength_save")
	int strengthSave = -1;

	@SerializedName("damage_vulnerabilities")
	String damageVulnerabilities;

	@SerializedName("damage_resistances")
	String damageResistances;

	@SerializedName("damage_immumities")
	String damageImmunities;

	@SerializedName("condition_immunities")
	String conditionImmunities;

	String senses;
	String languages;

	@SerializedName("challenge_rating")
	String challengeRating;

	List<Action> actions;

	@SerializedName("special_abilities")
	List<Action> specialAbilities;

	@SerializedName("legendary_actions")
	List<Action> legendaryActions;

	public Monster(JSONObject jsonObject) {

	    try {
	        name = jsonObject.getString("name");
	        size = jsonObject.getString("size");
	        type = jsonObject.getString("type");
	        subtype = jsonObject.getString("subtype");
	        alignment = jsonObject.getString("alignment");
	        armorClass = jsonObject.getInt("armor_class");
	        hitPoints= jsonObject.getInt("hit_points");
	        speed = jsonObject.getString("speed");
	        strength = jsonObject.getInt("strength");
	        dexterity = jsonObject.getInt("dexterity");
	        constitution = jsonObject.getInt("constitution");
	        intelligence = jsonObject.getInt("intelligence");
	        wisdom = jsonObject.getInt("wisdom");
	        charisma = jsonObject.getInt("charisma");
	        damageVulnerabilities = jsonObject.getString("damage_vulnerabilities");
	        damageResistances = jsonObject.getString("damage_resistances");
	        damageImmunities = jsonObject.getString("damage_immunities");
	        conditionImmunities = jsonObject.getString("condition_immunities");
	        senses = jsonObject.getString("senses");
	        languages = jsonObject.getString("languages");
	        challengeRating = jsonObject.getString("challenge_rating");
        } catch (Exception e) {
            Logs.i("Monster", "Problem loading " + jsonObject.toString(), null);
        }
    }

	public String getName() {
		return name;
	}

	public String getSize() {
		if (size == null) return "None";
		return size;
 	}

	public String getType() {
		return type;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getAlignment() {
		return alignment;
	}

	public long getArmorClass() {
		return armorClass;
	}

	public long getHitPoints() {
		return hitPoints;
	}

	public String getSpeed() {
		return speed;
	}

	public long getStrength() {
		return strength;
	}

	public long getDexterity() {
		return dexterity;
	}

	public long getConstitution() {
		return constitution;
	}

	public long getIntelligence() {
		return intelligence;
	}

	public long getWisdom() {
		return wisdom;
	}

	public long getCharisma() {
		return charisma;
	}

	public String getDamageVulnerabilities() {
		return damageVulnerabilities;
	}

	public String getDamageResistances() {
		return damageResistances;
	}

	public String getDamageImmunities() {
		return damageImmunities;
	}

	public String getConditionImmunities() {
		return conditionImmunities;
	}

	public String getSenses() {
		return senses;
	}

	public String getLanguages() {
		return languages;
	}

	public String getChallengeRating() {
		return challengeRating;
	}

	public List<Action> getActions() {
		return actions;
	}

	public List<Action> getSpecialAbilities() {
		return specialAbilities;
	}

	public List<Action> getLegendaryActions() {
		return legendaryActions;
	}

	public String getSavingThrows() {
		String savingThrows = "";
		if (constitutionSave > 0) {
			savingThrows = savingThrows + "CON +" + constitutionSave + ", ";
		}

		if (charismaSave > 0) {
			savingThrows = savingThrows + "CHA +" + charismaSave + ", ";
		}

		if (intelligenceSave > 0) {
			savingThrows = savingThrows + "INT +" + intelligenceSave + ", ";
		}

		if (wisdomSave > 0) {
			savingThrows = savingThrows + "WIS +" + wisdomSave + ", ";
		}

		if (dexteritySave > 0) {
			savingThrows = savingThrows + "DEX +" + dexteritySave + ", ";
		}

		if (strengthSave > 0) {
			savingThrows = savingThrows + "STR +" + strengthSave + ", ";
		}

		if (savingThrows.length() <= 2) return "None";

		return savingThrows.substring(0, savingThrows.length() - 2);
	}

	public Integer getChallengeXp() {

		HashMap<String, Integer> xpForChallenge = new HashMap<>();
		xpForChallenge.put("0", 10);
		xpForChallenge.put("1/8", 25);
		xpForChallenge.put("1/4", 50);
		xpForChallenge.put("1/2", 100);
		xpForChallenge.put("1", 200);
		xpForChallenge.put("2", 450);
		xpForChallenge.put("3", 700);
		xpForChallenge.put("4", 1100);
		xpForChallenge.put("5", 1800);
		xpForChallenge.put("6", 2300);
		xpForChallenge.put("7", 2900);
		xpForChallenge.put("8", 3900);
		xpForChallenge.put("14", 11500);
		xpForChallenge.put("15", 13000);
		xpForChallenge.put("16", 15000);
		xpForChallenge.put("17", 18000);
		xpForChallenge.put("18", 20000);
		xpForChallenge.put("19", 22000);
		xpForChallenge.put("20", 25000);
		xpForChallenge.put("21", 33000);
		xpForChallenge.put("22", 41000);
		xpForChallenge.put("23", 50000);
		xpForChallenge.put("24", 62000);
		xpForChallenge.put("25", 75000);

		return xpForChallenge.get(challengeRating);
	}

	private static transient HashMap<String, Integer> modifierAmount = new HashMap<>();
	static {
		modifierAmount.put("1", -5);
		modifierAmount.put("2", -4);
		modifierAmount.put("4", -3);
		modifierAmount.put("6", -2);
		modifierAmount.put("8", -1);
		modifierAmount.put("10", 0);
		modifierAmount.put("12", 1);
		modifierAmount.put("14", 2);
		modifierAmount.put("16", 3);
		modifierAmount.put("18", 4);
		modifierAmount.put("20", 5);
		modifierAmount.put("22", 6);
		modifierAmount.put("24", 7);
		modifierAmount.put("26", 8);
		modifierAmount.put("28", 9);
		modifierAmount.put("30", 10);
	}


	public String getModifierFor(long val) {
		int key = (int) val;
		while (!modifierAmount.containsKey("" + key)) {
			key --;
		}

		int modifier = modifierAmount.get(key + "");

		if (modifier >= 0) return "(+" + modifier + ")";
		return "(-" + modifier + ")";
	}
}
