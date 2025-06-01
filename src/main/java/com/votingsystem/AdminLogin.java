package com.votingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;

    public AdminLogin() {
        setTitle("Admin Login");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(120, 30, 160, 25);
        panel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 70, 160, 25);
        panel.add(passwordField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(120, 110, 160, 30);
        loginBtn.setBackground(new Color(70,130,180));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> checkLogin());

        add(panel);
        setVisible(true);
    }

    private void checkLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Simple hardcoded check (replace with DB check if you want)
        if ("admin".equals(username) && "admin123".equals(password)) {
            dispose();
            new AdminDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid admin credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLogin());
    }
}
