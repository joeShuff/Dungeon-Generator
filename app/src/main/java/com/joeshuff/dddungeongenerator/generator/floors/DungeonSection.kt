package com.joeshuff.dddungeongenerator.generator.floors

import com.joeshuff.dddungeongenerator.db.models.Point
import com.joeshuff.dddungeongenerator.db.toRealmList
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon
import com.joeshuff.dddungeongenerator.generator.dungeon.Room
import com.joeshuff.dddungeongenerator.generator.generating.DelauneyTriangulate
import com.joeshuff.dddungeongenerator.generator.generating.DelauneyTriangulate.Triangle
import com.joeshuff.dddungeongenerator.generator.generating.MinSpanningTree
import com.joeshuff.dddungeongenerator.generator.generating.PathFinding
import com.joeshuff.dddungeongenerator.generator.models.Corridor
import com.joeshuff.dddungeongenerator.generator.models.Rectangle
import com.joeshuff.dddungeongenerator.util.Logs
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import java.lang.Exception
import java.security.spec.ECField

open class DungeonSection(
        var id: Int,
        @Ignore val floor: Floor?,
        var width: Int,
        var height: Int): RealmObject() {

    var startPoint: Point? = Point()
        private set

    @Ignore
    private var triangulateGraph: ArrayList<Triangle> = ArrayList()

    @Ignore
    private var minSpanningTree: ArrayList<MinSpanningTree.Edge> = ArrayList()

    @Ignore
    private var triangularEdges: ArrayList<MinSpanningTree.Edge> = ArrayList()

    var corridors: RealmList<Corridor> = RealmList()
        private set

    @Ignore
    private var centers: ArrayList<Point> = ArrayList()

    @Ignore
    private var finalConnections: ArrayList<MinSpanningTree.Edge> = ArrayList()

    var rooms: RealmList<Room> = RealmList()

    constructor(): this(0, null, 0, 0)

    constructor(id: Int, floor: Floor, width: Int, height: Int, p: Point) : this(id, floor, width, height) {
        startPoint = p
    }

    fun branchOut() {
        var coverage = 0
        val totalCoverage = height * width
        val gennedRooms = arrayListOf<Room>()

        try {
            floor?.dungeon?.let {
                while (coverage < it.getGlobalModifier().getMapCoveragePercentage() * totalCoverage) {
                    coverage = gennedRooms.map { (it.area * 1.5f).toInt() }.sum()
                    Logs.i("COVERAGE", "$coverage out of ${it.getGlobalModifier().getMapCoveragePercentage() * totalCoverage}")

                    gennedRooms.add(Room(rooms.size + 1, this, it.rnd, it.getGlobalModifier()))
                }
            }
        } catch (e: Exception) {
            throw e
        }

        rooms = gennedRooms.toRealmList()
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
        floor?.dungeon?.let {
            finalConnections = ArrayList()
            finalConnections.addAll(minSpanningTree)

            if (linearProgression) return

            for (edge in triangularEdges) {
                if (finalConnections.contains(edge)) continue

                if (it.rnd.nextDouble() <= it.getGlobalModifier().getTriangulationAdditionChange()) {
                    finalConnections.add(edge)
                }
            }
        }
    }

    fun pathFind() {
        corridors.clear()
        PathFinding(this, rooms, finalConnections).findPaths().forEach {
            corridors.add(it)
        }
    }

    fun finalise() {
        finalConnections.clear()
        minSpanningTree.clear()
    }

    fun complete() {
        rooms.forEach { it.complete() }
    }

    fun clearUnnecessaryData() {
        triangularEdges.clear()
        triangulateGraph.clear()
        rooms.removeAll { room: Room -> !room.isSelected || room.isRejected }
    }

    fun getRooms(): List<Room> {
        return rooms
    }

    fun getGlobalCorridors(): List<Corridor> {
        val globalCorridors: ArrayList<Corridor> = ArrayList()

        for (localCorridor in corridors) {
            globalCorridors.add(localCorridor.globalise(startPoint?.x?: 0, startPoint?.y?: 0))
        }

        return globalCorridors
    }

    fun getTriangulateGraph(): List<Triangle> {
        return triangulateGraph
    }

    fun getGlobalTriangulateGraph(): List<Triangle> {
        val globalTriangles: ArrayList<Triangle> = ArrayList()

        for (triangle in getTriangulateGraph()) {
            startPoint?.let {
                globalTriangles.add(triangle.translate(it))
            }
        }

        return globalTriangles
    }

    fun getGlobalFinalConnections(): List<MinSpanningTree.Edge> {
        val globalEdges: ArrayList<MinSpanningTree.Edge> = ArrayList()

        for (edge in getFinalConnections()) {
            startPoint?.let {
                globalEdges.add(edge.translate(it))
            }
        }

        return globalEdges
    }

    fun getFinalConnections(): List<MinSpanningTree.Edge> {
        return finalConnections
    }

    fun getGlobalMinSpanningTree(): List<MinSpanningTree.Edge> {
        val globalEdges: ArrayList<MinSpanningTree.Edge> = ArrayList()

        for (edge in getMinSpanningTree()) {
            startPoint?.let {
                globalEdges.add(edge.translate(it))
            }
        }

        return globalEdges
    }

    fun getMinSpanningTree(): List<MinSpanningTree.Edge> {
        return minSpanningTree
    }

    fun me(): Rectangle? {
        startPoint?.let {
            return Rectangle(it.x, it.y, width, height)
        }

        return null
    }

    override fun equals(o: Any?): Boolean {
        return if (o !is DungeonSection) false else o.id == id
    }

    companion object {
        @Ignore
        var linearProgression = false
    }
}