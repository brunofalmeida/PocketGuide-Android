package com.example.cossettenavigation.map;

import com.example.cossettenavigation.Utilities;

import java.util.ArrayList;

/**
 *
 */
public class Floor {

    private String name;

    private ArrayList<AnchorBeacon> anchorBeacons = new ArrayList<>();
    private ArrayList<SupportBeacon> supportBeacons = new ArrayList<>();
    private ArrayList<Zone> zones = new ArrayList<>();




    public Floor(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(
                "%s { name = %s, anchorBeacons = %s, supportBeacons = %s, zones = %s }",
                getClass().getSimpleName(),
                name,
                Utilities.getAnchorBeaconNamesString(anchorBeacons),
                Utilities.getSupportBeaconNamesString(supportBeacons),
                Utilities.getZoneNamesString(zones));
    }


    public String getName() {
        return name;
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public ArrayList<AnchorBeacon> getAnchorBeacons() {
        return anchorBeacons;
    }

    public ArrayList<SupportBeacon> getSupportBeacons() {
        return supportBeacons;
    }


    public void addZones(Zone... zones) {
        for (Zone zone: zones) {
            for (AnchorBeacon anchorBeacon : zone.getAnchorBeacons()) {
                this.anchorBeacons.add(anchorBeacon);
                anchorBeacon.addFloor(this);
            }

            for (SupportBeacon supportBeacon : zone.getSupportBeacons()) {
                this.supportBeacons.add(supportBeacon);
                supportBeacon.addFloor(this);
            }

            this.zones.add(zone);
            zone.addFloor(this);
        }
    }

}
