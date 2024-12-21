/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TrafficManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Payment extends JFrame {
    private String userId;

    public Payment(String userId) {
        this.userId = userId;
        setTitle("Payment Portal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create and set up the panel
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Define vibrant colors
        Color backgroundColor = new Color(173, 216, 230); // Light Blue
        Color borderColor = new Color(34, 139, 34); // Green
        Color labelColor = new Color(0, 102, 204); // Dark Blue
        Color textColor = Color.BLACK; // Standard Black
        Color buttonColor = new Color(0, 204, 102); // Green
        Color buttonTextColor = Color.WHITE;

        // Set background color of the panel
        paymentPanel.setBackground(backgroundColor);
        paymentPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(borderColor, 2), "Manage Penalties", 0, 0, new Font("Arial", Font.BOLD, 16), borderColor));

        // Add components to the panel
        JLabel penaltyIdLabel = new JLabel("Violation ID:");
        penaltyIdLabel.setForeground(labelColor);
        JTextField penaltyIdField = new JTextField(15);
        penaltyIdField.setBackground(Color.WHITE);
        penaltyIdField.setForeground(textColor);

        JLabel amountLabel = new JLabel("Fine Amount:");
        amountLabel.setForeground(labelColor);
        JTextField amountField = new JTextField(15);
        amountField.setBackground(Color.WHITE);
        amountField.setForeground(textColor);

        JButton payFineButton = new JButton("Pay Fine");
        payFineButton.setBackground(buttonColor);
        payFineButton.setForeground(buttonTextColor);
        payFineButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Add components to the panel with GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        paymentPanel.add(penaltyIdLabel, gbc);

        gbc.gridx = 1;
        paymentPanel.add(penaltyIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        paymentPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        paymentPanel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        paymentPanel.add(payFineButton, gbc);

        // Action listener for the Pay Fine button
        payFineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String violationId = penaltyIdField.getText();
                String amount = amountField.getText();
                payFine(violationId, amount);
            }
        });

        // Add panel to the frame
        add(paymentPanel);
    }

    private void payFine(String violationId, String amount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check the current penalty amount
            String query = "SELECT penalty FROM violations WHERE violation_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, violationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double penalty = rs.getDouble("penalty");
                double paidAmount = Double.parseDouble(amount);

                if (paidAmount >= penalty) {
                    // Full fine amount is paid, delete the penalty
                    deletePenalty(violationId);
                } else {
                    JOptionPane.showMessageDialog(this, "Paid amount is less than the total penalty.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No violation found with the given ID.");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage());
        }
    }

    private void deletePenalty(String violationId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM violations WHERE violation_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, violationId);

            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "Penalty deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No violation found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting penalty: " + e.getMessage());
        }
    }
}
