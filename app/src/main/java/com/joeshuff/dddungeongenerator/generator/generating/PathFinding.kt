package com.joeshuff.dddungeongenerator.generator.generating

import android.graphics.Point
import com.joeshuff.dddungeongenerator.generator.dungeon.Room
import com.joeshuff.dddungeongenerator.generator.floors.DungeonSection
import com.joeshuff.dddungeongenerator.generator.models.Corridor
import java.util.*
import kotlin.math.abs

class PathFinding(
        val section: DungeonSection,
        val rooms: List<Room>,
        val connectingPoints: List<MinSpanningTree.Edge>) {

    fun findPaths(): List<Corridor> {
        val result: ArrayList<Corridor> = arrayListOf()

        connectingPoints.forEach {
            result.add(Corridor(aStarPath(section, it.start, it.end)))
        }

        return result
    }

    /**
     * This is the a* Path finding algorithm. It finds the best possible path from the Persons current position to the
     * defined destination position.
     *
     * @param destination - The goal location
     * @return List<Point> the list of tiles to move to, from their current location to the goal destination.
    </Point> */
    fun aStarPath(section: DungeonSection, start: Point, destination: Point): List<Point> {
        val emptyList: List<Point> = ArrayList()

        if (destination == start) {
            return emptyList
        }

        val closedSet: MutableList<Point> = ArrayList()

        val openSet: MutableList<Point> = ArrayList()
        openSet.add(Point(start.x, start.y))

        val gScore = HashMap<Point, Int>()
        gScore[openSet[0]] = 0

        val cameFrom = HashMap<Point, Point>()

        val fScore = HashMap<Point, Double>()
        fScore[openSet[0]] = heuristic(Point(start.x, start.y), destination)

        while (openSet.isNotEmpty()) {
            val current = getLowestFScore(openSet, fScore) ?: return emptyList

            if (current == destination) {
                return reconstructPath(cameFrom, current)
            }

            openSet.remove(current)
            closedSet.add(current)

            val neighbours = getNeighbours(section, current)

            for (neighbour in neighbours) {
                if (closedSet.contains(neighbour)) {
                    continue
                }

                val tentativeGScore = (gScore[current]?: 0) + distFromNeighbour(current, neighbour)

                if (!openSet.contains(neighbour)) {
                    openSet.add(neighbour)
                } else {
                    val prevScore = gScore[neighbour]?: 0

                    if (tentativeGScore >= prevScore) {
                        continue
                    }
                }

                cameFrom[neighbour] = current
                gScore[neighbour] = tentativeGScore

                fScore[neighbour] = heuristic(neighbour, destination)
            }
        }

        return emptyList
    }

    /**
     * This method is used to get the cheapest next node from the open list
     *
     * @param openSet - The open list of locations
     * @param fScore  - The estimated scores of each node to the goal
     * @return Point the next best node from openSet
     */
    private fun getLowestFScore(openSet: List<Point>, fScore: HashMap<Point, Double>): Point? {
        if (openSet.isEmpty()) return null

        var lowest = openSet[0]
        var lowestInt = fScore[lowest]?: Double.MAX_VALUE

        for (v in openSet) {
            val thisF = fScore[v]
            thisF?.let {
                if (it < lowestInt) {
                    lowest = v
                    lowestInt = it
                }
            }
        }

        return lowest
    }

    /**
     * This method gets the distance from one node to another.
     *
     * @param current   - One position
     * @param neighbour - The second position
     * @return - Integer, the distance between the 2 positions
     */
    private fun distFromNeighbour(current: Point, neighbour: Point): Int {
        return abs(current.x - neighbour.x) + abs(current.y - neighbour.y)
    }

    /**
     * This method is called once the A* Pathfinding algorithm has been completed. It reconstructs the path from the goal to the start point
     *
     * @param cameFrom - A map of a node(key) , and the value being the node that we came from to get to the key node.
     * @param current  - The final node. The goal destination
     * @return List<Point> this is the list of tiles that are needed to be walked on to reach the goal
    </Point> */
    private fun reconstructPath(cameFrom: HashMap<Point, Point>, goal: Point): List<Point> {
        val path: ArrayList<Point> = arrayListOf()
        path.add(goal)
        var current: Point? = goal

        while (current != null && cameFrom.keys.contains(current)) {
            current = cameFrom[current]
            current?.let {
                path.add(it)
            }
        }

        path.reverse()
        return path
    }

    private fun getNeighbours(section: DungeonSection, current: Point): List<Point> {
        val roomWidth = section.width
        val roomHeight = section.height

        val neighbours: MutableList<Point> = ArrayList()

        if (current.x + 1 <= roomWidth) { //EAST
            val eastNeighbour = Point(current.x + 1, current.y)

            var isValid = true
            for (r in rooms) {
                if (r.topBorder().contains(eastNeighbour) || r.bottomBorder().contains(eastNeighbour)) {
                    isValid = false
                }
            }
            if (isValid) neighbours.add(eastNeighbour)
        }

        if (current.y + 1 <= roomHeight) { //SOUTH
            val southNeighbour = Point(current.x, current.y + 1)
            var isValid = true

            for (r in rooms) {
                if (r.leftBorder().contains(southNeighbour) || r.rightBorder().contains(southNeighbour)) {
                    isValid = false
                }
            }
            if (isValid) neighbours.add(southNeighbour)
        }

        if (current.x - 1 >= 0) { //WEST
            val westNeighbour = Point(current.x - 1, current.y)
            var isValid = true

            for (r in rooms) {
                if (r.topBorder().contains(westNeighbour) || r.bottomBorder().contains(westNeighbour)) {
                    isValid = false
                }
            }
            if (isValid) neighbours.add(westNeighbour)
        }

        if (current.y - 1 >= 0) { //NORTH
            val northNeighbour = Point(current.x, current.y - 1)
            var isValid = true

            for (r in rooms) {
                if (r.leftBorder().contains(northNeighbour) || r.rightBorder().contains(northNeighbour)) {
                    isValid = false
                }
            }
            if (isValid) neighbours.add(northNeighbour)
        }

        return neighbours
    }

    private fun heuristic(start: Point, end: Point): Double {
        return (abs(start.x - end.x) + abs(start.y - end.y)).toDouble()
//		return Math.sqrt(Math.pow(start.x - end.x, 2) + Math.pow(start.y - end.y, 2));
    }
}