package com.example.cossettenavigation;

import android.app.Application;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

/**
 * Global application state used to detect and manage beacons.
 *
 * Background monitoring tutorial:
 * http://developer.estimote.com/android/tutorial/part-2-background-monitoring/
 *
 * Created by Bruno on 2016-07-15.
 */
public class ApplicationManager extends Application {

    private final String TAG = "ApplicationManager";

    private BeaconManager beaconManager;

    private final Region TEST_REGION = new Region("Test Region", null, null, null);

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.v(TAG, "BeaconManager.ServiceReadyCallback onServiceReady()");
                startMonitoring();
            }
        });
    }

    private void startMonitoring() {
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Log.v(TAG, "BeaconManager.MonitoringListener onEnteredRegion()");

                for (Beacon beacon : list) {
                    Log.v(TAG, "Beacon found: " + beacon.toString());
                }
            }

            @Override
            public void onExitedRegion(Region region) {
                Log.v(TAG, "BeaconManager.MonitoringListener onExitedRegion()");
            }
        });

        beaconManager.startMonitoring(TEST_REGION);
    }

    private void stopMonitoring() {
        beaconManager.stopMonitoring(TEST_REGION);
    }

}
