package com.example.cossettenavigation.map;

/**
 * A 2-dimensional point with double precision.
 */
public class Point {

    public double x;
    public double y;


    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format(
                "%s { x = %.0f, y = %.0f }",
                getClass().getSimpleName(), x, y);
    }

}
