package com.joeshuff.dddungeongenerator.screens.home

import com.joeshuff.dddungeongenerator.memory.MemoryGeneration

class DungeonHistoryItem(
        val dungeonName: String,
        val seed: String,
        val createdAt: String,
        val dungeonId: Int? = null,
        val memoryId: MemoryGeneration? = null
) {
}