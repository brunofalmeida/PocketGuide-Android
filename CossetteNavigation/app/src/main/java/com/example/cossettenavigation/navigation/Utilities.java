package com.example.cossettenavigation.navigation;

import com.estimote.sdk.Region;
import com.example.cossettenavigation.map.Beacon;

import java.util.ArrayList;

/**
 * Helper methods for comparison, conversion, etc.
 */
public class Utilities {

    /**
     * Checks if a beacon manager Region and a map Beacon represent the same beacon.
     * @return true if the UUID, major, and minor are equal, or false otherwise
     */
    public static boolean areEqual(Region region, Beacon beacon) {
        if (region.getProximityUUID() == beacon.getUUID() &&
                region.getMajor() == beacon.getMajor() &&
                region.getMinor() == beacon.getMinor()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * <p>
     * ArrayList to Array conversion.
     * </p>
     *
     * <p>
     * ArrayList &lt;double[]&gt; -> double[][]
     * </p>
     */
    public static double[][] getDoubleDoubleArray(ArrayList<double[]> arrayList) {
        double[][] array = new double[arrayList.size()][];

        for (int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }

        return array;
    }

    /**
     * <p>
     * ArrayList to Array conversion.
     * </p>
     *
     * <p>
     * ArrayList &lt;Double&gt; -> double[]
     * </p>
     */
    public static double[] getDoubleArray(ArrayList<Double> arrayList) {
        double[] array = new double[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }

        return array;
    }

}
