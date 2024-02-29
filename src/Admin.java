import java.sql.*;

public class Admin {
    private Connection connection;

    public Admin(String jdbcURL, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayer(String username, String password) {
        try {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            System.out.println("Player added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayer(String username) {
        try {
            String query = "DELETE FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            System.out.println("Player deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewPlayer(String username) {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String password = resultSet.getString("password");
                System.out.println("Username: " + username + ", Password: " + password);
            } else {
                System.out.println("Player not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editPlayer(String username, String newPassword) {
        try {
            String query = "UPDATE users SET password = ? WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
            System.out.println("Player updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/ponggamedb";
        String username = "root";
        String password = "Bandisomu2@";

        Admin adminModule = new Admin(jdbcURL, username, password);

        // implement an admin button login/signup and do the above operations on the
        // database
    }
}
