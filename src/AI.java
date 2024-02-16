import java.util.Random;

class AI extends Paddle {
    private int speed = 5;
    private Random random;

    public AI(int x, int y, int width, int height, int id) {
        super(x, y, width, height, id);
        random = new Random();
    }

    public void makeMove(Ball ball) {
        // Calculate the time it will take for the ball to reach the AI paddle's x
        // position
        double timeToReachX = (x - ball.x) / (double) ball.xVelocity;

        // Predict the future y position of the ball based on its current trajectory
        int futureBallY = (int) (ball.y + timeToReachX * ball.yVelocity);

        // Adjust the predicted y position to ensure it stays within the game bounds
        if (futureBallY < 0) {
            futureBallY = -futureBallY;
        } else if (futureBallY > GamePanel.GAME_HEIGHT - GamePanel.BALL_DIAMETER) {
            futureBallY = 2 * (GamePanel.GAME_HEIGHT - GamePanel.BALL_DIAMETER) - futureBallY;
        }

        // Move the paddle towards the predicted position
        if (y + height / 2 < futureBallY) {
            setYVelocity(speed);
        } else if (y + height / 2 > futureBallY) {
            setYVelocity(-speed);
        } else {
            setYVelocity(0);
        }

        // Move the paddle based on the calculated velocity
        move();

        // Ensure that the paddle stays within the game bounds
        if (y < 0) {
            y = 0;
        } else if (y > getMaxPosition()) {
            y = getMaxPosition();
        }
    }

    protected int getMaxPosition() {
        return GamePanel.GAME_HEIGHT - height;
    }
}
