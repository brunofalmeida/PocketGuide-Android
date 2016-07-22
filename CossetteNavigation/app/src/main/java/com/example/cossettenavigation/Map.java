package com.example.cossettenavigation;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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

    private static final String TAG = "Map";

    private static final String DEFAULT_WHITE_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";


    private static ArrayList<AnchorBeacon> anchorBeacons = new ArrayList<>();

    private static ArrayList<Zone> zones = new ArrayList<>();




    /**
     * Adds a beacon to the set of anchor beacons.
     * @return The anchor beacon that was added.
     */
    private static AnchorBeacon addAnchorBeacon(AnchorBeacon anchorBeacon) {
        anchorBeacons.add(anchorBeacon);
        return anchorBeacon;
    }

    /**
     * Adds a beacon to the set of anchor beacons using an absolute position.
     * @return The anchor beacon that was added.
     */
    private static AnchorBeacon addAbsolutePositionAnchorBeacon(String name,
                                                                double xPosition,
                                                                double yPosition,
                                                                String uuid,
                                                                int major,
                                                                int minor) {

        AnchorBeacon anchorBeacon = new AnchorBeacon(name, xPosition, yPosition, uuid, major, minor);
        return addAnchorBeacon(anchorBeacon);
    }

    /**
     * Adds a beacon to the set of anchor beacons using a position relative to another beacon.
     * @return The anchor beacon that was added.
     */
    private static AnchorBeacon addRelativePositionAnchorBeacon(String name,
                                                                Beacon referenceBeacon,
                                                                double xPositionOffset,
                                                                double yPositionOffset,
                                                                String uuid,
                                                                int major,
                                                                int minor) {

        AnchorBeacon anchorBeacon = new AnchorBeacon(
                name,
                referenceBeacon, xPositionOffset, yPositionOffset,
                uuid, major, minor);
        return addAnchorBeacon(anchorBeacon);
    }


    /**
     * Adds a zone to the set of zones.
     * @return The zone that was added.
     */
    private static Zone addZone(String name) {
        Zone zone = new Zone(name);
        zones.add(zone);
        return zone;
    }


    // Define beacons and zones
    static {
        AnchorBeacon b1 = addAbsolutePositionAnchorBeacon(
                "white17 - Entrance",
                20, 0,
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 46447, 25300);
 /*       AnchorBeacon b2 = addRelativePositionAnchorBeacon(
                "white5 - Kitchen",
                b1, -5, 25,
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 33753, 28870);*/
        AnchorBeacon b3 = addAbsolutePositionAnchorBeacon(
                "white1 - Lower Elevator",
                15, 60,
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 6607, 59029);
        AnchorBeacon b4 = addAbsolutePositionAnchorBeacon(
                "white2 - End Lower Hallway",
                30, 80,
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", 62315, 20156);


        Zone z1 = addZone("Main Hallway");
        z1.addAnchorBeacon(b1);
/*
        z1.addSupportBeacon(b2);
*/

        for (Beacon anchorBeacon : anchorBeacons) {
            Log.v(TAG, anchorBeacon.toString());
        }
        for (Zone zone : zones) {
            Log.v(TAG, zone.toString());
        }
    }


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

        @Override
        public String toString() {
            return String.format("Point { x = %f, y = %f }", x, y);
        }
    }


    private static abstract class Beacon {

        protected String name;
        protected Point position;

        protected UUID uuid;
        protected int major;
        protected int minor;


        /**
         * Standard constructor.
         */
        public Beacon(String name, Point position, UUID uuid, int major, int minor) {
            this.name = name;
            this.position = position;
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;
        }

        /**
         * Constructor using an absolute position.
         */
        public Beacon(String name,
                      double xPosition,
                      double yPosition,
                      String uuid,
                      int major,
                      int minor) {

            this(name, new Point(xPosition, yPosition), UUID.fromString(uuid), major, minor);
        }

        /**
         * Constructor using a position relative to another beacon.
         */
        public Beacon(String name,
                      Beacon referenceBeacon,
                      double xPositionOffset,
                      double yPositionOffset,
                      String uuid,
                      int major,
                      int minor) {

            this(
                    name,
                    referenceBeacon.getXPosition() + xPositionOffset,
                    referenceBeacon.getYPosition() + yPositionOffset,
                    uuid,
                    major,
                    minor);
        }

        public Point getPosition() {
            return position;
        }

        public double getXPosition() {
            return getPosition().getX();
        }

        public double getYPosition() {
            return getPosition().getY();
        }

        @Override
        public String toString() {
            return String.format(
                    "Beacon { name = %s, position = %s, uuid = %s, major = %d, minor = %d }",
                    name, position, uuid, major, minor);
        }

    }


    private static class AnchorBeacon extends Beacon {

        private ArrayList<WeakReference<Zone>> zones = new ArrayList<>();

        /**
         * Standard constructor.
         */
        public AnchorBeacon(String name, Point position, UUID uuid, int major, int minor) {
            super(name, position, uuid, major, minor);
        }

        /**
         * Constructor using an absolute position.
         */
        public AnchorBeacon(String name,
                            double xPosition,
                            double yPosition,
                            String uuid,
                            int major,
                            int minor) {

            super(name, xPosition, yPosition, uuid, major, minor);
        }

        /**
         * Constructor using a position relative to another beacon.
         */
        public AnchorBeacon(String name,
                            Beacon referenceBeacon,
                            double xPositionOffset,
                            double yPositionOffset,
                            String uuid,
                            int major,
                            int minor) {

            super(name, referenceBeacon, xPositionOffset, yPositionOffset, uuid, major, minor);
        }

        public void addZone(Zone zone) {
            zones.add(new WeakReference<Zone>(zone));
        }

    }


    private static class SupportBeacon extends Beacon {

        private WeakReference<Zone> zone = null;

        /**
         * Standard constructor.
         */
        public SupportBeacon(String name, Point position, UUID uuid, int major, int minor) {
            super(name, position, uuid, major, minor);
        }

        /**
         * Constructor using an absolute position.
         */
        public SupportBeacon(String name,
                             double xPosition,
                             double yPosition,
                             String uuid,
                             int major,
                             int minor) {

            super(name, xPosition, yPosition, uuid, major, minor);
        }

        /**
         * Constructor using a position relative to another beacon.
         */
        public SupportBeacon(String name,
                             Beacon referenceBeacon,
                             double xPositionOffset,
                             double yPositionOffset,
                             String uuid,
                             int major,
                             int minor) {

            super(name, referenceBeacon, xPositionOffset, yPositionOffset, uuid, major, minor);
        }

        public void setZone(Zone zone) {
            this.zone = new WeakReference<Zone>(zone);
        }

    }


    private static class Zone {

        private String name;

        // A Zone does not own its anchor beacons, since they can define multiple zones
        private ArrayList<WeakReference<AnchorBeacon>> anchorBeacons = new ArrayList<>();

        // A zone owns its support beacons, since they are only part of that zone
        private ArrayList<SupportBeacon> supportBeacons = new ArrayList<>();


        public Zone(String name) {
            this.name = name;
        }

        public void addAnchorBeacon(AnchorBeacon anchorBeacon) {
            anchorBeacons.add(new WeakReference<AnchorBeacon>(anchorBeacon));
            anchorBeacon.addZone(this);
        }

        public void addSupportBeacon(SupportBeacon supportBeacon) {
            supportBeacons.add(supportBeacon);
            supportBeacon.setZone(this);
        }

        @Override
        public String toString() {
            return String.format(
                    "Zone { name = %s, anchorBeacons = %s, supportBeacons = %s }",
                    name, anchorBeacons, supportBeacons);
        }

    }

}
