package com.example.cossettenavigation.pathfinding;

import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.map.Map;
import com.example.cossettenavigation.map.Zone;

/**
 *
 */
public class Step {

    private Beacon startBeacon;
    private Beacon endBeacon;

    private Zone zone;
    private double travelTime;

    // In degrees
    private double absoluteAngle;
    private double relativeAngle;




    public Step(Beacon startBeacon, Beacon endBeacon, Zone zone, double absoluteAngle, double relativeAngle) {
        this.startBeacon = startBeacon;
        this.endBeacon = endBeacon;
        this.zone = zone;
        this.travelTime = Map.estimateTravelTime(startBeacon, endBeacon, zone);
        this.absoluteAngle = absoluteAngle;
        this.relativeAngle = relativeAngle;
    }


    @Override
    public String toString() {
        return String.format(
                "%s { startBeacon = \"%s\", endBeacon = \"%s\", zone = \"%s\", travelTime = %.1f, absoluteAngle = %.0f, relativeAngle = %.0f",
                getClass().getSimpleName(),
                startBeacon.getName(),
                endBeacon.getName(),
                zone.getName(),
                travelTime,
                absoluteAngle,
                relativeAngle);
    }


    public Beacon getStartBeacon() {
        return startBeacon;
    }

    public Beacon getEndBeacon() {
        return endBeacon;
    }

    public Zone getZone() {
        return zone;
    }

    public double getTravelTime() {
        return travelTime;
    }

    public double getAbsoluteAngle() {
        return absoluteAngle;
    }

    public double getRelativeAngle() {
        return relativeAngle;
    }

}
