package com.ecoride.entities;

import com.ecoride.enums.K2558793_BookingStatus;
import java.util.Date;

//  Reservation entity class representing a car booking

public class K2558793_Reservation {
    private String bookingId;
    private Date bookingDate;
    private Date pickupDate;
    private Date returnDate;
    private int totalKm;
    private K2558793_BookingStatus bookingStatus;
    private K2558793_Customer customer;
    private K2558793_Car car;

//  Constructor for creating a new reservation

    public K2558793_Reservation(String bookingId, Date bookingDate, Date pickupDate,
                                Date returnDate, int totalKm, K2558793_Customer customer,
                                K2558793_Car car) {
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.totalKm = totalKm;
        this.bookingStatus = K2558793_BookingStatus.CONFIRMED;
        this.customer = customer;
        this.car = car;
    }

    // Getters and Setters

    public String getBookingId() { return bookingId; }
    public Date getBookingDate() { return bookingDate; }
    public Date getPickupDate() { return pickupDate; }
    public Date getReturnDate() { return returnDate; }
    public int getTotalKm() { return totalKm; }
    public K2558793_BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(K2558793_BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }
    public K2558793_Customer getCustomer() { return customer; }
    public K2558793_Car getCar() { return car; }

//  Calculates the number of rental days

    public int getRentalDays() {
        long difference = returnDate.getTime() - pickupDate.getTime();
        return (int) (difference / (1000 * 60 * 60 * 24)) + 1;
    }

//  Calculates base price (daily rental × number of days)

    public double calculateBasePrice() {
        int rentalDays = getRentalDays();
        return car.getCategory().getDailyRentalFee() * rentalDays;
    }

//  Calculates extra kilometer charges

    public double calculateExtraKmCharges() {
        int rentalDays = getRentalDays();
        int freeKmAllocation = car.getCategory().getFreeKmPerDay() * rentalDays;

        if (totalKm <= freeKmAllocation) {
            return 0.0;
        }

        int extraKm = totalKm - freeKmAllocation;
        return extraKm * car.getCategory().getExtraKmCharge();
    }

//  Checks if discount is applicable (7 or more days)

    public boolean checkDiscountEligibility() {
        return getRentalDays() >= 7;
    }

    @Override
    public String toString() {
        return String.format("BookingID: %s, Customer: %s, Car: %s, Pickup: %s",
                bookingId, customer.getName(), car.getModel(), pickupDate);
    }
}