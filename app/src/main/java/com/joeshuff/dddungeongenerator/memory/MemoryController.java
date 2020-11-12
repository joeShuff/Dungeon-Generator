package com.joeshuff.dddungeongenerator.memory;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryController {

    private static String MEMORY_KEY = "MEMORY_THING";

    public static MemoryGeneration addToMemory(Context c, MemoryGeneration memoryGeneration) throws JSONException {
        List<MemoryGeneration> stored = getMemory(c);
        stored.add(memoryGeneration);

        JSONArray array = new JSONArray("[]");

        for (MemoryGeneration mem : stored) {
            array.put(new Gson().toJson(mem));
        }

        saveToSharedPreferences(c, MEMORY_KEY, array.toString());

        return memoryGeneration;
    }

    public static void removeFromMemory(Context c, MemoryGeneration m) throws JSONException {
        List<MemoryGeneration> stored = getMemory(c);
        stored.remove(m);

        JSONArray array = new JSONArray("[]");

        for (MemoryGeneration mem : stored) {
            array.put(new Gson().toJson(mem));
        }

        saveToSharedPreferences(c, MEMORY_KEY, array.toString());
    }

    public static List<MemoryGeneration> getMemory(Context c) {
        JSONArray stored = null;
        try {
            stored = new JSONArray(getFromSharedPreferences(c, MEMORY_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        List<MemoryGeneration> rememberedGenerations = new ArrayList<>();

        for (int i = 0; i < stored.length(); i ++) {
            try {
                MemoryGeneration found = new GsonBuilder().create().fromJson(stored.getString(i), MemoryGeneration.class);

                if (found.getSeed() != null) {
                    rememberedGenerations.add(found);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(rememberedGenerations);
        return rememberedGenerations;
    }

    /**
     * This method saves a key-value pair to the shared preferences
     *
     * @param context - App context
     * @param key - Key to store against
     * @param value - Value to store
     */
    public static void saveToSharedPreferences(Context context, String key, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * This method fetches a String value from shared preferences
     *
     * @param context - App context
     * @param key - Key to fetch from
     *
     * @return String - Value if exists, "null" if not
     */
    public static String getFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(key, "[]");
    }

}
