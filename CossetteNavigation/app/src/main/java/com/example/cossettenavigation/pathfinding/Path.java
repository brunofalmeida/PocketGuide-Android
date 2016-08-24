package com.example.cossettenavigation.pathfinding;

import com.example.cossettenavigation.Utilities;
import com.example.cossettenavigation.map.Zone;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 */
public class Path implements Serializable {

    private double travelTime;
    private ArrayList<Step> steps;
    private Zone destination = null;


    public Path(double travelTime, ArrayList<Step> steps) {
        this.travelTime = travelTime;
        this.steps = steps;
    }

    @Override
    public String toString() {
        return String.format(
                "%s { travelTime = %.1f, steps = %s, destination = \"%s\" }",
                getClass().getSimpleName(),
                travelTime,
                Utilities.getStepsString(steps),
                (destination != null) ? destination.getName() : null);
    }

    public double getTravelTime() {
        return travelTime;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public Zone getDestination() {
        return destination;
    }

    public void setDestination(Zone destination) {
        this.destination = destination;
    }

}
