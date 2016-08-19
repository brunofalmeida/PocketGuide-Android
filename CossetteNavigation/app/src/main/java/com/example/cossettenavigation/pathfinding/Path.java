package com.example.cossettenavigation.pathfinding;

import com.example.cossettenavigation.Utilities;

import java.util.ArrayList;

/**
 *
 */
public class Path {

    private double travelTime;
    private ArrayList<Step> steps;


    public Path(double travelTime, ArrayList<Step> steps) {
        this.travelTime = travelTime;
        this.steps = steps;
    }

    @Override
    public String toString() {
        return String.format(
                "%s { travelTime = %.1f, steps = %s }",
                getClass().getSimpleName(), travelTime, Utilities.getStepsString(steps));
    }

    public double getTravelTime() {
        return travelTime;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

}
