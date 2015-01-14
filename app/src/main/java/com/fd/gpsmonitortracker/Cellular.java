package com.fd.gpsmonitortracker;

public class Cellular {

    private int id;
    private String name;
    private String number;
    private String isNotify;

    public Cellular(int id, String name, String number, String isNotify) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.isNotify = isNotify;
    }

    public Cellular() {

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIsNotify() {
        return isNotify;
    }

    public void setIsNotify(String isNotify) {
        this.isNotify = isNotify;
    }
}