import javax.swing.*;
import java.awt.*;
// Add this line for importing Connection class
import java.sql.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginSignupPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private Connection connection;

    public LoginSignupPage() {
        setTitle("Login / Signup");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        signupButton = new JButton("Signup");

        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> signup());

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signupButton);

        Insets buttonInsets = new Insets(5, 10, 5, 10);
        loginButton.setMargin(buttonInsets);
        signupButton.setMargin(buttonInsets);
        setVisible(true);

        connectToDatabase();
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username or password cannot be empty.", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Login successful
                dispose(); // Close the login/signup window
                // Proceed to the game (GameFrame)
                String retrievedUsername = resultSet.getString("username");
                // Instantiate GameFrame with retrievedUsername and connection
                GameFrame gameFrame = new GameFrame(retrievedUsername, connection);
                gameFrame.setVisible(true); // Show the game frame
                // gameFrame.requestFocus(); // Request focus for key events
                gameFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        // GameFrame is closed, so close the connection to the database
                        closeConnection();
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error during login. Please try again.", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void signup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username or password cannot be empty.", "Signup Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Check if username already exists
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another one.",
                        "Signup Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert new user into the users table
            String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery,
                    Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, username);
            insertStatement.setString(2, password);
            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Get the auto-generated user ID
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                int userId = -1;
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                }

                // Insert default values for wins and losses into the leaderboard table
                if (userId != -1) {
                    String leaderboardInsertQuery = "INSERT INTO leaderboard (user_id, wins, losses) VALUES (?, 0, 0)";
                    PreparedStatement leaderboardInsertStatement = connection.prepareStatement(leaderboardInsertQuery);
                    leaderboardInsertStatement.setInt(1, userId);
                    leaderboardInsertStatement.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Signup successful. You can now log in.", "Signup Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Signup failed. Please try again.", "Signup Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error during signup. Please try again.", "Signup Failed",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
            System.out.println("Connected to the database successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
