package com.example.cossettenavigation.pathfinding;

import android.util.Log;
import android.util.Pair;

import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.map.Map;
import com.example.cossettenavigation.map.SupportBeacon;

import java.util.ArrayList;

/**
 * Client code interface for the pathfinding algorithm.
 */
public class Pathfinder {

    private static final String TAG = "Pathfinder";

    static double INFINITY = Double.POSITIVE_INFINITY;




    /**
     * Determines the shortest path between two beacons in the map.
     * @return The shortest travel time (in seconds); a list of beacons representing the shortest path,
     * beginning with startBeacon and ending with endBeacon. Returns null if no path is found.
     */
    public static Pair<Double, ArrayList<Beacon>> getShortestPath(Beacon startBeacon, Beacon endBeacon) {
        if (startBeacon instanceof SupportBeacon) {
            return getShortestPath((SupportBeacon) startBeacon, endBeacon);
        }

        else if (startBeacon instanceof AnchorBeacon) {
            return getShortestPath((AnchorBeacon) startBeacon, endBeacon);
        }

        else {
            Log.e(TAG, "getShortestPath(Beacon, Beacon): Invalid startBeacon type");
            return null;
        }
    }


    private static Pair<Double, ArrayList<Beacon>> getShortestPath(SupportBeacon startBeacon, Beacon endBeacon) {
        double minimumTime = INFINITY;
        ArrayList<Beacon> shortestPath = null;

        for (AnchorBeacon anchorBeacon : startBeacon.getZone().getAnchorBeacons()) {

            Pair<Double, ArrayList<Beacon>> testPath = getShortestPath(anchorBeacon, endBeacon);
            double testTime = Map.estimateTravelTime(startBeacon, anchorBeacon) + testPath.first;

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
            double testTime = testPath.first + Map.estimateTravelTime(anchorBeacon, endBeacon);

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
