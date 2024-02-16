import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeaderboardPage extends JFrame {
    public LeaderboardPage() {
        setTitle("Leaderboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JTextArea leaderboardTextArea = new JTextArea();
        leaderboardTextArea.setEditable(false);
        // Here you can fetch leaderboard data from the database and display it in the
        // text area
        leaderboardTextArea.setText(
                "Leaderboard:\n1. Player1 - Wins: 10, Losses: 5\n2. Player2 - Wins: 8, Losses: 7\n3. Player3 - Wins: 6, Losses: 9");
        panel.add(leaderboardTextArea, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Main Page");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the main page
                new PongMainPage();
                // Close the leaderboard page frame
                dispose();
            }
        });
        panel.add(backButton, BorderLayout.SOUTH);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new LeaderboardPage();
    }
}
