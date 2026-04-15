package com.ecoride.entities;

import com.ecoride.enums.K2558793_AvailabilityStatus;
import com.ecoride.enums.K2558793_Category;

//  Car entity class representing a vehicle in the rental system

public class K2558793_Car {
    private String carId;
    private String model;
    private K2558793_Category category;
    private K2558793_AvailabilityStatus availabilityStatus;

//  Constructor for creating a new car

    public K2558793_Car(String carId, String model, K2558793_Category category) {
        this.carId = carId;
        this.model = model;
        this.category = category;
        this.availabilityStatus = K2558793_AvailabilityStatus.AVAILABLE;
    }

    // Getters and Setters

    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public K2558793_Category getCategory() { return category; }
    public void setCategory(K2558793_Category category) { this.category = category; }

    public K2558793_AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }

//  Updates the availability status of the car

    public void updateAvailability(K2558793_AvailabilityStatus status) {
        this.availabilityStatus = status;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s) - %s", carId, model, category.getDisplayName(), availabilityStatus);
    }
}