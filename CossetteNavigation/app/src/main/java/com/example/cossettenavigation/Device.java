package com.example.cossettenavigation;

import android.os.Build;

/**
 * from https://github.com/Shaked/androidportraitcamera
 */
public class Device {

    private static int orientation;

    public static void load() {
        //check if emulator is running
        if (Build.BRAND.toLowerCase().contains("generic")) {
            Device.orientation = 0;
        } else {
            Device.orientation = 90;
        }
    }

    public static int getOrientation() {
        return Device.orientation;
    }
}
