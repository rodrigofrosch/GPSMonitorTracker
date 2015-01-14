package com.fd.gpsmonitortracker;

public class Message {
    private int id;
    private String msg;
    private String location;
    private String direction;

    public Message() {
    }

    public Message(int id, String msg, String location, String direction) {
        this.id = id;
        this.msg = msg;
        this.location = location;
        this.direction = direction;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}