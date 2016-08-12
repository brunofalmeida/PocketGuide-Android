package com.example.cossettenavigation.pathfinding;

import android.util.Log;
import android.util.Pair;

import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.map.Map;
import com.example.cossettenavigation.map.SupportBeacon;

import java.util.ArrayList;

/**
 * Created by Bruno on 2016-08-10.
 */
public class Pathfinder {

    private static final String TAG = "Pathfinder";

    static double INFINITY = 999999999;


    /**
     * Determines the shortest path between two beacons in the map.
     * @return The shortest travel time (in seconds); a list of beacons representing the shortest path,
     * beginning with startBeacon and ending with endBeacon. Returns null if no path is found.
     */
    public static Pair<Double, ArrayList<Beacon>> getShortestPath(Beacon startBeacon, Beacon endBeacon) {
        if (startBeacon instanceof SupportBeacon) {
            SupportBeacon startSupportBeacon = (SupportBeacon) startBeacon;

            double minimumTime = INFINITY;
            ArrayList<Beacon> shortestPath = null;

            for (AnchorBeacon anchorBeacon : startSupportBeacon.getZone().getAnchorBeacons()) {

                Pair<Double, ArrayList<Beacon>> testPath = getShortestPath(anchorBeacon, endBeacon);
                double testTime = Map.estimateTravelTime(startSupportBeacon, anchorBeacon) + testPath.first;

                if (testTime < minimumTime) {
                    minimumTime = testTime;
                    shortestPath = testPath.second;
                    shortestPath.add(0, startSupportBeacon);
                }
            }

            if (shortestPath != null) {
                return new Pair<>(minimumTime, shortestPath);
            } else {
                Log.e(TAG, "getShortestPath(): No path found (startBeacon instanceof SupportBeacon)");
                return null;
            }
        }

        else if (endBeacon instanceof SupportBeacon) {
            SupportBeacon endSupportBeacon = (SupportBeacon) endBeacon;

            double minimumTime = INFINITY;
            ArrayList<Beacon> shortestPath = null;

            for (   AnchorBeacon anchorBeacon :
                    endSupportBeacon.getZone().getAnchorBeacons()) {

                Pair<Double, ArrayList<Beacon>> testPath = getShortestPath(startBeacon, anchorBeacon);
                double testTime = testPath.first + Map.estimateTravelTime(anchorBeacon, endSupportBeacon);

                if (testTime < minimumTime) {
                    minimumTime = testTime;
                    shortestPath = testPath.second;
                    shortestPath.add(endSupportBeacon);
                }
            }

            if (shortestPath != null) {
                return new Pair<>(minimumTime, shortestPath);
            } else {
                Log.e(TAG, "getShortestPath(): No path found (endBeacon instanceof SupportBeacon)");
                return null;
            }
        }

        else if (startBeacon instanceof AnchorBeacon && endBeacon instanceof AnchorBeacon) {
            return new SPFA((AnchorBeacon) startBeacon, (AnchorBeacon) endBeacon).getShortestPath();
        }

        else {
            Log.e(TAG, "getShortestPath(): Invalid beacon types");
            return null;
        }
    }


}
