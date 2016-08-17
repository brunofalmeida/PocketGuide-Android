package com.example.cossettenavigation.map;

import android.util.Log;

import com.example.cossettenavigation.pathfinding.Path;
import com.example.cossettenavigation.pathfinding.Pathfinder;

import java.util.ArrayList;

/**
 * <p>
 * Organizing class for mapping data.
 * Uses a rectangular grid system to define the locations of beacons and zones.
 * </p>
 *
 * <p>
 * Anchor Beacons - Placed in key locations (e.g. ends of hallways, doors, entrances and exits, stairs, elevators).
 * </p>
 *
 * <p>
 * Support Beacons - Placed in supporting locations to improve location estimates (e.g. along hallways, middle of rooms).
 * </p>
 *
 * <p>
 * Zones - Key areas within a floor or building (e.g. hallways, rectangular spaces, stairs, elevators).
 * </p>
 */
public class Map {

    // TODO - add optional estimatedTravelTime property to Zone objects?

    // Average speeds, in metres/second
    private static final double WALKING_TRAVEL_SPEED = 1.0;
    private static final double STAIRS_TRAVEL_SPEED = 0.5;
    private static final double ELEVATOR_TRAVEL_SPEED = 0.4;

    private static final String TAG = "Map";

    private static final String DEFAULT_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";


    public static ArrayList<Floor> floors = new ArrayList<>();

    public static ArrayList<AnchorBeacon> anchorBeacons = new ArrayList<>();
    public static ArrayList<SupportBeacon> supportBeacons = new ArrayList<>();

    public static ArrayList<Zone> zones = new ArrayList<>();


    /*
    Grid properties.
    The grid is defined with arbitrary units, which can be converted to real distances with the given ratio.
     */
    public static double gridWidth = 1;
    public static double gridHeight = 1;
    public static double metresPerGridUnit = 1;




    /**
     * Assumes that both beacons are part of the same zone and have a straight-line connection.
     * @return The estimated travel time (in seconds) between the two beacons.
     */
    public static double estimateTravelTime(Beacon startBeacon, Beacon endBeacon, Zone zone) {
        // Calculate straight line distance
        double distance = Math.sqrt(
                Math.pow(endBeacon.getXPosition() - startBeacon.getXPosition(), 2) +
                Math.pow(endBeacon.getYPosition() - startBeacon.getYPosition(), 2) +
                Math.pow(endBeacon.getFloor().getZPosition() - startBeacon.getFloor().getZPosition(), 2));

        double metres = distance * metresPerGridUnit;

        switch (zone.getZoneType()) {
            case HALLWAY:
                return metres / WALKING_TRAVEL_SPEED;
            case ROOM:
                return metres / WALKING_TRAVEL_SPEED;
            case STAIRS:
                return metres / STAIRS_TRAVEL_SPEED;
            case ELEVATOR:
                return metres / ELEVATOR_TRAVEL_SPEED;
            default:
                Log.e(TAG, "estimateTravelTime(): ZoneType not found");
                return metres / WALKING_TRAVEL_SPEED;
        }
    }

    public static double estimateTravelAngle(Beacon startBeacon, Beacon endBeacon) {
        return Math.toDegrees(Math.atan2(
                endBeacon.getYPosition() - startBeacon.getYPosition(),
                endBeacon.getXPosition() - startBeacon.getXPosition()));
    }




    private static Floor addFloor(Floor floor) {
        floors.add(floor);
        return floor;
    }

    private static AnchorBeacon addAnchorBeacon(AnchorBeacon anchorBeacon) {
        anchorBeacons.add(anchorBeacon);
        return anchorBeacon;
    }

    private static SupportBeacon addSupportBeacon(SupportBeacon supportBeacon) {
        supportBeacons.add(supportBeacon);
        return supportBeacon;
    }

    private static Zone addZone(Zone zone) {
        zones.add(zone);
        return zone;
    }




