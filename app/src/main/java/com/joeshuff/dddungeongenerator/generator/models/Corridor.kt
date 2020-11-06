package com.joeshuff.dddungeongenerator.generator.models

class Corridor(
        val startX: Int,
        val startY: Int,
        val heightX: Int,
        val heightY: Int
) {

    fun globalise(startX: Int, startY: Int): Corridor {
        return Corridor(this.startX + startX, this.startY + startY, heightX, heightY)
    }

}