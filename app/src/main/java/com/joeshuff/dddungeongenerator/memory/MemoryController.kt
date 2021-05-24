package com.joeshuff.dddungeongenerator.memory

import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.subjects.PublishSubject
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

object MemoryController {
    private const val MEMORY_KEY = "MEMORY_THING"

    val memoryHistory = PublishSubject.create<List<MemoryGeneration>>()

    @Throws(JSONException::class)
    fun addToMemory(c: Context, memoryGeneration: MemoryGeneration): MemoryGeneration {
        val stored = getMemory(c)
        stored.add(memoryGeneration)

        memoryHistory.onNext(stored)

        val array = JSONArray("[]")
        for (mem in stored) {
            array.put(Gson().toJson(mem))
        }

        saveToSharedPreferences(c, MEMORY_KEY, array.toString())
        return memoryGeneration
    }

    @Throws(JSONException::class)
    fun removeFromMemory(c: Context, m: MemoryGeneration) {
        val stored = getMemory(c)
        stored.remove(m)

        memoryHistory.onNext(stored)

        val array = JSONArray("[]")
        for (mem in stored) {
            array.put(Gson().toJson(mem))
        }

        saveToSharedPreferences(c, MEMORY_KEY, array.toString())
    }

    fun getMemory(c: Context): ArrayList<MemoryGeneration> {
        var stored: JSONArray? = null

        stored = try {
            JSONArray(getFromSharedPreferences(c, MEMORY_KEY))
        } catch (e: JSONException) {
            e.printStackTrace()
            return ArrayList()
        }

        val rememberedGenerations = arrayListOf<MemoryGeneration>()
        for (i in 0 until stored.length()) {
            try {
                val found = GsonBuilder().create().fromJson(stored.getString(i), MemoryGeneration::class.java)
                if (found.seed != null) {
                    rememberedGenerations.add(found)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        rememberedGenerations.sort()
        return rememberedGenerations
    }

    /**
     * This method saves a key-value pair to the shared preferences
     *
     * @param context - App context
     * @param key - Key to store against
     * @param value - Value to store
     */
    fun saveToSharedPreferences(context: Context, key: String, value: String) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.commit()
    }

    /**
     * This method fetches a String value from shared preferences
     *
     * @param context - App context
     * @param key - Key to fetch from
     *
     * @return String - Value if exists, "null" if not
     */
    fun getFromSharedPreferences(context: Context?, key: String?): String? {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getString(key, "[]")
    }
}