# Pocket Guide - Android

An Android application that uses Estimote beacons to help the user navigate through an indoor environment.

Developed at Cossette (http://www.cossette.com) in July-August 2016 and demonstrated to representatives from SickKids (http://www.sickkids.ca).


## Interface

### Discovery Mode

Shows the user their current floor and location in the building.

### Navigation Mode

Provides turn-by-turn instructions to guide the user to their destination; uses text/audio instructions, on-screen arrows, and device vibrations.


## Mapping Data

The mapping data is based on a 3D grid (with x, y, and z axes).

### Floors

- Define floors of the building
- z-position on grid

### Beacons

- Correspond to Estimote beacons
- Belong to a floor
- x-y position on grid
- UUID, major, and minor ID numbers

Anchor Beacons - Placed at key intersection points between areas (e.g. doors, entrances/exits, stairs, elevators).

Support Beacons - Placed at other points.

### Zones

- Define areas of the building
- Have a type (e.g. room, hallway, stairs, elevator)
- Can span floors
- May be a destination the user can navigate to
- Must be defined so that the user can walk between any two beacons in a straight line

### Units

All units are in metres and seconds.

### Pathfinding

Uses SPFA (https://en.wikipedia.org/wiki/Shortest_Path_Faster_Algorithm) and the known mapping data/obstacles to calculate the shortest path to the destination by time.


## Beacons and Tracking

Estimote: http://estimote.com

Estimote Developer (overview/tutorials): http://developer.estimote.com

Estimote Android SDK: https://github.com/Estimote/Android-SDK

Estimote Android SDK Documentation: http://estimote.github.io/Android-SDK/JavaDocs/

Beacon Physics: http://blog.estimote.com/post/106913675010/how-do-beacons-work-the-physics-of-beacon-tech

Beacon Signal Properties: https://community.estimote.com/hc/en-us/articles/201636913-What-are-Broadcasting-Power-RSSI-and-other-characteristics-of-beacon-s-signal-

Each beacon broadcasts a Bluetooth signal at 950ms intervals according the iBeacon standard (https://developer.apple.com/ibeacon/, http://developer.estimote.com/ibeacon/).
The application receives these signals and decides what to do using the IDs and strength of the signal received.

The `ApplicationBeaconManager` class manages and tracks beacons over time.

### Distance Estimates

Given the strength of the signal received from a beacon, the Estimote SDK estimates the distance to that beacon. Uses a weighted average to estimate the current distance to a beacon (most recent measurement has the greatest weight).

### Location/Proximity to Beacons

In discovery mode, the nearest estimated beacon is determined to be the user's current location.

In navigation mode, the step switches when the device is estimated to be within 3m of a beacon where the user must turn.

### Trilateration

https://en.wikipedia.org/wiki/Trilateration

Trilateration can be attempted, but is not accurate/reliable due to weak signal strength, fluctuating measurements, and interference from floors, objects, and people.

Algorithm: https://github.com/lemmingapex/Trilateration

Apache Commons Math: http://commons.apache.org/proper/commons-math/


## Team

Bruno Almeida: https://github.com/b-almeida

Gabriel Yeung: https://github.com/gabrielcyeung

Jacob Kelly: https://github.com/jacobjinkelly
