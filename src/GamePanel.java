import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.Random;
// import java.sql.PreparedStatement;
// import java.sql.SQLException;

public class GamePanel extends JPanel implements Runnable {
    AI ai;
    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int) (GAME_WIDTH * (0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_DIAMETER = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;
    JButton pauseResumeButton;
    JButton exitButton;
    boolean gamePaused = false;
    private boolean playerWins = false;
    private DatabaseManager databaseManager;
    String username; // Add username field
    Connection connection; // Add connection field

    GamePanel(String username, Connection connection) {
        this.username = username;
        this.connection = connection;
        databaseManager = new DatabaseManager("jdbc:mysql://localhost:3306/ponggamedb", "root", "Bandisomu2@");
        ai = new AI(GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        // Create Swing components on the EDT
        SwingUtilities.invokeLater(() -> {
            createButtons();
        });

        gameThread = new Thread(this);
        gameThread.start();
    }

    private void createButtons() {
        // Initialize buttons
        pauseResumeButton = new JButton("Pause");
        pauseResumeButton = new JButton("Pause");
        pauseResumeButton.setBackground(Color.CYAN); // Set contrasting background
        pauseResumeButton.setForeground(Color.BLACK); // Set contrasting text color
        pauseResumeButton.setBorder(BorderFactory.createLineBorder(Color.WHITE)); // Add border

        exitButton = new JButton("Exit");
        exitButton.setBackground(Color.MAGENTA); // Set contrasting background
        exitButton.setForeground(Color.BLACK); // Set contrasting text color
        exitButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        pauseResumeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!gamePaused) {
                    pauseGame();
                    pauseResumeButton.setText("Resume");
                } else {
                    resumeGame();
                    pauseResumeButton.setText("Pause");
                }
            }
        });

        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GamePanel.this);
                frame.dispose();
                // Open the main page
                SwingUtilities.invokeLater(() -> new PongMainPage(databaseManager));
            }
        });

        // Create control panel to hold the buttons
        JPanel controlPanel = new JPanel();
        controlPanel.add(pauseResumeButton);
        controlPanel.add(exitButton);

        // Set layout manager to BorderLayout
        setLayout(new BorderLayout());
        // Add control panel to the bottom of the frame
        add(controlPanel, BorderLayout.SOUTH);
    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), random.nextInt(GAME_HEIGHT - BALL_DIAMETER),
                BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPaddles() {
        paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = ai; // Assigning AI-controlled paddle
    }

    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }

    public void draw(Graphics g) {
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
        Toolkit.getDefaultToolkit().sync();
    }

    public void move() {
        paddle1.move();
        ai.makeMove(ball); // Pass the y-coordinate of the ball to AI's makeMove method
        ball.move();
    }

    public void checkCollision() {
        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }

        if (ball.intersects(paddle1) || ball.intersects(ai)) {
            ball.setXDirection(-ball.xVelocity);

            // Increase velocity slightly when intersecting with a paddle
            final float VELOCITY_INCREMENT = 0.6f; // Adjust this value as needed

            if (ball.xVelocity > 0) {
                ball.xVelocity += VELOCITY_INCREMENT; // Increase velocity if ball is moving right
            } else {
                ball.xVelocity--; // Decrease velocity if ball is moving left
            }

            // Adjust y-velocity (optional)
            if (ball.yVelocity > 0) {
                ball.yVelocity += VELOCITY_INCREMENT; // Increase y-velocity if ball is moving downward
            } else {
                ball.yVelocity--; // Decrease y-velocity if ball is moving upward
            }
        }

        if (paddle1.y <= 0)
            paddle1.y = 0;
        if (paddle1.y >= GAME_HEIGHT - PADDLE_HEIGHT)
            paddle1.y = GAME_HEIGHT - PADDLE_HEIGHT;
        if (ai.y <= 0)
            ai.y = 0;
        if (ai.y >= GAME_HEIGHT - PADDLE_HEIGHT)
            ai.y = GAME_HEIGHT - PADDLE_HEIGHT;

        if (ball.x <= 0) {
            score.player2++;
            newPaddles();
            newBall();
            System.out.println("Player 2: " + score.player2);
            checkGameResult(); // Check game result after player 2 scores
        }
        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            newPaddles();
            newBall();
            System.out.println("Player 1: " + score.player1);
            checkGameResult(); // Check game result after player 1 scores
        }
    }

    public void checkGameResult() {
        // Check if the game is over (e.g., player wins or loses)
        // Update wins/losses accordingly
        if (score.player1 >= 1) {
            playerWins = true;
            databaseManager.updateWins(username);
        } else if (score.player2 >= 1) {
            // ai agent so
            playerWins = false;
            databaseManager.updateLosses(username);
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        while (true) {
            if (!gamePaused) { // Only update game logic when not paused
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while (delta >= 1) {
                    move();
                    ai.makeMove(ball);
                    checkCollision();
                    repaint();
                    delta--;
                }
            } else {
                // If paused, allow repainting for visual updates
                try {
                    Thread.sleep(10); // Adjust sleep time as needed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {
            paddle1.keyReleased(e);
        }
    }

    // Method to pause the game
    public void pauseGame() {
        gamePaused = true;
    }

    // Method to resume the game
    public void resumeGame() {
        gamePaused = false;
    }

    // Method to stop the game
    public void stopGame() {
        // Add any cleanup code here if necessary
        gamePaused = true;
    }
}
