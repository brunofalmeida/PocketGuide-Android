package com.example.cossettenavigation;

import com.estimote.sdk.Region;
import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Beacon;
import com.example.cossettenavigation.map.Floor;
import com.example.cossettenavigation.map.SupportBeacon;
import com.example.cossettenavigation.map.Zone;

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

    public static String getAnchorBeaconNamesString(ArrayList<AnchorBeacon> anchorBeacons) {
        String string = "{ ";
        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            string += String.format("\"%s\", ", anchorBeacon.getName());
        }
        if (string.endsWith(", ")) {
            string = string.substring(0, string.length() - 2);
        }
        string += " }";
        return string;
    }

    public static String getSupportBeaconNamesString(ArrayList<SupportBeacon> supportBeacons) {
        String string = "{ ";
        for (SupportBeacon supportBeacon : supportBeacons) {
            string += String.format("\"%s\", ", supportBeacon.getName());
        }
        if (string.endsWith(", ")) {
            string = string.substring(0, string.length() - 2);
        }
        string += " }";
        return string;
    }

    public static String getZoneNamesString(ArrayList<Zone> zones) {
        String string = "{ ";
        for (Zone zone : zones) {
            string += String.format("\"%s\", ", zone.getName());
        }
        if (string.endsWith(", ")) {
            string = string.substring(0, string.length() - 2);
        }
        string += " }";
        return string;
    }

    public static String getFloorNamesString(ArrayList<Floor> floors) {
        String string = "{ ";
        for (Floor floor : floors) {
            string += String.format("\"%s\", ", floor.getName());
        }
        if (string.endsWith(", ")) {
            string = string.substring(0, string.length() - 2);
        }
        string += " }";
        return string;
    }

}