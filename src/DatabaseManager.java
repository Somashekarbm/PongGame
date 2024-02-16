import java.sql.*;

public class DatabaseManager {
    private Connection connection;
    private String jdbcURL = "jdbc:mysql://localhost:3306/pongDB";
    private String username = "aiml";
    private String password = "aiml";

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(jdbcURL, username, password);
    }

    public void insertPlayer(String playerName) throws SQLException {
        String query = "INSERT INTO players (name) VALUES (?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, playerName);
        preparedStatement.executeUpdate();
    }

}
