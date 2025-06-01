package com.votingsystem;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginVoter extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn, registerBtn;

    public LoginVoter() {
        setTitle("Voter Login");
        setSize(500, 400); // Bigger window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(248, 248, 255));

        JLabel titleLabel = new JLabel("Welcome to Online Voting");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBounds(120, 30, 300, 30);
        panel.add(titleLabel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setBounds(80, 100, 100, 25);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(180, 100, 200, 30);
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setBounds(80, 150, 100, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(180, 150, 200, 30);
        panel.add(passwordField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(180, 200, 90, 35);
        loginBtn.setBackground(new Color(60, 179, 113));
        loginBtn.setForeground(Color.WHITE);
        panel.add(loginBtn);

        registerBtn = new JButton("Register");
        registerBtn.setBounds(290, 200, 90, 35);
        registerBtn.setBackground(new Color(100, 149, 237));
        registerBtn.setForeground(Color.WHITE);
        panel.add(registerBtn);

        loginBtn.addActionListener(e -> checkLogin());
        registerBtn.addActionListener(e -> {
            dispose(); // Close login window
            new RegisterVoter(); // Open registration form
        });

        add(panel);
        setVisible(true);
    }

    private void checkLogin() {
        String username = emailField.getText(); // was userText
        String password = String.valueOf(passwordField.getPassword()); // was passText

        try (Connection con = DBConnection.getConnection()) {
        PreparedStatement ps = con.prepareStatement("SELECT id, name, is_admin FROM voters WHERE email=? AND password=?");
        ps.setString(1, username);
        ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
    int userId = rs.getInt("id");
    String name = rs.getString("name");
    boolean isAdmin = rs.getBoolean("is_admin");

    if (isAdmin) {
        JOptionPane.showMessageDialog(this, "Welcome Admin: " + name);
        dispose();
        new AdminDashboard(); // Admin window
    } else {
        JOptionPane.showMessageDialog(this, "Welcome Voter: " + name);
        dispose();
        new VotePage(userId, name); // Voter window
    }
} else {
    JOptionPane.showMessageDialog(this, "Invalid email or password.");
}

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginVoter::new);
    }
}
