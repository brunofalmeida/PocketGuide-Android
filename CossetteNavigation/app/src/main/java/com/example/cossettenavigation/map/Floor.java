package com.example.cossettenavigation.map;

import com.example.cossettenavigation.Utilities;

import java.util.ArrayList;

/**
 *
 */
public class Floor {

    private String name;

    private double zPosition;

    private ArrayList<AnchorBeacon> anchorBeacons = new ArrayList<>();
    private ArrayList<SupportBeacon> supportBeacons = new ArrayList<>();

    private ArrayList<Zone> zones = new ArrayList<>();




    public Floor(String name, double zPosition) {
        this.name = name;
        this.zPosition = zPosition;
    }

    public Floor(String name, Floor referenceFloor, double zPositionOffset) {
        this(name, referenceFloor.getZPosition() + zPositionOffset);
    }


    @Override
    public String toString() {
        return String.format(
                "%s { name = %s, zPosition = %.1f, anchorBeacons = %s, supportBeacons = %s, zones = %s }",
                getClass().getSimpleName(),
                name,
                zPosition,
                Utilities.getAnchorBeaconNamesString(anchorBeacons),
                Utilities.getSupportBeaconNamesString(supportBeacons),
                Utilities.getZoneNamesString(zones));
    }


    public String getName() {
        return name;
    }

    public double getZPosition() {
        return zPosition;
    }

    public ArrayList<AnchorBeacon> getAnchorBeacons() {
        return anchorBeacons;
    }

    public ArrayList<SupportBeacon> getSupportBeacons() {
        return supportBeacons;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }


    /**
     * Also updates the anchor beacons to refer to this floor.
     */
    public void addAnchorBeacons(AnchorBeacon... anchorBeacons) {
        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            if (!this.anchorBeacons.contains(anchorBeacon)) {
                this.anchorBeacons.add(anchorBeacon);
            }
            anchorBeacon.setFloor(this);
        }
    }

    /**
     * Also updates the support beacons to refer to this floor.
     */
    public void addSupportBeacons(SupportBeacon... supportBeacons) {
        for (SupportBeacon supportBeacon : supportBeacons) {
            if (!this.supportBeacons.contains(supportBeacon)) {
                this.supportBeacons.add(supportBeacon);
            }
            supportBeacon.setFloor(this);
        }
    }


    // TODO - check for valid references
    public void addZone(Zone zone) {
        if (!this.zones.contains(zone)) {
            this.zones.add(zone);
        }
    }

}
