package com.example.cossettenavigation;

import android.app.Application;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Map;
import com.example.cossettenavigation.map.Point;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Global application state used to detect and manage beacons.
 *
 * Monitoring is coarse, sending enter and exit events (30 second intervals).
 * Ranging is fine, providing power and approximate distance readings (1 second intervals).
 *
 * Created by Bruno on 2016-07-22.
 *
 * @see <a href="http://developer.estimote.com/android/tutorial/part-2-background-monitoring/">Monitoring tutorial</a>
 * @see <a href="http://developer.estimote.com/android/tutorial/part-3-ranging-beacons/">Ranging tutorial</a>
 */
public class ApplicationBeaconManager extends Application {

    private final String TAG = "AppBeaconManager";

    private final Region ALL_BEACONS_REGION = new Region("All Beacons", null, null, null);

    private BeaconManager beaconManager;

    /**
     * Set of beacons to be tracked over time (for location algorithms).
     */
    private HashMap<Region, BeaconData> trackedBeacons = new HashMap<>();




    /**
     * A collection of beacon data to be stored and updated over time.
     */
    private static class BeaconData {

        /**
         * Oldest measurements first, newest measurements last
         */
        private ArrayList<Double> accuracyMeasurements = new ArrayList<>();
        private ArrayList<Utils.Proximity> proximityMeasurements = new ArrayList<>();


        public void addMeasurements(Beacon beacon) {
            if (accuracyMeasurements.size() >= 5) {
                accuracyMeasurements.remove(0);
            }
            if (proximityMeasurements.size() >= 5) {
                proximityMeasurements.remove(0);
            }

            accuracyMeasurements.add(Utils.computeAccuracy(beacon));
            proximityMeasurements.add(Utils.computeProximity(beacon));
        }

        public double getEstimatedAccuracy() {
            double numerator = 0;
            double denominator = 0;

            for (int i = 0,                         weight = 1;
                 i < accuracyMeasurements.size();
                 i++,                               weight *= 2) {

                numerator += weight * accuracyMeasurements.get(i);
                denominator += weight;
            }

            if (denominator == 0) {
                return -1;
            } else {
                return numerator / denominator;
            }
        }

        @Override
        public String toString() {
            String string = "BeaconData { accuracyMeasurements = { ";
            for (Double accuracy : accuracyMeasurements) {
                string += accuracy + ", ";
            }
            string += "}, proximityMeasurements = { ";
            for (Utils.Proximity proximity : proximityMeasurements) {
                string += proximity + ", ";
            }
            string += "}, estimatedAccuracy = " + getEstimatedAccuracy() + " }";

            return string;
        }

    }


    /**
     * Checks if a beacon manager Region and a map Beacon represent the same beacon.
     * @return true if the UUID, major, and minor are equal, or false otherwise
     */
    private static boolean areEqual(Region region, com.example.cossettenavigation.map.Beacon beacon) {
        if (region.getProximityUUID() == beacon.getUUID() &&
                region.getMajor() == beacon.getMajor() &&
                region.getMinor() == beacon.getMinor()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * ArrayList to Array conversion for trilateration algorithm.
     * <br>
     * ArrayList &lt double[] &gt -> double[][]
     */
    private static double[][] getDoubleDoubleArray(ArrayList<double[]> arrayList) {
        double[][] array = new double[arrayList.size()][];

        for (int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }

        return array;
    }

    /**
     * ArrayList to Array conversion for trilateration algorithm.
     * <br>
     * ArrayList &lt Double &gt -> double[]
     */
    private static double[] getDoubleArray(ArrayList<Double> arrayList) {
        double[] array = new double[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }

        return array;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate()");

        super.onCreate();

        // Test Map class
        Map map = new Map();

        // App ID & App Token can be taken from App section of Estimote Cloud.
        //EstimoteSDK.initialize(this, getString(R.string.app_name), getString(R.string.app_name));
        // Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.v(TAG, getTrackedBeaconsLog());
                //Log.v(TAG, getTrackedBeaconsDescription());
                getEstimatedLocation();
            }
        }, 100, 1000);

        beaconManager = new BeaconManager(this);

        // Callback when the beacon manager has connected to the beacon service
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.v(TAG, "BeaconManager.ServiceReadyCallback onServiceReady()");

                setMonitoringListener();
                setRangingListener();

