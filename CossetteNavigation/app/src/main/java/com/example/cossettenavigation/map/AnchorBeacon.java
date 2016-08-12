package com.example.cossettenavigation.map;

import com.example.cossettenavigation.Utilities;

import java.util.ArrayList;

/**
 * A beacon placed in a key location and used to define zones.
 * @see Map
 */
public class AnchorBeacon extends Beacon implements Comparable {

    private ArrayList<Zone> zones = new ArrayList<>();




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

    @Override
    public String toString() {
        return String.format(
                "%s { name = \"%s\", position = %s, uuid = %s, major = %d, minor = %d, floor = \"%s\", zones = %s }",
                getClass().getSimpleName(), name, position, uuid, major, minor, floor.getName(),
                Utilities.getZoneNamesString(zones));
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public void addZone(Zone zone) {
        if (!this.zones.contains(zone)) {
            this.zones.add(zone);
        }
    }


    @Override
    public int compareTo(Object another) {
        if (another instanceof AnchorBeacon) {
            AnchorBeacon anotherAnchorBeacon = (AnchorBeacon) another;
            return this.name.compareTo(anotherAnchorBeacon.name);
        }
        else {
            return 0;
        }
    }

}
