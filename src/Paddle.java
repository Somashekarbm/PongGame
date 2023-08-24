import java.awt.*;
import java.awt.event.KeyEvent;

public class Paddle extends Rectangle {
    private int id;
    private int yVelocity;
    private int speed = 15;

    public Paddle(int x, int y, int width, int height, int id) {
        super(x, y, width, height);
        this.id = id;
    }

    public void keyPressed(KeyEvent e) {
        switch(id) {
            case 1:
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    setYVelocity(-speed);
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    setYVelocity(speed);
                }
                break;
            case 2:
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    setYVelocity(-speed);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    setYVelocity(speed);
                }
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch(id) {
            case 1:
                if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) {
                    setYVelocity(0);
                }
                break;
            case 2:
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    setYVelocity(0);
                }
                break;
        }
    }

    public void setYVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }

    public void move() {
        y = y + yVelocity;
    }

    public void draw(Graphics g) {
        if (id == 1) {
            g.setColor(Color.blue);
        } else {
            g.setColor(Color.red);
        }
        g.fillRect(x, y, width, height);
    }
}
