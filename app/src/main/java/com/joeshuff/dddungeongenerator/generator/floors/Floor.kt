package com.joeshuff.dddungeongenerator.generator.floors

import android.graphics.Point
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Room
import com.joeshuff.dddungeongenerator.generator.generating.DelauneyTriangulate.Triangle
import com.joeshuff.dddungeongenerator.generator.generating.MinSpanningTree
import com.joeshuff.dddungeongenerator.util.Logs
import java.util.*

class Floor(@Transient var dungeon: Dungeon, @Transient var rnd: Random, level: Int) {
    var sectionList: ArrayList<DungeonSection> = ArrayList()

    companion object {
        @Transient
        private val MIN_SECTION_SIZE = Dungeon.MAP_SIZE / 5

        @Transient
        private val MAX_SECTION_SIZE = (Dungeon.MAP_SIZE * 0.66).toInt()
    }

    var level = 0

    /**
     * Once a floor has started adding rooms, no new sections can be generated. When a room is generated with stairs to this floor
     * no action is taken and a room is assigned to those stairs later.
     */
    var startedAddingRooms = false

    fun sectionFrom(p: Point) {
        if (startedAddingRooms) return

        var width = (MIN_SECTION_SIZE + (MAX_SECTION_SIZE - MIN_SECTION_SIZE) * rnd.nextDouble()).toInt()
        var height = (MIN_SECTION_SIZE + (MAX_SECTION_SIZE - MIN_SECTION_SIZE) * rnd.nextDouble()).toInt()

        val startX = Math.max(p.x - width / 2, 0)
        val startY = Math.max(p.y - height / 2, 0)

        if (startX + width > Dungeon.MAP_SIZE) {
            width -= startX + width - Dungeon.MAP_SIZE
        }

        if (startY + height > Dungeon.MAP_SIZE) {
            height -= startY + height - Dungeon.MAP_SIZE
        }

        val startPoint = Point(startX, startY)

        sectionList.add(DungeonSection(sectionList.size, dungeon, this, width, height, startPoint))
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

                if (section.me().intersects(comparingSection.me())) {
                    newSectionList.remove(section)
                    newSectionList.remove(comparingSection)

//                    Logs.i("2 sections are overlapping (" + section.me() + ") (" + comparingSection.me() + ")");
                    var topLeft = Point(
                            Math.min(section.startPoint.x, comparingSection.startPoint.x),
                            Math.min(section.startPoint.y, comparingSection.startPoint.y)
                    )

                    val bottomRight = Point(
                            Math.max(section.startPoint.x + section.width, comparingSection.startPoint.x + comparingSection.width),
                            Math.max(section.startPoint.y + section.height, comparingSection.startPoint.y + comparingSection.height)
                    )

                    val center = Point(
                            topLeft.x + (bottomRight.x - topLeft.x) / 2,
                            topLeft.y + (bottomRight.y - topLeft.y) / 2)

                    val width = (section.width + comparingSection.width) / 2
                    val height = (section.height + comparingSection.height) / 2

                    topLeft = Point(center.x - width / 2, center.y - height / 2)

//                    int width = bottomRight.x - topLeft.x;
//                    int height = bottomRight.y - topLeft.y;

//                    Logs.i("combined to " + topLeft + " at " + width + "x" + height);
                    Logs.i("Floor", "rectangle('position', [" + section.startPoint.x + ", " + section.startPoint.y + "," + section.width + "," + section.height + "], 'edgecolor',[0, 1, 0])", null)
                    Logs.i("Floor", "rectangle('position', [" + comparingSection.startPoint.x + ", " + comparingSection.startPoint.y + "," + comparingSection.width + "," + comparingSection.height + "], 'edgecolor',[0, 1, 0])", null)

                    val newSection = DungeonSection(section.id, dungeon, this, width, height, topLeft)
                    Logs.i("Floor", "rectangle('position', [" + newSection.startPoint.x + ", " + newSection.startPoint.y + "," + newSection.width + "," + newSection.height + "], 'edgecolor',[1, 0, 0])", null)

                    newSectionList.add(newSection)
                    allClear = false
                    break
                }
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
        sectionList.add(DungeonSection(sectionList.size, dungeon, this, dungeon.width, dungeon.height, Point(0, 0)))
    }

    fun splitFloor() {
        sectionList.add(DungeonSection(sectionList.size, dungeon, this, dungeon.width / 2, dungeon.height, Point(0, 0)))
        sectionList.add(DungeonSection(sectionList.size, dungeon, this, dungeon.width / 2, dungeon.height, Point(dungeon.width / 2, 0)))
    }

    fun newFloor(elevation: Int, from: Room): Floor {
        val newOrExistingFloor = dungeon.addFloorForLevel(level + elevation)

        if (!newOrExistingFloor.startedAddingRooms) {
            newOrExistingFloor.sectionFrom(from.myGlobalCenter())
        }

        return newOrExistingFloor
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

    val allCorridors: List<List<Point>>
        get() {
            val allCorridors: MutableList<List<Point>> = ArrayList()
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

    init {
        this.level = level
    }
}