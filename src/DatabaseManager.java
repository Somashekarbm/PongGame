import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection connection;
    private String jdbcURL = "jdbc:mysql://localhost:3306/ponggamedb";
    private String username = "somu";
    private String password = "somu2002@";

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayer(String playerName) {
        try {
            String query = "INSERT INTO users (username, wins, losses) VALUES (?, 0, 0)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerName);
            preparedStatement.executeUpdate();
            updateLeaderboard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWins(String playerName) {
        try {
            String query = "UPDATE users SET wins = wins + 1 WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerName);
            preparedStatement.executeUpdate();
            updateLeaderboard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLosses(String playerName) {
        try {
            String query = "UPDATE users SET losses = losses + 1 WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerName);
            preparedStatement.executeUpdate();
            updateLeaderboard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLeaderboard() {
        List<String> leaderboard = new ArrayList<>();
        try {
            String query = "SELECT username FROM leaderboard ORDER BY wins DESC, losses ASC LIMIT 10";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String playerName = resultSet.getString("username");
                leaderboard.add(playerName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }

    private void updateLeaderboard() {
        try {
            // Clear existing data
            String clearQuery = "TRUNCATE TABLE leaderboard";
            PreparedStatement clearStatement = connection.prepareStatement(clearQuery);
            clearStatement.executeUpdate();

            // Insert updated data
            String insertQuery = "INSERT INTO leaderboard (user_id, username, wins, losses) SELECT id, username, wins, losses FROM users ORDER BY wins DESC, losses ASC LIMIT 10";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
