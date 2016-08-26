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
    //private ArrayList<Integer> timeRemaining;
    private Zone destination = null;


    public Path(double travelTime, ArrayList<Step> steps) {
        this.travelTime = travelTime;
        this.steps = steps;
        /*timeRemaining=new ArrayList<>(steps.size());
        //construct prefix sum array
        timeRemaining.set(0,(int)steps.get(0).getTravelTime());
        for (int i=1;i<steps.size();i++){
            timeRemaining.set(i,timeRemaining.get(i-1)+(int) steps.get(i).getTravelTime());
        }*/
    }

    @Override
    public String toString() {
        return String.format(
                "%s { travelTime = %.1f s, steps = %s, destination = \"%s\" }",
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

    /*public int getTimeRemaining(int i){
        return timeRemaining.get(i);
    }*/

    public Zone getDestination() {
        return destination;
    }

    public void setDestination(Zone destination) {
        this.destination = destination;
    }

}
