package com.joeshuff.dddungeongenerator.generator.generating

import com.joeshuff.dddungeongenerator.db.models.Point
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

object DelauneyTriangulate {

    fun calculate(centers: List<Point>): ArrayList<Triangle> {
        val lines: ArrayList<Triangle> = ArrayList()

        if (centers.size == 1) return lines
        if (centers.size == 2) {
            lines.add(Triangle(centers[0], centers[1], centers[0]))
            return lines
        }

        for (i in centers) {
            for (j in centers) {
                for (k in centers) {
                    var isTriangle = true

                    if (k === j || k === i || i === j) continue

                    for (a in centers) {
                        if (a === i || a === j || a === k) continue
                        if (inside(a, i, j, k)) {
                            isTriangle = false
                            break
                        }
                    }

                    if (isTriangle) {
                        val genned = Triangle(i, j, k)
                        if (!lines.contains(genned)) {
                            lines.add(genned)
                        }
                    }
                }
            }
        }
        return lines
    }

    private fun inside(target: Point, a: Point, b: Point, c: Point): Boolean {
        val circle = circleFromPoints(a, b, c) ?: return true
        return circle.inside(target)
    }

    private fun circleFromPoints(p1: Point, p2: Point, p3: Point): Circle? {
        val offset = p2.x.toDouble().pow(2.0) + p2.y.toDouble().pow(2.0)
        val bc = (p1.x.toDouble().pow(2.0) + p1.y.toDouble().pow(2.0) - offset) / 2.0
        val cd = (offset - p3.x.toDouble().pow(2.0) - p3.y.toDouble().pow(2.0)) / 2.0
        val det = (p1.x - p2.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p2.y).toDouble()

        if (det == 0.0) return null
        val idet = 1 / det
        val centerx = (bc * (p2.y - p3.y) - cd * (p1.y - p2.y)) * idet
        val centery = (cd * (p1.x - p2.x) - bc * (p2.x - p3.x)) * idet
        val radius = sqrt((p2.x - centerx).pow(2.0) + (p2.y - centery).pow(2.0))
        return Circle(Point(centerx.toInt(), centery.toInt()), radius)
    }

    class Triangle(var a: Point, var b: Point, var c: Point) {
        fun translate(p: Point): Triangle {
            return Triangle(
                    Point(a.x + p.x, a.y + p.y),
                    Point(b.x + p.x, b.y + p.y),
                    Point(c.x + p.x, c.y + p.y)
            )
        }

        val edges: List<MinSpanningTree.Edge>
            get() {
                val edges: MutableList<MinSpanningTree.Edge> = ArrayList()
                edges.add(MinSpanningTree.Edge(a, b))
                edges.add(MinSpanningTree.Edge(b, c))
                edges.add(MinSpanningTree.Edge(c, a))
                return edges
            }

        override fun equals(obj: Any?): Boolean {
            if (obj !is Triangle) return false
            var samepoints = 0
            if (a === obj.a || a === obj.b || a === obj.c) samepoints++
            if (b === obj.a || b === obj.b || b === obj.c) samepoints++
            if (c === obj.a || c === obj.b || c === obj.c) samepoints++
            return samepoints == 3
        }

    }

    class Circle(val center: Point, val radius: Double) {
        fun inside(target: Point): Boolean {
            return (target.x - center.x.toDouble()).pow(2.0) + (target.y - center.y.toDouble()).pow(2.0) < radius.pow(2.0)
        }
    }
}