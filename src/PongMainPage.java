import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PongMainPage extends JFrame {
    private DatabaseManager rankingsManager;
    private LeaderboardPage leaderboardPage;
    private GamePanel gamePanel; // Add a reference to the GamePanel

    public PongMainPage(DatabaseManager rankingsManager) {
        this.rankingsManager = rankingsManager;

        setTitle("Pong Game");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

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

    private void openPlayerLoginSignupPage() {
        new LoginSignupPage();
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public static void main(String[] args) {
        DatabaseManager manager = new DatabaseManager("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
        PongMainPage mainPage = new PongMainPage(manager);
        GamePanel gamePanel = new GamePanel("Player", manager.getConnection()); // Create GamePanel instance
        mainPage.setGamePanel(gamePanel); // Set the GamePanel instance in the PongMainPage
        SwingUtilities.invokeLater(() -> mainPage.setVisible(true));

    }

}
