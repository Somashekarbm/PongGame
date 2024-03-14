import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class PongMainPage extends JFrame {
    private DatabaseManager rankingsManager;
    private LeaderboardPage leaderboardPage;
    private PongMainPage mainpage;
    private GamePanel gamePanel; // Add a reference to the GamePanel

    public PongMainPage(DatabaseManager rankingsManager) {
        this.rankingsManager = rankingsManager;
        this.mainpage = this;
        setTitle("Pong Game");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        // Customize button colors
        UIDefaults defaults = UIManager.getDefaults();
        defaults.put("Button.background", new Color(0, 128, 192)); // Custom background color
        defaults.put("Button.foreground", Color.WHITE); // Custom foreground color

        JButton adminButton = new JButton("Admin Login/Signup");
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAdminLoginSignupPage();
                dispose();
            }
        });
        panel.add(adminButton);

        JButton playerButton = new JButton("Player Login/Signup");
        playerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPlayerLoginSignupPage();
                dispose();
            }
        });
        panel.add(playerButton);

        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (leaderboardPage == null) {
                    leaderboardPage = new LeaderboardPage(rankingsManager);
                    leaderboardPage.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            // Set leaderboardPage to null when the window is closed
                            leaderboardPage = null;
                        }
                    });
                }
                // Dispose the current frame
                dispose();
            }
        });
        panel.add(leaderboardButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Stop the game if it's running
                if (gamePanel != null) {
                    gamePanel.stopGame();
                }
                // Destroy the rankings view before exiting
                rankingsManager.destroyRankingsView();
                System.exit(0);
            }
        });
        panel.add(exitButton);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openAdminLoginSignupPage() {
        new AdminLoginSignupPage(rankingsManager);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    private void openPlayerLoginSignupPage() {
        LoginSignupPage loginSignupPage = new LoginSignupPage(rankingsManager, mainpage); // Pass mainpage instead of
                                                                                          // this
        loginSignupPage.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (loginSignupPage.isAuthenticated() && loginSignupPage.getUsername() != null) {
                    // Pass the mainpage instance to the GamePanel constructor
                    startGame(loginSignupPage.getUsername(), loginSignupPage.getConnection(), mainpage);
                } else {
                    // If not authenticated, show the main page again
                    mainpage.setVisible(true);
                }
            }
        });
        dispose();
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        getContentPane().removeAll(); // Remove all components from the content pane
        getContentPane().add(gamePanel); // Add the GamePanel to the content pane
        gamePanel.requestFocusInWindow();
        gamePanel.initGame(); // Call a method to initialize the game (without starting it)
        revalidate(); // Revalidate the frame to reflect the changes
    }

    public DatabaseManager getDatabaseManager() {
        return rankingsManager;
    }

    public void startGame(String username, Connection connection, PongMainPage pongMainPage) {

        try {
            // Instantiate the GamePanel with the authenticated username and connection
            GamePanel gamePanel = new GamePanel(rankingsManager, username, connection, this);
            pongMainPage.setGamePanel(gamePanel); // Set the GamePanel in PongMainPage
            // Close the current login/signup window
            dispose();
            // Now, let PongMainPage handle adding the GamePanel and starting the game
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseManager manager = new DatabaseManager("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
        PongMainPage mainPage = new PongMainPage(manager);
        SwingUtilities.invokeLater(() -> mainPage.setVisible(true));
    }
}
