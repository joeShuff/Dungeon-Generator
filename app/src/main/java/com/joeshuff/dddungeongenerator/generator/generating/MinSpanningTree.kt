package com.joeshuff.dddungeongenerator.generator.generating

import android.graphics.Point
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

object MinSpanningTree {
    private var longCorridors = false

    fun setLongCorridors(longCorridors: Boolean) {
        MinSpanningTree.longCorridors = longCorridors
    }

    fun calculate(edges: ArrayList<Edge>, points: List<Point>): ArrayList<Edge> {
        val mstree: ArrayList<Edge> = ArrayList()

        edges.sort()

        if (longCorridors) {
            edges.reverse()
        }

        var amountofEdges = edges.size
        while (amountofEdges > 0) {
            val minimum = edges[0]
            edges.removeAt(0)

            if (!addingCausesLoop(points, mstree, minimum)) {
                mstree.add(minimum)
            }

            amountofEdges--
        }

        return mstree
    }

    // A recursive function that uses visited[] and parent to detect
    // cycle in subgraph reachable from vertex v.
    private fun isCyclicUtil(v: Int, visited: BooleanArray, parent: Int, adjacent: Array<ArrayList<Int>>): Boolean {
        // Mark the current node as visited
        visited[v] = true
        var i: Int

        // Recur for all the vertices adjacent to this vertex
        val it: Iterator<Int> = adjacent[v].iterator()
        while (it.hasNext()) {
            i = it.next()

            // If an adjacent is not visited, then recur for that
            // adjacent
            if (!visited[i]) {
                if (isCyclicUtil(i, visited, v, adjacent)) return true
            } else if (i != parent) return true
        }
        return false
    }

    // Returns true if the graph contains a cycle, else false.
    private fun isCyclic(adjacent: Array<ArrayList<Int>>): Boolean {
        // Mark all the vertices as not visited and not part of
        // recursion stack
        val visited = BooleanArray(adjacent.size) {false}

        // Call the recursive helper function to detect cycle in
        // different DFS trees
        for (u in adjacent.indices) if (!visited[u]) // Don't recur for u if already visited
            if (isCyclicUtil(u, visited, -1, adjacent)) return true
        return false
    }

    @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
    private fun addingCausesLoop(points: List<Point>, tree: List<Edge>, newEdge: Edge): Boolean {
        val tempTree: MutableList<Edge> = ArrayList()
        tempTree.addAll(tree)

        tempTree.add(newEdge)

        val adjacent: Array<ArrayList<Int>> = Array(points.size) {ArrayList<Int>()}

        for (e in tempTree) {
            points.forEachIndexed { index, point ->
                if (point === e.start && !(adjacent[index] as ArrayList<Int>).contains(e.end)) adjacent[index].add(points.indexOf(e.end))
                if (point === e.end && !(adjacent[index] as ArrayList<Int>).contains(e.start)) adjacent[index].add(points.indexOf(e.start))
            }
        }

        return isCyclic(adjacent)
    }

    class Edge(val start: Point, val end: Point) : Comparable<Edge> {
        var distance = 0.0
            private set

        fun translate(p: Point): Edge {
            return Edge(
                    Point(start.x + p.x, start.y + p.y),
                    Point(end.x + p.x, end.y + p.y)
            )
        }

        fun calculateDistance() {
            distance = sqrt((start.x - end.x.toDouble()).pow(2.0) + (start.y - end.y.toDouble()).pow(2.0))
        }

        override fun compareTo(o: Edge): Int {
            return distance.compareTo(o.distance)
        }

        override fun equals(obj: Any?): Boolean {
            if (obj !is Edge) return false
            return (start === obj.start || start === obj.end) && (end === obj.start || end === obj.end)
        }

        init {
            calculateDistance()
        }
    }
}