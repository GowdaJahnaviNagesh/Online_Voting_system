package com.votingsystem;

import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class VotePage extends JFrame {
    private int voterId;
    private String voterName;
    private JComboBox<String> candidateBox;
    private JButton voteBtn;

    public VotePage(int voterId, String voterName) {
        this.voterId = voterId;
        this.voterName = voterName;

        setTitle("Cast Your Vote");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 255, 250));

        JLabel welcome = new JLabel("Welcome, " + voterName);
        welcome.setBounds(30, 20, 300, 25);
        welcome.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcome);

        JLabel selectLabel = new JLabel("Select Candidate:");
        selectLabel.setBounds(50, 80, 150, 25);
        panel.add(selectLabel);

        candidateBox = new JComboBox<>();
        loadCandidates();
        candidateBox.setBounds(170, 80, 150, 25);
        panel.add(candidateBox);

        voteBtn = new JButton("Vote");
        voteBtn.setBounds(140, 150, 120, 35);
        voteBtn.setBackground(new Color(255, 99, 71));
        voteBtn.setForeground(Color.WHITE);
        voteBtn.setFocusPainted(false);
        panel.add(voteBtn);

        voteBtn.addActionListener(e -> castVote());
        JButton viewResultBtn = new JButton("View Results");
       viewResultBtn.setBounds(130, 200, 140, 30); // Within visible height

        panel.add(viewResultBtn);

        viewResultBtn.addActionListener(e -> new ResultPage());


        add(panel);
        setVisible(true);
    }

    private void loadCandidates() {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM candidates");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                candidateBox.addItem(rs.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void castVote() {
        String selected = (String) candidateBox.getSelectedItem();

        try (Connection con = DBConnection.getConnection()) {
            // Check if already voted
            PreparedStatement check = con.prepareStatement("SELECT * FROM votes WHERE voter_id = ?");
            check.setInt(1, voterId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "You have already voted.");
                return;
            }

            // Insert vote
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO votes (voter_id, candidate_name) VALUES (?, ?)");
            ps.setInt(1, voterId);
            ps.setString(2, selected);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Vote cast successfully!");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
