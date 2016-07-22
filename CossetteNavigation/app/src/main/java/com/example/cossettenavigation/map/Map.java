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
        Log.v(TAG, "static initializer");

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

        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            Log.v(TAG, anchorBeacon.toString());
        }
        for (Zone zone : zones) {
            Log.v(TAG, zone.toString());
        }
    }

}
