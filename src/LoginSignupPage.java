import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginSignupPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private Connection connection;
    private boolean isAuthenticated = false;
    private String username;
    private DatabaseManager databaseManager;
    private PongMainPage parent;
    private String jdbcURL = "jdbc:mysql://localhost:3306/ponggamedb";

    public LoginSignupPage(DatabaseManager databaseManager, PongMainPage parent) {
        this.databaseManager = databaseManager;
        if (!(parent instanceof PongMainPage)) {
            throw new IllegalArgumentException("Parent window must be an instance of PongMainPage");
        }
        this.parent = parent;
        setTitle("Login / Signup");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use GridBagLayout
        // Use GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Padding between components

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 25)); // Set preferred size
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 25)); // Set preferred size
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across two columns
        add(loginButton, gbc);

        signupButton = new JButton("Signup");
        signupButton.addActionListener(e -> signup());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span across two columns
        add(signupButton, gbc);

        setVisible(true);

        // Initialize the connection
        connectToDatabase(); // Ensure connection is initialized
    }

    public Connection getConnection() {
        return connection;
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
                isAuthenticated = true;
                this.username = username;
                openGameWindow(); // Open the game window after successful login
                dispose(); // Close the login/signup window
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

    public void closeWindow() {
        closeConnection(); // Close the database connection when the window is closed
        dispose(); // Close the window
    }

    public String getUsername() {
        return username;
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, "root", "Bandisomu2@");
            System.out.println("Connected to the database successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // Handle the case where connection fails by setting it to null
            connection = null;
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

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void openGameWindow() {
        // Create a new JFrame for the game window
        JFrame gameWindow = new JFrame("Pong Game");
        gameWindow.setSize(400, 200);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Call updateGameHistoryOnClose when the window is closed
                updateGameHistoryOnClose();
            }
        });

        // Add buttons for play game and game history
        JButton playGameButton = new JButton("Play Game");
        JButton gameHistoryButton = new JButton("Game History");

        // Add action listeners for the buttons
        playGameButton.addActionListener(e -> playGame());
        gameHistoryButton.addActionListener(e -> showGameHistory());

        // Add buttons to the game window
        gameWindow.setLayout(new GridLayout(2, 1));
        gameWindow.add(playGameButton);
        gameWindow.add(gameHistoryButton);

        gameWindow.setVisible(true);
    }

    private void playGame() {
        // Ensure that the player is authenticated before starting the game
        if (isAuthenticated) {
            // Create a new GameFrame instead of directly creating a GamePanel
            new GameFrame(parent, username, connection);

            // Call insertGameHistory to record the start of the game
            try {
                databaseManager.insertGameHistory(username);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error recording game start.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }

            // Close the current login/signup window
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Please login first.", "Login Required", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateGameHistoryOnClose() {
        if (isAuthenticated()) {
            try {
                GamePanel gamePanel = parent.getGamePanel();
                if (gamePanel != null) {
                    if (gamePanel.isGameOver()) {
                        if (gamePanel.playerWins()) {
                            databaseManager.updateGameHistory(username, "Win");
                        } else {
                            databaseManager.updateGameHistory(username, "Lost");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public GamePanel getGamePanel() {
        if (parent != null) {
            return parent.getGamePanel();
        }
        return null;
    }

    private void showGameHistory() {
        JFrame historyFrame = new JFrame("Game History");
        historyFrame.setSize(400, 300);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel historyPanel = new JPanel(new BorderLayout());

        JTextArea historyTextArea = new JTextArea(10, 30);
        historyTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> historyFrame.dispose());
        historyPanel.add(closeButton, BorderLayout.SOUTH);

        try {
            String query = "SELECT * FROM gamehistory WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            int userId = databaseManager.getUserIdByUsername(username);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder history = new StringBuilder();
            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                String status = resultSet.getString("status");
                history.append("Timestamp: ").append(timestamp).append(", Status: ").append(status).append("\n");
            }

            if (history.length() > 0) {
                historyTextArea.setText(history.toString());
            } else {
                historyTextArea.setText("No game history found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving game history.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        historyFrame.add(historyPanel);
        historyFrame.setLocationRelativeTo(this);
        historyFrame.setVisible(true);
    }

    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/ponggamedb";
        String username = "root";
        String password = "Bandisomu2@";

        SwingUtilities.invokeLater(() -> {
            // Create an instance of DatabaseManager
            DatabaseManager databaseManager = new DatabaseManager(jdbcURL, username, password);

            // Create an instance of PongMainPage with DatabaseManager as a parameter
            PongMainPage parent = new PongMainPage(databaseManager);

            // Create an instance of LoginSignupPage with DatabaseManager and PongMainPage
            // instances
            new LoginSignupPage(databaseManager, parent);
        });
    }
}
