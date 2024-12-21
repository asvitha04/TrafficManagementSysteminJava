/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trafficmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDashboard extends JPanel {
    private String userId;

    public UserDashboard(String userId) {
        this.userId = userId;
        setLayout(new BorderLayout());

        // Header
        JLabel welcomeLabel = new JLabel("User Dashboard", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Add padding
        add(welcomeLabel, BorderLayout.NORTH);

        // Main Content Panel
        add(createUserDashboardPanel(), BorderLayout.CENTER);
    }

    // Method to create the user dashboard panel
    private JPanel createUserDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 15, 15)); // Increased spacing

        // Button 1: View Vehicle Details
        JButton viewVehicleDetailsButton = new JButton("View Vehicle Details");
        viewVehicleDetailsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        viewVehicleDetailsButton.setBackground(new Color(173, 216, 230)); // Light blue background
        viewVehicleDetailsButton.setForeground(Color.BLACK);
        viewVehicleDetailsButton.setFocusPainted(false);
        viewVehicleDetailsButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        viewVehicleDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewVehicleDetails();
            }
        });

        // Button 2: Pay Fine
        JButton payFineButton = new JButton("Pay Fine");
        payFineButton.setFont(new Font("Arial", Font.PLAIN, 16));
        payFineButton.setBackground(new Color(240, 128, 128)); // Light coral background
        payFineButton.setForeground(Color.BLACK);
        payFineButton.setFocusPainted(false);
        payFineButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        payFineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                payFine();
            }
        });

        panel.add(viewVehicleDetailsButton);
        panel.add(payFineButton);

        // Add padding and borders around the panel
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        return panel;
    }

    // Method to handle viewing vehicle details
    private void viewVehicleDetails() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM vehicles WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder vehicleDetails = new StringBuilder();

            while (rs.next()) {
                vehicleDetails.append("Vehicle ID: ").append(rs.getString("vehicle_id"))
                        .append("\nModel: ").append(rs.getString("model"))
                        .append("\nLicense Plate: ").append(rs.getString("license_plate"))
                        .append("\nDetails: ").append(rs.getString("details"))
                        .append("\n\n");
            }

            if (vehicleDetails.length() > 0) {
                JOptionPane.showMessageDialog(this, vehicleDetails.toString(), "Vehicle Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No vehicles found for this user.", "No Data", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while retrieving vehicle details. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Method to handle paying fines
private void payFine() {
    String fineId = JOptionPane.showInputDialog(this, "Enter Fine ID:");

    if (fineId == null || fineId.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Fine ID cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false); // Begin transaction

        // Check if the fine ID is valid
        String checkQuery = "SELECT * FROM fines WHERE fine_id = ?"; // Correct column name
        PreparedStatement checkPstmt = conn.prepareStatement(checkQuery);
        checkPstmt.setInt(1, Integer.parseInt(fineId)); // Set parameter for checking fine
        ResultSet rs = checkPstmt.executeQuery();

        if (rs.next()) {
            // Fine ID is valid, proceed with payment
            String payQuery = "UPDATE fines SET status = 'paid' WHERE fine_id = ?"; // Correct column name
            PreparedStatement payPstmt = conn.prepareStatement(payQuery);
            payPstmt.setInt(1, Integer.parseInt(fineId)); // Set parameter for updating fine status
            int rowsUpdated = payPstmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Payment successful, now remove from violations table
                String removeQuery = "DELETE FROM violations WHERE violation_id = ?"; // Correct column name
                PreparedStatement removePstmt = conn.prepareStatement(removeQuery);
                removePstmt.setInt(1, Integer.parseInt(fineId)); // Set parameter for removing violation
                int rowsDeleted = removePstmt.executeUpdate();

                // Commit transaction if both updates are successful
                if (rowsDeleted > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Fine paid successfully and violation removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    conn.rollback(); // Rollback transaction if violation deletion fails
                    JOptionPane.showMessageDialog(this, "Payment successful, but failed to remove violation.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                conn.rollback(); // Rollback transaction if payment update fails
                JOptionPane.showMessageDialog(this, "Failed to update fine status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Fine ID. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(this, "An error occurred while processing your request. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true); // Reset to default commit behavior
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}

}
