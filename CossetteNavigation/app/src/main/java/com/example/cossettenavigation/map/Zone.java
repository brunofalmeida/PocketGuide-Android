package com.example.cossettenavigation.map;

import com.example.cossettenavigation.Utilities;

import java.util.ArrayList;

/**
 * A key area within a floor or building.
 * @see Map
 */
public class Zone {

    private String name;

    private ArrayList<AnchorBeacon> anchorBeacons = new ArrayList<>();
    private ArrayList<SupportBeacon> supportBeacons = new ArrayList<>();
    private ArrayList<Floor> floors = new ArrayList<>();



    public Zone(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(
                "%s { name = %s, anchorBeacons = %s, supportBeacons = %s, floors = %s }",
                getClass().getSimpleName(),
                name,
                Utilities.getAnchorBeaconNamesString(anchorBeacons),
                Utilities.getSupportBeaconNamesString(supportBeacons),
                Utilities.getFloorNamesString(floors));
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
     * Also updates the anchor beacons to refer to this zone.
     */
    public void addAnchorBeacons(AnchorBeacon... anchorBeacons) {
        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            this.anchorBeacons.add(anchorBeacon);
            anchorBeacon.addZone(this);
        }
    }

    /**
     * Also updates the support beacons to refer to this zone.
     */
    public void addSupportBeacons(SupportBeacon... supportBeacons) {
        for (SupportBeacon supportBeacon : supportBeacons) {
            this.supportBeacons.add(supportBeacon);
            supportBeacon.setZone(this);
        }
    }

    public void addFloor(Floor floor) {
        floors.add(floor);
    }

}
