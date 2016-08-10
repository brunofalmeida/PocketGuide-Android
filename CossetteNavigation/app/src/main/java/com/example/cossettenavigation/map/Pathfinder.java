package com.example.cossettenavigation.map;

import android.util.Log;
import android.util.Pair;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Bruno on 2016-08-10.
 */
public class Pathfinder {

    private static final String TAG = "Pathfinder";

    private static double INFINITY = 999999999;


    /**
     * Determines the shortest path between two beacons in the map.
     * @return A list of beacons representing the shortest path,
     * beginning with startBeacon and ending with endBeacon.
     */
    public static Pair<Double, ArrayList<Beacon>> getShortestPath(Beacon startBeacon, Beacon endBeacon) {
        if (startBeacon instanceof SupportBeacon) {
            SupportBeacon startSupportBeacon = (SupportBeacon) startBeacon;

            double minimumTime = INFINITY;
            ArrayList<Beacon> shortestPath = null;

            for (   WeakReference<AnchorBeacon> anchorBeaconReference :
                    startSupportBeacon.getZone().get().getAnchorBeacons()) {

                AnchorBeacon anchorBeacon = anchorBeaconReference.get();

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

            for (   WeakReference<AnchorBeacon> anchorBeaconReference :
                    endSupportBeacon.getZone().get().getAnchorBeacons()) {

                AnchorBeacon anchorBeacon = anchorBeaconReference.get();

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

        else {
            return spfa(startBeacon, endBeacon);
        }
    }


    private static Pair<Double, ArrayList<Beacon>> spfa(Beacon startBeacon, Beacon endBeacon) {
        return new Pair<>(0.0, new ArrayList<Beacon>());
    }


}
