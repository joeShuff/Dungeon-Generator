package com.joeshuff.dddungeongenerator.generator.dungeon;

import android.graphics.Point;

import java.io.Serializable;

public class Movement  {

    public Movement(Point oldStart, Point newStart) {
        this.oldStart = oldStart;
        this.newStart = newStart;
    }

    public void increment() {
        timesMade ++;
    }

    public Point oldStart;

    public Point newStart;

    int timesMade = 1;

    public int getTimesMade() {
        return timesMade;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Movement)) return false;
        Movement other = (Movement) o;

        return other.oldStart.equals(oldStart) && other.newStart.equals(newStart);
    }
}
