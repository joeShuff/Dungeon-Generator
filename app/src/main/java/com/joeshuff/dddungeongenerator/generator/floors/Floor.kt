package com.joeshuff.dddungeongenerator.generator.floors

import com.joeshuff.dddungeongenerator.db.models.Point
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Room
import com.joeshuff.dddungeongenerator.generator.generating.DelauneyTriangulate.Triangle
import com.joeshuff.dddungeongenerator.generator.generating.MinSpanningTree
import com.joeshuff.dddungeongenerator.generator.models.Corridor
import com.joeshuff.dddungeongenerator.util.Logs
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class Floor(
        @Ignore var dungeon: Dungeon?,
        @Ignore var rnd: Random?,
        var level: Int) : RealmObject()
{
    var sectionList: RealmList<DungeonSection> = RealmList()

    constructor(): this(null, null, 0)

    companion object {
        @Ignore
        private val MIN_SECTION_SIZE = Dungeon.MAP_SIZE / 5

        @Ignore
        private val MAX_SECTION_SIZE = (Dungeon.MAP_SIZE * 0.66).toInt()
    }

    /**
     * Once a floor has started adding rooms, no new sections can be generated. When a room is generated with stairs to this floor
     * no action is taken and a room is assigned to those stairs later.
     */
    @Ignore
    var startedAddingRooms = false

    fun sectionFrom(p: Point) {
        rnd?.let {
            if (startedAddingRooms) return

            var width = (MIN_SECTION_SIZE + (MAX_SECTION_SIZE - MIN_SECTION_SIZE) * it.nextDouble()).toInt()
            var height = (MIN_SECTION_SIZE + (MAX_SECTION_SIZE - MIN_SECTION_SIZE) * it.nextDouble()).toInt()

            val startX = max(p.x - width / 2, 0)
            val startY = max(p.y - height / 2, 0)

            if (startX + width > Dungeon.MAP_SIZE) {
                width -= startX + width - Dungeon.MAP_SIZE
            }

            if (startY + height > Dungeon.MAP_SIZE) {
                height -= startY + height - Dungeon.MAP_SIZE
            }

            val startPoint = Point(startX, startY)

            sectionList.add(DungeonSection(sectionList.size, this, width, height, startPoint))
        }
    }

    fun getRoomClosestTo(root: Room): Room? {
        val rootRec = root.me()

        val closest = allRooms.sortedWith(kotlin.Comparator { room: Room, t1: Room ->
                    (Math.sqrt(Math.pow(room.me().intersection(rootRec).width.toDouble(), 2.0) + Math.pow(room.me().intersection(rootRec).height.toDouble(), 2.0)) -
                            Math.sqrt(Math.pow(t1.me().intersection(rootRec).width.toDouble(), 2.0) + Math.pow(t1.me().intersection(rootRec).height.toDouble(), 2.0))).toInt()
                }).firstOrNull()

        val closestRoom = closest?: allRooms.firstOrNull()

        Logs.i("Floor", "Closest room to (" + root.globalStartX + "," + root.globalStartY + "; " + root.width + "x" + root.height + ") is room " + closestRoom?.id?:"NONE", null)
        return closestRoom
    }

    fun mergeSections() {
        val newSectionList: MutableList<DungeonSection> = ArrayList()
        newSectionList.addAll(sectionList)
        var allClear = true

        for (section in sectionList) {
            for (comparingSection in sectionList) {
                if (section === comparingSection) continue

                var doBreak = false

                section.startPoint?.let { startPoint1 ->
                    comparingSection.startPoint?.let { startPoint2 ->
                        if (section.me()?.intersects(comparingSection.me()) == true) {
                            newSectionList.remove(section)
                            newSectionList.remove(comparingSection)

//                          Logs.i("2 sections are overlapping (" + section.me() + ") (" + comparingSection.me() + ")");
                            var topLeft = Point(
                                    min(startPoint1.x, startPoint2.x),
                                    min(startPoint1.y, startPoint2.y)
                            )

                            val bottomRight = Point(
                                    max(startPoint1.x + section.width, startPoint2.x + comparingSection.width),
                                    max(startPoint1.y + section.height, startPoint2.y + comparingSection.height)
                            )

                            val center = Point(
                                    topLeft.x + (bottomRight.x - topLeft.x) / 2,
                                    topLeft.y + (bottomRight.y - topLeft.y) / 2)

                            val width = (section.width + comparingSection.width) / 2
                            val height = (section.height + comparingSection.height) / 2

                            topLeft = Point(center.x - width / 2, center.y - height / 2)

//                            int width = bottomRight.x - topLeft.x;
//                            int height = bottomRight.y - topLeft.y;
//
//                            Logs.i("combined to " + topLeft + " at " + width + "x" + height);
                            Logs.i("Floor", "rectangle('position', [" + startPoint1.x + ", " + startPoint1.y + "," + section.width + "," + section.height + "], 'edgecolor',[0, 1, 0])", null)
                            Logs.i("Floor", "rectangle('position', [" + startPoint2.x + ", " + startPoint2.y + "," + comparingSection.width + "," + comparingSection.height + "], 'edgecolor',[0, 1, 0])", null)

                            val newSection = DungeonSection(section.id, this, width, height, topLeft)
                            Logs.i("Floor", "rectangle('position', [" + topLeft.x + ", " + topLeft.y + "," + newSection.width + "," + newSection.height + "], 'edgecolor',[1, 0, 0])", null)

                            newSectionList.add(newSection)
                            allClear = false
                            doBreak = true
                        }
                    }
                }

                if (doBreak) break
            }

            if (!allClear) break
        }

        sectionList.clear()
        sectionList.addAll(newSectionList)

        if (!allClear) {
            mergeSections()
        }
    }

    fun fillFloor() {
        dungeon?.let {
            sectionList.add(DungeonSection(sectionList.size, this, it.width, it.height, Point(0, 0)))
        }
    }

    fun splitFloor() {
        dungeon?.let {
            sectionList.add(DungeonSection(sectionList.size, this, it.width / 2, it.height, Point(0, 0)))
            sectionList.add(DungeonSection(sectionList.size, this, it.width / 2, it.height, Point(it.width / 2, 0)))
        }
    }

    fun newFloor(elevation: Int, from: Room): Floor? {
        dungeon?.let {
            val newOrExistingFloor = it.addFloorForLevel(level + elevation)

            if (!newOrExistingFloor.startedAddingRooms) {
                newOrExistingFloor.sectionFrom(from.myGlobalCenter())
            }

            return newOrExistingFloor
        }

        return null
    }

    fun branchOut() {
        startedAddingRooms = true
        mergeSections()

        for (section in sectionList) {
            section.branchOut()
        }
    }

    fun getSectionList(): List<DungeonSection> {
        return sectionList
    }

    fun complete() {
        sectionList.forEach { it.complete() }
    }

    val allCorridors: List<Corridor>
        get() {
            val allCorridors: MutableList<Corridor> = ArrayList()
            for (section in sectionList) {
                allCorridors.addAll(section.getGlobalCorridors())
            }
            return allCorridors
        }

    val allRooms: List<Room>
        get() {
            val rooms: MutableList<Room> = ArrayList()
            for (section in sectionList) {
                rooms.addAll(section.getRooms())
            }
            return rooms
        }

    val allMinSpanningTree: List<MinSpanningTree.Edge>
        get() {
            val edges: MutableList<MinSpanningTree.Edge> = ArrayList()
            for (section in sectionList) {
                edges.addAll(section.getGlobalMinSpanningTree())
            }
            return edges
        }

    val triangulationEdges: List<Triangle>
        get() {
            val triangles: MutableList<Triangle> = ArrayList()
            for (section in sectionList) {
                triangles.addAll(section.getGlobalTriangulateGraph())
            }
            return triangles
        }

    val finalConnections: List<MinSpanningTree.Edge>
        get() {
            val finalEdges: MutableList<MinSpanningTree.Edge> = ArrayList()
            for (section in sectionList) {
                finalEdges.addAll(section.getGlobalFinalConnections())
            }
            return finalEdges
        }
}