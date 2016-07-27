package com.example.cossettenavigation.map;

import android.util.Log;

import java.util.ArrayList;

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

    private static final String DEFAULT_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";


    private static ArrayList<AnchorBeacon> anchorBeacons = new ArrayList<>();

    private static ArrayList<Zone> zones = new ArrayList<>();


    public static ArrayList<AnchorBeacon> getAnchorBeacons() {
        return anchorBeacons;
    }

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
    private static AnchorBeacon addAnchorBeacon(String name,
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
    private static AnchorBeacon addAnchorBeacon(String name,
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


    private static void addLooseBeacons() {
        AnchorBeacon b1 = addAnchorBeacon(
                "white1 - Lower Elevator",
                15, 60,
                DEFAULT_UUID, 6607, 59029);
        AnchorBeacon b2 = addAnchorBeacon(
                "white2 - End Lower Hallway",
                30, 80,
                DEFAULT_UUID, 62315, 20156);
    }

    private static void addFloor1() {
        AnchorBeacon b1 = addAnchorBeacon(
                "white17 - Entrance",
                20, 0,
                DEFAULT_UUID, 46447, 25300);
        AnchorBeacon b2 = addAnchorBeacon(
                "white1 - Lower Elevator",
                15, 60,
                DEFAULT_UUID, 6607, 59029);
        AnchorBeacon b3 = addAnchorBeacon(
                "white2 - End Lower Hallway",
                30, 80,
                DEFAULT_UUID, 62315, 20156);

/*        // TODO - make a support beacon
        AnchorBeacon b4 = addAnchorBeacon(
                "white5 - Kitchen",
                b1, -5, 25,
                DEFAULT_UUID, 33753, 28870);*/

        Zone z1 = addZone("Main Hallway");
        z1.addAnchorBeacons(b1, b2, b3);
//        z1.addSupportBeacons(b4);
    }

    private static void addFloor2() {
        AnchorBeacon ice1 = addAnchorBeacon(
                "ice1 - Floor 2",
                20, 0,
                DEFAULT_UUID, 9051, 52752);
        AnchorBeacon ice2 = addAnchorBeacon(
                "ice2 - Floor 2",
                0, 25,
                DEFAULT_UUID, 27598, 15040);
        AnchorBeacon ice3 = addAnchorBeacon(
                "ice3 - Floor 2",
                10, 100,
                DEFAULT_UUID, 62693, 23343);
        AnchorBeacon ice4 = addAnchorBeacon(
                "ice4 - Floor 2",
                60, 0,
                DEFAULT_UUID, 42484, 10171);

        Zone z1 = addZone("Open Area - Floor 2");
        z1.addAnchorBeacons(ice1, ice2, ice3, ice4);
    }




    // Define beacons and zones
    static {
        Log.v(TAG, "static initializer");

        addFloor2();

        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            Log.v(TAG, anchorBeacon.toString());
        }
        for (Zone zone : zones) {
            Log.v(TAG, zone.toString());
        }
    }

}
