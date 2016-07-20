package com.example.cossettenavigation;

import java.util.UUID;

/**
 * Organizing class for mapping data.
 *
 * Uses a rectangular grid system to define "anchor beacons" and their location.
 * Other beacons ("support beacons") are defined relative to anchor beacons in zones.
 *
 * Created by Bruno on 2016-07-20.
 */
public class Map {

    private static class Point {

        private double x;
        private double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

    }


    private static class Beacon {

        private String name;
        private Point location;

        private UUID uuid;
        private int major;
        private int minor;

        public Beacon(String name, Point location, UUID uuid, int major, int minor) {
            this.name = name;
            this.location = location;
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;
        }

        public Beacon(String name, double x, double y, UUID uuid, int major, int minor) {
            this(name, new Point(x, y), uuid, major, minor);
        }

    }

}
