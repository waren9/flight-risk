package com.example.flightrisk;

import java.util.List;

public class FlightRoute {
    private List<Waypoint> waypoints;

    public List<Waypoint> getWaypoints() { return waypoints; }
    public void setWaypoints(List<Waypoint> waypoints) { this.waypoints = waypoints; }

    public static class Waypoint {
        private double lat;
        private double lon;
        private double alt;

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLon() { return lon; }
        public void setLon(double lon) { this.lon = lon; }

        public double getAlt() { return alt; }
        public void setAlt(double alt) { this.alt = alt; }
    }
}
