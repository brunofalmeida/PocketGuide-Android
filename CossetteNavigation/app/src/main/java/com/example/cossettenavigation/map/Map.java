package com.example.cossettenavigation.map;

import android.util.Log;
import android.util.Pair;

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

    /**
     * In m/s.
     */
    private static final double NORMAL_WALKING_SPEED = 1.4;

    private static final String TAG = "Map";

    private static final String DEFAULT_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";


    public static ArrayList<AnchorBeacon> anchorBeacons = new ArrayList<>();
    public static ArrayList<SupportBeacon> supportBeacons = new ArrayList<>();
    public static ArrayList<Zone> zones = new ArrayList<>();
    public static ArrayList<Floor> floors = new ArrayList<>();

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
    public static double estimateTravelTime(Beacon startBeacon, Beacon endBeacon) {
        double distance = Math.sqrt(
                Math.pow(endBeacon.getXPosition() - startBeacon.getXPosition(), 2) +
                Math.pow(endBeacon.getYPosition() - startBeacon.getYPosition(), 2) );
        return distance * metresPerGridUnit / NORMAL_WALKING_SPEED;
    }




    /**
     * Adds an anchor beacon to the map.
     * @return The anchor beacon that was added.
     */
    private static AnchorBeacon addAnchorBeacon(AnchorBeacon anchorBeacon) {
        anchorBeacons.add(anchorBeacon);
        return anchorBeacon;
    }




    /**
     * Adds a support beacon to the map.
     * @return The support beacon that was added.
     */
    private static SupportBeacon addSupportBeacon(SupportBeacon supportBeacon) {
        supportBeacons.add(supportBeacon);
        return supportBeacon;
    }




    /**
     * Adds a zone to the map.
     * @return The zone that was added.
     */
    private static Zone addZone(Zone zone) {
        zones.add(zone);
        return zone;
    }




    /**
     * @return The floor that was added.
     */
    private static Floor addFloor(Floor floor) {
        floors.add(floor);
        return floor;
    }








    /*
    Define beacons and zones

    1. define beacons
    2. define floors -> add beacons
    3. define zones -> add beacons
     */
    static {
        Log.v(TAG, "static initializer");




        // Start floor 1

/*        gridWidth = 30;
        gridHeight = 100;

        AnchorBeacon b1 = addAnchorBeacon(
                "white17 - F1",
                20, 100,
                DEFAULT_UUID, 46447, 25300);
        AnchorBeacon b2 = addAnchorBeacon(
                "white1 - F1",
                15, 40,
                DEFAULT_UUID, 6607, 59029);
        AnchorBeacon b3 = addAnchorBeacon(
                "white2 - F1",
                30, 20,
                DEFAULT_UUID, 62315, 20156);

*//*        // TODO - make a support beacon
        AnchorBeacon b4 = addAnchorBeacon(
                "white5 - Kitchen",
                b1, -5, 25,
                DEFAULT_UUID, 33753, 28870);*//*

        Zone z1 = addZone("Main Lower Hallway");
        z1.addAnchorBeacons(b1, b2, b3);
//        z1.addSupportBeacons(b4);*/

        // End floor 1




        // Start floor 2

        gridWidth = 100;
        gridHeight = 100;
        metresPerGridUnit = 0.1;

        AnchorBeacon ice1 = addAnchorBeacon(new AnchorBeacon(
                "ice1 - F2",
                20, 100,
                DEFAULT_UUID, 9051, 52752));
        AnchorBeacon ice2 = addAnchorBeacon(new AnchorBeacon(
                "ice2 - F2",
                0, 75,
                DEFAULT_UUID, 27598, 15040));
/*        AnchorBeacon ice3 = addAnchorBeacon(new AnchorBeacon(
                "ice3 - F2",
                10, 0,
                DEFAULT_UUID, 62693, 23343));*/
        AnchorBeacon ice4 = addAnchorBeacon(new AnchorBeacon(
                "ice4 - F2",
                50, 100,
                DEFAULT_UUID, 42484, 10171));

        Floor floor2 = addFloor(new Floor("Floor 2", 5));
        floor2.addAnchorBeacons(ice1, ice2, ice4);

        Zone z1 = addZone(new Zone("Open Area - Floor 2"));
        z1.addAnchorBeacons(ice1, ice2, ice4);

/*        Zone z2 = addZone("2");
        Zone z3 = addZone("3");
        Zone z4 = addZone("4");
        Zone z5 = addZone("5");*/

        // End floor 2




        // Start other beacons

/*        AnchorBeacon b1 = addAnchorBeacon(
                "white1 - F1",
                15, 40,
                DEFAULT_UUID, 6607, 59029);
        AnchorBeacon b2 = addAnchorBeacon(
                "white2 - F1",
                30, 20,
                DEFAULT_UUID, 62315, 20156);*/

        // End other beacons




        // Test Pathfinder
        Pair<Double, ArrayList<Beacon>> result = Pathfinder.getShortestPath(ice4, ice2);

        String log = "Time: " + result.first.toString() + ", Path = { ";
        for (Beacon beacon : result.second) {
            log += beacon.toString() + ", ";
        }
        Log.v(TAG, log);


        // Log all mapping data
        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            Log.v(TAG, anchorBeacon.toString());
        }
        for (SupportBeacon supportBeacon : supportBeacons) {
            Log.v(TAG, supportBeacon.toString());
        }
        for (Floor floor : floors) {
            Log.v(TAG, floor.toString());
        }
        for (Zone zone : zones) {
            Log.v(TAG, zone.toString());
        }
    }

}
