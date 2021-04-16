package com.joeshuff.dddungeongenerator.generator.models

import com.joeshuff.dddungeongenerator.db.models.Point
import com.joeshuff.dddungeongenerator.db.toRealmList
import com.joeshuff.dddungeongenerator.util.Logs
import io.realm.RealmList
import io.realm.RealmObject

open class Corridor(): RealmObject() {
    var sections: RealmList<CorridorSection> = RealmList()

    constructor(points: List<Point>): this() {
        var currentSection = CorridorSection()

        points.forEachIndexed { index, point ->
            when (index) {
                0 -> currentSection.setStart(point)
                else -> {
                    if (currentSection.isSameDirection(point)) {
                        currentSection.makeBigger()
                    } else {
                        val prev = points.getOrNull(index - 1)

                        prev?.let {
                            currentSection.finalise()
                            sections.add(currentSection)
                            currentSection = CorridorSection(it, point)
                        }?: run {
                            Logs.e("GENERATE", "previous corridor couldn't be found", Throwable("Corridor generation error"))
                        }
                    }
                }
            }
        }

        currentSection.finalise()
        sections.add(currentSection)
    }

    fun globalise(startX: Int, startY: Int): Corridor {
        val corridor = Corridor()
        corridor.sections = ArrayList(sections.map { it.globalise(startX, startY) }).toRealmList()
        return corridor
    }
}

