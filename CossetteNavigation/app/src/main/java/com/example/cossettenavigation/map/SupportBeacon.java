package com.example.cossettenavigation.map;

import java.lang.ref.WeakReference;

/**
 * Created by Bruno on 2016-07-22.
 */
public class SupportBeacon extends Beacon {

    private WeakReference<Zone> zone = null;


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

    public void setZone(Zone zone) {
        this.zone = new WeakReference<Zone>(zone);
    }

    @Override
    public String toString() {
        return String.format(
                "%s { name = %s, position = %s, uuid = %s, major = %d, minor = %d, zone = %s }",
                getClass().getSimpleName(), name, position, uuid, major, minor, zone);
    }

}
