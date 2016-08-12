package com.example.cossettenavigation.map;

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
        String zonesString = "{ ";
        for (Zone zone : zones) {
            zonesString += String.format("\"%s\", ", zone.getName());
        }
        zonesString += "}";

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
                "%s { name = %s, zones = %s, anchorBeacons = %s, supportBeacons = %s }",
                getClass().getSimpleName(), name, zonesString, anchorBeaconsString, supportBeaconsString);
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
