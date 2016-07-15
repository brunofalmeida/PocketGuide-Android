package com.example.cossettenavigation;

import android.app.IntentService;
import android.content.Intent;

/**
 * A service to manage beacon ranging (occurs at 1 second intervals).
 *
 * Created by Bruno on 2016-07-15.
 */
public class BeaconRangingService extends IntentService {

    public BeaconRangingService() {
        super("BeaconRangingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

}
