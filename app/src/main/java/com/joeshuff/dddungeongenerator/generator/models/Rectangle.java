package com.joeshuff.dddungeongenerator.generator.models;

import android.graphics.Point;

public class Rectangle extends android.support.constraint.solver.widgets.Rectangle {

    public Rectangle() {
        super();
    }

    public Rectangle(int x, int y, int width, int height) {
        super();
        setBounds(x, y, width, height);
    }

    public int getMinX() {
        return x;
    }

    public int getMaxX() {
        return x + width;
    }

    public int getMinY() {
        return y;
    }

    public int getMaxY() {
        return y + height;
    }

    public Rectangle intersection(Rectangle rectangle) {
        Rectangle res = new Rectangle();

        int x1 = Math.max(getMinX(), rectangle.getMinX());
        int y1 = Math.max(getMinY(), rectangle.getMinY());
        int x2 = Math.min(getMaxX(), rectangle.getMaxX());
        int y2 = Math.min(getMaxY(), rectangle.getMaxY());

        res.setBounds(x1, y1, x2 - x1, y2 - y1);
        return res;
    }

    public boolean intersects(Rectangle rectangle) {
        if (isEmpty() || rectangle.width <= 0 || rectangle.height <= 0) return false;

        double x1 = rectangle.x;
        double y1 = rectangle.y;
        double x2 = x1 + rectangle.width;
        double y2 = y1 + rectangle.height;

        return x + width > x1 && x < x2 &&
                y + height > y1 && y < y2;
    }

    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    public boolean isEmpty() {
        return width <= 0.0f || height <= 0.0f;
    }
}
