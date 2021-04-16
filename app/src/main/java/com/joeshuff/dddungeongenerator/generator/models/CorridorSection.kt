package com.joeshuff.dddungeongenerator.generator.models

import com.joeshuff.dddungeongenerator.db.models.Point
import io.realm.RealmObject
import io.realm.annotations.Ignore
import kotlin.math.absoluteValue

open class CorridorSection(
        var startX: Int,
        var startY: Int,
        var width: Int = 1,
        var height: Int = 1
): RealmObject() {
    private enum class Direction {
        POS_X, NEG_X, POS_Y, NEG_Y
    }

    @Ignore
    private var direction: Direction? = null

    constructor(): this(0, 0)

    constructor(point: Point): this(point.x, point.y)

    constructor(start: Point, next: Point): this(start.x, start.y) {
        isSameDirection(next)
        makeBigger()
    }

    fun globalise(startX: Int, startY: Int): CorridorSection {
        return CorridorSection(this.startX + startX, this.startY + startY, width, height)
    }

    fun isSameDirection(next: Point): Boolean {
        if (direction == null) {
            when {
                (startX > next.x) -> {direction = Direction.NEG_X; width = -1; height = 3}
                (startX < next.x) -> {direction = Direction.POS_X; width = 1; height = 3}
                (startY > next.y) -> {direction = Direction.NEG_Y; height = -1; width = 3}
                (startY < next.y) -> {direction = Direction.POS_Y; height = 1; width = 3}
            }

            return true
        } else {
            return when (direction) {
                Direction.NEG_X -> startX > next.x && next.y == startY
                Direction.POS_X -> startX < next.x && next.y == startY
                Direction.NEG_Y -> startY > next.y && next.x == startX
                Direction.POS_Y -> startY < next.y && next.x == startX
                else -> false
            }
        }
    }

    fun makeBigger(amount: Int = 1) {
        when (direction) {
            Direction.POS_X -> width ++
            Direction.NEG_X -> width --
            Direction.POS_Y -> height ++
            Direction.NEG_Y -> height --
        }
    }

    fun finalise() {
        when (direction) {
            Direction.NEG_X -> {
                startX += width
                width = width.absoluteValue
            }
            Direction.NEG_Y -> {
                startY += height
                height = height.absoluteValue
            }
        }
    }

    fun setStart(point: Point) {
        startX = point.x
        startY = point.y
    }
}