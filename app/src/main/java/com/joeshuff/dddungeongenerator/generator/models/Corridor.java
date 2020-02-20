package com.joeshuff.dddungeongenerator.generator.models;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Corridor {

    int startX = 0;
    int startY = 0;

    int heightX = 0; //GOES LEFT OR RIGHT
    int heightY = 0; //GOES UP OR DOWN

    public Corridor(int startX, int startY, int heightX, int heightY) {
        this.startX = startX;
        this.startY = startY;
        this.heightX = heightX;
        this.heightY = heightY;
    }

    public Corridor globalise(int startX, int startY) {
        return new Corridor(this.startX  + startX, this.startY + startY, heightX, heightY);
    }

    public static List<Corridor> toCorridors(List<Point> points) {
        return new ArrayList<>();
    }

}
