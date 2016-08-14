package com.example.cossettenavigation.map;

/**
 * A beacon placed in a supporting location and used to improve location estimates.
 * @see Map
 */
public class SupportBeacon extends Beacon {

    /**
     * A reference to the zone this beacon is in. (A SupportBeacon can only be part of one zone.)
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
                "%s { name = \"%s\", position = %s, uuid = %s, major = %d, minor = %d, zone = \"%s\" }",
                getClass().getSimpleName(), name, position, uuid, major, minor, zone.getName());
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

}
