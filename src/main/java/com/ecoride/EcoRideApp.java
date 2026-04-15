package com.ecoride;

import com.ecoride.ui.EcoRideGUI;
import javax.swing.*;

/*
     * Main application class for EcoRide Car Rental System
     * This is the entry point of the application
 */

public class EcoRideApp {


//  Main method - application entry point

    public static void main(String[] args) {

    // Use SwingUtilities to ensure thread-safe GUI operations

        SwingUtilities.invokeLater(() -> {
            try {

            // Set system look and feel for native appearance

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("Error setting look and feel: " + e.getMessage());
            }

            // Create and display the main GUI

            EcoRideGUI gui = new EcoRideGUI();
            gui.show();

            System.out.println("EcoRide Car Rental System started successfully!");
            System.out.println("Application is now running...");
        });
    }
}