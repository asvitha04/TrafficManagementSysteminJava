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

public class AdminDashboard extends JPanel {
    public AdminDashboard() {
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLACK);
        add(welcomeLabel, BorderLayout.NORTH);

        add(createAdminDashboardPanel(), BorderLayout.CENTER);
    }

    private JPanel createAdminDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("User Name:");
        JTextField nameField = new JTextField();

        JLabel violationTypeLabel = new JLabel("Violation Type:");
        String[] violationTypes = {"Speeding", "Signal Jumping", "Parking Violation", "No Helmet"};
        JComboBox<String> violationTypeComboBox = new JComboBox<>(violationTypes);

        JLabel violationDetailsLabel = new JLabel("Violation Details:");
        JTextField violationDetailsField = new JTextField();

        JLabel updateLabel = new JLabel("Update Violation (by ID):");
        JTextField updateIdField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(violationTypeLabel);
        formPanel.add(violationTypeComboBox);
        formPanel.add(violationDetailsLabel);
        formPanel.add(violationDetailsField);
        formPanel.add(updateLabel);
        formPanel.add(updateIdField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton reportViolationsButton = new JButton("Report Violation");
        reportViolationsButton.setBackground(new Color(0, 153, 0)); // Vibrant green
        reportViolationsButton.setForeground(Color.WHITE);
        reportViolationsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String violationType = (String) violationTypeComboBox.getSelectedItem();
                String violationDetails = violationDetailsField.getText();
                reportViolation(name, violationType, violationDetails);
            }
        });

        JButton updateViolationButton = new JButton("Update Violation");
        updateViolationButton.setBackground(new Color(0, 102, 204)); // Vibrant blue
        updateViolationButton.setForeground(Color.WHITE);
        updateViolationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = updateIdField.getText();
                String violationType = (String) violationTypeComboBox.getSelectedItem();
                String violationDetails = violationDetailsField.getText();
                updateViolation(id, violationType, violationDetails);
            }
        });

        buttonPanel.add(reportViolationsButton);
        buttonPanel.add(updateViolationButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void reportViolation(String name, String violationType, String violationDetails) {
    if (name == null || name.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try (Connection conn = DatabaseConnection.getConnection()) {
        // Find the phone number associated with the name
        String findPhoneQuery = "SELECT phone FROM users WHERE name = ?";
        String phoneNumber = "UNKNOWN";

        try (PreparedStatement findPhoneStmt = conn.prepareStatement(findPhoneQuery)) {
            findPhoneStmt.setString(1, name);
            try (ResultSet rs = findPhoneStmt.executeQuery()) {
                if (rs.next()) {
                    phoneNumber = rs.getString("phone");
                }
            }
        }

        // Insert violation into the violations table
        String insertQuery = "INSERT INTO violations (phone_number, violation_type, violation_details) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, phoneNumber);
            pstmt.setString(2, violationType);
            pstmt.setString(3, violationDetails);
            pstmt.executeUpdate();
        }

        // Now insert a corresponding fine into the fines table
        String insertFineQuery = "INSERT INTO fines (phone_number, violation_type, status) VALUES (?, ?, 'unpaid')";
        try (PreparedStatement finePstmt = conn.prepareStatement(insertFineQuery)) {
            finePstmt.setString(1, phoneNumber);
            finePstmt.setString(2, violationType);
            finePstmt.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Violation reported successfully and fine generated.", "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error reporting violation and generating fine.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void updateViolation(String violationId, String violationType, String violationDetails) {
        if (violationId == null || violationId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Violation ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String updateQuery = "UPDATE violations SET violation_type = ?, violation_details = ? WHERE violation_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, violationType);
                pstmt.setString(2, violationDetails);
                pstmt.setString(3, violationId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Violation updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Violation ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating violation.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(new AdminDashboard());
        frame.setVisible(true);
    }
}



