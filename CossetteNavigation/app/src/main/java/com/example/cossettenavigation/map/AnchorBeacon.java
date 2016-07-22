package com.example.cossettenavigation.map;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Bruno on 2016-07-22.
 */
public class AnchorBeacon extends Beacon {

    private ArrayList<WeakReference<Zone>> zones = new ArrayList<>();


    /**
     * Constructor using an absolute position.
     */
    public AnchorBeacon(String name,
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
    public AnchorBeacon(String name,
                        Beacon referenceBeacon,
                        double xPositionOffset,
                        double yPositionOffset,
                        String uuid,
                        int major,
                        int minor) {

        super(name, referenceBeacon, xPositionOffset, yPositionOffset, uuid, major, minor);
    }

    public void addZone(Zone zone) {
        zones.add(new WeakReference<Zone>(zone));
    }

}
