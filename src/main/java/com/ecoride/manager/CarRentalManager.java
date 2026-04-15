package com.ecoride.manager;

import com.ecoride.entities.*;
import com.ecoride.enums.*;
import com.ecoride.storage.DataStorage;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

// Main business logic controller for the car rental system
public class CarRentalManager {
    private DataStorage dataStorage;

    // Constants for business rules
    private static final double REFUNDABLE_DEPOSIT = 5000.0;
    private static final int MIN_ADVANCE_BOOKING_DAYS = 3;
    private static final int CANCELLATION_DEADLINE_DAYS = 2;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s.'-]+$");
    private static final Pattern CONTACT_NUMBER_PATTERN = Pattern.compile("^[\\d\\s\\-\\(\\)\\+]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern NIC_PASSPORT_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    public CarRentalManager() {
        this.dataStorage = new DataStorage();
    }

    public DataStorage getDataStorage() {
        return dataStorage;
    }

/*
     * CAR MANAGEMENT METHODS
     * Adds a new car to the system with auto-generated ID
*/

    public K2558793_Car addCar(String model, K2558793_Category category) {
        return dataStorage.addCar(model, category);
    }

    // Updates car details
    public boolean updateCar(String carId, String model, K2558793_Category category) {
        return dataStorage.updateCar(carId, model, category);
    }

    // Removes a car from the system (only if available)
    public boolean removeCar(String carId) {
        return dataStorage.removeCar(carId);
    }

    // Updates car availability status
    public boolean updateCarAvailability(String carId, K2558793_AvailabilityStatus status) {
        return dataStorage.updateCarAvailability(carId, status);
    }

    // Gets cars by availability status
    public List<K2558793_Car> getCarsByStatus(K2558793_AvailabilityStatus status) {
        return dataStorage.getCarsByStatus(status);
    }

    // Gets all availability statuses
    public K2558793_AvailabilityStatus[] getAllAvailabilityStatuses() {
        return dataStorage.getAllAvailabilityStatuses();
    }

/*
     * CUSTOMER MANAGEMENT METHODS
     * Registers a new customer with auto-generated ID
*/

    public K2558793_Customer registerCustomer(String nicPassport, String name, String contactNumber, String email) {
        // Validate all inputs first
        Map<String, String> validationErrors = validateCustomerInputs(nicPassport, name, contactNumber, email);

        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", validationErrors.values()));
        }

        // Check if customer already exists with same NIC/Passport
        if (dataStorage.findCustomerByNic(nicPassport) != null) {
            throw new IllegalArgumentException("Customer with NIC/Passport " + nicPassport + " already exists");
        }

        return dataStorage.addCustomer(nicPassport, name.trim(), contactNumber.trim(), email.trim());
    }

    // Updates customer profile with validation
    public boolean updateCustomerProfile(String customerId, String name, String contactNumber, String email) {
        K2558793_Customer customer = dataStorage.getCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }

        // Validate all inputs
        Map<String, String> validationErrors = validateCustomerInputs(customer.getNicPassport(), name, contactNumber, email);

        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", validationErrors.values()));
        }

        customer.setName(name.trim());
        customer.setContactNumber(contactNumber.trim());
        customer.setEmail(email.trim());
        return true;
    }

    // Alternative version that returns error messages instead of throwing exceptions
    public Map<String, String> updateCustomerProfileWithDetailedErrors(String customerId, String name, String contactNumber, String email) {
        Map<String, String> errors = new HashMap<>();
        K2558793_Customer customer = dataStorage.getCustomer(customerId);

        if (customer == null) {
            errors.put("general", "Customer not found");
            return errors;
        }

        // Validate inputs and collect detailed errors
        errors.putAll(validateCustomerInputs(customer.getNicPassport(), name, contactNumber, email));

        // If no errors, update the customer
        if (errors.isEmpty()) {
            customer.setName(name.trim());
            customer.setContactNumber(contactNumber.trim());
            customer.setEmail(email.trim());
        }

        return errors;
    }

    // Comprehensive validation method for customer inputs
    private Map<String, String> validateCustomerInputs(String nicPassport, String name, String contactNumber, String email) {
        Map<String, String> errors = new HashMap<>();

        // Validate NIC/Passport
        if (nicPassport == null || nicPassport.trim().isEmpty()) {
            errors.put("nicPassport", "NIC/Passport cannot be empty");
        } else if (!NIC_PASSPORT_PATTERN.matcher(nicPassport.trim()).matches()) {
            errors.put("nicPassport", "NIC/Passport can only contain letters and numbers");
        }

        // Validate name - more specific error for numbers
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Name cannot be empty");
        } else if (name.trim().matches(".*\\d.*")) {
            errors.put("name", "Name cannot contain numbers");
        } else if (!NAME_PATTERN.matcher(name.trim()).matches()) {
            errors.put("name", "Name can only contain letters, spaces, hyphens, and apostrophes");
        } else if (name.trim().length() < 2) {
            errors.put("name", "Name must be at least 2 characters long");
        }

        // Validate contact number - more specific error for non-numbers
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            errors.put("contactNumber", "Contact number cannot be empty");
        } else {
            String digitsOnly = contactNumber.replaceAll("\\D", "");
            if (digitsOnly.length() < 7) {
                errors.put("contactNumber", "Contact number must contain at least 7 digits");
            } else if (digitsOnly.length() > 15) {
                errors.put("contactNumber", "Contact number cannot exceed 15 digits");
            }

            // Check if contains non-digit characters (other than allowed formatting chars)
            String testContact = contactNumber.replaceAll("[\\s\\-\\(\\)\\+]", "");
            if (!testContact.matches("^\\d+$")) {
                errors.put("contactNumber", "Contact number can only contain numbers and formatting characters (spaces, hyphens, parentheses, plus)");
            }
        }

        // Validate email - more specific error
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email cannot be empty");
        } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errors.put("email", "Invalid email format. Please use a valid email address (e.g., user@example.com)");
        }

        return errors;
    }

