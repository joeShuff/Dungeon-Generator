package com.joeshuff.dddungeongenerator.generator.features;

import android.graphics.Point;
import com.joeshuff.dddungeongenerator.generator.dungeon.Dungeon;
import com.joeshuff.dddungeongenerator.generator.dungeon.Modifier;
import com.joeshuff.dddungeongenerator.generator.dungeon.Room;
import com.joeshuff.dddungeongenerator.generator.floors.Floor;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StairsFeature implements RoomFeature {

    public enum DIRECTION {
        UP(1), DOWN(-1);

        int elevation = 0;
        DIRECTION(int elevation) {
            this.elevation = elevation;
        }

        public int getElevation() {
            return elevation;
        }

        public DIRECTION opposite() {
            if (elevation == 1) return DOWN;
            return UP;
        }
    }

    static transient List<Connection> possibleConnections = Arrays.asList(
            new Connection("There is a ladder passing up through a hole in the ceiling.", "There is a ladder heading down in the floor"),
            new Connection("There is a huge staircase climbing to a new level.", "There is a huge staircase leading to a lower level"),
            new Connection("There appears to be a slide coming from above", "There is a slide descending down into darkness."),
            new Connection("There is a huge pile of rubble on the floor as a result of the ceiling collapsing", "There is a massive hole in the floor, this appears to have collapsed."),
            new Connection("There is a small staircase waiting to be explored.", "There is a small staircase descending to the floor below"));

    transient Connection chosenType;

    transient Room myRoom;

    transient Random rnd;

    transient Floor floorTarget;
    transient Room connectedRoom = null;

    DIRECTION direction;

    String description;
    int connectedRoomId = 0;
    int connectedFloorId = 0;

    public StairsFeature(String seed, Modifier modifier, Room myRoom) {
        this.rnd = new Random(Dungeon.stringToSeed(seed));
        this.myRoom = myRoom;

        chosenType = possibleConnections.get(rnd.nextInt(possibleConnections.size()));

        if (rnd.nextDouble() <= modifier.getGrowthChanceUp()) { //UP
            direction = DIRECTION.UP;
            description = chosenType.getUpDesc();
        } else {
            direction = DIRECTION.DOWN;
            description = chosenType.getDownDesc();
        }
    }

    public StairsFeature(DIRECTION direction, String description, int roomId, int connectedFloorId) {
        this.direction = direction;
        this.description = description;
        this.connectedRoomId = roomId;
        this.connectedFloorId = connectedFloorId;
    }

    public DIRECTION getDirection() {
        return direction;
    }

    public void setFloorTarget(Floor floorTarget) {
        this.floorTarget = floorTarget;
    }

    public Room getConnectedRoom() {
        if (connectedRoom == null) {
            Point myRoomCentre = new Point(
                    myRoom.getGlobalStartX() + (myRoom.getWidth() / 2),
                    myRoom.getGlobalStartY() + (myRoom.getHeight() / 2)
            );

            connectedRoom = floorTarget.getRoomClosestTo(myRoom);
            connectedFloorId = floorTarget.getLevel();
            connectedRoomId = connectedRoom.getId();

            String otherRoomDesc = "";

            if (direction == DIRECTION.UP) {
                otherRoomDesc = chosenType.getDownDesc();
            } else if (direction == DIRECTION.DOWN) {
                otherRoomDesc = chosenType.getUpDesc();
            }

            connectedRoom.connectRoomTo(myRoom, otherRoomDesc, direction);
        }

        return connectedRoom;
    }

    public int getConnectedRoomId() {
        return connectedRoomId;
    }

    public int getConnectedFloorId() {
        return connectedFloorId;
    }

    @Override
    public int getPageForMoreInfo() {
        return 0;
    }

    @Override
    public String getFeatureName() {
        return null;
    }

    @Override
    public String getFeatureDescription() {
        return description;
    }

    public static class Connection {
        String upDesc;
        String downDesc;

        public Connection(String up, String down) {
            this.upDesc = up;
            this.downDesc = down;
        }

        public String getUpDesc() {
            return upDesc;
        }

        public String getDownDesc() {
            return downDesc;
        }
    }
}
