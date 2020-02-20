package com.joeshuff.dddungeongenerator.generator.floors;

import android.graphics.Point;
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.generator.dungeon.Room;
import com.joeshuff.dddungeongenerator.generator.generating.DelauneyTriangulate;
import com.joeshuff.dddungeongenerator.generator.generating.MinSpanningTree;
import com.joeshuff.dddungeongenerator.generator.models.Rectangle;

import java.util.*;
import java.util.List;

public class Floor {

    List<DungeonSection> sectionList = new ArrayList<>();

    private transient static final int MIN_SECTION_SIZE = Dungeon.MAP_SIZE / 5;
    private transient static final int MAX_SECTION_SIZE = (int) (Dungeon.MAP_SIZE * 0.66d);

    transient Dungeon dungeon;
    transient Random rnd;

    int level = 0;

    /**
     * Once a floor has started adding rooms, no new sections can be generated. When a room is generated with stairs to this floor
     * no action is taken and a room is assigned to those stairs later.
     */
    boolean startedAddingRooms = false;

    public Floor(Dungeon mainDungeon, Random rnd, int level) {
        dungeon = mainDungeon;
        this.rnd = rnd;
        this.level = level;
    }

    public void sectionFrom(Point p) {
        if (startedAddingRooms) return;

        int width = (int) (MIN_SECTION_SIZE + ((MAX_SECTION_SIZE - MIN_SECTION_SIZE) * rnd.nextDouble()));
        int height = (int) (MIN_SECTION_SIZE + ((MAX_SECTION_SIZE - MIN_SECTION_SIZE) * rnd.nextDouble()));

        int startX = Math.max(p.x - (width / 2), 0);
        int startY = Math.max(p.y - (height / 2), 0);

        if (startX + width > Dungeon.MAP_SIZE) {
            width -= (startX + width) - Dungeon.MAP_SIZE;
        }

        if (startY + height > Dungeon.MAP_SIZE) {
            height -= (startY + height) - Dungeon.MAP_SIZE;
        }

        Point startPoint = new Point(startX, startY);

        sectionList.add(new DungeonSection(sectionList.size(), dungeon, this, width, height, startPoint));
    }

    public Room getRoomClosestTo(Room root) {
        Rectangle rootRec = root.me();

        Optional<Room> closest = getAllRooms().stream()
                .sorted((room, t1) ->
                        (int) (Math.sqrt(Math.pow(room.me().intersection(rootRec).width, 2) + Math.pow(room.me().intersection(rootRec).height, 2)) -
                                Math.sqrt(Math.pow(t1.me().intersection(rootRec).width, 2) + Math.pow(t1.me().intersection(rootRec).height, 2))))
                .findFirst();

        Room closestRoom;
        if (closest.isPresent()) {
            closestRoom = closest.get();
        } else {
            try {
                closestRoom = getAllRooms().get(0);
            } catch (Exception e) {
                return null;
            }
        }

        System.out.println("Closest room to (" + root.getGlobalStartX() + "," + root.getGlobalStartY() + "; " + root.getWidth() + "x" + root.getHeight() + ") is room " + closestRoom.getId());
        return closestRoom;
    }

