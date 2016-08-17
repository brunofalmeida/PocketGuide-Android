package com.example.cossettenavigation.map;

import com.example.cossettenavigation.Utilities;

import java.util.ArrayList;

/**
 * A key area within a floor or building.
 * @see Map
 */
public class Zone {

    private String name;

    private ArrayList<Floor> floors = new ArrayList<>();

    private ArrayList<AnchorBeacon> anchorBeacons = new ArrayList<>();
    private ArrayList<SupportBeacon> supportBeacons = new ArrayList<>();




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

    public ArrayList<Floor> getFloors() {
        return floors;
    }

    public ArrayList<AnchorBeacon> getAnchorBeacons() {
        return anchorBeacons;
    }

    public ArrayList<SupportBeacon> getSupportBeacons() {
        return supportBeacons;
    }


    /**
     * Also updates the anchor beacons and associated floors to refer to this zone.
     */
    public void addAnchorBeacons(AnchorBeacon... anchorBeacons) {
        for (AnchorBeacon anchorBeacon : anchorBeacons) {
            if (!this.anchorBeacons.contains(anchorBeacon)) {
                this.anchorBeacons.add(anchorBeacon);
            }
            anchorBeacon.addZone(this);

            if (!this.floors.contains(anchorBeacon.getFloor())) {
                this.floors.add(anchorBeacon.getFloor());
            }
            anchorBeacon.getFloor().addZone(this);
        }
    }

    /**
     * Also updates the support beacons and associated floors to refer to this zone.
     */
    public void addSupportBeacons(SupportBeacon... supportBeacons) {
        for (SupportBeacon supportBeacon : supportBeacons) {
            if (!this.supportBeacons.contains(supportBeacon)) {
                this.supportBeacons.add(supportBeacon);
            }
            supportBeacon.setZone(this);

            if (!this.floors.contains(supportBeacon.getFloor())) {
                this.floors.add(supportBeacon.getFloor());
            }
            supportBeacon.getFloor().addZone(this);
        }
    }

}
