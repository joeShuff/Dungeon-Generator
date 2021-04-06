package com.joeshuff.dddungeongenerator.generator.monsters;

import android.content.Context;
import com.google.gson.Gson;
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Creates various Monster objects filling in params from the Monster class
 * Creates lists for each 'theme' and adds the monsters to their respective themes
 *
 */

public class Bestiary {

	private static List<Monster> monsterList = new ArrayList<>();

	List<String> bossSize = Arrays.asList("large", "gargantuan", "huge");

	private static String loadJSONFromAsset(Context c) {
		String json = null;
		try {
			InputStream is = c.getAssets().open("monsters.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;
	}

	public static void launchBestiary(Context c) {
		try {
			JSONArray jsonObject = new JSONArray(loadJSONFromAsset(c));

			for (int i = 0; i < jsonObject.length(); i ++) {
				monsterList.add(new Gson().fromJson(jsonObject.getJSONObject(i).toString(), Monster.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getTypes() {
		List<String> res = new ArrayList<>();

		for (Monster m : monsterList) {
			if (res.contains(m.getType())) continue;
			res.add(m.getType());
		}

		return res;
	}

	public static Monster getBoss(Random rnd, Modifier modifier, boolean ignorePreferences) {
		//A boss is defined as a monster of size
		List<String> preferredTypes = modifier.getPreferredMonsters().stream().map(monster_class -> monster_class.getType()).collect(Collectors.toList());

		if (preferredTypes.isEmpty() || ignorePreferences) {
			preferredTypes.clear();
			preferredTypes.addAll(Arrays.stream(MonsterClass.MONSTER_CLASS.values()).map(monster_class -> monster_class.getType()).collect(Collectors.toList()));
		}

		List<String> ignoreTypes = modifier.getBlockedMonsters().stream().map(monster_class -> monster_class.getType()).collect(Collectors.toList());

		List<Monster> gargantuanPreferred = monsterList.stream()
				.filter(monster -> monster.getSize().equalsIgnoreCase("gargantuan") &&
						preferredTypes.contains(monster.getType()) &&
						!ignoreTypes.contains(monster.getType()))
				.collect(Collectors.toList());

		List<Monster> hugePreferred = monsterList.stream()
				.filter(monster -> monster.getSize().equalsIgnoreCase("huge") &&
						preferredTypes.contains(monster.getType()) &&
						!ignoreTypes.contains(monster.getType()))
				.collect(Collectors.toList());

		List<Monster> largePreferred = monsterList.stream()
				.filter(monster -> monster.getSize().equalsIgnoreCase("large") &&
						preferredTypes.contains(monster.getType()) &&
						!ignoreTypes.contains(monster.getType()))
				.collect(Collectors.toList());

		List<Monster> allPreferred = new ArrayList<>();

		allPreferred.addAll(gargantuanPreferred);
		allPreferred.addAll(hugePreferred);
		allPreferred.addAll(largePreferred);

		if (allPreferred.size() == 0) return getBoss(rnd, modifier, true);

		return allPreferred.get(rnd.nextInt(allPreferred.size()));
	}

	public static Monster getMonster(Random rnd, Modifier modifier, boolean ignorePreferred) {
		//A boss is defined as a monster of size
		List<String> preferredTypes = modifier.getPreferredMonsters().stream().map(monster_class -> monster_class.getType()).collect(Collectors.toList());

		if (preferredTypes.isEmpty() || ignorePreferred) {
			preferredTypes.clear();
			preferredTypes.addAll(Arrays.stream(MonsterClass.MONSTER_CLASS.values()).map(monster_class -> monster_class.getType()).collect(Collectors.toList()));
		}

		List<String> ignoreTypes = modifier.getBlockedMonsters().stream().map(monster_class -> monster_class.getType()).collect(Collectors.toList());

		List<Monster> tinyPreferred = monsterList.stream()
				.filter(monster -> monster.getSize().equalsIgnoreCase("tiny") &&
						preferredTypes.contains(monster.getType()) &&
						!ignoreTypes.contains(monster.getType()))
				.collect(Collectors.toList());

		List<Monster> smallPreferred = monsterList.stream()
				.filter(monster -> monster.getSize().equalsIgnoreCase("small") &&
						preferredTypes.contains(monster.getType()) &&
						!ignoreTypes.contains(monster.getType()))
				.collect(Collectors.toList());

		List<Monster> mediumPreferred = monsterList.stream()
				.filter(monster -> monster.getSize().equalsIgnoreCase("medium") &&
						preferredTypes.contains(monster.getType()) &&
						!ignoreTypes.contains(monster.getType()))
				.collect(Collectors.toList());

		List<Monster> allPreferred = new ArrayList<>();

		allPreferred.addAll(tinyPreferred);
		allPreferred.addAll(smallPreferred);
		allPreferred.addAll(mediumPreferred);

		if (allPreferred.size() == 0) return getMonster(rnd, modifier, true);

		return allPreferred.get(rnd.nextInt(allPreferred.size()));
	}

}
