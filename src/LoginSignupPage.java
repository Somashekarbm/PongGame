import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

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
    private JFrame gameWindow;

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
        gameWindow = new JFrame("Pong Game");
        gameWindow.setSize(400, 200);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                // Check for new messages when the window is activated (e.g., after login)
                checkAndDisplayNewMessages();
            }
        });
        // Create buttons for play game, game history, feedback/bug report, chat, and
        // logout
        JButton playGameButton = new JButton("Play Game");
        JButton gameHistoryButton = new JButton("Game History");
        JButton feedbackButton = new JButton("Submit Feedback/Bug Report");
        JButton chatButton = new JButton("Chat"); // Adding the chat button
        JButton logoutButton = new JButton("Logout"); // Adding the logout button
        // Add action listeners for the buttons
        playGameButton.addActionListener(e -> playGame());
        gameHistoryButton.addActionListener(e -> showGameHistory());
        feedbackButton.addActionListener(e -> submitFeedbackOrBugReport());
        chatButton.addActionListener(e -> openChatWindow()); // Adding action listener for the chat button
        logoutButton.addActionListener(e -> logout()); // Adding action listener for the logout button
        // Create a panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1)); // Updated to accommodate the logout button
        buttonPanel.add(playGameButton);
        buttonPanel.add(gameHistoryButton);
        buttonPanel.add(feedbackButton);
        buttonPanel.add(chatButton); // Adding the chat button to the panel
        buttonPanel.add(logoutButton); // Adding the logout button to the panel

        // Add the button panel to the game window
        gameWindow.add(buttonPanel);

        gameWindow.setVisible(true);
    }

    private void logout() {
        // Reset authentication status
        isAuthenticated = false;

        // Close the current game window
        closeWindow();

        // Make the parent window (PongMainPage) visible
        parent.setVisible(true);
    }

    public void closeWindow() {
        closeConnection();
        if (gameWindow != null) {
            gameWindow.dispose(); // Dispose the game window
        }
    }

    private void checkAndDisplayNewMessages() {
        try {
            int userId = databaseManager.getUserIdByUsername(username);
            List<String> newMessages = databaseManager.getNewMessages(userId);
            for (String message : newMessages) {
                JOptionPane.showMessageDialog(null, message, "New Message", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getNewMessages(int userId) {
        List<String> messages = new ArrayList<>();
        try {
            String query = "SELECT * FROM chatmessages WHERE receiver_id = ? AND is_read = 0"; // Assuming there's a
                                                                                               // column 'is_read' to
                                                                                               // track read/unread
                                                                                               // messages
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Assuming you have a DatabaseManager object in your LoginSignupPageJ class

            while (resultSet.next()) {
                // Then, you can call the getUsernameById method like this:
                String sender = databaseManager.getUsernameById(resultSet.getInt("sender_id"));
                String message = resultSet.getString("message");
                messages.add(sender + ": " + message);
                // Mark the message as read
                markMessageAsRead(resultSet.getInt("message_id")); // Assuming 'message_id' is the primary key of the
                                                                   // chatmessages table
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private void markMessageAsRead(int messageId) {
        try {
            String query = "UPDATE chatmessages SET is_read = 1 WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, messageId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void submitFeedbackOrBugReport() {
        // Prompt user for feedback or bug report details
        String type = (String) JOptionPane.showInputDialog(this, "Please select the type of your report:",
                "Report Type", JOptionPane.QUESTION_MESSAGE, null,
                new String[] { "Feedback", "Bug Report" }, "Feedback");
        if (type == null) {
            // User canceled the input dialog
            return;
        }

        String subject = JOptionPane.showInputDialog(this, "Please provide the subject of your " + type + ":");
        String description = JOptionPane.showInputDialog(this, "Please provide the description of your " + type + ":");

        if (subject != null && !subject.isEmpty() && description != null && !description.isEmpty()) {
            // Insert feedback or bug report into the database
            try {
                int userId = databaseManager.getUserIdByUsername(username);
                databaseManager.insertFeedbackOrBugReport(userId, type, subject, description);
                JOptionPane.showMessageDialog(this, "Feedback/Bug Report submitted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to submit Feedback/Bug Report.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void openChatWindow() {
        String receiverUsername = JOptionPane.showInputDialog(this, "Enter the username of the recipient:");
        if (receiverUsername != null && !receiverUsername.isEmpty()) {
            String message = JOptionPane.showInputDialog(this, "Enter your message:");
            if (message != null && !message.isEmpty()) {
                try {
                    int senderId = databaseManager.getUserIdByUsername(username);
                    int receiverId = databaseManager.getUserIdByUsername(receiverUsername);
                    if (senderId != -1 && receiverId != -1) {
                        databaseManager.sendChatMessage(senderId, receiverId, message);
                        JOptionPane.showMessageDialog(this, "Message sent successfully.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid sender or receiver.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error sending message.");
                    ex.printStackTrace();
                }
            }
        }
    }

    private void playGame() {
        // Ensure that the player is authenticated before starting the game
        if (isAuthenticated) {
            // Create a new GameFrame instead of directly creating a GamePanel
            new GameFrame(parent, username, connection);

            // Close the current login/signup window
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Please login first.", "Login Required", JOptionPane.ERROR_MESSAGE);
        }
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
