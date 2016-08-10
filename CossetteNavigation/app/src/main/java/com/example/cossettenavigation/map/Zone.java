package com.example.cossettenavigation.map;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * A key area within a floor or building.
 * @see Map
 */
public class Zone {

    private String name;

    /**
     * References to anchor beacons that define this zone.
     * (A Zone does not own its anchor beacons, since they can define multiple zones.)
     */
    private ArrayList<WeakReference<AnchorBeacon>> anchorBeacons = new ArrayList<>();

    /**
     * Support beacons that are part of this zone.
     * (A zone owns its support beacons, since they are only part of that zone.)
     */
    private ArrayList<SupportBeacon> supportBeacons = new ArrayList<>();




    public Zone(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(
                "%s { name = %s, anchorBeacons = %s, supportBeacons = %s }",
                getClass().getSimpleName(), name, anchorBeacons, supportBeacons);
    }


    /**
     * Also updates the anchor beacon to refer to this zone.
     */
    public void addAnchorBeacon(AnchorBeacon anchorBeacon) {
        anchorBeacons.add(new WeakReference<>(anchorBeacon));
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
