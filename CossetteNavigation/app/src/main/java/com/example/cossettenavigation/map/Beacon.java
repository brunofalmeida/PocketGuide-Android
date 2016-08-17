package com.example.cossettenavigation.map;

import java.util.UUID;

/**
 * A beacon on the map, defined with a name, grid position, and identifiers.
 */
public abstract class Beacon {

    private static final String TAG = "Beacon";

    protected String name;

    protected Floor floor;
    protected Point position;

    protected UUID uuid;
    protected int major;
    protected int minor;




    /**
     * Standard constructor.
     */
    private Beacon(String name, Floor floor, Point position, UUID uuid, int major, int minor) {
        this.name = name;
        this.floor = floor;
        this.position = position;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    /**
     * Constructor using an absolute position.
     */
    public Beacon(String name,
                  Floor floor,
                  double xPosition,
                  double yPosition,
                  String uuid,
                  int major,
                  int minor) {

        this(name, floor, new Point(xPosition, yPosition), UUID.fromString(uuid), major, minor);
    }

    /**
     * Constructor using a position relative to another beacon.
     */
    public Beacon(String name,
                  Floor floor,
                  Beacon referenceBeacon,
                  double xPositionOffset,
                  double yPositionOffset,
                  String uuid,
                  int major,
                  int minor) {

        this(
                name,
                floor,
                referenceBeacon.getXPosition() + xPositionOffset,
                referenceBeacon.getYPosition() + yPositionOffset,
                uuid,
                major,
                minor);
    }


    @Override
    public String toString() {
        return String.format(
                "%s { name = \"%s\", floor = \"%s\", position = %s, uuid = %s, major = %d, minor = %d }",
                getClass().getSimpleName(), name, floor.getName(), position, uuid, major, minor);
    }

    public int compareTo(Object another) {
        if (another instanceof Beacon) {
            Beacon anotherBeacon = (Beacon) another;
            return this.name.compareTo(anotherBeacon.name);
        }
        else {
            return 0;
        }
    }


    public String getName() {
        return name;
    }

    public Floor getFloor() {
        return floor;
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
