package com.joeshuff.dddungeongenerator.generator.generating;

import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;


public class DelauneyTriangulate {

    public static List<Triangle> calculate(List<Point> centers) {
        List<Triangle> lines = new ArrayList<>();

        if (centers.size() == 1) return lines;
        if (centers.size() == 2) {
            lines.add(new Triangle(centers.get(0), centers.get(1), centers.get(0)));
            return lines;
        }

        for (Point i : centers) {
            for (Point j : centers) {
                for (Point k : centers) {
                    boolean isTriangle = true;

                    if (k == j || k == i || i == j) continue;

                    for (Point a : centers) {
                        if (a == i || a == j || a == k) continue;

                        if (inside(a, i, j, k)) {
                            isTriangle = false;
                            break;
                        }
                    }

                    if (isTriangle) {
                        Triangle genned = new Triangle(i, j, k);
                        if (!lines.contains(genned)) {
                            lines.add(genned);
                        }
                    }
                }
            }
        }

        return lines;
    }

    private static boolean inside(Point target, Point a, Point b, Point c) {
        Circle circle = circleFromPoints(a, b, c);

        if (circle == null) return true;

        return circle.inside(target);
    }

    public static class Triangle {
        public Point a;
        public Point b;
        public Point c;

        public Triangle(Point a, Point b, Point c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public Triangle translate(Point p) {
            return new Triangle(
                    new Point(this.a.x + p.x, this.a.y + p.y),
                    new Point(this.b.x + p.x, this.b.y + p.y),
                    new Point(this.c.x + p.x, this.c.y + p.y)
            );
        }

        public List<MinSpanningTree.Edge> getEdges() {
            List<MinSpanningTree.Edge> edges = new ArrayList<>();
            edges.add(new MinSpanningTree.Edge(a, b));
            edges.add(new MinSpanningTree.Edge(b, c));
            edges.add(new MinSpanningTree.Edge(c, a));

            return edges;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Triangle)) return false;

            int samepoints = 0;
            Triangle other = (Triangle) obj;

           if (this.a == other.a || this.a == other.b || this.a == other.c) samepoints ++;
           if (this.b == other.a || this.b == other.b || this.b == other.c) samepoints ++;
           if (this.c == other.a || this.c == other.b || this.c == other.c) samepoints ++;

           if (samepoints == 3) return true;

           return false;
        }
    }

    public static Circle circleFromPoints(final Point p1, final Point p2, final Point p3)
    {
        final double offset = Math.pow(p2.x,2) + Math.pow(p2.y,2);
        final double bc =   ( Math.pow(p1.x,2) + Math.pow(p1.y,2) - offset )/2.0;
        final double cd =   (offset - Math.pow(p3.x, 2) - Math.pow(p3.y, 2))/2.0;
        final double det =  (p1.x - p2.x) * (p2.y - p3.y) - (p2.x - p3.x)* (p1.y - p2.y);

        if (det == 0) return null;

        final double idet = 1/det;

        final double centerx =  (bc * (p2.y - p3.y) - cd * (p1.y - p2.y)) * idet;
        final double centery =  (cd * (p1.x - p2.x) - bc * (p2.x - p3.x)) * idet;
        final double radius =
                Math.sqrt( Math.pow(p2.x - centerx,2) + Math.pow(p2.y-centery,2));

        return new Circle(new Point((int) centerx,(int) centery),radius);
    }

    static class Circle
    {
        final Point center;
        final double radius;
        public Circle(Point center, double radius)
        {
            this.center = center; this.radius = radius;
        }

        public boolean inside(Point target) {
            return Math.pow(target.x - center.x, 2) + Math.pow(target.y - center.y, 2) < Math.pow(radius, 2);
        }
    }
}
