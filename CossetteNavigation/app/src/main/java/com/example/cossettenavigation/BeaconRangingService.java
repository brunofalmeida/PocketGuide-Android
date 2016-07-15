package com.example.cossettenavigation;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.estimote.sdk.Region;

/**
 * A service to manage beacon ranging (occurs at 1 second intervals).
 *
 * Created by Bruno on 2016-07-15.
 */
public class BeaconRangingService extends IntentService {

    private static final String TAG = "BeaconRangingService";

    public static final String INTENT_KEY_REGION = "region";

    public BeaconRangingService() {
        super("BeaconRangingService");
    }

    /**
     * Service Intent handler. Expects a Region object.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Region region = (Region) intent.getParcelableExtra(INTENT_KEY_REGION);
        Log.v(TAG, region.toString());
    }

}
