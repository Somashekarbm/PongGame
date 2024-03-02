import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class GameFrame extends JFrame {
   GamePanel panel;
   DatabaseManager databaseManager;

   // Modify the constructor to accept username, connection, and PongMainPage
   // object
   public GameFrame(PongMainPage pongMainPage, String username, Connection connection) {
      this.databaseManager = pongMainPage.getDatabaseManager(); // Get the DatabaseManager from PongMainPage
      panel = new GamePanel(databaseManager, username, connection, pongMainPage); // Pass pongMainPage object
      this.add(panel);
      this.setTitle("PONG GAME");
      this.setResizable(false);
      this.setBackground(Color.black);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.pack();
      this.setVisible(true);
      this.setLocationRelativeTo(null);

      panel.requestFocusInWindow(); // Ensure game panel has focus
      panel.initGame(); // Start the game loop
   }

}
