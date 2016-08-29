package com.example.cossettenavigation.pathfinding;

import com.example.cossettenavigation.map.Beacon;

import java.io.Serializable;

public class NavigationStep implements Serializable {

    private String descriptionOne;
    private String descriptionTwo;
    private double arrowAngle;

    private Beacon endBeacon;
    private double minimumTime;
    private double timeRemaining;



    public NavigationStep(String descriptionOne,
                          String descriptionTwo,
                          double arrowAngle,
                          Beacon endBeacon,
                          double minimumTime) {

        this.descriptionOne = descriptionOne;
        this.descriptionTwo = descriptionTwo;
        this.arrowAngle = arrowAngle;
        this.endBeacon = endBeacon;
        this.minimumTime = minimumTime;
        this.timeRemaining = 0;
    }

    @Override
    public String toString() {
        return String.format(
                "%s { descriptionOne = %s, descriptionTwo = %s, arrowAngle = %.0f deg, endBeacon = \"%s\", minimumTime = %.1f s }",
                getClass().getSimpleName(),
                descriptionOne,
                descriptionTwo,
                arrowAngle,
                (endBeacon != null)? endBeacon.getName() : null,
                minimumTime);
    }

    public String getDescriptionOne() {
        return descriptionOne;
    }

    public String getDescriptionTwo() {
        return descriptionTwo;
    }

    public double getArrowAngle() {
        return arrowAngle;
    }

    public Beacon getEndBeacon() {
        return endBeacon;
    }

    public double getMinimumTime() {
        return minimumTime;
    }

    public double getTimeRemaining() {
        return timeRemaining;
    }

    public void addTimeRemaining(double time) {
        this.timeRemaining += time;
    }

}
