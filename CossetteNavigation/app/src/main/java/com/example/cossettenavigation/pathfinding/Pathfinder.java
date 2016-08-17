package com.example.cossettenavigation.pathfinding;

import android.util.Log;
import android.util.Pair;

import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.map.Map;
import com.example.cossettenavigation.map.SupportBeacon;
import com.example.cossettenavigation.map.Zone;

import java.util.ArrayList;

/**
 * Interface for the pathfinding algorithm.
 */
public class Pathfinder {

    // TODO - add Path class - list of Step objects (2 beacons, zone, string description)
    // TODO - add ZoneType enum - hallway, open room, stairs, elevator
    // TODO - use Zone type for string description
    // TODO - account for not starting at a beacon? (go to nearest exit, close/far end of hallway, etc.) - can skip and assume starting beacon
    // TODO - estimate current Zone - closest beacon -> how many zones? -> 1 zone (definite), 2 zones (whichever has the next closest beacon)
    // TODO - add constants for average speed by stairs/elevator - calculate estimated distance based on Zone type
    // TODO - calculate angle for transitioning between steps - round to nearest 90 degree angle -> direction?

    private static final String TAG = "Pathfinder";

    static double INFINITY = Double.POSITIVE_INFINITY;




    /**
     * Determines the shortest path between two beacons in the map.
     * @return The shortest travel time (in seconds); a list of beacons representing the shortest path,
     * beginning with startBeacon and ending with endBeacon. Returns null if no path is found.
     */
    public static Path getShortestPath(Beacon startBeacon, Beacon endBeacon) {
        Pair<Double, ArrayList<Beacon>> result = null;

        if (startBeacon instanceof SupportBeacon) {
            result = getShortestPath((SupportBeacon) startBeacon, endBeacon);
        }

        else if (startBeacon instanceof AnchorBeacon) {
            result = getShortestPath((AnchorBeacon) startBeacon, endBeacon);
        }

        else {
            Log.e(TAG, "getShortestPath(Beacon, Beacon): Invalid startBeacon type");
            return null;
        }


        if (result != null) {
            // Decompose results
            double travelTime = result.first;
            ArrayList<Beacon> beacons = result.second;

            ArrayList<Step> steps = new ArrayList<>();

            // Generate each step
            for (int i = 0; i < beacons.size() - 1; i++) {
                Beacon beaconOne = beacons.get(i);
                Beacon beaconTwo = beacons.get(i + 1);

                // Find the common zone
                Zone zone = null;
                for (Zone beaconOneZone : beaconOne.getZones()) {
                    if (beaconTwo.getZones().contains(beaconOneZone)) {
                        zone = beaconOneZone;
                    }
                }

                // TODO - calculate absolute and relative angles (replace 90 with calculation)
                if (zone != null) {
                    Step step;

                    if (i == 0) {
                        step = new Step(beaconOne, beaconTwo, zone, 90, 90);
                    } else {
                        step = new Step(beaconOne, beaconTwo, zone, 90, 90 - steps.get(i - 1).getAbsoluteAngle());
                    }

                    steps.add(step);

                } else {
                    Log.e(TAG, "getShortestPath(Beacon, Beacon): Zone for Step " + i + " not found");
                    return null;
                }
            }

            return new Path(travelTime, steps);

        } else {
            return null;
        }
    }


    private static Pair<Double, ArrayList<Beacon>> getShortestPath(SupportBeacon startBeacon, Beacon endBeacon) {
        double minimumTime = INFINITY;
        ArrayList<Beacon> shortestPath = null;

        for (AnchorBeacon anchorBeacon : startBeacon.getZone().getAnchorBeacons()) {

            Pair<Double, ArrayList<Beacon>> testPath = getShortestPath(anchorBeacon, endBeacon);
            double testTime = Map.estimateTravelTime(startBeacon, anchorBeacon, startBeacon.getZone()) + testPath.first;

            if (testTime < minimumTime) {
                minimumTime = testTime;
                shortestPath = testPath.second;
                shortestPath.add(0, startBeacon);
            }
        }

        if (shortestPath != null) {
            return new Pair<>(minimumTime, shortestPath);
        } else {
            Log.e(TAG, "getShortestPath(SupportBeacon, Beacon): No path found");
            return null;
        }
    }


    private static Pair<Double, ArrayList<Beacon>> getShortestPath(AnchorBeacon startBeacon, Beacon endBeacon) {
        if (endBeacon instanceof SupportBeacon) {
            return getShortestPath(startBeacon, (SupportBeacon) endBeacon);
        }

        else if (endBeacon instanceof AnchorBeacon) {
            return getShortestPath(startBeacon, (AnchorBeacon) endBeacon);
        }

        else {
            Log.e(TAG, "getShortestPath(AnchorBeacon, Beacon): Invalid endBeacon type");
            return null;
        }
    }


    private static Pair<Double, ArrayList<Beacon>> getShortestPath(AnchorBeacon startBeacon, SupportBeacon endBeacon) {
        double minimumTime = INFINITY;
        ArrayList<Beacon> shortestPath = null;

        for (AnchorBeacon anchorBeacon : endBeacon.getZone().getAnchorBeacons()) {
            Pair<Double, ArrayList<Beacon>> testPath = getShortestPath(startBeacon, anchorBeacon);
            double testTime = testPath.first + Map.estimateTravelTime(anchorBeacon, endBeacon, endBeacon.getZone());

            if (testTime < minimumTime) {
                minimumTime = testTime;
                shortestPath = testPath.second;
                shortestPath.add(endBeacon);
            }
        }

        if (shortestPath != null) {
            return new Pair<>(minimumTime, shortestPath);
        } else {
            Log.e(TAG, "getShortestPath(AnchorBeacon, SupportBeacon): No path found");
            return null;
        }
    }


    private static Pair<Double, ArrayList<Beacon>> getShortestPath(AnchorBeacon startBeacon, AnchorBeacon endBeacon) {
        return new SPFA(startBeacon, endBeacon).getShortestPath();
    }


}
