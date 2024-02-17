
// GameFrame.java
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class GameFrame extends JFrame {
   GamePanel panel;

   // Modify the constructor to accept username and connection
   GameFrame(String username, Connection connection) {
      panel = new GamePanel(username, connection); // Pass username and connection to GamePanel
      this.add(panel);
      this.setTitle("PONG GAME");
      this.setResizable(false);
      this.setBackground(Color.black);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.pack();
      this.setVisible(true);
      this.setLocationRelativeTo(null);
   }
}