    public void mergeSections() {
        List<DungeonSection> newSectionList = new ArrayList<>();
        newSectionList.addAll(sectionList);
        boolean allClear = true;

        for (DungeonSection section: sectionList) {
            for (DungeonSection comparingSection : sectionList) {
                if (section == comparingSection) continue;

                if (section.me().intersects(comparingSection.me())) {
                    newSectionList.remove(section);
                    newSectionList.remove(comparingSection);

//                    System.out.println("2 sections are overlapping (" + section.me() + ") (" + comparingSection.me() + ")");

                    Point topLeft = new Point(
                            Math.min(section.getStartPoint().x, comparingSection.getStartPoint().x),
                            Math.min(section.getStartPoint().y, comparingSection.getStartPoint().y)
                    );

                    Point bottomRight = new Point(
                            Math.max(section.getStartPoint().x + section.getWidth(), comparingSection.getStartPoint().x + comparingSection.getWidth()),
                            Math.max(section.getStartPoint().y + section.getHeight(), comparingSection.getStartPoint().y + comparingSection.getHeight())
                    );

                    Point center = new Point(
                            topLeft.x + ((bottomRight.x - topLeft.x) / 2),
                            topLeft.y + ((bottomRight.y - topLeft.y) / 2));

                    int width = (section.getWidth() + comparingSection.getWidth()) / 2;
                    int height = (section.getHeight() + comparingSection.getHeight()) / 2;

                    topLeft = new Point(center.x - (width / 2), center.y - (height / 2));

//                    int width = bottomRight.x - topLeft.x;
//                    int height = bottomRight.y - topLeft.y;

//                    System.out.println("combined to " + topLeft + " at " + width + "x" + height);
                    System.out.println("rectangle('position', [" + section.getStartPoint().x + ", " + section.getStartPoint().y + "," + section.getWidth() + "," + section.getHeight() + "], 'edgecolor',[0, 1, 0])");
                    System.out.println("rectangle('position', [" + comparingSection.getStartPoint().x + ", " + comparingSection.getStartPoint().y + "," + comparingSection.getWidth() + "," + comparingSection.getHeight() + "], 'edgecolor',[0, 1, 0])");

                    DungeonSection newSection = new DungeonSection(section.getId(), dungeon, this, width, height, topLeft);
                    System.out.println("rectangle('position', [" + newSection.getStartPoint().x + ", " + newSection.getStartPoint().y + "," + newSection.getWidth() + "," + newSection.getHeight() + "], 'edgecolor',[1, 0, 0])");

                    newSectionList.add(newSection);
                    allClear = false;
                    break;
                }
            }

            if (!allClear) break;
        }

        sectionList.clear();
        sectionList.addAll(newSectionList);

        if (!allClear) {
            mergeSections();
        }
    }

    public void fillFloor() {
        sectionList.add(new DungeonSection(sectionList.size(), dungeon, this, dungeon.width, dungeon.height, new Point(0, 0)));
    }

    public void splitFloor() {
        sectionList.add(new DungeonSection(sectionList.size(), dungeon, this, dungeon.width / 2, dungeon.height, new Point(0, 0)));
        sectionList.add(new DungeonSection(sectionList.size(), dungeon, this, dungeon.width / 2, dungeon.height, new Point(dungeon.width / 2, 0)));
    }

    public Floor newFloor(int elevation, Room from) {
        Floor newOrExistingFloor = dungeon.addFloorForLevel(level + elevation);

        if (!newOrExistingFloor.startedAddingRooms) {
            newOrExistingFloor.sectionFrom(from.myGlobalCenter());
        }

        return newOrExistingFloor;
    }

    public void branchOut() {
        startedAddingRooms = true;
        mergeSections();

        for (DungeonSection section : sectionList) {
            section.branchOut();
        }
    }

    public List<DungeonSection> getSectionList() {
        return sectionList;
    }

    public List<List<Point>> getAllCorridors() {
        List<List<Point>> allCorridors = new ArrayList<>();

        for (DungeonSection section : sectionList) {
            allCorridors.addAll(section.getGlobalCorridors());
        }

        return allCorridors;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();

        for (DungeonSection section : sectionList) {
            rooms.addAll(section.getRooms());
        }

        return rooms;
    }

    public List<MinSpanningTree.Edge> getAllMinSpanningTree() {
        List<MinSpanningTree.Edge> edges = new ArrayList<>();

        for (DungeonSection section : sectionList) {
            edges.addAll(section.getGlobalMinSpanningTree());
        }

        return edges;
    }

    public List<DelauneyTriangulate.Triangle> getTriangulationEdges() {
        List<DelauneyTriangulate.Triangle> triangles = new ArrayList<>();

        for (DungeonSection section : sectionList) {
            triangles.addAll(section.getGlobalTriangulateGraph());
        }

        return triangles;
    }

    public List<MinSpanningTree.Edge> getFinalConnections() {
        List<MinSpanningTree.Edge> finalEdges = new ArrayList<>();

        for (DungeonSection section : sectionList) {
            finalEdges.addAll(section.getGlobalFinalConnections());
        }

        return finalEdges;
    }

    public int getLevel() {
        return level;
    }
}