    /*
    Define floors, beacons, and zones

    1. define floor
    2. define beacons -> associate with floor
    3. define zones -> associate with beacons
     */
    static {
        Log.v(TAG, "static {}");




        // Start floor 1

/*        gridWidth = 30;
        gridHeight = 100;


*//*        // TODO - make a support beacon
        AnchorBeacon b4 = addAnchorBeacon(
                "white5 - Kitchen",
                b1, -5, 25,
                DEFAULT_UUID, 33753, 28870);*//*

        Zone z1 = addZone("Main Lower Hallway");
        z1.addAnchorBeacons(b1, b2, b3);
//        z1.addSupportBeacons(b4);*/

        Floor floor1 = addFloor(new Floor("Floor 1", 0));

        AnchorBeacon white17 = addAnchorBeacon(new AnchorBeacon(
                "white17 - F1",
                floor1,
                5, 0,
                DEFAULT_UUID, 46447, 25300));

        SupportBeacon white5 = addSupportBeacon(new SupportBeacon(
                "white5 - F1",
                floor1,white17,
                -5, 8,
                DEFAULT_UUID, 33753, 28870));

        AnchorBeacon white10 = addAnchorBeacon(new AnchorBeacon(
                "white10 - F1",
                floor1, white5,
                2,15,
                DEFAULT_UUID, 65261, 60647));

        // End floor 1




        // Start floor 2

        gridWidth = 100;
        gridHeight = 100;
        metresPerGridUnit = 0.1;

        Floor floor2 = addFloor(new Floor("Floor 2", 5));

        AnchorBeacon white15 = addAnchorBeacon(new AnchorBeacon(
                "white15 - F2",
                floor2, white10,
                0,0,
                DEFAULT_UUID, 2949, 35856));

        SupportBeacon white1 = addSupportBeacon(new SupportBeacon(
                "white1 - F2",
                floor2, white15,
                0, -8,
                DEFAULT_UUID, 6607, 59029));

        AnchorBeacon white25 = addAnchorBeacon(new AnchorBeacon(
                "white25 - F2",
                floor2, white1,
                2, -3,
                DEFAULT_UUID, 27415, 8243));

        AnchorBeacon white9 = addAnchorBeacon(new AnchorBeacon(
                "white9 - F2",
                floor2, white1,
                -3,-5,
                DEFAULT_UUID, 47609, 17713));

/*      BLUE BEACONS that were used previously for debugging.
        AnchorBeacon ice1 = addAnchorBeacon(new AnchorBeacon(
                "ice1 - F2",
                floor2,
                20, 100,
                DEFAULT_UUID, 9051, 52752));
        AnchorBeacon ice2 = addAnchorBeacon(new AnchorBeacon(
                "ice2 - F2",
                floor2,
                0, 75,
                DEFAULT_UUID, 27598, 15040));
       AnchorBeacon ice3 = addAnchorBeacon(new AnchorBeacon(
                "ice3 - F2",
                floor2,
                10, 0,
                DEFAULT_UUID, 62693, 23343));
        AnchorBeacon ice4 = addAnchorBeacon(new AnchorBeacon(
                "ice4 - F2",
                floor2,
                50, 100,
                DEFAULT_UUID, 42484, 10171));


        Zone z1 = addZone(new Zone("Open Area - Floor 2", Zone.ZoneType.ROOM));
        z1.addAnchorBeacons(ice1, ice2, ice4);

        Zone z2 = addZone("2");
        Zone z3 = addZone("3");
        Zone z4 = addZone("4");
        Zone z5 = addZone("5");*/

        // End floor 2

        //Zones
        Zone z1 = addZone(new Zone("Main Hallway - Floor 1", Zone.ZoneType.ROOM));
        z1.addAnchorBeacons(white17, white10);
        z1.addSupportBeacons(white5);

        Zone z2 = addZone(new Zone("Stairs- Floor 1 to Floor 2", Zone.ZoneType.STAIRS));
        z2.addAnchorBeacons(white10, white15);

        Zone z3 = addZone(new Zone("Health Lab - Floor 2", Zone.ZoneType.ROOM));
        z3.addAnchorBeacons(white15);
        z3.addSupportBeacons(white1);

        Zone z4 = addZone(new Zone("Open Area - Floor 2", Zone.ZoneType.ROOM));
        z4.addAnchorBeacons(white25, white9);
        z4.addSupportBeacons(white1);

        //End Zones


        // Start other beacons

/*        AnchorBeacon b1 = addAnchorBeacon(
                "white1 - F1",
                15, 40,
                DEFAULT_UUID, 6607, 59029);
        AnchorBeacon b2 = addAnchorBeacon(
                "white2 - F1",
                30, 20,
                DEFAULT_UUID, 62315, 20156);

        // End other beacons




        // Test Pathfinder
        Path result = Pathfinder.getShortestPath(ice4, ice2);
        if (result != null) {
            Log.v(TAG, "Pathfinder Test: " + result);
        } else {
            Log.e(TAG, "Pathfinder test failed");
        }*/


        // Log all mapping data
        for (Floor floor : floors) {
            Log.v(TAG, floor.toString());
        }
        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            Log.v(TAG, anchorBeacon.toString());
        }
        for (SupportBeacon supportBeacon : supportBeacons) {
            Log.v(TAG, supportBeacon.toString());
        }
        for (Zone zone : zones) {
            Log.v(TAG, zone.toString());
        }
    }

}
