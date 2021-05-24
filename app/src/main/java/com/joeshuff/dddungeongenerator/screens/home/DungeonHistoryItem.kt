package com.joeshuff.dddungeongenerator.screens.home

import com.joeshuff.dddungeongenerator.memory.MemoryGeneration
import java.text.SimpleDateFormat
import java.util.*

class DungeonHistoryItem(
        val dungeonName: String,
        val seed: String,
        val createdAt: Int,
        val dungeonId: Int? = null,
        val memoryId: MemoryGeneration? = null
) {
    fun getCreatedDate(): String {
        return SimpleDateFormat("dd MMM yyyy | HH:mm:ss").format(Date(createdAt.toLong() * 1000L))
    }
}