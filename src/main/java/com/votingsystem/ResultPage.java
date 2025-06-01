package com.votingsystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ResultPage extends JFrame {
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel winnerLabel;

    public ResultPage() {
        setTitle("Election Results");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table model
        tableModel = new DefaultTableModel(new Object[]{"Candidate", "Votes"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultTable = new JTable(tableModel);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Winner label
        winnerLabel = new JLabel("Winner: ", SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        winnerLabel.setForeground(Color.BLUE);
        add(winnerLabel, BorderLayout.SOUTH);

        loadResults();
        setVisible(true);
    }

    private void loadResults() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT c.name, COUNT(v.id) AS votes " +
                         "FROM candidates c LEFT JOIN votes v ON c.name = v.candidate_name " +
                         "GROUP BY c.name ORDER BY votes DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            int maxVotes = -1;
            String winner = "No votes yet";

            while (rs.next()) {
                String name = rs.getString("name");
                int votes = rs.getInt("votes");
                tableModel.addRow(new Object[]{name, votes});

                if (votes > maxVotes) {
                    maxVotes = votes;
                    winner = name;
                }
            }

            winnerLabel.setText("Winner: " + winner + " (" + maxVotes + " votes)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
