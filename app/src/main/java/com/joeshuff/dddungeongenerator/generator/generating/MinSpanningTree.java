package com.joeshuff.dddungeongenerator.generator.generating;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MinSpanningTree {

    private static boolean longCorridors = false;

    public static void setLongCorridors(boolean longCorridors) {
        MinSpanningTree.longCorridors = longCorridors;
    }

    public static List<Edge> calculate(List<Edge> edges, List<Point> points) {
        List<Edge> mstree = new ArrayList<>();

        if (longCorridors) {
            Collections.sort(edges, Collections.reverseOrder());
        } else {
            Collections.sort(edges);
        }

        int amountofEdges = edges.size();
        while (amountofEdges > 0) {
            Edge minimum = edges.get(0);
            edges.remove(0);

            if (!addingCausesLoop(points, mstree, minimum)) {
                mstree.add(minimum);
            }

            amountofEdges --;
        }

        return mstree;
    }

    // A recursive function that uses visited[] and parent to detect
    // cycle in subgraph reachable from vertex v.
    private static boolean isCyclicUtil(int v, Boolean visited[], int parent, List<Integer>[] adjacent)
    {
        // Mark the current node as visited
        visited[v] = true;
        int i;

        // Recur for all the vertices adjacent to this vertex
        Iterator<Integer> it = adjacent[v].iterator();
        while (it.hasNext())
        {
            i = it.next();

            // If an adjacent is not visited, then recur for that
            // adjacent
            if (!visited[i])
            {
                if (isCyclicUtil(i, visited, v, adjacent))
                    return true;
            }

            // If an adjacent is visited and not parent of current
            // vertex, then there is a cycle.
            else if (i != parent)
                return true;
        }
        return false;
    }

    // Returns true if the graph contains a cycle, else false.
    private static boolean isCyclic(List<Integer>[] adjacent)
    {
        // Mark all the vertices as not visited and not part of
        // recursion stack
        Boolean visited[] = new Boolean[adjacent.length];
        for (int i = 0; i < adjacent.length; i++)
            visited[i] = false;

        // Call the recursive helper function to detect cycle in
        // different DFS trees
        for (int u = 0; u < adjacent.length; u++)
            if (!visited[u]) // Don't recur for u if already visited
                if (isCyclicUtil(u, visited, -1, adjacent))
                    return true;

        return false;
    }

    private static boolean addingCausesLoop(List<Point> points, List<Edge> tree, Edge newEdge) {
        List<Edge> tempTree = new ArrayList<>();
        tempTree.addAll(tree);

        tempTree.add(newEdge);

        List<Integer>[] adjacent = new List[points.size()];

        for (int i = 0; i < points.size(); i ++) {
            adjacent[i] = new ArrayList<>();
        }

        for (Edge e : tempTree) {
            int index = 0;

            for (Point p : points) {

                if (p == e.start && !adjacent[index].contains(e.end)) adjacent[index].add(points.indexOf(e.end));
                if (p == e.end && !adjacent[index].contains(e.start)) adjacent[index].add(points.indexOf(e.start));

                index ++;
            }
        }

        return isCyclic(adjacent);
    }

    public static class Edge implements Comparable<Edge> {
        private Point start;
        private Point end;
        private double distance;

        public Edge(Point start, Point end) {
            this.start = start;
            this.end = end;
            calculateDistance();
        }

        public Edge translate(Point p) {
            return new Edge(
                    new Point(this.start.x + p.x, this.start.y + p.y),
                    new Point(this.end.x + p.x, this.end.y + p.y)
            );
        }

        public void calculateDistance() {
            distance = Math.sqrt(Math.pow(start.x - end.x, 2) + Math.pow(start.y - end.y, 2 ));
        }

        public double getDistance() {
            return distance;
        }

        public Point getStart() {
            return start;
        }

        public Point getEnd() {
            return end;
        }

        @Override
        public int compareTo(Edge o) {
            return (int) (getDistance() - o.getDistance());
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Edge))return false;
            Edge other = (Edge) obj;
            return  ((start == other.start || start == other.end) && (end == other.start || end == other.end));
        }
    }
}
