import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLoginSignupPage extends JFrame {
    private DatabaseManager rankingsManager;

    public AdminLoginSignupPage(DatabaseManager rankingsManager) {
        this.rankingsManager = rankingsManager;

        setTitle("Admin Login/Signup");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JPanel userInputPanel = new JPanel(new GridLayout(2, 1));
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30)); // Set preferred size for username field
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30)); // Set preferred size for password field
        userInputPanel.add(usernameLabel);
        userInputPanel.add(usernameField);
        userInputPanel.add(passwordLabel);
        userInputPanel.add(passwordField);
        panel.add(userInputPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");
        JButton backButton = new JButton("Back");
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (validateLogin(username, password)) {
                    openAdminFeaturesWindow();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(AdminLoginSignupPage.this, "Invalid username or password.");
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (!username.isEmpty() && !password.isEmpty()) {
                    signupAdmin(username, password);
                } else {
                    JOptionPane.showMessageDialog(AdminLoginSignupPage.this, "Username and password cannot be empty.");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new PongMainPage(rankingsManager); // Open the PongMainPage
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private boolean validateLogin(String username, String password) {
        try {
            // Query the admin table for the provided username and password
            String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = rankingsManager.getConnection().prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            // If a matching admin is found, return true
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of any error
        }
    }

    private void signupAdmin(String username, String password) {

        rankingsManager.insertAdmin(username, password);

        // After signup, show a message to the admin
        JOptionPane.showMessageDialog(AdminLoginSignupPage.this, "Admin signup successful.");

        // Add a back button to return to the previous page
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Dispose of the current window
                dispose();

                // Open the previous page (if needed)
                // For example, you might want to open the login page again
                new AdminLoginSignupPage(rankingsManager);
            }
        });

        // Create a panel for the back button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);

        // Add the button panel to the frame
        add(buttonPanel, BorderLayout.SOUTH);

        // Optionally, you can close the signup window after successful signup
        // dispose();
    }

    private void openAdminFeaturesWindow() {
        JFrame adminFeaturesWindow = new JFrame("Admin Features");
        adminFeaturesWindow.setSize(400, 300);
        adminFeaturesWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        JButton addPlayerButton = new JButton("Add Player");
        addPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter username:");
                String password = JOptionPane.showInputDialog("Enter password:");
                if (username != null && password != null) {
                    Admin admin = new Admin("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
                    admin.addPlayer(username, password);
                }
            }
        });
        panel.add(addPlayerButton);

        JButton deletePlayerButton = new JButton("Delete Player");
        deletePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter username to delete:");
                if (username != null) {
                    Admin admin = new Admin("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
                    admin.deletePlayer(username);
                }
            }
        });
        panel.add(deletePlayerButton);

        JButton viewPlayerButton = new JButton("View Player");
        viewPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter username to view:");
                if (username != null) {
                    Admin admin = new Admin("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
                    admin.viewPlayer(username);
                }
            }
        });
        panel.add(viewPlayerButton);

        JButton editPlayerButton = new JButton("Edit Player");
        editPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog("Enter username to edit:");
                String newPassword = JOptionPane.showInputDialog("Enter new password:");
                if (username != null && newPassword != null) {
                    Admin admin = new Admin("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
                    admin.editPlayer(username, newPassword);
                }
            }
        });
        panel.add(editPlayerButton);

        JButton backButton1 = new JButton("Back");
        backButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Dispose of the current window
                adminFeaturesWindow.dispose();

                // Open the login/signup page
                new AdminLoginSignupPage(rankingsManager);
            }
        });
        panel.add(backButton1);

        adminFeaturesWindow.add(panel);
        adminFeaturesWindow.setLocationRelativeTo(null);
        adminFeaturesWindow.setVisible(true);
    }

    public static void main(String[] args) {
        DatabaseManager manager = new DatabaseManager("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
        SwingUtilities.invokeLater(() -> new AdminLoginSignupPage(manager));
    }
}
