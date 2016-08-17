package com.example.cossettenavigation.pathfinding;

import java.util.ArrayList;

/**
 *
 */
public class Path {

    private ArrayList<Step> steps;
    private double travelTime;


    public Path(ArrayList<Step> steps, double travelTime) {
        this.steps = steps;
        this.travelTime = travelTime;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public double getTravelTime() {
        return travelTime;
    }

}
