package com.ecoride.entities;

//  Customer entity class representing a rental customer

public class K2558793_Customer {
    private String customerId;
    private String nicPassport;
    private String name;
    private String contactNumber;
    private String email;

//  Constructor for creating a new customer

    public K2558793_Customer(String customerId, String nicPassport, String name, String contactNumber, String email) {
        this.customerId = customerId;
        this.nicPassport = nicPassport;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    // Getters and Setters

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getNicPassport() { return nicPassport; }
    public void setNicPassport(String nicPassport) { this.nicPassport = nicPassport; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", customerId, name, nicPassport);
    }
}