package com.ecoride.entities;

//  Invoice entity class representing a rental invoice

public class K2558793_Invoice {
    private String invoiceId;
    private double basePrice;
    private double discountAmount;
    private double extraKmCharges;
    private double taxAmount;
    private double depositAmount;
    private double finalPayableAmount;
    private K2558793_Reservation reservation;

    // Constants

    private static final double REFUNDABLE_DEPOSIT = 5000.0;
    private static final double DISCOUNT_RATE = 0.10;

//   Constructor for creating a new invoice

    public K2558793_Invoice(String invoiceId, K2558793_Reservation reservation) {
        this.invoiceId = invoiceId;
        this.reservation = reservation;
        this.depositAmount = REFUNDABLE_DEPOSIT;
        calculateFinalAmount();
    }

    // Getters

    public String getInvoiceId() { return invoiceId; }
    public double getBasePrice() { return basePrice; }
    public double getDiscountAmount() { return discountAmount; }
    public double getExtraKmCharges() { return extraKmCharges; }
    public double getTaxAmount() { return taxAmount; }
    public double getDepositAmount() { return depositAmount; }
    public double getFinalPayableAmount() { return finalPayableAmount; }
    public K2558793_Reservation getReservation() { return reservation; }

//  Calculates final payable amount with all charges and discounts

    public double calculateFinalAmount() {

        // Calculate base price

        basePrice = reservation.calculateBasePrice();

        // Apply discount if eligible

        discountAmount = 0.0;
        if (reservation.checkDiscountEligibility()) {
            discountAmount = basePrice * DISCOUNT_RATE;
        }

        // Calculate price after discount

        double priceAfterDiscount = basePrice - discountAmount;

        // Add extra KM charges

        extraKmCharges = reservation.calculateExtraKmCharges();
        double priceWithExtraKm = priceAfterDiscount + extraKmCharges;

        // Calculate tax

        double taxRate = reservation.getCar().getCategory().getTaxRate();
        taxAmount = priceWithExtraKm * taxRate;

        // Calculate final amount (including deposit)

        finalPayableAmount = priceWithExtraKm + taxAmount + depositAmount;

        return finalPayableAmount;
    }

//  Gets detailed invoice string for display

    public String getInvoiceDetails() {
        return String.format(
                "=== EcoRide Car Rental Invoice ===\n" +
                        "Invoice ID: %s\n" +
                        "Booking ID: %s\n" +
                        "Customer: %s\n" +
                        "Car: %s (%s)\n" +
                        "Rental Period: %d days\n" +
                        "Total Kilometers: %d km\n" +
                        "----------------------------------\n" +
                        "Base Price: LKR %.2f\n" +
                        "Discount: LKR %.2f\n" +
                        "Extra KM Charges: LKR %.2f\n" +
                        "Tax: LKR %.2f\n" +
                        "Refundable Deposit: LKR %.2f\n" +
                        "----------------------------------\n" +
                        "Final Payable Amount: LKR %.2f\n" +
                        "==================================",
                invoiceId, reservation.getBookingId(), reservation.getCustomer().getName(),
                reservation.getCar().getModel(), reservation.getCar().getCategory(),
                reservation.getRentalDays(), reservation.getTotalKm(),
                basePrice, discountAmount, extraKmCharges, taxAmount, depositAmount, finalPayableAmount
        );
    }
}