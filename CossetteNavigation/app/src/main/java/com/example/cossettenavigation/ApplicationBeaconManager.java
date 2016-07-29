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

        double accuracy;
        Utils.Proximity proximity;

        public BeaconData(Beacon beacon) {
            this.accuracy = Utils.computeAccuracy(beacon);
            this.proximity = Utils.computeProximity(beacon);
        }

        public double getAccuracy() {
            return accuracy;
        }

        public Utils.Proximity getProximity() {
            return proximity;
        }

        public void setAccuracy(double accuracy) {
            this.accuracy = accuracy;
        }

        public void setProximity(Utils.Proximity proximity) {
            this.proximity = proximity;
        }

        @Override
        public String toString() {
            return String.format(
                    "BeaconData { accuracy = %f, proximity = %s }",
                    getAccuracy(), getProximity());
        }
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

        // TODO
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.v(TAG, getTrackedBeaconsLog());
                Log.v(TAG, getTrackedBeaconsDescription());
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

                if (list.size() == 1) {
                    updateTrackedBeacon(region, list.get(0));
                } else {
                    Log.w(TAG, "Unexpected number of beacons in region: " + list.size());
                }

                startRanging(region);
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.v(TAG, "BeaconManager.MonitoringListener onExitedRegion()");

                Log.v(TAG, "Region: " + region);
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

    private void stopRanging() {
//        beaconManager.stopRanging(ALL_BEACONS_REGION);
    }

    private void updateTrackedBeacon(Region region, Beacon beacon) {
        Log.v(TAG, String.format(
                "Beacon: accuracy = %f, proximity = %s, %s",
                Utils.computeAccuracy(beacon), Utils.computeProximity(beacon), beacon));
        trackedBeacons.put(region, new BeaconData(beacon));
    }

    public String getTrackedBeaconsLog() {
        Log.v(TAG, "getTrackedBeaconsLog()");

        String string = "trackedBeacons:\n";

        for (java.util.Map.Entry<Region, BeaconData> beacon : trackedBeacons.entrySet()) {
            string += String.format(
                    "%s : %s\n",
                    beacon.getValue(), beacon.getKey());
        }

        return string;
    }

    public String getTrackedBeaconsDescription() {
        Log.v(TAG, "getTrackedBeaconsDescription()");

        String string = "";

        for (java.util.Map.Entry<Region, BeaconData> entry : trackedBeacons.entrySet()) {
            string += String.format(
                    "%s : %f\n",
                    entry.getKey().getIdentifier(), entry.getValue().getAccuracy());
        }

        return string;
    }

}
