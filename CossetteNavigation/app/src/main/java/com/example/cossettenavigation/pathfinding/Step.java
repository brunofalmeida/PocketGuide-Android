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
    private Double travelAngle;
    private double turnAngle;




    public Step(Beacon startBeacon, Beacon endBeacon, Zone zone, Double travelAngle, double turnAngle) {
        this.startBeacon = startBeacon;
        this.endBeacon = endBeacon;
        this.zone = zone;
        this.travelTime = Map.estimateTravelTime(startBeacon, endBeacon, zone);
        this.travelAngle = travelAngle;
        this.turnAngle = turnAngle;
    }


    @Override
    public String toString() {
        return String.format(
                "%s { startBeacon = \"%s\", endBeacon = \"%s\", zone = \"%s\", travelTime = %.1f, travelAngle = %s, turnAngle = %.0f",
                getClass().getSimpleName(),
                startBeacon.getName(),
                endBeacon.getName(),
                zone.getName(),
                travelTime,
                (travelAngle != null) ? String.format("%.0f", travelAngle) : "null",
                turnAngle);
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

    public Double getTravelAngle() {
        return travelAngle;
    }

    public double getTurnAngle() {
        return turnAngle;
    }

}
