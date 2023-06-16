package com.example.contactsaccess.Asset;

public class Asset {
    private int id;
    private String type;
    private String address;
    private double price;
    private double squareMeters;

    // Constructor
    public Asset(int id, String type, String address, double price, double squareMeters) {
        this.id = id;
        this.type = type;
        this.address = address;
        this.price = price;
        this.squareMeters = squareMeters;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSquareMeters() {
        return squareMeters;
    }

    public void setSquareMeters(double squareMeters) {
        this.squareMeters = squareMeters;
    }
}
