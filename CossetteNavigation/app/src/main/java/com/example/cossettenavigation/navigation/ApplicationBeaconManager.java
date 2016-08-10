package com.example.cossettenavigation.navigation;

import android.app.Application;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Map;
import com.example.cossettenavigation.map.Point;
import com.example.cossettenavigation.map.SupportBeacon;
import com.example.cossettenavigation.map.Zone;
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
    private HashMap<Region, BeaconTrackingData> trackedBeacons = new HashMap<>();




    @Override
    public void onCreate() {
        //Log.v(TAG, "onCreate()");

        super.onCreate();

        // Initialize Map class
        Map map = new Map();

        // App ID & App Token can be taken from App section of Estimote Cloud.
        //EstimoteSDK.initialize(this, getString(R.string.app_name), getString(R.string.app_name));
        // Optional, debug logging.
        EstimoteSDK.enableDebugLogging(true);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //logTrackedBeacons();
                //Log.v(TAG, getTrackedBeaconsDescription());

                //getEstimatedLocation();
            }
        }, 1, 1000);

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

                beaconManager.startRanging(region);

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

                beaconManager.stopRanging(region);

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

        // Monitor all anchor beacons
        for (AnchorBeacon anchorBeacon : Map.anchorBeacons) {
            beaconManager.startMonitoring(new Region(
                    anchorBeacon.getName(),
                    anchorBeacon.getUUID(),
                    anchorBeacon.getMajor(),
                    anchorBeacon.getMinor()));
        }
    }




    private void updateTrackedBeacon(Region region, Beacon beacon) {
        //Log.v(TAG, "updateTrackedBeacon()");

/*        Log.v(TAG, String.format(
        "Beacon: accuracy = %f, proximity = %s, %s",
        Utils.computeAccuracy(beacon), Utils.computeProximity(beacon), beacon));*/

        // Add the beacon if it isn't already tracked
        if (!trackedBeacons.containsKey(region)) {

            // Try to find the beacon in the map
            com.example.cossettenavigation.map.Beacon mapBeacon = null;

            for (AnchorBeacon anchorBeacon : Map.anchorBeacons) {
                if (Utilities.areEqual(region, anchorBeacon)) {
                    mapBeacon = anchorBeacon;
                }
            }

            for (Zone zone : Map.zones) {
                for (SupportBeacon supportBeacon : zone.getSupportBeacons()) {
                    if (Utilities.areEqual(region, supportBeacon)) {
                        mapBeacon = supportBeacon;
                    }
                }
            }

            if (mapBeacon == null) {
                Log.e(TAG, String.format(
                        "updateTrackedBeacon(): Tracked beacon not found in map\nregion = %s\nbeacon = %s",
                        region, beacon));
            } else {
                trackedBeacons.put(region, new BeaconTrackingData(mapBeacon));
            }
        }

        // The beacon must be in the tracked set, so update it with measurements
        trackedBeacons.get(region).addMeasurements(beacon);

        //Log.v(TAG, trackedBeacons.get(region).toString());
    }

    private void removeTrackedBeacon(Region region) {
        trackedBeacons.remove(region);
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
        for (HashMap.Entry<Region, BeaconTrackingData> trackedBeacon : trackedBeacons.entrySet()) {
            // Add position and distance (in metres)
            positions.add(new double[] {
                    trackedBeacon.getValue().getBeacon().getXPosition() * Map.metresPerGridUnit,
                    trackedBeacon.getValue().getBeacon().getYPosition() * Map.metresPerGridUnit });
            distances.add(trackedBeacon.getValue().getEstimatedAccuracy());
        }


        // Trilaterate position

        // If there are 3 or more beacons (required for 2D triangulation)
        if (positions.size() >= 3) {

/*            double[][] positions = new double[][] { { 5.0, -6.0 }, { 13.0, -15.0 }, { 21.0, -3.0 }, { 12.4, -21.2 } };
            double[] distances = new double[] { 8.06, 13.97, 23.32, 15.31 };*/

            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(
                    new TrilaterationFunction(
                            Utilities.getDoubleDoubleArray(positions), Utilities.getDoubleArray(distances)),
                    new LevenbergMarquardtOptimizer());
            LeastSquaresOptimizer.Optimum optimum = solver.solve();

            // the answer
            double[] centroid = optimum.getPoint().toArray();

            // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
            RealVector standardDeviation = optimum.getSigma(0);
            RealMatrix covarianceMatrix = optimum.getCovariances(0);



            Point estimatedLocation = new Point(centroid[0] / Map.metresPerGridUnit, centroid[1] / Map.metresPerGridUnit);

            //Log.i(TAG, "getEstimatedLocation(): " + estimatedLocation);

            return estimatedLocation;

        } else {
            //Log.i(TAG, "getEstimatedLocation(): Not enough beacons to trilaterate location");

            return null;
        }
    }




    public void logTrackedBeacons() {
        String string = "logTrackedBeacons():\n";

        for (java.util.Map.Entry<Region, BeaconTrackingData> beacon : trackedBeacons.entrySet()) {
            string += String.format(
                    "%s : %s\n",
                    beacon.getValue(), beacon.getKey());
        }

        Log.v(TAG, string);
    }

    public String getTrackedBeaconsDescription() {
        //Log.v(TAG, "getTrackedBeaconsDescription()");

        String string = "";

        for (java.util.Map.Entry<Region, BeaconTrackingData> entry : trackedBeacons.entrySet()) {
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
                    estimatedLocation.x, estimatedLocation.y);
        }

        return string;
    }


}
