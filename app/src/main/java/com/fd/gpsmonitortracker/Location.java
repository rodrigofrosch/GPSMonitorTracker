package com.fd.gpsmonitortracker;

import java.util.ArrayList;

public class Location implements IRadius {
    private int id;
    private String name;
    private String radius;
    private String latitude;
    private String longitude;
    private String altitude;
    private String lastMsgSend;
    private String lastMsgSendText;
    private int msgIn;
    private String msgInText;
    private int msgOut;
    private String msgOutText;
    private String isNotify;
    private ArrayList<Location> locationList;

    public Location() {
    }

    public Location(int id, String name, String radius, String latitude, String longitude,
                    String altitude, String lastMsgSend, int msgIn, String msgInText, int msgOut, String msgOutText, String isNotify) {
        this.id = id;
        this.name = name;
        this.radius = radius;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.lastMsgSend = lastMsgSend;
        this.msgIn = msgIn;
        this.msgInText = msgInText;
        this.msgOut = msgOut;
        this.msgOutText = msgOutText;
        this.isNotify = isNotify;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getRadius() {
        return this.radius;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getAltitude() {
        return this.altitude;
    }

    public void setLastMsgSend(String value){
        this.lastMsgSend = value;
    }

    public String getLastMsgSend(){
        return this.lastMsgSend;
    }

    public String getLastMsgSendText() {
        return lastMsgSendText;
    }

    public void setLastMsgSendText(String lastMsgSendText) {
        this.lastMsgSendText = lastMsgSendText;
    }

    public void setMsgIn(int value){
        this.msgIn = value;
    }

    public int getMsgIn(){
        return this.msgIn;
    }

    public void setMsgInText(String value){
        this.msgInText = value;
    }

    public String getMsgInText(){
        return this.msgInText;
    }

    public void setMsgOutText(String value){
        this.msgOutText = value;
    }

    public String getMsgOutText(){
        return this.msgOutText;
    }

    public void setMsgOut(int value){
        this.msgOut = value;
    }

    public int getMsgOut(){
        return this.msgOut;
    }

    public void setLocationList(ArrayList<Location> locationList){
        this.locationList = locationList;
    }

    public ArrayList<Location> getLocationList(){
        return this.locationList;
    }

    public String getIsNotify() {
        return isNotify;
    }

    public void setIsNotify(String isNotify) {
        this.isNotify = isNotify;
    }

    @Override
    public double CoordDistance(double lat1, double lng1, double lat2, double lng2) {
            double earthRadius = 3958.75;
            double dLat = Math.toRadians(lat2-lat1);
            double dLng = Math.toRadians(lng2-lng1);
            double sindLat = Math.sin(dLat / 2);
            double sindLng = Math.sin(dLng / 2);
            double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                    * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double dist = (earthRadius * c) / 0.62137;
            if (dist < 0) dist += 21;
            return dist;
        }

    @Override
    public Boolean inRadius(Location locLoad, Location newLoc) {
        // TODO Auto-generated method stub
        if (this.CoordDistance(
                Double.parseDouble(locLoad.getLatitude()),
                Double.parseDouble(locLoad.getLongitude()),
                Double.parseDouble(newLoc.getLatitude()),
                Double.parseDouble(newLoc.getLongitude())) <= Double.parseDouble(locLoad.getRadius())/1000) {
            return true;
        } else
            return false;
    }


}
