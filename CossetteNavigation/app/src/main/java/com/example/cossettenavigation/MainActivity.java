package com.example.cossettenavigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.estimote.sdk.SystemRequirementsChecker;
import com.example.cossettenavigation.beacons.ApplicationBeaconManager;
import com.example.cossettenavigation.beacons.BeaconTrackingData;
import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.map.Floor;
import com.example.cossettenavigation.map.Point2D;
import com.example.cossettenavigation.map.Zone;
import com.example.cossettenavigation.pathfinding.Path;
import com.example.cossettenavigation.pathfinding.Step;

import java.util.Timer;
import java.util.TimerTask;

/*
    TODO - navigation mode - check distance/time to help determine when to switch steps
    TODO - show multiple steps at a time - arrows+text for show current/next direction
    TODO - check that pathfinding only uses 1 step for an elevator/stairs over multiple floors - merge consecutive steps in the same zone?

    TODO - fix camera stretch

    TODO - add notifications when within range of beacons of a specific zone - tap to enter navigation?
    TODO - change enable/disable camera icon when toggled
    TODO - add voice directions
    TODO - add voice dictation to search
    TODO - add enable/disable audio button

    TODO - modify beacons, floors, and zones to have an identifier (for code) and a description (for users)
    TODO - add distance/time units in logs
    TODO - convert all logs from verbose to info

    TODO - account for not starting at a beacon? (go to nearest exit, close/far end of hallway, etc.) - can skip and assume starting beacon
*/

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";

    private static int PERMISSION_REQUEST_CODE_CAMERA = 1;

    public static final String INTENT_KEY_PATH = "path";

    private static double BEACON_RANGE_FOR_SWITCHING_STEPS = 2;

    private boolean mVisible;
    private boolean cVisible;
    private boolean cGranted;
    private FrameLayout m_camera_view = null;
    private LinearLayout bottomBar;
    private CameraView mCameraView = null;

    private ImageView direction;
    private TextView instruction;
    private ListView nextStep;

    private ApplicationBeaconManager beaconManager;

    private Path path = null;
    private int stepIndex = -1;
    private Timer navigationUpdateTimer = new Timer();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m_camera_view = (FrameLayout) findViewById(R.id.camera_view);
        bottomBar=(LinearLayout) findViewById(R.id.bottomBar);

        mVisible = true;

        // Set up the user interaction to manually show or hide the UI.
        m_camera_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        // Check for camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Request the write permission on Android 6.0+
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.CAMERA },
                    PERMISSION_REQUEST_CODE_CAMERA);
        } else {
            cameraPermissionGranted();
        }

        //set up arrow and direction description

        direction = new ImageView(this);
        direction.setImageResource(R.drawable.arrow);

        instruction=new TextView(this);
        instruction.setTextColor(getResources().getColor(android.R.color.white));
        instruction.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);

        nextStep=(ListView) findViewById(R.id.nextStep);

        //WIDTH AND HEIGHT SHOULD MATCH THOSE IN ARROW.XML VECTOR FILE, OTHERWISE DRAWABLE WILL BE PIXELATED
        int arrowWidth=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,155, getResources().getDisplayMetrics());
        int arrowHeight=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,155, getResources().getDisplayMetrics());

        FrameLayout.LayoutParams arrowParams=new FrameLayout.LayoutParams(arrowWidth,arrowHeight);
        FrameLayout.LayoutParams instructionParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        arrowParams.gravity=Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        instructionParams.gravity=Gravity.BOTTOM;

        //instruction.setText("a a a a a a a a a a a");
        instructionParams.setMargins(32, 0, 200, 32);

        direction.setLayoutParams(arrowParams);
        instruction.setLayoutParams(instructionParams);

        m_camera_view.addView(direction);
        bottomBar.addView(instruction);

        beaconManager = (ApplicationBeaconManager) getApplication();

        final TextView debugView = (TextView) findViewById(R.id.debug_view);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Show debug info
                        debugView.setText(beaconManager.getTrackedBeaconsDescription());

                        // Navigation mode - Show floor and destination
                        if (path != null) {
/*                            String status = "";

                            Floor floor = beaconManager.getEstimatedFloor();
                            if (floor != null) {
                                status += "On " + floor.getName() + "\n";
                            }

                            if (path.getDestination() != null) {
                                status += "Going to " + path.getDestination().getName();
                            }

                            if (status.endsWith("\n")) {
                                status = status.substring(0, status.length() - 1);
                            }

                            instruction.setText(status);*/
                        }

                        // Discovery mode - Show floor and nearby destinations
                        else {
                            String nearbyZones = "";

                            Floor floor = beaconManager.getEstimatedFloor();
                            if (floor != null) {
                                nearbyZones += floor.getName() + " - ";
                            }

                            nearbyZones += "Nearby:";

                            for (Zone zone : beaconManager.getNearbyZones()) {
                                nearbyZones += "\n" + zone.getName();
                            }
                            instruction.setText(nearbyZones);
                        }
                    }
                });
            }
        }, 1, 100);


        //direction.setVisibility(View.INVISIBLE);

        // Set up navigation if a Path is provided
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = (Path) extras.getSerializable(INTENT_KEY_PATH);

            if (path != null) {
                Log.i(TAG, path.toString());

                if (path.getSteps().size() > 0) {
                    startNavigation();
                } else {
                    stopNavigation();
                    instruction.setText("You are already there!");
                }
            }
        }
    }




    /* Navigation */

    private void startNavigation() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.v(TAG, "startNavigation() timer");

                // Steps are left
                if (stepIndex < path.getSteps().size()) {
                    boolean isLastStep;
                    if (stepIndex + 1 < path.getSteps().size()) {
                        isLastStep = false;
                    } else {
                        isLastStep = true;
                    }

                    Beacon nextBeacon;
                    if (!isLastStep) {
                        nextBeacon = path.getSteps().get(stepIndex + 1).getStartBeacon();
                    } else {
                        nextBeacon = path.getSteps().get(stepIndex).getEndBeacon();
                    }

                    // If the next beacon is in range
                    if (shouldSwitchSteps(nextBeacon) || stepIndex == -1) {
                        stepIndex++;

                        if (!isLastStep) {
                            startStep(path.getSteps().get(stepIndex));
                        }
                    }
                }

                // No steps left
                else {
                    stopNavigation();
                    cancel();
                }
            }
        }, 1, 100);
    }

    private void startStep(final Step step) {
        Log.v(TAG, "startStep()");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                direction.setVisibility(View.VISIBLE);

                // Show turn
                direction.setRotation((float) step.getTurnAngle());
                if (stepIndex == 0) {
                    instruction.setText("Start at " + step.getStartBeacon().getName());
                } else {
                    instruction.setText(step.getTurnDescription());
                }

                // In 5 seconds, show travel
                resetNavigationUpdateTimer();
                navigationUpdateTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v(TAG, "startStep(): travel timer");
                                direction.setRotation(0);
                                instruction.setText(step.getTravelDescription());
                            }
                        });
                    }
                }, 5000);
            }
        });
    }

    private void stopNavigation() {
        Log.v(TAG, "stopNavigation()");

        path = null;
        stepIndex = -1;
        resetNavigationUpdateTimer();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                direction.setVisibility(View.INVISIBLE);
                instruction.setText("You have arrived!");
            }
        });
    }

    private void resetNavigationUpdateTimer() {
        navigationUpdateTimer.cancel();
        navigationUpdateTimer.purge();
        navigationUpdateTimer = new Timer();
    }

    private boolean shouldSwitchSteps(Beacon beacon) {
        Point2D location = beaconManager.getEstimatedLocation();
        BeaconTrackingData beaconTrackingData = beaconManager.getBeaconTrackingData(beacon);

/*        if (location != null) {
            if (Map.distanceBetweenPoints(
                    new Point3D(location.x, location.y, 0),
                    new Point3D(beacon.getXPosition(), beacon.getYPosition(), 0))
                        <= BEACON_RANGE_FOR_SWITCHING_STEPS) {
                return true;
            }
        }*/

        if (beaconTrackingData != null) {
            if (beaconTrackingData.getEstimatedAccuracy() <= BEACON_RANGE_FOR_SWITCHING_STEPS) {
                return true;
            }
        }

        return false;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.v(TAG, "In onRequestPermissionsResult()");

        if (requestCode == PERMISSION_REQUEST_CODE_CAMERA) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                cameraPermissionGranted();
            } else {
                cGranted=false;
            }
        }
    }

    private void cameraPermissionGranted() {
        Log.v(TAG, "Camera permission granted");
        cGranted=true;
        cVisible=true;
        mCameraView = new CameraView(this); // create a SurfaceView to show camera data
        FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
        camera_view.addView(mCameraView);   // add the SurfaceView to the layout
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_debug:
                Intent intent = new Intent(this, DebugActivity.class);
                startActivity(intent);
                return true;
            case R.id.camera_button:
                cameraOnOff();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        if (mCameraView != null) {
            mCameraView.activityOnResume();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void cameraOnOff() {

        if (cGranted) {
            if (cVisible) hideCamera();
            else showCamera();
        }
    }
    
    private void hideCamera(){
        mCameraView.setVisibility(View.INVISIBLE);
        cVisible=false;
    }

    private void showCamera(){
        mCameraView=new CameraView(this);
        m_camera_view.addView(mCameraView);
        cVisible=true;
    }

    private void toggle() {
        /*if (mVisible) hide();
        else show();*/
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        View decorView = getWindow().getDecorView();
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.coordinatorLayout);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        //rootView.setFitsSystemWindows(false);
        //decorView.setSystemUiVisibility(uiOptions);

        m_camera_view.setSystemUiVisibility(/*View.SYSTEM_UI_FLAG_LOW_PROFILE*/
                View.SYSTEM_UI_FLAG_FULLSCREEN
                /*| View.SYSTEM_UI_FLAG_LAYOUT_STABLE*/
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                /*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/
                /*| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION*/);

        mVisible=false;
    }

    @SuppressLint("InlinedApi")
    private void show() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        View decorView = getWindow().getDecorView();
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.coordinatorLayout);

        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //rootView.setFitsSystemWindows(true);
        //decorView.setSystemUiVisibility(0);

        //m_camera_view.setSystemUiVisibility(0);

        mVisible=true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.v(TAG, "onConfigurationChanged()");
        super.onConfigurationChanged(newConfig);
        mCameraView.activityOnConfigurationChanged();
        }

    public void onSearchFABClick(View view) {
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
    }

}
