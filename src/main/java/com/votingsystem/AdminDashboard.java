package com.votingsystem;

import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {
    private JTable candidateTable;
    private DefaultTableModel tableModel;
    private JTextField candidateNameField;
    private JButton addBtn, removeBtn, logoutBtn;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Manage Candidates & Votes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Include ID column (hidden later)
        tableModel = new DefaultTableModel(new Object[]{"ID", "Candidate", "Votes"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        candidateTable = new JTable(tableModel);
        candidateTable.getColumnModel().getColumn(0).setMinWidth(0);
        candidateTable.getColumnModel().getColumn(0).setMaxWidth(0);
        candidateTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(candidateTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        candidateNameField = new JTextField(20);
        addBtn = new JButton("Add Candidate");
        removeBtn = new JButton("Remove Selected");
        logoutBtn = new JButton("Logout");

        removeBtn.setVisible(true);

        bottomPanel.add(new JLabel("Candidate Name:"));
        bottomPanel.add(candidateNameField);
        bottomPanel.add(addBtn);
        bottomPanel.add(removeBtn);
        bottomPanel.add(logoutBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);
        loadCandidates();

        addBtn.addActionListener(e -> addCandidate());
        removeBtn.addActionListener(e -> deleteSelectedCandidate());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginVoter(); // go back to shared login
        });

        setVisible(true);
    }

    private void loadCandidates() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            String query = "SELECT c.id, c.name, COUNT(v.candidate_name) AS votes " +
                           "FROM candidates c LEFT JOIN votes v ON c.name = v.candidate_name " +
                           "GROUP BY c.id, c.name ORDER BY votes DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("votes")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addCandidate() {
        String name = candidateNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Candidate name cannot be empty");
            return;
        }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement check = con.prepareStatement("SELECT * FROM candidates WHERE name = ?");
            check.setString(1, name);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Candidate already exists");
                return;
            }

            PreparedStatement ps = con.prepareStatement("INSERT INTO candidates (name) VALUES (?)");
            ps.setString(1, name);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Candidate added successfully");
            candidateNameField.setText("");
            loadCandidates();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteSelectedCandidate() {
        int selectedRow = candidateTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a candidate to remove.");
            return;
        }

        int candidateId = (int) tableModel.getValueAt(selectedRow, 0);
        String candidateName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete \"" + candidateName + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM candidates WHERE id=?");
                ps.setInt(1, candidateId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Candidate deleted.");
                loadCandidates();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
