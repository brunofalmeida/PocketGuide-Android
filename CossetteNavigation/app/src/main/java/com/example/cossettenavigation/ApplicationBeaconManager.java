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

import java.util.List;

/**
 * Global application state used to detect and manage beacons.
 *
 * Monitoring is coarse, sending enter and exit events.
 * Ranging is fine, providing power readings.
 * - A service to manage beacon ranging (occurs at 1 second intervals).
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
                Log.v(TAG, "BeaconManager.RangingListener onBeaconsDiscovered()");

                Log.v(TAG, "Region: " + region);

                for (Beacon beacon : list) {
                    Log.v(TAG, String.format(
                            "Beacon: accuracy = %f, proximity = %s, %s",
                            Utils.computeAccuracy(beacon), Utils.computeProximity(beacon), beacon));
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

}
