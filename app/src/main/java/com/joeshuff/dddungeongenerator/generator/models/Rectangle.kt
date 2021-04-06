package com.joeshuff.dddungeongenerator.generator.models

import android.graphics.Point
import kotlin.math.max
import kotlin.math.min

class Rectangle: androidx.constraintlayout.solver.widgets.Rectangle {

    constructor() : super()

    constructor(x: Int, y: Int, width: Int, height: Int): super() {
        setBounds(x, y, width, height)
    }

    fun getMinX() = x

    fun getMaxX() = x + width

    fun getMinY() = y

    fun getMaxY() = y + height

    fun intersection(rectangle: Rectangle): Rectangle {
        val res = Rectangle()

        val x1 = max(getMinX(), rectangle.getMinX())
        val y1 = max(getMinY(), rectangle.getMinY())
        val x2 = min(getMaxX(), rectangle.getMaxX())
        val y2 = min(getMaxY(), rectangle.getMaxY())

        res.setBounds(x1, y1, x2 - x1, y2 - y1)
        return res
    }

    fun intersects(rectangle: Rectangle): Boolean {
        if (isEmpty() || rectangle.width <= 0 || rectangle.height <= 0) return false
        val x1 = rectangle.x.toDouble()
        val y1 = rectangle.y.toDouble()
        val x2 = x1 + rectangle.width
        val y2 = y1 + rectangle.height
        return x + width > x1 && x < x2 && y + height > y1 && y < y2
    }

    operator fun contains(p: Point): Boolean {
        return contains(p.x, p.y)
    }

    fun isEmpty(): Boolean {
        return width <= 0.0f || height <= 0.0f
    }
}