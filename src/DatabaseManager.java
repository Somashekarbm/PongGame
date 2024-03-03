import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String jdbcURL, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPlayer(String playerName) {
        try {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerName);
            preparedStatement.setString(2, "default_password");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateWins(String playerName) {
        try {
            String query = "UPDATE leaderboard SET wins = wins + 1 WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLosses(String playerName) {
        try {
            String query = "UPDATE leaderboard SET losses = losses + 1 WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, playerName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // for now i have set the status to null,later lets implement the status to
    // store whether the user has won/lost during that particular game.
    public void insertGameHistory(String username) throws SQLException {
        String query = "INSERT INTO gamehistory (user_id, timestamp, status) VALUES (?, CURRENT_TIMESTAMP, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int userId = getUserIdByUsername(username);
            statement.setInt(1, userId);
            statement.setString(2, "Pending"); // Set initial status to "Pending"
            statement.executeUpdate();
        }
    }

    public int getUserIdByUsername(String username) throws SQLException {
        int userId = -1;
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("user_id");
                }
            }
        }
        return userId;
    }

    public void updateGameHistory(String username, String status) throws SQLException {
        String query = "UPDATE gamehistory SET status = ? WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    public void createRankingsView() {
        try {
            // Check if the rankings view already exists
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "rankings", null);
            if (resultSet.next()) {
                // The rankings view already exists, you can choose to drop it before creating
                // it again
                destroyRankingsView();
            }
            resultSet.close();

            // Create the rankings view
            Statement statement = connection.createStatement();
            String query = "CREATE VIEW rankings AS " +
                    "SELECT " +
                    "(SELECT COUNT(*) + 1 FROM leaderboard l2 WHERE l2.wins > l.wins OR (l2.wins = l.wins AND l2.losses < l.losses)) AS ranking, "
                    +
                    "u.username, " +
                    "l.wins, " +
                    "l.losses " +
                    "FROM " +
                    "leaderboard l " +
                    "JOIN " +
                    "users u ON l.user_id = u.user_id " +
                    "WHERE " +
                    "(l.wins > 0 OR l.losses > 0) " + // Filter out users with 0 wins and 0 losses
                    "ORDER BY " +
                    "l.wins DESC, " +
                    "l.losses ASC";
            statement.executeUpdate(query);
            System.out.println("Rankings view created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to destroy the rankings view
    public void destroyRankingsView() {
        try {
            Statement statement = connection.createStatement();
            String query = "DROP VIEW IF EXISTS rankings";
            statement.executeUpdate(query);
            System.out.println("Rankings view destroyed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