                startMonitoring();
            }
        });
    }


    private void setMonitoringListener() {
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.v(TAG, "BeaconManager.MonitoringListener onEnteredRegion()");

                Log.v(TAG, "Region: " + region);
                for (Beacon beacon : list) {
                    Log.v(TAG, "Beacon: " + beacon);
                }

                startRanging(region);

                if (list.size() == 1) {
                    updateTrackedBeacon(region, list.get(0));
                } else {
                    Log.w(TAG, "Unexpected number of beacons in region: " + list.size());
                }
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.v(TAG, "BeaconManager.MonitoringListener onExitedRegion()");

                Log.v(TAG, "Region: " + region);

                stopRanging(region);

                removeTrackedBeacon(region);
            }
        });
    }

    private void setRangingListener() {
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
/*                Log.v(TAG, "BeaconManager.RangingListener onBeaconsDiscovered()");

                Log.v(TAG, "Region: " + region);
                for (Beacon beacon : list) {
                    Log.v(TAG, "Beacon: " + beacon);
                }*/

                if (list.size() == 1) {
                    updateTrackedBeacon(region, list.get(0));
                } else {
                    Log.w(TAG, "Unexpected number of beacons in region: " + list.size());
                }
            }
        });
    }


    private void startMonitoring() {
//        beaconManager.startMonitoring(ALL_BEACONS_REGION);

        // Start monitoring all anchor beacons
        for (AnchorBeacon anchorBeacon : Map.getAnchorBeacons()) {
            beaconManager.startMonitoring(new Region(
                    anchorBeacon.getName(),
                    anchorBeacon.getUUID(),
                    anchorBeacon.getMajor(),
                    anchorBeacon.getMinor()));
        }
    }

    private void stopMonitoring() {
//        beaconManager.stopMonitoring(ALL_BEACONS_REGION);

        // Stop monitoring all anchor beacons
        for (AnchorBeacon anchorBeacon : Map.getAnchorBeacons()) {
            beaconManager.stopMonitoring(new Region(
                    anchorBeacon.getName(),
                    anchorBeacon.getUUID(),
                    anchorBeacon.getMajor(),
                    anchorBeacon.getMinor()));
        }
    }


    private void startRanging(Region region) {
//        beaconManager.startRanging(ALL_BEACONS_REGION);

        beaconManager.startRanging(region);
    }

    private void stopRanging(Region region) {
//        beaconManager.stopRanging(ALL_BEACONS_REGION);

        beaconManager.stopRanging(region);
    }

    private void updateTrackedBeacon(Region region, Beacon beacon) {
        //Log.v(TAG, "updateTrackedBeacon()");

/*        Log.v(TAG, String.format(
        "Beacon: accuracy = %f, proximity = %s, %s",
        Utils.computeAccuracy(beacon), Utils.computeProximity(beacon), beacon));*/

        if (!trackedBeacons.containsKey(region)) {
            trackedBeacons.put(region, new BeaconData());
        }

        trackedBeacons.get(region).addMeasurements(beacon);

        //Log.v(TAG, trackedBeacons.get(region).toString());
    }

    private void removeTrackedBeacon(Region region) {
        trackedBeacons.remove(region);
    }

    public String getTrackedBeaconsLog() {
        //Log.v(TAG, "getTrackedBeaconsLog()");

        String string = "trackedBeacons:\n";

        for (java.util.Map.Entry<Region, BeaconData> beacon : trackedBeacons.entrySet()) {
            string += String.format(
                    "%s : %s\n",
                    beacon.getValue(), beacon.getKey());
        }

        return string;
    }

    public String getTrackedBeaconsDescription() {
        //Log.v(TAG, "getTrackedBeaconsDescription()");

        String string = "";

        for (java.util.Map.Entry<Region, BeaconData> entry : trackedBeacons.entrySet()) {
            string += String.format(
                    "%s : %.3f m\n",
                    entry.getKey().getIdentifier(), entry.getValue().getEstimatedAccuracy());
        }

        Point estimatedLocation = getEstimatedLocation();
        if (estimatedLocation == null) {
            string += "Location Unavailable";
        } else {
            string += String.format(
                    "(%f, %f)",
                    estimatedLocation.getX(), estimatedLocation.getY());
        }

        return string;
    }

    /**
     * @see <a href="https://github.com/lemmingapex/Trilateration">Trilateration example</a>
     * @return Estimated location (on map grid), or null if not found
     */
    public Point getEstimatedLocation() {
        // Get beacon positions and distances
        // Convert positions to metres
        // { { x, y }, { x, y }, ... }
        ArrayList<double[]> positions = new ArrayList<>();
        ArrayList<Double> distances = new ArrayList<>();

        // Loop through tracked beacons
        for (HashMap.Entry<Region, BeaconData> trackedBeacon : trackedBeacons.entrySet()) {

            // Loop through beacons in map
            for (AnchorBeacon mapAnchorBeacon : Map.getAnchorBeacons()) {

                // If they both refer to the same beacon
                if (areEqual(trackedBeacon.getKey(), mapAnchorBeacon)) {

                    // Add position and distance (in metres)
                    positions.add(new double[] {
                            mapAnchorBeacon.getXPosition() * Map.metresPerGridUnit,
                            mapAnchorBeacon.getYPosition() * Map.metresPerGridUnit });
                    distances.add(trackedBeacon.getValue().getEstimatedAccuracy());
                }
            }
        }


        // Trilaterate position

        // If there are 3 or more beacons (required for 2D triangulation)
        if (positions.size() >= 3) {

/*            double[][] positions = new double[][] { { 5.0, -6.0 }, { 13.0, -15.0 }, { 21.0, -3.0 }, { 12.4, -21.2 } };
            double[] distances = new double[] { 8.06, 13.97, 23.32, 15.31 };*/

            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(
                    new TrilaterationFunction(
                            getDoubleDoubleArray(positions), getDoubleArray(distances)),
                    new LevenbergMarquardtOptimizer());
            LeastSquaresOptimizer.Optimum optimum = solver.solve();

            // the answer
            double[] centroid = optimum.getPoint().toArray();

            // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
            RealVector standardDeviation = optimum.getSigma(0);
            RealMatrix covarianceMatrix = optimum.getCovariances(0);



            Point estimatedLocation = new Point(centroid[0] / Map.metresPerGridUnit, centroid[1] / Map.metresPerGridUnit);
            Log.i(TAG, "getEstimatedLocation(): " + estimatedLocation);
            return estimatedLocation;

        } else {
            Log.i(TAG, "getEstimatedLocation(): Not enough beacons to trilaterate location");

            return null;
        }
    }

}
