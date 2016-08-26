package com.example.cossettenavigation.beacons;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import java.util.ArrayList;

/**
 * A collection of data for tracking a beacon, to be stored and updated over time.
 */
public class BeaconTrackingData {

    private static final String TAG = "BeaconTrackingData";

    private com.example.cossettenavigation.map.Beacon beacon;

    // Oldest measurements first, newest measurements last
    private ArrayList<Double> accuracyMeasurements = new ArrayList<>();
    private ArrayList<Utils.Proximity> proximityMeasurements = new ArrayList<>();


    public BeaconTrackingData(com.example.cossettenavigation.map.Beacon beacon) {
        this.beacon = beacon;
    }


    @Override
    public String toString() {
        String string = "BeaconTrackingData { accuracyMeasurements = { ";
        for (Double accuracy : accuracyMeasurements) {
            string += accuracy + ", ";
        }
        string += "}, proximityMeasurements = { ";
        for (Utils.Proximity proximity : proximityMeasurements) {
            string += proximity + ", ";
        }
        string += "}, estimatedAccuracy = " + getEstimatedAccuracy() + " }";

        return string;
    }


    public com.example.cossettenavigation.map.Beacon getBeacon() {
        return beacon;
    }

    public void addMeasurements(Beacon beacon) {
/*        Log.i(TAG, String.format(
                "addMeasurements(): \"%s\", \"%s\"",
                this.beacon.getName(), this.beacon.getDescription()));*/

        if (accuracyMeasurements.size() >= 5) {
            accuracyMeasurements.remove(0);
        }
        if (proximityMeasurements.size() >= 5) {
            proximityMeasurements.remove(0);
        }

        accuracyMeasurements.add(Utils.computeAccuracy(beacon));
        proximityMeasurements.add(Utils.computeProximity(beacon));
    }

    // TODO - require minimum number of measurements?
    public double getEstimatedAccuracy() {
        double numerator = 0;
        double denominator = 0;

        double weight = 1;
        for (int i = 0; i < accuracyMeasurements.size(); i++) {
            numerator += (accuracyMeasurements.get(i) * weight);
            denominator += weight;

            weight *= 1.5;
        }

        if (denominator == 0) {
            return Double.POSITIVE_INFINITY;
        } else {
            return numerator / denominator;
        }
    }

}
