import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PongMainPage extends JFrame {
    private DatabaseManager rankingsManager;
    private LeaderboardPage leaderboardPage;

    public PongMainPage() {
        setTitle("Pong Game");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton playButton = new JButton("Play Game");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginSignupPage();
                dispose();
            }
        });
        panel.add(playButton);

        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (leaderboardPage == null) {
                    leaderboardPage = new LeaderboardPage();
                    leaderboardPage.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            // Destroy the rankings view when leaderboard window is closed
                            rankingsManager.destroyRankingsView();
                            leaderboardPage = null;
                        }
                    });
                }
                // Create the rankings view when leaderboard button is clicked
                rankingsManager.createRankingsView();
                dispose();
            }
        });
        panel.add(leaderboardButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Destroy the rankings view before exiting
                System.exit(0);
            }
        });
        panel.add(exitButton);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialize the rankings manager
        initialize();
    }

    private void initialize() {
        try {
            // Initialize the rankings manager with the database connection
            rankingsManager = new DatabaseManager("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openLoginSignupPage() {
        new LoginSignupPage();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PongMainPage::new);
    }
}
