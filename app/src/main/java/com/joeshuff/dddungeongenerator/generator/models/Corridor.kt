package com.joeshuff.dddungeongenerator.generator.models

import android.graphics.Point
import com.joeshuff.dddungeongenerator.util.Logs

class Corridor() {
    var sections: ArrayList<CorridorSection> = arrayListOf()

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
        corridor.sections = ArrayList(sections.map { it.globalise(startX, startY) })
        return corridor
    }
}

