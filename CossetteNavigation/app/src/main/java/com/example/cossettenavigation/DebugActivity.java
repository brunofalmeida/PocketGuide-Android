package com.example.cossettenavigation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class DebugActivity extends AppCompatActivity {

    private static final String TAG = "DebugActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_debug);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/



        final ApplicationBeaconManager beaconManager = (ApplicationBeaconManager) getApplication();
        final TextView beaconList = (TextView) findViewById(R.id.beacon_list);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beaconList.setText(beaconManager.getTrackedBeaconsDescription());
                    }
                });
            }
        }, 100, 1000);




        LinearLayout mapLayout = (LinearLayout) findViewById(R.id.map_layout);

        LinearLayout.LayoutParams floorMapViewLayoutParams = new LinearLayout.LayoutParams(0, 0);
        floorMapViewLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        floorMapViewLayoutParams.weight = 1;
        mapLayout.addView(new FloorMapView(this), floorMapViewLayoutParams);

        // Multiple floor map views - test
/*        for (int i = 0; i < 2; i++) {
            // Floor map view
            LinearLayout.LayoutParams floorMapLayoutParams = new LinearLayout.LayoutParams(0, 0);
            floorMapLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            floorMapLayoutParams.weight = 1;
            mapLayout.addView(new FloorMapView(this), floorMapLayoutParams);

            // Separator (blank) view
            LinearLayout.LayoutParams separatorLayoutParams = new LinearLayout.LayoutParams(0, 0);
            separatorLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            separatorLayoutParams.height = 100;
            mapLayout.addView(new View(this), separatorLayoutParams);
        }*/
    }

}
