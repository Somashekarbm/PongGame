import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String jdbcURL, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertAdmin(String username, String password) {
        String sql = "INSERT INTO admin (username, password) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("Admin inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting admin: " + e.getMessage());
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
    public void insertGameHistory(String username, Timestamp startTime, String status) {
        try {
            // 1. Get user ID based on username
            String query = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            int userId;
            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            } else {
                // Handle case where username not found (e.g., log error or throw exception)
                System.err.println("Username " + username + " not found in database.");
                return;
            }

            // 2. Insert game history with retrieved user ID
            String insertQuery = "INSERT INTO gamehistory (user_id, status, timestamp) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, status);
            preparedStatement.setTimestamp(3, startTime);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void insertFeedbackOrBugReport(int userId, String reportType, String subject, String description) {
        try {
            String query = "INSERT INTO feedbackbugreports (user_id, report_type, subject, description) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, reportType);
            preparedStatement.setString(3, subject);
            preparedStatement.setString(4, description);
            preparedStatement.executeUpdate();
            System.out.println("Feedback or Bug Report inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void sendChatMessage(int senderId, int receiverId, String message) {
        try {
            String query = "INSERT INTO chatmessages (sender_id, receiver_id, message) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, senderId);
            preparedStatement.setInt(2, receiverId);
            preparedStatement.setString(3, message);
            preparedStatement.executeUpdate();
            System.out.println("Chat message sent successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUsernameById(int userId) {
        String username = "";
        try {
            String query = "SELECT username FROM users WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                username = resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    public List<String> getNewMessages(int userId) throws SQLException {
        List<String> newMessages = new ArrayList<>();
        String query = "SELECT * FROM chatmessages WHERE receiver_id = ? AND is_new = 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String sender = getUsernameById(resultSet.getInt("sender_id"));
                    String message = resultSet.getString("message");
                    newMessages.add(sender + ": " + message);
                    // Mark the message as read
                    markMessageAsRead(resultSet.getInt("message_id"));
                }
            }
        }
        return newMessages;
    }

    private void markMessageAsRead(int messageId) throws SQLException {
        String query = "UPDATE chatmessages SET is_new = 0 WHERE message_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, messageId);
            statement.executeUpdate();
        }
    }

    // to retrive the complete chat history
    // public List<String> getChatMessages(int userId1, int userId2) {
    // List<String> messages = new ArrayList<>();
    // try {
    // String query = "SELECT * FROM chatmessages WHERE (sender_id = ? AND
    // receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
    // PreparedStatement preparedStatement = connection.prepareStatement(query);
    // preparedStatement.setInt(1, userId1);
    // preparedStatement.setInt(2, userId2);
    // preparedStatement.setInt(3, userId2);
    // preparedStatement.setInt(4, userId1);
    // ResultSet resultSet = preparedStatement.executeQuery();
    // while (resultSet.next()) {
    // String sender = getUsernameById(resultSet.getInt("sender_id"));
    // String message = resultSet.getString("message");
    // messages.add(sender + ": " + message);
    // }
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // return messages;
    // }

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
