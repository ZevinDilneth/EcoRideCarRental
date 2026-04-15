package com.ecoride.entities;

import java.util.Date;

//  Payment entity class handling payment transactions

public class K2558793_Payment {
    private String paymentId;
    private double amount;
    private Date paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    private K2558793_Invoice invoice;

//  Constructor for creating a payment

    public K2558793_Payment(String paymentId, double amount, String paymentMethod, K2558793_Invoice invoice) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = new Date();
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "PENDING";
        this.invoice = invoice;
    }

    // Getters and Setters

    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public Date getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public K2558793_Invoice getInvoice() { return invoice; }

//  Processes deposit payment

    public boolean processDeposit() {
        this.paymentStatus = "COMPLETED";
        return true;
    }

//  Processes final payment

    public boolean processFinalPayment() {
        this.paymentStatus = "COMPLETED";
        return true;
    }

//  Refunds deposit

    public boolean refundDeposit() {
        this.paymentStatus = "REFUNDED";
        return true;
    }
}