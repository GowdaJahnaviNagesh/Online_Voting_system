package com.votingsystem;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterVoter extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JButton registerBtn, backBtn;

    public RegisterVoter() {
        setTitle("Register New Voter");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 255, 255));

        JLabel titleLabel = new JLabel("Create Your Voter Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBounds(100, 40, 300, 30);
        panel.add(titleLabel);

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        nameLabel.setBounds(80, 120, 100, 25);
        panel.add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(180, 120, 200, 30);
        panel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setBounds(80, 170, 100, 25);
        panel.add(emailLabel);
        emailField = new JTextField();
        emailField.setBounds(180, 170, 200, 30);
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setBounds(80, 220, 100, 25);
        panel.add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(180, 220, 200, 30);
        panel.add(passwordField);

        registerBtn = new JButton("Register");
        registerBtn.setBounds(180, 280, 90, 35);
        registerBtn.setBackground(new Color(34, 139, 34));
        registerBtn.setForeground(Color.WHITE);
        panel.add(registerBtn);

        backBtn = new JButton("Back");
        backBtn.setBounds(290, 280, 90, 35);
        backBtn.setBackground(new Color(220, 20, 60));
        backBtn.setForeground(Color.WHITE);
        panel.add(backBtn);

        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginVoter();
        });

        add(panel);
        setVisible(true);
    }

    private void register() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO voters (name, email, password) VALUES (?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration successful!");
            dispose();
            new LoginVoter();
        } catch (SQLIntegrityConstraintViolationException dup) {
            JOptionPane.showMessageDialog(this, "Email already registered.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
