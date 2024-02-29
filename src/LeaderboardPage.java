import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeaderboardPage extends JFrame {
    private DatabaseManager databaseManager;
    private JTextArea leaderboardTextArea;

    public LeaderboardPage(DatabaseManager manager) {
        this.databaseManager = manager;

        setTitle("Leaderboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        leaderboardTextArea = new JTextArea();
        leaderboardTextArea.setEditable(false);
        panel.add(new JScrollPane(leaderboardTextArea), BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Main Page");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PongMainPage(databaseManager); // Pass the databaseManager to the main page
                dispose();
            }
        });
        panel.add(backButton, BorderLayout.SOUTH);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);

        // Display leaderboard details
        displayLeaderboard();
    }

    private void displayLeaderboard() {
        try {
            // Create the rankings view
            databaseManager.createRankingsView();

            // Fetch data from the leaderboard table
            Connection connection = databaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM rankings");

            StringBuilder leaderboardText = new StringBuilder("Leaderboard:\n");
            int rank = 1;
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int wins = resultSet.getInt("wins");
                int losses = resultSet.getInt("losses");
                leaderboardText.append(rank).append(". ").append(username).append(" - Wins: ").append(wins)
                        .append(", Losses: ").append(losses).append("\n");
                rank++;
            }
            leaderboardTextArea.setText(leaderboardText.toString());

            resultSet.close();
            statement.close();
            // Note: Do not close the connection here to avoid premature closure
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching leaderboard data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
