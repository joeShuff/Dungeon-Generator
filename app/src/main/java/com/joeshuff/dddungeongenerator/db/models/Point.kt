package com.joeshuff.dddungeongenerator.db.models

import io.realm.RealmObject

open class Point(var x: Int, var y: Int): RealmObject() {

    constructor(): this(0,0)

    override fun equals(other: Any?): Boolean {
        return if (other !is Point) false else (x == other.x && y == other.y)
    }

}