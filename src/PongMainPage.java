import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PongMainPage extends JFrame {
    public PongMainPage() {
        setTitle(" Pong ");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton playButton = new JButton("Play Game");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the game frame
                openLoginSignupPage();
                // new GameFrame();
                // Close the main page frame
                dispose();
            }
        });
        panel.add(playButton);

        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the leaderboard page
                new LeaderboardPage();
                // Close the main page frame
                dispose();
            }
        });
        panel.add(leaderboardButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Exit the application
                System.exit(0);
            }
        });
        panel.add(exitButton);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openLoginSignupPage() {
        // Open the LoginSignupPage
        LoginSignupPage loginSignupPage = new LoginSignupPage();
    }

    public static void main(String[] args) {
        new PongMainPage();
    }
}
