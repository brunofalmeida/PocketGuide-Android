package com.example.cossettenavigation.map;

import android.util.Log;

/**
 * A beacon placed in a supporting location and used to improve location estimates.
 * @see Map
 */
public class SupportBeacon extends Beacon {

    private static final String TAG = "SupportBeacon";

    /**
     * A SupportBeacon can only be part of one zone.
     */
    private Zone zone = null;




    /**
     * Constructor using an absolute position.
     */
    public SupportBeacon(String name,
                         double xPosition,
                         double yPosition,
                         String uuid,
                         int major,
                         int minor) {

        super(name, xPosition, yPosition, uuid, major, minor);
    }

    /**
     * Constructor using a position relative to another beacon.
     */
    public SupportBeacon(String name,
                         Beacon referenceBeacon,
                         double xPositionOffset,
                         double yPositionOffset,
                         String uuid,
                         int major,
                         int minor) {

        super(name, referenceBeacon, xPositionOffset, yPositionOffset, uuid, major, minor);
    }

    @Override
    public String toString() {
        return String.format(
                "%s { name = \"%s\", position = %s, uuid = %s, major = %d, minor = %d, floor = \"%s\", zone = \"%s\" }",
                getClass().getSimpleName(), name, position, uuid, major, minor, floor.getName(),
                zone.getName());
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        if (this.zone != null) {
            Log.e(TAG, "Overriding zone for support beacon: " + this);
        }
        this.zone = zone;
    }

}