/*
     * RESERVATION MANAGEMENT METHODS
     * Validates booking rules before making reservation
*/

    private String validateBooking(K2558793_Car car, Date pickupDate, int totalKm) {
        // Check car availability
        if (car.getAvailabilityStatus() != K2558793_AvailabilityStatus.AVAILABLE) {
            return "Car is not available for booking";
        }

        // Check minimum advance booking (3 days)
        Date currentDate = new Date();
        long daysDifference = getDaysDifference(currentDate, pickupDate);
        if (daysDifference < MIN_ADVANCE_BOOKING_DAYS) {
            return "Booking must be made at least " + MIN_ADVANCE_BOOKING_DAYS + " days in advance";
        }

        // Check total kilometers (must be positive)
        if (totalKm <= 0) {
            return "Total kilometers must be positive";
        }
        return null; // No validation errors
    }

    // Makes a new reservation with business rule validation and auto-generated ID
    public K2558793_Reservation makeReservation(String customerId, String carId, Date pickupDate,
                                                Date returnDate, int totalKm) {
        K2558793_Customer customer = dataStorage.getCustomer(customerId);
        K2558793_Car car = dataStorage.getCar(carId);

        if (customer == null || car == null) {
            return null;
        }

        // Validate booking rules
        String validationError = validateBooking(car, pickupDate, totalKm);
        if (validationError != null) {
            return null;
        }

        // Generate booking ID
        String bookingId = dataStorage.generateReservationId();

        // Create reservation
        K2558793_Reservation reservation = new K2558793_Reservation(
                bookingId, new Date(), pickupDate, returnDate, totalKm, customer, car
        );

        // Update car status to reserved
        updateCarAvailability(carId, K2558793_AvailabilityStatus.RESERVED);

        // Store reservation
        dataStorage.addReservation(reservation);

        return reservation;
    }

    // Searches reservation by booking ID
    public K2558793_Reservation searchReservationById(String bookingId) {
        return dataStorage.getReservation(bookingId);
    }

    // Searches reservations by customer name
    public List<K2558793_Reservation> searchReservationByName(String name) {
        return dataStorage.findReservationsByCustomerName(name);
    }

    // Checks if cancellation is allowed (within 2 days from reservation)
    public boolean isCancellationAllowed(K2558793_Reservation reservation) {
        Date currentDate = new Date();
        long daysSinceBooking = getDaysDifference(reservation.getBookingDate(), currentDate);
        return daysSinceBooking <= CANCELLATION_DEADLINE_DAYS;
    }

    // Cancels a reservation with validation
    public boolean cancelReservation(String bookingId) {
        K2558793_Reservation reservation = dataStorage.getReservation(bookingId);
        if (reservation == null) {
            return false;
        }

        // Check cancellation deadline
        if (!isCancellationAllowed(reservation)) {
            return false;
        }

        // Update reservation status
        reservation.setBookingStatus(K2558793_BookingStatus.CANCELLED);

        // Update car availability back to available
        updateCarAvailability(reservation.getCar().getCarId(), K2558793_AvailabilityStatus.AVAILABLE);

        return true;
    }

/*
     * INVOICE AND PAYMENT METHODS
     * Generates invoice for a reservation with auto-generated ID
*/

    public K2558793_Invoice generateInvoice(K2558793_Reservation reservation) {
        String invoiceId = dataStorage.generateInvoiceId();
        K2558793_Invoice invoice = new K2558793_Invoice(invoiceId, reservation);
        dataStorage.addInvoice(invoice);
        return invoice;
    }

    // Processes deposit payment with auto-generated ID
    public K2558793_Payment processDeposit(K2558793_Invoice invoice, String paymentMethod) {
        String paymentId = dataStorage.generatePaymentId();
        K2558793_Payment payment = new K2558793_Payment(paymentId, REFUNDABLE_DEPOSIT, paymentMethod, invoice);
        payment.processDeposit();
        dataStorage.addPayment(payment);
        return payment;
    }

    // Processes final payment with auto-generated ID
    public K2558793_Payment processFinalPayment(K2558793_Invoice invoice, String paymentMethod) {
        String paymentId = dataStorage.generatePaymentId();
        double finalAmount = invoice.getFinalPayableAmount() - REFUNDABLE_DEPOSIT;
        K2558793_Payment payment = new K2558793_Payment(paymentId, finalAmount, paymentMethod, invoice);
        payment.processFinalPayment();
        dataStorage.addPayment(payment);
        return payment;
    }

/*
     * UTILITY METHODS
     * Calculates days difference between two dates
*/

    private long getDaysDifference(Date date1, Date date2) {
        long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    // Gets all available cars
    public List<K2558793_Car> getAvailableCars() {
        return dataStorage.getAvailableCars();
    }

    // Gets all cars
    public List<K2558793_Car> getAllCars() {
        return dataStorage.getAllCars();
    }

    // Gets all customers
    public List<K2558793_Customer> getAllCustomers() {
        return dataStorage.getAllCustomers();
    }

    // Gets all reservations
    public List<K2558793_Reservation> getAllReservations() {
        return dataStorage.getAllReservations();
    }

    // Gets all categories from Table 1
    public K2558793_Category[] getAllCategories() {
        return dataStorage.getAllCategories();
    }
}