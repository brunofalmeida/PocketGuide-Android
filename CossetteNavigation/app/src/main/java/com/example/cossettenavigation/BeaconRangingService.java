package com.example.cossettenavigation;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

/**
 * A service to manage beacon ranging (occurs at 1 second intervals).
 *
 * Created by Bruno on 2016-07-15.
 */
public class BeaconRangingService extends IntentService {

    private static final String TAG = "BeaconRangingService";

    public static final String INTENT_KEY_REGION = "region";


    private BeaconManager beaconManager;

    private final Region TEST_REGION = new Region("Test Region", null, null, null);


    public BeaconRangingService() {
        super("BeaconRangingService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(this);
    }

    /**
     * Service Intent handler.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "onHandleIntent()");
        Region region = (Region) intent.getParcelableExtra(INTENT_KEY_REGION);
        Log.v(TAG, region.toString());

        // TODO - Use Region from Intent to limit scope of ranging

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.v(TAG, "BeaconManager.ServiceReadyCallback onServiceReady()");
                startRanging();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopRanging();
    }

    private void startRanging() {
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                Log.v(TAG, "BeaconManager.RangingListener onBeaconsDiscovered()");

                Log.v(TAG, region.toString());
                for (Beacon beacon : list) {
                    Log.v(TAG, beacon.toString());
                }
            }
        });

        beaconManager.startRanging(TEST_REGION);
    }

    private void stopRanging() {
        beaconManager.stopRanging(TEST_REGION);
    }

}
