package com.example.cossettenavigation.pathfinding;

import android.util.Log;
import android.util.Pair;

import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.map.Map;
import com.example.cossettenavigation.map.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * <p>
 *     Shortest Path Faster Algorithm (SPFA)
 * </p>
 *
 * <p>
 *     Should not be used directly by client code; use the algorithm through the Pathfinding class.
 * </p>
 *
 * @see Pathfinder
 * @see <a href="https://en.wikipedia.org/wiki/Shortest_Path_Faster_Algorithm">SPFA</a>
 */
class SPFA {

    private static final String TAG = "SPFA";

    static Pair<Double, ArrayList<Beacon>> spfa(AnchorBeacon startBeacon,
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
            shortestTotalTimes.put(beacon, new Pair<Double, AnchorBeacon>(Pathfinder.INFINITY, null));
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

        if (shortestTotalTimes.get(endBeacon).first == Pathfinder.INFINITY) {
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
