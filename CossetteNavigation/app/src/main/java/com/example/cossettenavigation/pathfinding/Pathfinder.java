package com.example.cossettenavigation.pathfinding;

import android.util.Log;
import android.util.Pair;

import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.map.Map;
import com.example.cossettenavigation.map.SupportBeacon;
import com.example.cossettenavigation.map.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by Bruno on 2016-08-10.
 */
public class Pathfinder {

    private static final String TAG = "Pathfinder";

    private static double INFINITY = 999999999;


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
            return spfa((AnchorBeacon) startBeacon, (AnchorBeacon) endBeacon);
        }

        else {
            Log.e(TAG, "getShortestPath(): Invalid beacon types");
            return null;
        }
    }


    private static Pair<Double, ArrayList<Beacon>> spfa(AnchorBeacon startBeacon,
                                                        AnchorBeacon endBeacon) {
        // Construct graph from map (adjacency list)
        // each AnchorBeacon -> { (connected beacon, travel time), ... }
        HashMap<AnchorBeacon, ArrayList<Pair<AnchorBeacon, Double>>> graph = new HashMap<>();


        for (AnchorBeacon beacon : Map.anchorBeacons) {
            ArrayList<Pair<AnchorBeacon, Double>> beaconConnections = new ArrayList<>();

            for (Zone zone : beacon.getZones()) {

                for (AnchorBeacon connectedBeacon : zone.getAnchorBeacons()) {

                    if (beacon != connectedBeacon) {
                        beaconConnections.add(new Pair<>(
                                connectedBeacon,
                                Map.estimateTravelTime(beacon, connectedBeacon)));
                    }
                }
            }

            graph.put(beacon, beaconConnections);
        }


        // each AnchorBeacon -> (shortest travel time from root, previous beacon in shortest path)
        HashMap<AnchorBeacon, Pair<Double, AnchorBeacon>> shortestTotalTimes = new HashMap<>();

        // List of nodes to be visited next
        PriorityQueue<AnchorBeacon> queue = new PriorityQueue<>();


        // Set all travel times to infinity
        for (AnchorBeacon beacon : graph.keySet()) {
            shortestTotalTimes.put(beacon, new Pair<Double, AnchorBeacon>(INFINITY, null));
        }

        // Setup root node
        shortestTotalTimes.put(startBeacon, new Pair<Double, AnchorBeacon>(0.0, null));
        queue.offer(startBeacon);

        while (queue.size() > 0) {
            AnchorBeacon currentBeacon = queue.poll();

            for (Pair<AnchorBeacon, Double> connection : graph.get(currentBeacon)) {
                AnchorBeacon connectedBeacon = connection.first;
                double connectionTime = connection.second;

                double testTotalTime = shortestTotalTimes.get(currentBeacon).first + connectionTime;

                if (testTotalTime < shortestTotalTimes.get(connectedBeacon).first) {
                    shortestTotalTimes.put(connectedBeacon, new Pair<>(testTotalTime, currentBeacon));
                    if (!queue.contains(connectedBeacon)) {
                        queue.offer(connectedBeacon);
                    }
                }
            }
        }

        if (shortestTotalTimes.get(endBeacon).first == INFINITY) {
            Log.e(TAG, "spfa(): No path found");

            return null;

        } else {
            Log.e(TAG, "spfa(): Path found");

            double shortestTotalTime = shortestTotalTimes.get(endBeacon).first;
            ArrayList<Beacon> path = new ArrayList<>();

            path.add(endBeacon);

            AnchorBeacon currentBeacon = endBeacon;
            while (shortestTotalTimes.get(currentBeacon).second != null) {
                currentBeacon = shortestTotalTimes.get(currentBeacon).second;
                path.add(0, currentBeacon);
            }

            return new Pair<>(shortestTotalTime, path);
        }
    }


}
