
package trafficmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class TrafficManagementSystem extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JTextField phoneField;
    private JTextField cityField;
    private JTextField areaField;
    private JTextField ageField;
    private JTextField captchaField;
    private String captchaText;

    public TrafficManagementSystem() {
        setTitle("Traffic Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set the background image
        setContentPane(new BackgroundPanel());

        // Initialize the login panel
        showLoginPanel();
    }

    private void showLoginPanel() {
        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Adding padding

        // Customize labels and fields
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setForeground(Color.BLACK);
        userIdField = new JTextField();
        loginPanel.add(userIdLabel);
        loginPanel.add(userIdField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        passwordField = new JPasswordField();
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.BLACK);
        roleComboBox = new JComboBox<>(new String[]{"User", "Admin"});
        loginPanel.add(roleLabel);
        loginPanel.add(roleComboBox);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0, 153, 0)); // Vibrant green
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        loginPanel.add(loginButton);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBackground(new Color(0, 102, 204)); // Vibrant blue
        signUpButton.setForeground(Color.WHITE);
        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSignUpPanel();
            }
        });
        loginPanel.add(signUpButton);

        getContentPane().removeAll();
        add(loginPanel, BorderLayout.CENTER);
        validate();
    }

    private void login() {
        String userId = userIdField.getText();
        String password = new String(passwordField.getPassword());
        String role = roleComboBox.getSelectedItem().toString();

        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE user_id = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if (role.equals("Admin")) {
                    getContentPane().removeAll();
                    add(new AdminDashboard(), BorderLayout.CENTER);
                    validate();
                } else {
                    getContentPane().removeAll();
                    add(new UserDashboard(userId), BorderLayout.CENTER);
                    validate();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while processing your request. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showSignUpPanel() {
        JPanel signUpPanel = new JPanel(new GridLayout(10, 2, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
            }
        };
        signUpPanel.setOpaque(false);
        signUpPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Adding padding
        
        // Customize sign-up panel
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(Color.BLACK);
        JTextField nameField = new JTextField();
        signUpPanel.add(nameLabel);
        signUpPanel.add(nameField);

        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setForeground(Color.BLACK);
        JTextField newUserIdField = new JTextField();
        signUpPanel.add(userIdLabel);
        signUpPanel.add(newUserIdField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.BLACK);
        JPasswordField newPasswordField = new JPasswordField();
        signUpPanel.add(passwordLabel);
        signUpPanel.add(newPasswordField);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setForeground(Color.BLACK);
        phoneField = new JTextField();
        signUpPanel.add(phoneLabel);
        signUpPanel.add(phoneField);

        JLabel cityLabel = new JLabel("City:");
        cityLabel.setForeground(Color.BLACK);
        cityField = new JTextField();
        signUpPanel.add(cityLabel);
        signUpPanel.add(cityField);

        JLabel areaLabel = new JLabel("Area:");
        areaLabel.setForeground(Color.BLACK);
        areaField = new JTextField();
        signUpPanel.add(areaLabel);
        signUpPanel.add(areaField);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setForeground(Color.BLACK);
        ageField = new JTextField();
        signUpPanel.add(ageLabel);
        signUpPanel.add(ageField);

        JLabel captchaLabel = new JLabel("Captcha:");
        captchaLabel.setForeground(Color.BLACK);
        captchaText = generateCaptcha();
        JLabel captchaDisplay = new JLabel("CAPTCHA: " + captchaText);
        captchaDisplay.setForeground(Color.BLACK);
        signUpPanel.add(captchaLabel);
        signUpPanel.add(captchaDisplay);

        captchaField = new JTextField();
        signUpPanel.add(captchaField);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 153, 153)); // Vibrant teal
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerUser(newUserIdField.getText(), new String(newPasswordField.getPassword()), nameField.getText());
            }
        });
        signUpPanel.add(registerButton);

        JButton backButton = new JButton("Back");
        backButton.setBackground(new Color(204, 0, 0)); // Vibrant red
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginPanel();
            }
        });
        signUpPanel.add(backButton);

        getContentPane().removeAll();
        add(signUpPanel, BorderLayout.CENTER);
        validate();
    }

    private void registerUser(String userId, String password, String name) {
        String phone = phoneField.getText();
        String city = cityField.getText();
        String area = areaField.getText();
        String age = ageField.getText();
        String captchaInput = captchaField.getText();

        if (userId.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty() || city.isEmpty() || area.isEmpty() || age.isEmpty() || captchaInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!captchaInput.equals(captchaText)) {
            JOptionPane.showMessageDialog(this, "Invalid CAPTCHA. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (user_id, password, name, phone, city, area, age) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            pstmt.setString(3, name); // Set name
            pstmt.setString(4, phone);
            pstmt.setString(5, city);
            pstmt.setString(6, area);
            pstmt.setString(7, age);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful. Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            showLoginPanel();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Fine paid successfully ","", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String generateCaptcha() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder captcha = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            captcha.append(characters.charAt(random.nextInt(characters.length())));
        }

        return captcha.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TrafficManagementSystem().setVisible(true);
            }
        });
    }

    // Background panel class for setting background image
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = new ImageIcon("C:\\Users\\ASVITHA\\Downloads\\project\\51310290_m-660x396.jpg").getImage(); // Replace with your image path
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}
