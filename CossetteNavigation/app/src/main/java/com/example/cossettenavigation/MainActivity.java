package com.example.cossettenavigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.estimote.sdk.SystemRequirementsChecker;
import com.example.cossettenavigation.beacons.ApplicationBeaconManager;
import com.example.cossettenavigation.beacons.BeaconTrackingData;
import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.pathfinding.Path;
import com.example.cossettenavigation.pathfinding.Step;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

;

/*
    TODO - navigation mode - check time to help determine when to switch steps
    TODO - show multiple steps at a time - arrows+text for show current/next direction

    TODO - start/stop ranging beacons only after monitoring them (enter/exit region handlers)
    TODO - cleanup ranging intervals/beacon tracking data measurement storage (remove constants)
    TODO - shorten background monitoring interval (5/10s?)

    TODO - fix camera stretch

    TODO - add notifications when within range of beacons of a specific zone - tap to enter navigation?
    TODO - change enable/disable camera icon when toggled
    TODO - add enable/disable audio button

    TODO - modify beacons, floors, and zones to have an identifier (for code) and a description (for users)

    TODO - check that pathfinding only uses 1 step for an elevator/stairs over multiple floors - merge consecutive steps in the same zone?
    TODO - account for not starting at a beacon? (go to nearest exit, close/far end of hallway, etc.) - can skip and assume starting beacon
*/

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";

    private static int PERMISSION_REQUEST_CODE_CAMERA = 1;

    public static final String INTENT_KEY_PATH = "path";

    private static double BEACON_RANGE_FOR_SWITCHING_STEPS = 2;

    private boolean mVisible; //UI elements (status bar, toolbar, bottom bar visible)
    private boolean cVisible; //camera visible
    private boolean cGranted; //camera permission granted

    private FrameLayout m_camera_view = null;
    private CameraView mCameraView = null;
    private LinearLayout bottomBar;

    //UI elements (all for navigation mode, some for discovery mode)
    private RelativeLayout toggleArrows;
    private ImageView toggleUp;
    private ImageView toggleDown;
    private ImageView direction;
    private TextView instruction;
    private TextView description;
    private TextView time;

    private FloatingActionButton FAB;

    private ApplicationBeaconManager beaconManager;

    private Timer discoveryModeTimer = new Timer();

    private Path path = null;
    private int stepIndex = -1;
    private Timer navigationTimer = new Timer();
    private Timer navigationStepUpdateTimer = new Timer();

    private TextToSpeech textToSpeech = null;
    private boolean isTextToSpeechAvailable = false;
    /**
     * True to enable, false to disable
     */
    private final boolean isTextToSpeechEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");

        super.onCreate(savedInstanceState);

        // Initialize text to speech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    isTextToSpeechAvailable = true;
                    textToSpeech.setLanguage(Locale.CANADA);

                    Log.i(TAG, "textToSpeech: init success");

                    //speakText("Hello World");
                }

                else {
                    isTextToSpeechAvailable = false;

                    Log.i(TAG, "textToSpeech: init error");
                }
            }
        });

        // Make the volume buttons control the text to speech volume (music stream)
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m_camera_view = (FrameLayout) findViewById(R.id.camera_view);

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

        bottomBar=(LinearLayout) findViewById(R.id.bottomBar);

        direction=(ImageView) findViewById(R.id.arrow);
        direction.bringToFront();
        toggleArrows=(RelativeLayout) findViewById(R.id.toggleArrows);
        instruction=(TextView) findViewById(R.id.instruction);
        time=(TextView) findViewById(R.id.time);
        description=(TextView) findViewById(R.id.description);

        //get FAB
        FAB=(FloatingActionButton) findViewById(R.id.FAB);

        beaconManager = (ApplicationBeaconManager) getApplication();

        final TextView debugView = (TextView) findViewById(R.id.debug_view);

        // Periodic general UI update
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Show debug info
                        debugView.setText(beaconManager.getTrackedBeaconsDescription());
                    }
                });
            }
        }, 1, 100);


        // Check intent for a Path object - discovery or navigation mode
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = (Path) extras.getSerializable(INTENT_KEY_PATH);

            if (path != null) {
                enterNavigationMode();
            } else {
                enterDiscoveryMode();
            }
        }
        else {
            enterDiscoveryMode();
        }
    }




    /* Discovery */

    private void enterDiscoveryMode() {
        Log.i(TAG, "enterDiscoveryMode()");

        exitNavigationMode();

        FAB.setImageResource(R.drawable.search);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        // Discovery UI updating
        discoveryModeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*String nearbyZones = "";

                        Floor floor = beaconManager.getEstimatedFloor();
                        if (floor != null) {
                            nearbyZones += floor.getName() + " - ";
                        }

                        nearbyZones += "Nearby:";

                        for (Zone zone : beaconManager.getNearbyZones()) {
                            nearbyZones += "\n" + zone.getName();
                        }*/
                        Beacon nearestBeacon=beaconManager.getNearestBeacon();

                        if (nearestBeacon!=null) {
                            instruction.setText("You are on "+nearestBeacon.getDescription());
                            description.setText(nearestBeacon.getFloor().getName());
                        } else {
                            instruction.setText("Unknown Location");
                            description.setText("No Beacons Found");
                        }
                    }
                });
            }
        }, 1, 100);
    }

    private void exitDiscoveryMode() {
        Log.i(TAG, "exitDiscoveryMode()");

        resetDiscoveryModeTimer();
    }




    /* Navigation */

    private void enterNavigationMode() {
        Log.i(TAG, "enterNavigationMode(): " + path.toString());

        exitDiscoveryMode();

        verifyPath();

        FAB.setImageResource(R.drawable.close);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterDiscoveryMode();
            }
        });

        bottomBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                /*//bottomBar.setBackgroundColor(android.R.color.holo_red_light);
                LinearLayout helpResize=(LinearLayout) findViewById(R.id.helpResize);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) helpResize.getLayoutParams();
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 156, getResources().getDisplayMetrics());;
                bottomBar.setLayoutParams(params);*/
                return true;
            }
        });

        toggleArrows.setVisibility(View.VISIBLE);
        direction.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);

        toggleUp=(ImageView) findViewById(R.id.toggleUp);
        toggleDown=(ImageView) findViewById(R.id.toggleDown);

        toggleUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "toggleUp.setOnClickListener()");
                if (stepIndex>0) {
                    stepIndex--;
                    startStep();
                }
            }
        });

        toggleDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "toggleDown.setOnClickListener()");
                if (stepIndex+1<path.getSteps().size()){
                    stepIndex++;
                    startStep();
                }
            }
        });

        //limit on number of characters
