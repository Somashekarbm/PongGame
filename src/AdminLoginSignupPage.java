import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminLoginSignupPage extends JFrame {
    private DatabaseManager rankingsManager;
    private GamePanel gamePanel;

    public AdminLoginSignupPage(DatabaseManager rankingsManager) {
        this.rankingsManager = rankingsManager;

        setTitle("Admin Login/Signup");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        panel.add(usernameLabel);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordLabel);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
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
        panel.add(loginButton);

        JButton signupButton = new JButton("Signup");
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
        panel.add(signupButton);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private boolean validateLogin(String username, String password) {
        return username.equals("root") && password.equals("Bandisomu2@");
    }

    private void signupAdmin(String username, String password) {
        // You can implement admin signup functionality if needed
        JOptionPane.showMessageDialog(AdminLoginSignupPage.this, "Admin signup feature not implemented.");
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

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop the game loop
                if (gamePanel != null) {
                    gamePanel.stopGame();
                }

                // Dispose of the current window
                dispose();

                // Open the PongMainPage
                new PongMainPage(rankingsManager).setVisible(true);
            }
        });

        panel.add(exitButton);

        adminFeaturesWindow.add(panel);
        adminFeaturesWindow.setLocationRelativeTo(null);
        adminFeaturesWindow.setVisible(true);
    }

    public static void main(String[] args) {
        DatabaseManager manager = new DatabaseManager("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
        SwingUtilities.invokeLater(() -> new AdminLoginSignupPage(manager));
    }
}
