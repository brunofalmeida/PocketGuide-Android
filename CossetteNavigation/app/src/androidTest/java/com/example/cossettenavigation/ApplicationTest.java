package com.example.cossettenavigation;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Map;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private static final String TAG = "ApplicationTest";

    public ApplicationTest() {
        super(Application.class);
    }

    public void test_assertEquals() {
        assertEquals(1, 1);
    }

    public void test_estimateTravelAngle() {
        Map map = new Map();

        for (AnchorBeacon anchorBeacon1 : Map.anchorBeacons) {
            for (AnchorBeacon anchorBeacon2 : Map.anchorBeacons) {
                if (anchorBeacon1 != anchorBeacon2) {
                    Log.v(TAG,
                            anchorBeacon1 + "\n" + anchorBeacon2 + "\n" +
                            Map.estimateTravelAngle(anchorBeacon1, anchorBeacon2) + " degrees");
                }
            }

        }
    }

    public void test_getShortestPath() {
/*        for (AnchorBeacon anchorBeacon1 : Map.anchorBeacons) {
            for (AnchorBeacon anchorBeacon2 : Map.anchorBeacons) {
                if (anchorBeacon1 != anchorBeacon2) {
                    Log.v(TAG, Pathfinder.getShortestPath(anchorBeacon1, anchorBeacon2).toString());
                }
            }
        }*/
    }

}
