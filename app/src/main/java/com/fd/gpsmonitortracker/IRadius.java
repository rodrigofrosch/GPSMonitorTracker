package com.fd.gpsmonitortracker;

public interface IRadius {
    public Boolean inRadius(Location l1, Location l2);

    public double CoordDistance(double latitude1, double longitude1, double latitude2, double longitude2);
}
