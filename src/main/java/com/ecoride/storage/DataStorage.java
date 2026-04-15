package com.ecoride.storage;

import com.ecoride.entities.*;
import com.ecoride.enums.*;
import java.util.*;

//  Data storage class managing all collections for the rental system

public class DataStorage {

    // HashMaps for efficient lookup by ID

    private Map<String, K2558793_Car> cars;
    private Map<String, K2558793_Customer> customers;
    private Map<String, K2558793_Reservation> reservations;
    private Map<String, K2558793_Invoice> invoices;
    private Map<String, K2558793_Payment> payments;

    // Counters for auto-generating IDs

    private int carCounter = 1;
    private int customerCounter = 1;
    private int reservationCounter = 1;
    private int invoiceCounter = 1;
    private int paymentCounter = 1;

    public DataStorage() {
        this.cars = new HashMap<>();
        this.customers = new HashMap<>();
        this.reservations = new HashMap<>();
        this.invoices = new HashMap<>();
        this.payments = new HashMap<>();

        initializeSampleData();
    }

//  Initialize with sample data for testing

    private void initializeSampleData() {

        // Add sample cars using Table 1 categories

        addCar("Toyota Aqua", K2558793_Category.HYBRID);
        addCar("Nissan Leaf", K2558793_Category.ELECTRIC);
        addCar("Toyota Corolla", K2558793_Category.COMPACT_PETROL);
        addCar("BMW X5", K2558793_Category.LUXURY_SUV);

        // No sample customers - users will register their own

        System.out.println("Initialized with " + cars.size() + " sample cars. No sample customers.");
    }

    // ID Generation methods

    public String generateCarId() {
        String id = "CAR" + (carCounter++);
        return id;
    }

    public String generateCustomerId() {
        String id = "CUST" + (customerCounter++);
        return id;
    }

    public String generateReservationId() { return "BOOK" + (reservationCounter++); }
    public String generateInvoiceId() { return "INV" + (invoiceCounter++); }
    public String generatePaymentId() { return "PAY" + (paymentCounter++); }

    // Car management methods

    public void addCar(K2558793_Car car) {
        cars.put(car.getCarId(), car);
    }

    public K2558793_Car addCar(String model, K2558793_Category category) {
        String carId = generateCarId();
        K2558793_Car car = new K2558793_Car(carId, model, category);
        cars.put(carId, car);
        return car;
    }

    public K2558793_Car getCar(String carId) {
        return cars.get(carId);
    }

    public List<K2558793_Car> getAllCars() {
        return new ArrayList<>(cars.values());
    }

    public List<K2558793_Car> getAvailableCars() {
        List<K2558793_Car> availableCars = new ArrayList<>();
        for (K2558793_Car car : cars.values()) {
            if (car.getAvailabilityStatus() == K2558793_AvailabilityStatus.AVAILABLE) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    public boolean updateCar(String carId, String model, K2558793_Category category) {
        K2558793_Car car = cars.get(carId);
        if (car != null) {
            car.setModel(model);
            car.setCategory(category);
            return true;
        }
        return false;
    }

    public boolean removeCar(String carId) {
        K2558793_Car car = cars.get(carId);
        if (car != null && car.getAvailabilityStatus() == K2558793_AvailabilityStatus.AVAILABLE) {
            return cars.remove(carId) != null;
        }
        return false;
    }

//  Updates car availability status

    public boolean updateCarAvailability(String carId, K2558793_AvailabilityStatus newStatus) {
        K2558793_Car car = cars.get(carId);
        if (car != null) {
            car.updateAvailability(newStatus);
            return true;
        }
        return false;
    }

//  Gets cars by availability status

    public List<K2558793_Car> getCarsByStatus(K2558793_AvailabilityStatus status) {
        List<K2558793_Car> result = new ArrayList<>();
        for (K2558793_Car car : cars.values()) {
            if (car.getAvailabilityStatus() == status) {
                result.add(car);
            }
        }
        return result;
    }

//  Gets all availability statuses

    public K2558793_AvailabilityStatus[] getAllAvailabilityStatuses() {
        return K2558793_AvailabilityStatus.values();
    }

    // Customer management methods

    public void addCustomer(K2558793_Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    public K2558793_Customer addCustomer(String nicPassport, String name, String contactNumber, String email) {
        String customerId = generateCustomerId();
        K2558793_Customer customer = new K2558793_Customer(customerId, nicPassport, name, contactNumber, email);
        customers.put(customerId, customer);
        return customer;
    }

    public K2558793_Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }

    public List<K2558793_Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public K2558793_Customer findCustomerByNic(String nicPassport) {
        for (K2558793_Customer customer : customers.values()) {
            if (customer.getNicPassport().equals(nicPassport)) {
                return customer;
            }
        }
        return null;
    }

    // Reservation management methods

    public void addReservation(K2558793_Reservation reservation) {
        reservations.put(reservation.getBookingId(), reservation);
    }

    public K2558793_Reservation getReservation(String bookingId) {
        return reservations.get(bookingId);
    }

    public List<K2558793_Reservation> getAllReservations() {
        return new ArrayList<>(reservations.values());
    }

    public List<K2558793_Reservation> findReservationsByCustomerName(String name) {
        List<K2558793_Reservation> result = new ArrayList<>();
        for (K2558793_Reservation reservation : reservations.values()) {
            if (reservation.getCustomer().getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(reservation);
            }
        }
        return result;
    }

    // Invoice management methods

    public void addInvoice(K2558793_Invoice invoice) {
        invoices.put(invoice.getInvoiceId(), invoice);
    }

    public K2558793_Invoice getInvoice(String invoiceId) {
        return invoices.get(invoiceId);
    }

    // Payment management methods

    public void addPayment(K2558793_Payment payment) {
        payments.put(payment.getPaymentId(), payment);
    }

    public K2558793_Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }

    // Get all categories from Table 1

    public K2558793_Category[] getAllCategories() {
        return K2558793_Category.values();
    }
}