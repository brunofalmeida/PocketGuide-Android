package com.example.cossettenavigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.estimote.sdk.SystemRequirementsChecker;

/**
 * A full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";

    private static int PERMISSION_REQUEST_CODE_CAMERA = 1;

    private boolean mVisible;
    private boolean cVisible;
    private boolean cGranted;
    private FrameLayout m_camera_view = null;
    private CameraView mCameraView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.v(TAG, "onCreate()");

        super.onCreate(savedInstanceState);

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
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CODE_CAMERA);
        } else {
            cameraPermissionGranted();
        }

        ImageView currentDirection = new ImageView(this);

        currentDirection.setImageResource(R.drawable.uparrow);

        int width=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,155, getResources().getDisplayMetrics());
        int height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,155, getResources().getDisplayMetrics());

        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(width,height);
        params.gravity=Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;

        currentDirection.setLayoutParams(params);

        m_camera_view.setForegroundGravity(Gravity.CENTER);

        m_camera_view.addView(currentDirection);
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
        CoordinatorLayout rootView=(CoordinatorLayout) findViewById(R.id.coordinatorLayout);

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
        CoordinatorLayout rootView=(CoordinatorLayout) findViewById(R.id.coordinatorLayout);

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
