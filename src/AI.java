import java.util.Random;

class AI extends Paddle {
    private int speed = 2; // Reduce the speed
    private Random random;
    private static final double MISS_PROBABILITY = 0.9; // Increase the probability of missing the ball

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

        // Introduce randomness to make the paddle occasionally miss the ball
        if (random.nextDouble() < MISS_PROBABILITY) {
            // Determine the range within which the future position can vary
            int minAdjustment = -speed * 2; // Adjust this value to control how much the paddle can miss
            int maxAdjustment = speed * 2; // Adjust this value to control how much the paddle can miss

            // Randomly adjust the future position within the specified range
            futureBallY += random.nextInt(maxAdjustment - minAdjustment + 1) + minAdjustment;
        }

        // Adjust the predicted y position to ensure it stays within the game bounds
        futureBallY = Math.max(futureBallY, 0); // Ensure futureBallY is not less than 0
        futureBallY = Math.min(futureBallY, GamePanel.GAME_HEIGHT - GamePanel.BALL_DIAMETER); // Ensure futureBallY is
                                                                                              // not greater than the
                                                                                              // height of the game
                                                                                              // panel

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
