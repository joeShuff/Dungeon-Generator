package com.joeshuff.dddungeongenerator.generator.dungeon

import com.joeshuff.dddungeongenerator.db.models.Point

class Movement(var oldStart: Point, var newStart: Point) {
    fun increment() {
        timesMade++
    }

    var timesMade = 1

    override fun equals(o: Any?): Boolean {
        if (o !is Movement) return false
        return o.oldStart == oldStart && o.newStart == newStart
    }

}