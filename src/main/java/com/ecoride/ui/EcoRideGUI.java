package com.ecoride.ui;

import com.ecoride.entities.*;
import com.ecoride.enums.*;
import com.ecoride.manager.CarRentalManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// Main user interface for the EcoRide Car Rental System

public class EcoRideGUI {
    private CarRentalManager rentalManager;
    private JFrame mainFrame;
    private JTabbedPane tabbedPane;
    private JPanel dashboardPanel;
    private JPanel carManagementPanel;

    // Date format for display
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Statistics cards for easy updating
    private JLabel availableCarsValue;
    private JLabel totalCustomersValue;
    private JLabel totalReservationsValue;
    private JLabel systemStatusValue;

    public EcoRideGUI() {
        this.rentalManager = new CarRentalManager();
        initializeGUI();
    }

    // Initializes the main GUI components
    private void initializeGUI() {
        mainFrame = new JFrame("EcoRide Car Rental System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 600);
        mainFrame.setLocationRelativeTo(null);

        // Create tabbed pane for different functionalities
        tabbedPane = new JTabbedPane();

        // Create dashboard panel first (we'll store reference to update it)
        dashboardPanel = createDashboardPanel();
        carManagementPanel = createCarManagementPanel();

        // Add tabs
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Car Management", carManagementPanel);
        tabbedPane.addTab("Customer Management", createCustomerManagementPanel());
        tabbedPane.addTab("Make Reservation", createReservationPanel());
        tabbedPane.addTab("Search Reservations", createSearchPanel());
        tabbedPane.addTab("Generate Invoice", createInvoicePanel());

        // Add tab change listener to refresh panels when selected
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedComponent() == dashboardPanel) {
                    refreshDashboard();
                } else if (tabbedPane.getSelectedComponent() == carManagementPanel) {
                    refreshCarManagementPanel();
                }
            }
        });

        mainFrame.add(tabbedPane);
        mainFrame.setVisible(true);
    }

    // Creates the dashboard panel with system overview
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to EcoRide Car Rental System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLUE);

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("System Statistics"));

        // Create stat cards with references to update them
        JPanel availableCarsCard = createStatCard("Available Cars", "0", Color.GREEN);
        JPanel totalCustomersCard = createStatCard("Total Customers", "0", Color.BLUE);
        JPanel totalReservationsCard = createStatCard("Total Reservations", "0", Color.ORANGE);
        JPanel systemStatusCard = createStatCard("System Status", "Active", Color.GREEN);

        // Get references to the value labels for updating
        availableCarsValue = (JLabel) ((BorderLayout) availableCarsCard.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        totalCustomersValue = (JLabel) ((BorderLayout) totalCustomersCard.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        totalReservationsValue = (JLabel) ((BorderLayout) totalReservationsCard.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        systemStatusValue = (JLabel) ((BorderLayout) systemStatusCard.getLayout()).getLayoutComponent(BorderLayout.CENTER);

        statsPanel.add(availableCarsCard);
        statsPanel.add(totalCustomersCard);
        statsPanel.add(totalReservationsCard);
        statsPanel.add(systemStatusCard);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(e -> refreshDashboard());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshButton);

        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial refresh
        refreshDashboard();
        return panel;
    }

    // Refreshes the dashboard with current data
    private void refreshDashboard() {
        try {
            int availableCars = rentalManager.getAvailableCars().size();
            int totalCustomers = rentalManager.getAllCustomers().size();
            int totalReservations = rentalManager.getAllReservations().size();

            availableCarsValue.setText(String.valueOf(availableCars));
            totalCustomersValue.setText(String.valueOf(totalCustomers));
            totalReservationsValue.setText(String.valueOf(totalReservations));

            // Update colors based on system status
            if (availableCars > 0) {
                systemStatusValue.setText("Active");
                systemStatusValue.setForeground(Color.GREEN);
            } else {
                systemStatusValue.setText("No Cars Available");
                systemStatusValue.setForeground(Color.RED);
            }

            System.out.println("Dashboard refreshed - Cars: " + availableCars +
                    ", Customers: " + totalCustomers +
                    ", Reservations: " + totalReservations);
        } catch (Exception e) {
            showError("Error refreshing dashboard: " + e.getMessage());
        }
    }

    // Creates a statistics card for dashboard
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // Creates car management panel with availability status control
    private JPanel createCarManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Car Management"));

        JTextField carIdField = new JTextField();
        JTextField modelField = new JTextField();
        JComboBox<K2558793_Category> categoryCombo = new JComboBox<>(rentalManager.getAllCategories());
        JComboBox<K2558793_AvailabilityStatus> statusCombo = new JComboBox<>(K2558793_AvailabilityStatus.values());

        formPanel.add(new JLabel("Car ID:"));
        formPanel.add(carIdField);
        formPanel.add(new JLabel("Model:"));
        formPanel.add(modelField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Availability Status:"));
        formPanel.add(statusCombo);

        JButton addButton = new JButton("Add New Car");
        JButton updateButton = new JButton("Update Car");
        JButton removeButton = new JButton("Remove Car");
        JButton updateStatusButton = new JButton("Update Status Only");
        JButton refreshButton = new JButton("Refresh Cars List");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(refreshButton);

        formPanel.add(new JLabel());
        formPanel.add(buttonPanel);

        // Cars table
        String[] columnNames = {"Car ID", "Model", "Category", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable carsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(carsTable);

        // Load cars into table
        refreshCarsTable(tableModel);

        // Button actions
        addButton.addActionListener(e -> {
            try {
                String model = modelField.getText().trim();
                K2558793_Category category = (K2558793_Category) categoryCombo.getSelectedItem();

                if (model.isEmpty()) {
                    showError("Please enter car model");
                    return;
                }

                K2558793_Car newCar = rentalManager.addCar(model, category);
                if (newCar != null) {
                    showSuccess("Car added successfully!\nCar ID: " + newCar.getCarId());
                    refreshCarsTable(tableModel);
                    clearFields(carIdField, modelField);
                    // Auto-refresh dashboard if it's visible
                    if (tabbedPane.getSelectedComponent() == dashboardPanel) {
                        refreshDashboard();
                    }
                } else {
                    showError("Failed to add car.");
                }
            } catch (Exception ex) {
                showError("Error adding car: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> {
            try {
                String carId = carIdField.getText().trim();
                String model = modelField.getText().trim();
                K2558793_Category category = (K2558793_Category) categoryCombo.getSelectedItem();

                if (carId.isEmpty() || model.isEmpty()) {
                    showError("Please enter Car ID and Model");
                    return;
                }

                boolean success = rentalManager.updateCar(carId, model, category);
                if (success) {
                    showSuccess("Car updated successfully!");
                    refreshCarsTable(tableModel);
                    clearFields(carIdField, modelField);
                    // Auto-refresh dashboard if it's visible
                    if (tabbedPane.getSelectedComponent() == dashboardPanel) {
                        refreshDashboard();
                    }
                } else {
                    showError("Failed to update car. Car not found.");
                }
            } catch (Exception ex) {
                showError("Error updating car: " + ex.getMessage());
            }
        });

        removeButton.addActionListener(e -> {
            try {
                String carId = carIdField.getText().trim();

                if (carId.isEmpty()) {
                    showError("Please enter Car ID to remove");
                    return;
                }

                boolean success = rentalManager.removeCar(carId);
                if (success) {
                    showSuccess("Car removed successfully!");
                    refreshCarsTable(tableModel);
                    clearFields(carIdField, modelField);
                    // Auto-refresh dashboard if it's visible
                    if (tabbedPane.getSelectedComponent() == dashboardPanel) {
                        refreshDashboard();
                    }
                } else {
                    showError("Failed to remove car. Car not found or not available.");
                }
            } catch (Exception ex) {
                showError("Error removing car: " + ex.getMessage());
            }
        });

        updateStatusButton.addActionListener(e -> {
            try {
                String carId = carIdField.getText().trim();
                K2558793_AvailabilityStatus newStatus = (K2558793_AvailabilityStatus) statusCombo.getSelectedItem();

                if (carId.isEmpty()) {
                    showError("Please enter Car ID");
                    return;
                }

                boolean success = rentalManager.updateCarAvailability(carId, newStatus);
                if (success) {
                    showSuccess("Car status updated successfully!\nNew Status: " + newStatus);
                    refreshCarsTable(tableModel);

                    // Auto-refresh dashboard if it's visible
                    if (tabbedPane.getSelectedComponent() == dashboardPanel) {
                        refreshDashboard();
                    }
                } else {
                    showError("Failed to update car status. Car not found.");
                }
            } catch (Exception ex) {
                showError("Error updating car status: " + ex.getMessage());
            }
        });

        refreshButton.addActionListener(e -> {
            try {
                refreshCarsTable(tableModel);
                showSuccess("Cars list refreshed successfully!");
            } catch (Exception ex) {
                showError("Error refreshing cars list: " + ex.getMessage());
            }
        });

        // Table selection listener
        carsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && carsTable.getSelectedRow() != -1) {
                int selectedRow = carsTable.getSelectedRow();
                String selectedCarId = carsTable.getValueAt(selectedRow, 0).toString();
                carIdField.setText(selectedCarId);
                modelField.setText(carsTable.getValueAt(selectedRow, 1).toString());

                // Set category in combo box
                String categoryName = carsTable.getValueAt(selectedRow, 2).toString();
                for (K2558793_Category category : rentalManager.getAllCategories()) {
                    if (category.getDisplayName().equals(categoryName)) {
                        categoryCombo.setSelectedItem(category);
                        break;
                    }
                }

                // Set status in combo box
                String statusName = carsTable.getValueAt(selectedRow, 3).toString();
                for (K2558793_AvailabilityStatus status : K2558793_AvailabilityStatus.values()) {
                    if (status.name().equals(statusName)) {
                        statusCombo.setSelectedItem(status);
                        break;
                    }
                }
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Refreshes the car management panel
    private void refreshCarManagementPanel() {
        try {
            // Refresh the cars table in the car management panel
            Component[] components = carManagementPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) comp;
                    Component view = scrollPane.getViewport().getView();
                    if (view instanceof JTable) {
                        JTable table = (JTable) view;
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        refreshCarsTable(model);
                        break;
                    }
                }
            }
            showInfo("Car Management panel refreshed automatically");
        } catch (Exception e) {
            showError("Error refreshing car management panel: " + e.getMessage());
        }
    }

    // Creates customer management panel
    private JPanel createCustomerManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Registration"));

        JTextField customerIdField = new JTextField();
        customerIdField.setEditable(false);
        JTextField nicField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField emailField = new JTextField();

        formPanel.add(new JLabel("Customer ID (Auto-generated):"));
        formPanel.add(customerIdField);
        formPanel.add(new JLabel("NIC/Passport:"));
        formPanel.add(nicField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        JButton registerButton = new JButton("Register Customer");
        JButton refreshButton = new JButton("Refresh List");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(refreshButton);

        formPanel.add(new JLabel());
        formPanel.add(buttonPanel);

        // Customers table
        String[] columnNames = {"Customer ID", "Name", "NIC/Passport", "Contact", "Email"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable customersTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(customersTable);

        // Load customers into table
        refreshCustomersTable(tableModel);

        registerButton.addActionListener(e -> {
            try {
                String nic = nicField.getText().trim();
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();
                String email = emailField.getText().trim();

                if (nic.isEmpty() || name.isEmpty() || contact.isEmpty() || email.isEmpty()) {
                    showError("Please fill all fields");
                    return;
                }

                K2558793_Customer newCustomer = rentalManager.registerCustomer(nic, name, contact, email);
                if (newCustomer != null) {
                    showSuccess("Customer registered successfully!\nCustomer ID: " + newCustomer.getCustomerId());
                    customerIdField.setText(newCustomer.getCustomerId());
                    refreshCustomersTable(tableModel);
                    clearFields(nicField, nameField, contactField, emailField);
                    // Auto-refresh dashboard if it's visible
                    if (tabbedPane.getSelectedComponent() == dashboardPanel) {
                        refreshDashboard();
                    }
                } else {
                    showError("Registration failed. Customer with this NIC/Passport may already exist.");
                }
            } catch (Exception ex) {
                showError("Registration failed: " + ex.getMessage());
            }
        });

        refreshButton.addActionListener(e -> {
            try {
                refreshCustomersTable(tableModel);
                showSuccess("Customer list refreshed!");
            } catch (Exception ex) {
                showError("Error refreshing customer list: " + ex.getMessage());
            }
        });

        // Table selection listener
        customersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customersTable.getSelectedRow() != -1) {
                int selectedRow = customersTable.getSelectedRow();
                customerIdField.setText(customersTable.getValueAt(selectedRow, 0).toString());
                nameField.setText(customersTable.getValueAt(selectedRow, 1).toString());
                nicField.setText(customersTable.getValueAt(selectedRow, 2).toString());
                contactField.setText(customersTable.getValueAt(selectedRow, 3).toString());
                emailField.setText(customersTable.getValueAt(selectedRow, 4).toString());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Creates reservation panel
    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Make Reservation"));

        JComboBox<K2558793_Customer> customerCombo = new JComboBox<>();
        JComboBox<K2558793_Car> carCombo = new JComboBox<>();
        JTextField pickupDateField = new JTextField(dateFormat.format(new Date()));
        JTextField returnDateField = new JTextField(dateFormat.format(new Date(System.currentTimeMillis() + 86400000 * 5))); // 5 days later
        JTextField totalKmField = new JTextField("500");

        // Load customers and available cars
        refreshCustomerCombo(customerCombo);
        refreshAvailableCarsCombo(carCombo);

        formPanel.add(new JLabel("Customer:"));
        formPanel.add(customerCombo);
        formPanel.add(new JLabel("Available Cars:"));
        formPanel.add(carCombo);
        formPanel.add(new JLabel("Pickup Date (yyyy-mm-dd):"));
        formPanel.add(pickupDateField);
        formPanel.add(new JLabel("Return Date (yyyy-mm-dd):"));
        formPanel.add(returnDateField);
        formPanel.add(new JLabel("Total Kilometers:"));
        formPanel.add(totalKmField);

        JButton makeReservationButton = new JButton("Make Reservation");
        JButton refreshCarsButton = new JButton("Refresh Available Cars");
        JButton refreshCustomersButton = new JButton("Refresh Customers");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(makeReservationButton);
        buttonPanel.add(refreshCarsButton);
        buttonPanel.add(refreshCustomersButton);

        formPanel.add(new JLabel());
        formPanel.add(buttonPanel);

        makeReservationButton.addActionListener(e -> {
            try {
                K2558793_Customer customer = (K2558793_Customer) customerCombo.getSelectedItem();
                K2558793_Car car = (K2558793_Car) carCombo.getSelectedItem();

                if (customer == null || car == null) {
                    showError("Please select customer and car");
                    return;
                }

                Date pickupDate = dateFormat.parse(pickupDateField.getText());
                Date returnDate = dateFormat.parse(returnDateField.getText());
                int totalKm = Integer.parseInt(totalKmField.getText());

                K2558793_Reservation reservation = rentalManager.makeReservation(
                        customer.getCustomerId(), car.getCarId(), pickupDate, returnDate, totalKm
                );

                if (reservation != null) {
                    showSuccess("Reservation created successfully!\nBooking ID: " + reservation.getBookingId());
                    refreshAvailableCarsCombo(carCombo);

                    // Auto-refresh dashboard if it's visible
                    if (tabbedPane.getSelectedComponent() == dashboardPanel) {
                        refreshDashboard();
                    }
                } else {
                    showError("Reservation failed. Please check availability and booking rules.");
                }

            } catch (Exception ex) {
                showError("Reservation failed: " + ex.getMessage());
            }
        });

        refreshCarsButton.addActionListener(e -> {
            try {
                refreshAvailableCarsCombo(carCombo);
                showSuccess("Available cars list refreshed!");
            } catch (Exception ex) {
                showError("Error refreshing cars: " + ex.getMessage());
            }
        });

        refreshCustomersButton.addActionListener(e -> {
            try {
                refreshCustomerCombo(customerCombo);
                showSuccess("Customers list refreshed!");
            } catch (Exception ex) {
                showError("Error refreshing customers: " + ex.getMessage());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

        // Show available cars info
        JTextArea infoArea = new JTextArea(10, 60);
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoArea.setText(getAvailableCarsInfo());
        JScrollPane infoScrollPane = new JScrollPane(infoArea);
        panel.add(infoScrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Creates search reservations panel
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Search options
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchByIdButton = new JButton("Search by Booking ID");
        JButton searchByNameButton = new JButton("Search by Customer Name");
        JButton showAllButton = new JButton("Show All Reservations");
        JButton cancelReservationButton = new JButton("Cancel Reservation");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchByIdButton);
        searchPanel.add(searchByNameButton);
        searchPanel.add(showAllButton);
        searchPanel.add(cancelReservationButton);

        // Results table
        String[] columnNames = {"Booking ID", "Customer", "Car", "Pickup Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable resultsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(resultsTable);

        // Button actions
        searchByIdButton.addActionListener(e -> {
            try {
                String bookingId = searchField.getText().trim();
                if (bookingId.isEmpty()) {
                    showError("Please enter a booking ID to search");
                    return;
                }

                K2558793_Reservation reservation = rentalManager.searchReservationById(bookingId);
                tableModel.setRowCount(0); // Clear table
                if (reservation != null) {
                    addReservationToTable(tableModel, reservation);
                } else {
                    showError("No reservation found with ID: " + bookingId);
                }
            } catch (Exception ex) {
                showError("Search failed: " + ex.getMessage());
            }
        });

        searchByNameButton.addActionListener(e -> {
            try {
                String name = searchField.getText().trim();
                if (name.isEmpty()) {
                    showError("Please enter a customer name to search");
                    return;
                }

                List<K2558793_Reservation> reservations = rentalManager.searchReservationByName(name);
                tableModel.setRowCount(0); // Clear table
                if (!reservations.isEmpty()) {
                    for (K2558793_Reservation reservation : reservations) {
                        addReservationToTable(tableModel, reservation);
                    }
                } else {
                    showError("No reservations found for: " + name);
                }
            } catch (Exception ex) {
                showError("Search failed: " + ex.getMessage());
            }
        });

        showAllButton.addActionListener(e -> {
            try {
                List<K2558793_Reservation> reservations = rentalManager.getAllReservations();
                tableModel.setRowCount(0); // Clear table
                if (!reservations.isEmpty()) {
                    for (K2558793_Reservation reservation : reservations) {
                        addReservationToTable(tableModel, reservation);
                    }
                } else {
                    showInfo("No reservations found.");
                }
            } catch (Exception ex) {
                showError("Error loading reservations: " + ex.getMessage());
            }
        });

        cancelReservationButton.addActionListener(e -> {
            try {
                int selectedRow = resultsTable.getSelectedRow();
                if (selectedRow == -1) {
                    showError("Please select a reservation to cancel");
                    return;
                }

                String bookingId = resultsTable.getValueAt(selectedRow, 0).toString();
                boolean success = rentalManager.cancelReservation(bookingId);
                if (success) {
                    showSuccess("Reservation cancelled successfully!");
                    refreshDashboard();

                    // Refresh the table
                    showAllButton.doClick();
                } else {
                    showError("Failed to cancel reservation. Cancellation may not be allowed after 2 days.");
                }
            } catch (Exception ex) {
                showError("Cancellation failed: " + ex.getMessage());
            }
        });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Creates invoice generation panel
    private JPanel createInvoicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField bookingIdField = new JTextField(20);
        JButton generateButton = new JButton("Generate Invoice");

        inputPanel.add(new JLabel("Booking ID:"));
        inputPanel.add(bookingIdField);
        inputPanel.add(generateButton);

        JTextArea invoiceArea = new JTextArea(20, 60);
        invoiceArea.setEditable(false);
        invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(invoiceArea);

        generateButton.addActionListener(e -> {
            try {
                String bookingId = bookingIdField.getText().trim();
                if (bookingId.isEmpty()) {
                    showError("Please enter a booking ID");
                    return;
                }

                K2558793_Reservation reservation = rentalManager.searchReservationById(bookingId);
                if (reservation != null) {
                    K2558793_Invoice invoice = rentalManager.generateInvoice(reservation);
                    invoiceArea.setText(invoice.getInvoiceDetails());
                } else {
                    invoiceArea.setText("No reservation found with ID: " + bookingId);
                    showError("No reservation found with ID: " + bookingId);
                }
            } catch (Exception ex) {
                showError("Error generating invoice: " + ex.getMessage());
                invoiceArea.setText("Error generating invoice: " + ex.getMessage());
            }
        });

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /*
     * UTILITY METHODS
     * Refreshes cars table with current data
     */
    private void refreshCarsTable(DefaultTableModel tableModel) {
        try {
            tableModel.setRowCount(0);
            for (K2558793_Car car : rentalManager.getAllCars()) {
                tableModel.addRow(new Object[]{
                        car.getCarId(),
                        car.getModel(),
                        car.getCategory().getDisplayName(),
                        car.getAvailabilityStatus()
                });
            }
        } catch (Exception e) {
            showError("Error refreshing cars table: " + e.getMessage());
        }
    }

    // Refreshes customers table with current data
    private void refreshCustomersTable(DefaultTableModel tableModel) {
        try {
            tableModel.setRowCount(0);
            List<K2558793_Customer> customers = rentalManager.getAllCustomers();

            for (K2558793_Customer customer : customers) {
                tableModel.addRow(new Object[]{
                        customer.getCustomerId(),
                        customer.getName(),
                        customer.getNicPassport(),
                        customer.getContactNumber(),
                        customer.getEmail()
                });
            }
        } catch (Exception e) {
            showError("Error refreshing customers table: " + e.getMessage());
        }
    }

    // Refreshes customer combo box
    private void refreshCustomerCombo(JComboBox<K2558793_Customer> combo) {
        try {
            combo.removeAllItems();
            List<K2558793_Customer> customers = rentalManager.getAllCustomers();

            for (K2558793_Customer customer : customers) {
                combo.addItem(customer);
            }
        } catch (Exception e) {
            showError("Error refreshing customer combo: " + e.getMessage());
        }
    }

    // Refreshes available cars combo box
    private void refreshAvailableCarsCombo(JComboBox<K2558793_Car> combo) {
        try {
            combo.removeAllItems();
            for (K2558793_Car car : rentalManager.getAvailableCars()) {
                combo.addItem(car);
            }
        } catch (Exception e) {
            showError("Error refreshing cars combo: " + e.getMessage());
        }
    }

    // Adds reservation to table model
    private void addReservationToTable(DefaultTableModel tableModel, K2558793_Reservation reservation) {
        tableModel.addRow(new Object[]{
                reservation.getBookingId(),
                reservation.getCustomer().getName(),
                reservation.getCar().getModel(),
                dateFormat.format(reservation.getPickupDate()),
                reservation.getBookingStatus()
        });
    }

    // Gets information about available cars
    private String getAvailableCarsInfo() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Available Cars for Reservation ===\n\n");

            List<K2558793_Car> availableCars = rentalManager.getAvailableCars();
            if (availableCars.isEmpty()) {
                sb.append("No cars available for reservation at the moment.\n");
            } else {
                for (K2558793_Car car : availableCars) {
                    sb.append(String.format("• %s - %s\n", car.getCarId(), car.getModel()));
                    sb.append(String.format("  Category: %s\n", car.getCategory().getDisplayName()));
                    sb.append(String.format("  Daily Rate: LKR %.2f\n", car.getCategory().getDailyRentalFee()));
                    sb.append(String.format("  Free KM/Day: %d km\n", car.getCategory().getFreeKmPerDay()));
                    sb.append(String.format("  Extra KM Charge: LKR %.2f/km\n", car.getCategory().getExtraKmCharge()));
                    sb.append(String.format("  Tax Rate: %.1f%%\n\n", car.getCategory().getTaxRate() * 100));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error loading available cars information: " + e.getMessage();
        }
    }

    // Clears multiple text fields
    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    // Utility methods for showing messages
    private void showError(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // Shows the main GUI
    public void show() {
        mainFrame.setVisible(true);
    }
}