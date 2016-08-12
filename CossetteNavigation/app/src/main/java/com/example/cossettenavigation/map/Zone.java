package com.example.cossettenavigation.map;

import java.util.ArrayList;

/**
 * A key area within a floor or building.
 * @see Map
 */
public class Zone {

    private String name;

    /**
     * Anchor beacons that define this zone.
     */
    private ArrayList<AnchorBeacon> anchorBeacons = new ArrayList<>();

    /**
     * Support beacons that are part of this zone.
     */
    private ArrayList<SupportBeacon> supportBeacons = new ArrayList<>();




    public Zone(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String anchorBeaconsString = "{ ";
        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            anchorBeaconsString += String.format("\"%s\", ", anchorBeacon.getName());
        }
        anchorBeaconsString += "}";

        String supportBeaconsString = "{ ";
        for (SupportBeacon supportBeacon : supportBeacons) {
            supportBeaconsString += String.format("\"%s\", ", supportBeacon.getName());
        }
        supportBeaconsString += "}";

        return String.format(
                "%s { name = %s, anchorBeacons = %s, supportBeacons = %s }",
                getClass().getSimpleName(), name, anchorBeaconsString, supportBeaconsString);
    }

    public String getName() {
        return name;
    }

    public ArrayList<AnchorBeacon> getAnchorBeacons() {
        return anchorBeacons;
    }

    public ArrayList<SupportBeacon> getSupportBeacons() {
        return supportBeacons;
    }


    /**
     * Also updates the anchor beacon to refer to this zone.
     */
    public void addAnchorBeacon(AnchorBeacon anchorBeacon) {
        anchorBeacons.add(anchorBeacon);
        anchorBeacon.addZone(this);
    }

    /**
     * Also updates the anchor beacons to refer to this zone.
     */
    public void addAnchorBeacons(AnchorBeacon... anchorBeacons) {
        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            addAnchorBeacon(anchorBeacon);
        }
    }


    /**
     * Also updates the support beacon to refer to this zone.
     */
    public void addSupportBeacon(SupportBeacon supportBeacon) {
        supportBeacons.add(supportBeacon);
        supportBeacon.setZone(this);
    }

    /**
     * Also updates the support beacons to refer to this zone.
     */
    public void addSupportBeacons(SupportBeacon... supportBeacons) {
        for (SupportBeacon supportBeacon : supportBeacons) {
            addSupportBeacon(supportBeacon);
        }
    }

}
