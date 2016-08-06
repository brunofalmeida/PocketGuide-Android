package com.example.cossettenavigation.map;

import java.util.UUID;

/**
 * A beacon on the map, defined with a name, grid position, and identifiers.
 */
public abstract class Beacon {

    protected String name;

    protected Point position;

    protected UUID uuid;
    protected int major;
    protected int minor;




    /**
     * Standard constructor.
     */
    public Beacon(String name, Point position, UUID uuid, int major, int minor) {
        this.name = name;
        this.position = position;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    /**
     * Constructor using an absolute position.
     */
    public Beacon(String name,
                  double xPosition,
                  double yPosition,
                  String uuid,
                  int major,
                  int minor) {

        this(name, new Point(xPosition, yPosition), UUID.fromString(uuid), major, minor);
    }

    /**
     * Constructor using a position relative to another beacon.
     */
    public Beacon(String name,
                  Beacon referenceBeacon,
                  double xPositionOffset,
                  double yPositionOffset,
                  String uuid,
                  int major,
                  int minor) {

        this(
                name,
                referenceBeacon.getXPosition() + xPositionOffset,
                referenceBeacon.getYPosition() + yPositionOffset,
                uuid,
                major,
                minor);
    }


    @Override
    public String toString() {
        return String.format(
                "%s { name = %s, position = %s, uuid = %s, major = %d, minor = %d }",
                getClass().getSimpleName(), name, position, uuid, major, minor);
    }


    public String getName() {
        return name;
    }

    public Point getPosition() {
        return position;
    }

    public double getXPosition() {
        return getPosition().x;
    }

    public double getYPosition() {
        return getPosition().y;
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

}
