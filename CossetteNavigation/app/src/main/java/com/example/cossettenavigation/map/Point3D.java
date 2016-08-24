package com.example.cossettenavigation.map;

import java.io.Serializable;

/**
 * A 3-dimensional point with double precision.
 */
public class Point3D implements Serializable {

    public double x;
    public double y;
    public double z;


    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return String.format(
                "%s { x = %.1f, y = %.1f, z = %.1f }",
                getClass().getSimpleName(), x, y, z);
    }

}
