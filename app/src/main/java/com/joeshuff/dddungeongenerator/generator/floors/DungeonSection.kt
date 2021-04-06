package com.joeshuff.dddungeongenerator.generator.floors

import android.graphics.Point
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Room
import com.joeshuff.dddungeongenerator.generator.generating.DelauneyTriangulate
import com.joeshuff.dddungeongenerator.generator.generating.DelauneyTriangulate.Triangle
import com.joeshuff.dddungeongenerator.generator.generating.MinSpanningTree
import com.joeshuff.dddungeongenerator.generator.generating.PathFinding
import com.joeshuff.dddungeongenerator.generator.models.Rectangle
import java.util.*

class DungeonSection(val id: Int, @Transient val mainDungeon: Dungeon, @Transient val floor: Floor, val width: Int, val height: Int) {
    var startPoint = Point()
        private set

    @Transient
    private var triangulateGraph: ArrayList<Triangle> = ArrayList()

    @Transient
    private var minSpanningTree: ArrayList<MinSpanningTree.Edge> = ArrayList()

    @Transient
    private var triangularEdges: ArrayList<MinSpanningTree.Edge> = ArrayList()
    var corridors: List<List<Point>> = ArrayList()
        private set

    @Transient
    private var centers: ArrayList<Point> = ArrayList()

    @Transient
    private var finalConnections: ArrayList<MinSpanningTree.Edge> = ArrayList()
    var rooms: ArrayList<Room> = ArrayList()

    constructor(id: Int, mainDungeon: Dungeon, floor: Floor, width: Int, height: Int, p: Point) : this(id, mainDungeon, floor, width, height) {
        startPoint = p
    }

    fun branchOut() {
        var coverage = 0
        val totalCoverage = height * width

        while (coverage < mainDungeon.getGlobalModifier().getMapCoveragePercentage() * totalCoverage) {
            coverage = 0

            rooms.forEach { r ->
                coverage += (r.area * 1.5f).toInt()
            }

            rooms.add(Room(this, rooms.size + 1, mainDungeon.rnd, mainDungeon.getGlobalModifier()))
        }
    }

    fun calculateNearestPartner() {
        var allDone = false

        while (!allDone) {
            allDone = true

            for (r1 in rooms) {

                if (r1.isRejected) continue

                var nearest: Room? = null

                var largestCoverage = 0

                for (r2 in rooms) {
                    if (r1 == r2) continue
                    if (r2.isRejected) continue

                    val coverage = r1.intersects(r2)
                    if (coverage > largestCoverage) {
                        nearest = r2
                        largestCoverage = coverage
                    }
                }

                if (nearest != null) {
                    if (r1.moveAwayFrom(nearest)) {
                        allDone = false
//                        Logs.i("Nearest to " + r1.getId() + " is " + nearest.getId());
                    }
                }
            }
        }
    }

    fun calculateRejectedRooms() {
        var validRooms = 0

        for (r in rooms) {
            r.calculateRejected(rooms)
            if (r.isSelected && !r.isRejected) validRooms++
        }

        if (validRooms == 0) {
            var best: Room? = null
            var fail = Int.MAX_VALUE.toDouble()

            for (r in rooms) {
                if (r.calcHowMuchItFailedBy() < fail) {
                    fail = r.calcHowMuchItFailedBy()
                    best = r
                }
            }

            best?.forceSelect()
        }
    }

    fun triangulate() {
        centers = ArrayList()

        for (r in rooms) {
            if (!r.isSelected || r.isRejected) continue
            centers.add(r.myCenter())
        }

        triangulateGraph = DelauneyTriangulate.calculate(centers)
    }

    fun minSpanningTree() {
        if (triangulateGraph.isEmpty()) return

        triangularEdges = ArrayList()

        for (triangle in triangulateGraph) {
            triangularEdges.addAll(triangle.edges)
        }

        val tempEdges: ArrayList<MinSpanningTree.Edge> = ArrayList()
        tempEdges.addAll(triangularEdges)

        minSpanningTree = MinSpanningTree.calculate(tempEdges, centers)
    }

    fun combinePaths() {
        finalConnections = ArrayList()
        finalConnections.addAll(minSpanningTree)

        if (linearProgression) return

        for (edge in triangularEdges) {
            if (finalConnections.contains(edge)) continue

            if (mainDungeon.rnd.nextDouble() <= mainDungeon.getGlobalModifier().getTriangulationAdditionChange()) {
                finalConnections.add(edge)
            }
        }
    }

    fun pathFind() {
        corridors = PathFinding(this, rooms, finalConnections).findPaths()
    }

    fun finalise() {
        finalConnections.clear()
        minSpanningTree.clear()
    }

    fun clearUnnecessaryData() {
        triangularEdges.clear()
        triangulateGraph.clear()
        rooms.removeIf { room: Room -> !room.isSelected || room.isRejected }
    }

    fun getRooms(): List<Room> {
        return rooms
    }

    fun getGlobalCorridors(): List<List<Point>> {
        val globalCorridors: ArrayList<List<Point>> = ArrayList()

        for (localCorridor in corridors) {
            val globalCorridor: ArrayList<Point> = ArrayList()

            for (point in localCorridor) {
                globalCorridor.add(Point(point.x + startPoint.x, point.y + startPoint.y))
            }

            globalCorridors.add(globalCorridor)
        }
        return globalCorridors
    }

    fun getTriangulateGraph(): List<Triangle> {
        return triangulateGraph
    }

    fun getGlobalTriangulateGraph(): List<Triangle> {
        val globalTriangles: ArrayList<Triangle> = ArrayList()

        for (triangle in getTriangulateGraph()) {
            globalTriangles.add(triangle.translate(startPoint))
        }

        return globalTriangles
    }

    fun getGlobalFinalConnections(): List<MinSpanningTree.Edge> {
        val globalEdges: ArrayList<MinSpanningTree.Edge> = ArrayList()

        for (edge in getFinalConnections()) {
            globalEdges.add(edge.translate(startPoint))
        }

        return globalEdges
    }

    fun getFinalConnections(): List<MinSpanningTree.Edge> {
        return finalConnections
    }

    fun getGlobalMinSpanningTree(): List<MinSpanningTree.Edge> {
        val globalEdges: ArrayList<MinSpanningTree.Edge> = ArrayList()

        for (edge in getMinSpanningTree()) {
            globalEdges.add(edge.translate(startPoint))
        }

        return globalEdges
    }

    fun getMinSpanningTree(): List<MinSpanningTree.Edge> {
        return minSpanningTree
    }

    fun me(): Rectangle {
        return Rectangle(startPoint.x, startPoint.y, width, height)
    }

    override fun equals(o: Any?): Boolean {
        return if (o !is DungeonSection) false else o.id == id
    }

    companion object {
        var linearProgression = false
    }
}