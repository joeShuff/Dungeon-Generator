package com.joeshuff.dddungeongenerator.generator.dungeon;

import android.graphics.Point;
import com.joeshuff.dddungeongenerator.Logs;
import com.joeshuff.dddungeongenerator.generator.features.*;
import com.joeshuff.dddungeongenerator.generator.floors.DungeonSection;
import com.joeshuff.dddungeongenerator.generator.models.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Room  {

    static transient List<String> featureIntros = Arrays.asList("You've heard rumors that ",
            "You've discovered that ",
            "Maps have told you that ",
            "It became apparent that ",
            "You know that ");

    private transient DungeonSection thisSection;
    private int sectionStartX = 0;
    private int sectionStartY = 0;

    private static transient final int MIN_MEAN_ROOM_SIZE = 45;
    private static transient final int MAX_MEAN_ROOM_SIZE = 85;

    private static transient final int MIN_STD_DEV = 15;
    private static transient final int MAX_STD_DEV = 45;

    private static transient int MEAN_ROOM_SIZE = 65;
    private static transient int STD_DEV = 30;

    private int id;

    private String detail;

    private int startX;
    private int startY;

    private boolean isSelected = false;
    private boolean isRejected = false;

    private int width;
    private int height;

    static transient double DEFAULT_MONSTER_CHANCE = 0.15d;
    static transient double DEFAULT_TRAP_CHANCE = 0.2d;
    static transient double DEFAULT_TREASURE_CHANCE = 0.25d;
    static transient double DEFAULT_STAIRS_CHANCE = 0.05d;

    private int timesShifted = 0;

    transient Random rnd;

    List<RoomFeature> featureList = new ArrayList<>();

    transient List<Movement> movementHistory = new ArrayList<>();

    public static void changeRoomSize(float percentage) {
        MEAN_ROOM_SIZE = (int) (MIN_MEAN_ROOM_SIZE + ((MAX_MEAN_ROOM_SIZE - MIN_MEAN_ROOM_SIZE) * percentage));
        STD_DEV = (int) (MIN_STD_DEV + ((MAX_STD_DEV - MIN_STD_DEV) * percentage));
    }

    public void setId(int id) {
        this.id = id;
    }

    public Room(DungeonSection section, int id, Random rnd, Modifier modifier) {
        this.rnd = rnd;
        this.thisSection = section;
        this.sectionStartX = section.getStartPoint().x;
        this.sectionStartY = section.getStartPoint().y;

        this.id = id;

        Point center = randomPointInCircle((section.getWidth() / 4));

        this.height = (int) ((rnd.nextGaussian() * STD_DEV) + MEAN_ROOM_SIZE);
        this.width = (int) ((rnd.nextGaussian() * STD_DEV) + MEAN_ROOM_SIZE);

        this.detail = "This room is " + height + "ft x " + width + "ft\n" + RoomDetail.getDetail(rnd);

        startX = (section.getWidth() / 2) + center.x - (this.width / 2);
        startY = (section.getHeight() / 2) + center.y - (this.height / 2);

        generateFeatures(modifier);
    }

    public String getDetail() {
        return detail;
    }

    public String getFullRoomDescription() {
        List<String> descParts = getRoomDescription();

        String res = "";

        for (String d : descParts) {
            res = res + d + "\n";
        }

        return res;
    }

    public List<String> getRoomDescription() {
        List<String> description = new ArrayList<>();

        for (RoomFeature feature : getFeatureList()) {
            description.add(featureIntros.get(new Random().nextInt(featureIntros.size())) + feature.getFeatureDescription());
        }

        return description;
    }

    public void connectRoomTo(Room room, String connection, StairsFeature.DIRECTION direction) {
        StairsFeature newStairs = new StairsFeature(direction.opposite(), connection, room.getId(), room.getThisSection().getFloor().getLevel());
        featureList.add(newStairs);
    }

    private Point randomPointInCircle(int radius) {
        double t = 2.0f * Math.PI * rnd.nextDouble();
        double u = rnd.nextDouble() + rnd.nextDouble();
        double r;

        if (u > 1) {
            r = 2 - u;
        } else {
            r = u;
        }

        Point p = new Point();
        p.x = (int) (radius * r * Math.cos(t));
        p.y = (int) (radius * r * Math.sin(t));

        return p;
    }

    public boolean moveAwayFrom(Room room) {
        Point center = myCenter();
        Point otherCenter = room.myCenter();

        if (timesShifted >= 1000) {
            isRejected = true;
            return true;
        }

        Point diff = new Point(center.x - otherCenter.x, center.y - otherCenter.y);

        Rectangle intersection = room.meWithBorder().intersection(meWithBorder());

        if (intersection.isEmpty()) return false;
        if (diff.x == 0 && diff.y == 0) return false;

        int xOverlap = intersection.width;
        int yOverlap = intersection.height;

        if (diff.x < 0) { //other is to my right
            xOverlap *= -1;
        }

        if (diff.y < 0) { //other is below me
            yOverlap *= -1;
        }

        boolean canMoveX = (startX + xOverlap > 0 && (startX + xOverlap + width) < thisSection.getWidth());
        boolean canMoveY = (startY + yOverlap > 0 && (startY + yOverlap + height) < thisSection.getHeight());

        int myCentreX = startX + (width / 2);
        int myCentreY = startY + (height / 2);

        int sectionCentreX = thisSection.getWidth() / 2;
        int sectionCentreY = thisSection.getHeight() / 2;

        boolean towardCentreX = ((myCentreX < sectionCentreX && xOverlap > 0) ||
                                    myCentreX > sectionCentreX && xOverlap < 0);

        boolean towardCentreY = ((myCentreY < sectionCentreY && yOverlap > 0) ||
                                myCentreY > sectionCentreY && yOverlap < 0);

        if ((canMoveX) && (canMoveY)) {
            if (Math.abs(xOverlap) > Math.abs(yOverlap)) {
                yOverlap = 0;
            } else {
                xOverlap = 0;
            }
        } else if (canMoveX) {
            yOverlap = 0;
        } else if (canMoveY) {
            xOverlap = 0;
        }

//        xOverlap = diff.x; //Old spreading
//        yOverlap = diff.y;

        if (startX + xOverlap > 0 && (startX + xOverlap + width) < thisSection.getWidth()) {
            if (startY + yOverlap > 0 && (startY + yOverlap + height) < thisSection.getHeight()) {

                Movement thisMovement = new Movement(new Point(startX, startY), new Point(startX + xOverlap, startY + yOverlap));

                if (movementHistory.contains(thisMovement)) {
                    movementHistory.get(movementHistory.indexOf(thisMovement)).increment();
                    thisMovement = movementHistory.get(movementHistory.indexOf(thisMovement));
                } else {
                    movementHistory.add(thisMovement);
                }

                if (thisMovement.getTimesMade() > 5) {
                    Logs.i("Room", "Rejecting " + getId() + " because it's bouncing", null);
                    isRejected = true;
                    return false;
                }

                startX += xOverlap;
                startY += yOverlap;

                timesShifted ++;
                return true;
            }
        }

        return false;
    }

    public Point myCenter() {
        return new Point(startX + (width / 2), startY + (height / 2));
    }

    public Point myGlobalCenter() {
        return new Point(getGlobalStartX() + (width / 2), getGlobalStartY() + (height / 2));
    }

    public double calcHowMuchItFailedBy() {
        double lowerBound = MEAN_ROOM_SIZE - STD_DEV;
        double upperBound = MEAN_ROOM_SIZE + STD_DEV;

        double widthDiff = 0;
        double heightDiff = 0;

        if (width > upperBound) widthDiff = Math.abs(width - upperBound) / upperBound;
        if (width < lowerBound) widthDiff = Math.abs(lowerBound - width) / lowerBound;

        if (height > upperBound) heightDiff = Math.abs(height - upperBound) / upperBound;
        if (height < lowerBound) heightDiff = Math.abs(lowerBound - height) / lowerBound;

        return (widthDiff + heightDiff) / 2;
    }

    public void forceSelect() {
        isRejected = false;
        isSelected = true;
    }

    private boolean willIBeRejected() {
        int lowerBound = MEAN_ROOM_SIZE - STD_DEV;
        int upperBound = MEAN_ROOM_SIZE + STD_DEV;

        if (width < upperBound && width > lowerBound && height < upperBound && height > lowerBound) {
            return false;
        }

        return true;
    }

    public void calculateRejected(List<Room> rooms) {
        for (Room other : rooms) {
            if (this == other) continue;
            if (other.isRejected) continue;

            if (me().intersects(other.me())) {
                if (getArea() > other.getArea()) {
                    isRejected = true;
                } else {
                    other.isRejected = true;
                }
            }
        }

        isSelected = !willIBeRejected();
    }

    public int intersects(Room r) {
        double totalCoverage = Math.min(r.getArea(), getArea());

        Rectangle me = new Rectangle(startX - 3, startY - 3, width + 6, height + 6);
        Rectangle other = new Rectangle(r.startX, r.startY, r.width, r.height);

        Rectangle intersection = me.intersection(other);

        if (intersection.width < 0 || intersection.height < 0) return 0;

        double actualCoverage = me.intersection(other).height * me.intersection(other).width;

        return (int) (100 * (actualCoverage / totalCoverage));
    }

    public List<RoomFeature> getFeatureList() {
        return featureList;
    }

    public void generateFeatures(Modifier modifier) {
        if (willIBeRejected()) return;

        //monster chance
        double monChance = rnd.nextDouble();
        if (monChance <= DEFAULT_MONSTER_CHANCE * modifier.getMonsterChanceModifier()) {
            featureList.add(new MonsterFeature(String.format("%.0f", (monChance * Math.pow(10, 16))), modifier));
        }

        double trapChance = rnd.nextDouble();
        //trap chance
        if (trapChance <= DEFAULT_TRAP_CHANCE * modifier.getTrapChanceModifier()) {
            featureList.add(new TrapFeature(String.format("%.0f", (trapChance * Math.pow(10, 16))), modifier));
        }

        //treasure chance
        double treasureChance = rnd.nextDouble();
        if (rnd.nextDouble() <= DEFAULT_TREASURE_CHANCE * modifier.getTreasureChanceModifier()) {
            featureList.add(new TreasureFeature(String.format("%.0f", (treasureChance * Math.pow(10, 16)))));
        }

        //Stairs Chance
        //As you get further away from the ground floor, the chance halves every floor you are away, to stop infinite growing

        double stairModifier = modifier.stairChanceModifier;
        int level = thisSection.getFloor().getLevel();

        double chanceOfStairFeature = (DEFAULT_STAIRS_CHANCE * stairModifier) / (1 + Math.abs(level));
        double stairChance = rnd.nextDouble();

        if (stairChance <= chanceOfStairFeature) {
            StairsFeature stairsFeature = new StairsFeature(String.format("%.0f", (stairChance * Math.pow(10, 16))), modifier, this);

            featureList.add(stairsFeature);

            stairsFeature.setFloorTarget(thisSection.getFloor().newFloor(stairsFeature.getDirection().getElevation(), this));
        }
    }

    public boolean containsPoint(Point p) {
        return (p.x > startX && p.x < (startX + width) &&
                p.y > startY && p.y < (startY + height));
    }

    public boolean containsGlobalPoint(Point p) {
        return (p.x > getGlobalStartX() && p.x < (getGlobalStartX() + width) &&
                p.y > getGlobalStartY() && p.y < (getGlobalStartY() + height));
    }

    //=========================
    //=========================
    //===== GETTERS ===========
    //=========================
    //=========================
    public int getId() {
        return id;
    }

    public int getGlobalStartX() {
        return startX + sectionStartX;
    }

    public int getGlobalStartY() {
        return startY + sectionStartY;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getArea() {
        return width * height;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Rectangle me() {
        return new Rectangle(startX, startY, width, height);
    }

    public Rectangle meWithBorder() {
        return new Rectangle(startX - 2, startY - 2, width + 4, height + 4);
    }

    public Rectangle topBorder() {
        return new Rectangle(startX, startY - 4, width, 6);
    }

    public Rectangle rightBorder() {
        return new Rectangle(startX + width - 2, startY, 6, height);
    }

    public Rectangle leftBorder() {
        return new Rectangle(startX - 4, startY, 6, height);
    }

    public Rectangle bottomBorder() {
        return new Rectangle(startX, startY + height - 2, width, 6);
    }

    public DungeonSection getThisSection() {
        return thisSection;
    }
}
