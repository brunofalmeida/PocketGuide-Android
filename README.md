# Pocket Guide - Android

An Android application that uses Estimote beacons to help the user navigate through an indoor environment.

Developed at Cossette (http://www.cossette.com) in July-August 2016 and demonstrated to representatives from SickKids (http://www.sickkids.ca).

## Interface

### Discovery Mode

Shows the user their current location in the building.

### Navigation Mode

Provides turn-by-turn instructions to guide the user to their destination using text/audio instructions and device vibrations.

## Mapping Data

The mapping data is based around a grid in 3D space (x-y-z axes).

### Floors

Floors in the building; specified with a z-position.

### Beacons

Belong to a specific floor. Have an x-y position on the grid, as well as UUID, major, and minor ID numbers.

### Anchor Beacons

Placed at key intersection points between areas (e.g. doors, entrances and exits, stairs, elevators).

### Support Beacons

Placed at other (non-intersection) points. Can be used to help trilaterate the device position.

### Zones

Define areas of the building, and can span floors (e.g. rooms, hallways, stairs, elevators). A zone is defined such that the user can walk between any two beacons in a straight line.

### Units

All grid units are in metres and seconds.

### Pathfinding

Uses SPFA (https://en.wikipedia.org/wiki/Shortest_Path_Faster_Algorithm) and the known mapping data/obstacles to calculate the shortest path to the destination by time.

## Beacons

Estimote Beacons: http://estimote.com

Each beacon broadcasts a Bluetooth signal at 950ms intervals according the iBeacon standard (https://developer.apple.com/ibeacon/, http://developer.estimote.com/ibeacon/).
The application receives these signals and decides what to do using the IDs and strength of the signal received.

## Beacon Tracking

### Distance Estimates

Given the strength of the signal received from a beacon, the Estimote SDK estimates the distance to that beacon. The `ApplicationBeaconManager` class tracks beacons over time, using a weighted average to estimate the current distance to a beacon (with the most recent measurement having the greatest weight).

### Location/Proximity to Beacons

In discovery mode, the nearest estimated beacon is determined to be the user's current location.

In navigation, the step switches when the device is estimated to be within 3m of a beacon where the user must turn.

### Trilateration

https://en.wikipedia.org/wiki/Trilateration

Trilateration can be attempted, but is not accurate/reliable due to the weak signal strength, fluctuating measurements, and high amounts of interference from floors, objects, and people.

Algorithm Source: https://github.com/lemmingapex/Trilateration

Apache Commons Math: http://commons.apache.org/proper/commons-math/

## Team

Bruno Almeida: https://github.com/b-almeida

Gabriel Yeung: https://github.com/gabrielcyeung

Jacob Kelly: https://github.com/jacobjinkelly

## Links

Estimote Android SDK: https://github.com/Estimote/Android-SDK

Estimote Android SDK Documentation: http://estimote.github.io/Android-SDK/JavaDocs/

Estimote Developer (overview/tutorials): http://developer.estimote.com

Beacon Physics: http://blog.estimote.com/post/106913675010/how-do-beacons-work-the-physics-of-beacon-tech

Beacon Signal Properties: https://community.estimote.com/hc/en-us/articles/201636913-What-are-Broadcasting-Power-RSSI-and-other-characteristics-of-beacon-s-signal-
