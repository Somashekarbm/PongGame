import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

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
    String username;
    Connection connection;
    private JPanel controlPanel;

    public GamePanel(DatabaseManager databaseManager, String username, Connection connection, PongMainPage frame) {
        this.username = username;
        this.connection = connection;
        this.databaseManager = databaseManager;

        // Initialize AI and paddles
        ai = new AI(GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
        newPaddles();

        // Initialize ball and score
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);

        // Set up panel properties
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(SCREEN_SIZE);

        // Create control panel
        createControlPanel();

        // Create parent panel to hold game panel and control panel
        JPanel parentPanel = new JPanel(new BorderLayout());
        parentPanel.add(this, BorderLayout.CENTER);
        parentPanel.add(controlPanel, BorderLayout.SOUTH);

        // Add parent panel to the content pane of the frame
        if (frame != null) {
            Container contentPane = frame.getContentPane();
            if (contentPane != null) {
                contentPane.add(parentPanel);
            } else {
                System.err.println("Error: Content pane is null.");
            }
        } else {
            System.err.println("Error: Frame is null.");
        }

        // Add a WindowListener to the frame
        if (frame != null) {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    closeConnection();
                }
            });
        }
    }

    public void initGame() {
        // Start the game thread
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Method to stop the game
    public void stopGame() {
        // Interrupt the game thread to stop it
        gameThread.interrupt();
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void createControlPanel() {
        pauseResumeButton = new JButton("Pause");
        pauseResumeButton.setBackground(Color.CYAN);
        pauseResumeButton.setForeground(Color.BLACK);
        pauseResumeButton.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        exitButton = new JButton("Exit");
        exitButton.setBackground(Color.MAGENTA);
        exitButton.setForeground(Color.BLACK);
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

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GamePanel.this);
                frame.dispose();
                SwingUtilities.invokeLater(() -> new PongMainPage(databaseManager));
            }
        });

        controlPanel = new JPanel();
        controlPanel.add(pauseResumeButton);
        controlPanel.add(exitButton);
    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), random.nextInt(GAME_HEIGHT - BALL_DIAMETER),
                BALL_DIAMETER, BALL_DIAMETER);
    }

    public void newPaddles() {
        paddle1 = new Paddle(0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddle2 = ai;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
    }

    public void move() {
        paddle1.move();
        ai.makeMove(ball);
        ball.move();
    }

    public void checkCollision() {
        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - BALL_DIAMETER) {
            ball.setYDirection(-ball.yVelocity);
        }

        if (ball.intersects(paddle1) || ball.intersects(ai)) {
            ball.setXDirection(-ball.xVelocity);

            final float VELOCITY_INCREMENT = 0.6f;

            if (ball.xVelocity > 0) {
                ball.xVelocity += VELOCITY_INCREMENT;
            } else {
                ball.xVelocity--;
            }

            if (ball.yVelocity > 0) {
                ball.yVelocity += VELOCITY_INCREMENT;
            } else {
                ball.yVelocity--;
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
            checkGameResult();
        }
        if (ball.x >= GAME_WIDTH - BALL_DIAMETER) {
            score.player1++;
            newPaddles();
            newBall();
            checkGameResult();
        }
    }

    public void checkGameResult() {
        if (score.player1 >= 1) {
            playerWins = true;
            databaseManager.updateWins(username);
        } else if (score.player2 >= 1) {
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
            if (!gamePaused) {
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while (delta >= 1) {
                    move();
                    checkCollision();
                    repaint();
                    delta--;
                }
            } else {
                try {
                    Thread.sleep(10);
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

    public void pauseGame() {
        gamePaused = true;
    }

    public void resumeGame() {
        gamePaused = false;
    }
}