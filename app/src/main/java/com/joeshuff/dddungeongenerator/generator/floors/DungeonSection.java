package com.joeshuff.dddungeongenerator.generator.floors;

import android.graphics.Point;
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.generator.dungeon.Room;
import com.joeshuff.dddungeongenerator.generator.generating.DelauneyTriangulate;
import com.joeshuff.dddungeongenerator.generator.generating.MinSpanningTree;
import com.joeshuff.dddungeongenerator.generator.generating.PathFinding;
import com.joeshuff.dddungeongenerator.generator.models.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DungeonSection  {

    private int id;

    private int height = 0;
    private int width = 0;

    private transient Dungeon mainDungeon;
    private transient Floor floor;

    private Point startPoint = new Point();

    private transient List<DelauneyTriangulate.Triangle> triangulateGraph = new ArrayList<>();
    private transient List<MinSpanningTree.Edge> minSpanningTree = new ArrayList<>();
    private transient List<MinSpanningTree.Edge> triangularEdges = new ArrayList<>();

    private List<List<Point>> corridors = new ArrayList<>();
    private transient List<Point> centers = new ArrayList<>();
    private transient List<MinSpanningTree.Edge> finalConnections = new ArrayList<>();

    public List<Room> rooms = new ArrayList<>();

    public static boolean linearProgression = false;

    public DungeonSection(int id, Dungeon mainDungeon, Floor floor, int width, int height, Point p) {
        this(id, mainDungeon, floor, width, height);
        this.startPoint = p;
    }

    public DungeonSection(int id, Dungeon mainDungeon, Floor floor, int width, int height) {
        this.id = id;
        this.mainDungeon = mainDungeon;
        this.floor = floor;
        this.height = height;
        this.width = width;
    }

    public int getId() {
        return id;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    protected void branchOut() {
        int coverage = 0;
        int totalCoverage = height * width;

        while (coverage < mainDungeon.getGlobalModifier().getMapCoveragePercentage() * totalCoverage) {
            coverage = 0;

            for (Room r : rooms) {
                coverage += r.getArea() * 1.5f;
            }

            rooms.add(new Room(this, rooms.size()  + 1, mainDungeon.getRnd(), mainDungeon.getGlobalModifier()));
        }
    }

    public void calculateNearestPartner() {
        boolean allDone = false;

        while (!allDone) {
            allDone = true;

            for (Room r1 : rooms) {

                if (r1.isRejected()) continue;

                Room nearest = null;
                int largestCoverage = 0;

                for (Room r2 : rooms) {
                    if (r1 == r2) continue;
                    if (r2.isRejected()) continue;

                    int coverage = r1.intersects(r2);
                    if (coverage > largestCoverage) {
                        nearest = r2;
                        largestCoverage = coverage;
                    }
                }

                if (nearest != null) {
                    if (r1.moveAwayFrom(nearest)) {
                        allDone = false;
//                        System.out.println("Nearest to " + r1.getId() + " is " + nearest.getId());
                    }
                }
            }
        }
    }

    public void calculateRejectedRooms() {
        int validRooms = 0;

        for (Room r : rooms) {
            r.calculateRejected(rooms);
            if (r.isSelected() && !r.isRejected()) validRooms ++;
        }

        if (validRooms == 0) {
            Room best = null;
            double fail = Integer.MAX_VALUE;

            for (Room r : rooms) {
                if (r.calcHowMuchItFailedBy() < fail) {
                    fail = r.calcHowMuchItFailedBy();
                    best = r;
                }
            }

            best.forceSelect();
        }
    }

    public void triangulate() {
        centers = new ArrayList<>();

        for (Room r : rooms) {
            if (!r.isSelected() || r.isRejected()) continue;
            centers.add(r.myCenter());
        }

        triangulateGraph = DelauneyTriangulate.calculate(centers);
    }

    public void minSpanningTree() {
        if (triangulateGraph.isEmpty()) return;

        triangularEdges = new ArrayList<>();

        for (DelauneyTriangulate.Triangle triangle : triangulateGraph) {
            triangularEdges.addAll(triangle.getEdges());
        }

        List<MinSpanningTree.Edge> tempEdges = new ArrayList<>();
        tempEdges.addAll(triangularEdges);

        minSpanningTree = MinSpanningTree.calculate(tempEdges, centers);
    }

    public void combinePaths() {
        finalConnections = new ArrayList<>();
        finalConnections.addAll(minSpanningTree);

        if (linearProgression) return;

        for (MinSpanningTree.Edge edge : triangularEdges) {
            if (finalConnections.contains(edge)) continue;

            if (mainDungeon.getRnd().nextDouble() <= mainDungeon.getGlobalModifier().getTriangulationAdditionChange()) {
                finalConnections.add(edge);
            }
        }
    }

    public void pathFind() {
        corridors = PathFinding.findPaths(this, rooms, finalConnections);
    }

    public void finalise() {
        finalConnections.clear();
        minSpanningTree.clear();
    }

    public void clearUnnecessaryData() {
        triangularEdges.clear();
        triangulateGraph.clear();
        rooms.removeIf(room -> !room.isSelected() || room.isRejected());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Floor getFloor() {
        return floor;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<List<Point>> getCorridors() {
        return corridors;
    }

    public List<List<Point>> getGlobalCorridors() {
        List<List<Point>> globalCorridors = new ArrayList<>();

        for (List<Point> localCorridor : getCorridors()) {

            List<Point> globalCorridor = new ArrayList<>();

            for (Point point : localCorridor) {
                globalCorridor.add(new Point(point.x + startPoint.x, point.y + startPoint.y));
            }

            globalCorridors.add(globalCorridor);
        }

        return globalCorridors;
    }

    public List<DelauneyTriangulate.Triangle> getTriangulateGraph() {
        return triangulateGraph;
    }

    public List<DelauneyTriangulate.Triangle> getGlobalTriangulateGraph() {
        List<DelauneyTriangulate.Triangle> globalTriangles = new ArrayList<>();

        for (DelauneyTriangulate.Triangle triangle : getTriangulateGraph()) {
            globalTriangles.add(triangle.translate(startPoint));
        }

        return globalTriangles;
    }

    public List<MinSpanningTree.Edge> getGlobalFinalConnections() {
        List<MinSpanningTree.Edge> globalEdges = new ArrayList<>();

        for (MinSpanningTree.Edge edge : getFinalConnections()) {
            globalEdges.add(edge.translate(startPoint));
        }

        return globalEdges;
    }

    public List<MinSpanningTree.Edge> getFinalConnections() {
        return finalConnections;
    }

    public List<MinSpanningTree.Edge> getGlobalMinSpanningTree() {
        List<MinSpanningTree.Edge> globalEdges = new ArrayList<>();

        for (MinSpanningTree.Edge edge : getMinSpanningTree()) {
            globalEdges.add(edge.translate(startPoint));
        }

        return globalEdges;
    }

    public List<MinSpanningTree.Edge> getMinSpanningTree() {
        return minSpanningTree;
    }

    public Dungeon getMainDungeon() {
        return mainDungeon;
    }

    public Rectangle me() {
        return new Rectangle(startPoint.x, startPoint.y, width, height);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DungeonSection)) return false;
        return ((DungeonSection) o).id == id;
    }
}

