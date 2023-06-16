package com.example.contactsaccess.Contact;

import java.io.Serializable;

public class Contact implements Serializable {
    private String name;
    private String phoneNumber;
    private String floorInterested;
    private String maxPrice;
    private String notes;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Contact(String name, String phoneNumber, String pisoInteresado, String maxPrice, String notes) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.floorInterested = pisoInteresado;
        this.maxPrice = maxPrice;
        this.notes = notes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", floorInterested='" + floorInterested + '\'' +
                ", maxPrice=" + maxPrice +
                ", notes='" + notes + '\'' +
                '}';
    }

    public void setFloorInterested(String floorInterested) {
        this.floorInterested = floorInterested;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFloorInterested() {
        return floorInterested;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public String getNotes() {
        return notes;
    }
}
