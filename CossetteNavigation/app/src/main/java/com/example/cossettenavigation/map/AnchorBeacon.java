package com.example.cossettenavigation.map;

import java.util.ArrayList;

/**
 * A beacon placed in a key location and used to define zones.
 * @see Map
 */
public class AnchorBeacon extends Beacon implements Comparable {

    /**
     * References to zones this beacon is a part of.
     */
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
        String zonesString = "{ ";
        for (Zone zone : zones) {
            zonesString += String.format("\"%s\", ", zone.getName());
        }
        zonesString += "}";

        return String.format(
                "%s { name = \"%s\", position = %s, uuid = %s, major = %d, minor = %d, zones = %s }",
                getClass().getSimpleName(), name, position, uuid, major, minor, zonesString);
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public void addZone(Zone zone) {
        zones.add(zone);
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