/*        instruction.setText("Walk 4 m ahead");
        time.setText("20 min");
        description.setText("Top of North Stairwell");
        nextStep.setText("Walk down staircase");*/


        if (path.getSteps().size() > 0) {
            startNavigation();
        } else {
            stopNavigation();

            String text = "You are already there!";
            instruction.setText(text);
            speakText(text);
        }
    }

    private void exitNavigationMode() {
        Log.i(TAG, "exitNavigationMode()");
        stopNavigation();
    }

    private void startNavigation() {
        verifyPath();

        stepIndex = -1;

        // Navigation UI update
        navigationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
/*                        String status = "";

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
                });
            }
        }, 1, 100);

        // Navigation location tracking
        navigationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.v(TAG, "startNavigation() timer");

                // If there are steps left
                if (stepIndex < path.getSteps().size()) {

                    // Check if this is the last step
                    boolean isLastStep;
                    if (stepIndex + 1 < path.getSteps().size()) {
                        isLastStep = false;
                    } else {
                        isLastStep = true;
                    }

                    // Get the next beacon to be in range of
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
                            startStep();
                        }
                    }
                }

                // No steps left
                else {
                    finishNavigation();
                    stopNavigation();
                }
            }
        }, 1, 100);
    }

    private void finishNavigation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "You have arrived!";
                instruction.setText(text);
                speakText(text);
            }
        });
    }

    private void stopNavigation() {
        Log.v(TAG, "stopNavigation()");

        path = null;
        stepIndex = -1;
        resetNavigationTimer();
        resetNavigationStepUpdateTimer();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                direction.setVisibility(View.GONE);
                toggleArrows.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
            }
        });
    }

    private void startStep() {
        Log.v(TAG, "startStep()");

        verifyPath();

        final Step step = path.getSteps().get(stepIndex);
        resetNavigationStepUpdateTimer();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                direction.setVisibility(View.VISIBLE);

                // Show turn
                direction.setRotation((float) step.getTurnAngle());
                String text;
                if (stepIndex == 0) {
                    text = "Start at " + step.getStartBeacon().getDescription();
                } else {
                    text = step.getTurnDescription();
                }
                instruction.setText(text);
                speakText(text);

                // In 5 seconds, show travel
                navigationStepUpdateTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v(TAG, "startStep(): travel timer");
                                direction.setRotation(0);
                                instruction.setText(step.getTravelDescription());
                                description.setText(step.getDestinationDescription());
                                //time.setText(String.valueOf(path.getTimeRemaining(stepIndex))+"min");
                                speakText(step.getTravelDescription());
                            }
                        });
                    }
                }, 5000);
            }
        });
    }

    private boolean shouldSwitchSteps(Beacon beacon) {
        BeaconTrackingData beaconTrackingData = beaconManager.getBeaconTrackingData(beacon);
        if (beaconTrackingData != null) {
            if (beaconTrackingData.getEstimatedAccuracy() <= BEACON_RANGE_FOR_SWITCHING_STEPS) {
                return true;
            }
        }

        return false;
    }




    private boolean verifyPath() {
        if (path != null) {
            return true;
        } else {
            Log.e(TAG, "path = null");
            return false;
        }
    }

    private void resetDiscoveryModeTimer() {
        discoveryModeTimer.cancel();
        discoveryModeTimer.purge();
        discoveryModeTimer = new Timer();
    }

    private void resetNavigationTimer() {
        navigationTimer.cancel();
        navigationTimer.purge();
        navigationTimer = new Timer();
    }

    private void resetNavigationStepUpdateTimer() {
        navigationStepUpdateTimer.cancel();
        navigationStepUpdateTimer.purge();
        navigationStepUpdateTimer = new Timer();
    }

    private void speakText(String text) {
        if (isTextToSpeechAvailable && isTextToSpeechEnabled && Build.VERSION.SDK_INT >= 21) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "");
        }
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
        direction.bringToFront();
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
}
