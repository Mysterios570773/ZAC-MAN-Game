import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle; 
import javax.swing.ImageIcon;
import java.awt.Point;

public class Zack {
    private int x, y;
    public static final int MOVE_AMOUNT = 32; 
    public static final int WIDTH = 32; 
    public static final int HEIGHT = 32; 
    private Image zackImage;
    private Image heartImage; 
    private int health; 
    private Map map;
    private boolean isFrozen = false; 
    private long freezeEndTime = 0; 
    private static final long FREEZE_DURATION = 2000; 

    public Zack(int blockX, int blockY, Map map) {
        this.x = blockX * MOVE_AMOUNT; 
        this.y = blockY * MOVE_AMOUNT; 
        this.map = map; 
        zackImage = new ImageIcon("zack.png").getImage();
        heartImage = new ImageIcon("heart.png").getImage();
        this.health = 10; 
    }

    public void draw(Graphics g) { 
        g.drawImage(zackImage, x, y, null);
        for (int i = 0; i < health; i++) {
            g.drawImage(heartImage, 32 * i, 0, null);
        }
    }

    public boolean moveUp() { 
        if (isAlive() && !isFrozen && map.isWalkable(x / MOVE_AMOUNT, (y - MOVE_AMOUNT) / MOVE_AMOUNT)) { 
            y -= MOVE_AMOUNT; 
            return true;
        }
        return false; 
    }

    public boolean moveDown() { 
        if (isAlive() && !isFrozen && map.isWalkable(x / MOVE_AMOUNT, (y + MOVE_AMOUNT) / MOVE_AMOUNT)) {
            y += MOVE_AMOUNT; 
            return true; 
        }
        return false;
    }

    public boolean moveLeft() {
        if (isAlive() && !isFrozen && map.isWalkable((x - MOVE_AMOUNT) / MOVE_AMOUNT, y / MOVE_AMOUNT)) {
            x -= MOVE_AMOUNT; 
            return true; 
        }
        return false; 
    }

    public boolean moveRight() { 
        if (isAlive() && !isFrozen && map.isWalkable((x + MOVE_AMOUNT) / MOVE_AMOUNT, y / MOVE_AMOUNT)) {
            x += MOVE_AMOUNT; 
            return true;
        }
        return false;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public int getHealth() {
        return health; 
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0; 
        }
    }

    public void pushBack(int amount, int directionX, int directionY) {
        if (directionX != 0) {
            if (map.isWalkable((x + directionX) / MOVE_AMOUNT, y / MOVE_AMOUNT)) {
                x += directionX;
            } else {
                if (directionY != 0 && map.isWalkable(x / MOVE_AMOUNT, (y + directionY) / MOVE_AMOUNT)) {
                    y += directionY;
                } else {
                    if (map.isWalkable((x - directionX) / MOVE_AMOUNT, y / MOVE_AMOUNT)) {
                        x -= directionX; 
                    }
                }
            }
        }
        if (directionY != 0) {
            if (map.isWalkable(x / MOVE_AMOUNT, (y + directionY) / MOVE_AMOUNT)) {
                y += directionY;
            } else {
                if (directionX != 0 && map.isWalkable((x + directionX) / MOVE_AMOUNT, y / MOVE_AMOUNT)) {
                    x += directionX;
                } else {
                    if (map.isWalkable(x / MOVE_AMOUNT, (y - directionY) / MOVE_AMOUNT)) {
                        y -= directionY; 
                    }
                }
            }
        }
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x >= map.getWidth() * MOVE_AMOUNT) x = (map.getWidth() - 1) * MOVE_AMOUNT;
        if (y >= map.getHeight() * MOVE_AMOUNT) y = (map.getHeight() - 1) * MOVE_AMOUNT;
    }
    
    
    
    public void handleCollisionWithEnemy(Enemy enemy) {
        int enemyX = enemy.getX();
        int enemyY = enemy.getY();

        if (x == enemyX && y == enemyY) {
            takeDamage(1); 
            pushBack(2, (x < enemyX ? -MOVE_AMOUNT : MOVE_AMOUNT), (y < enemyY ? -MOVE_AMOUNT : MOVE_AMOUNT)); 
        }
    }

    public boolean isAlive() {
        return health > 0; 
    }

    public boolean isAtCorner() {
        return (x % MOVE_AMOUNT == 0 && y % MOVE_AMOUNT == 0);
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void freeze() {
        isFrozen = true;
        freezeEndTime = System.currentTimeMillis() + FREEZE_DURATION; 
    }

    public void updateFreezeStatus() {
        if (isFrozen && System.currentTimeMillis() > freezeEndTime) {
            isFrozen = false; 
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    } 
    
    public Point getPosition() {
        return new Point(x / MOVE_AMOUNT, y / MOVE_AMOUNT);
    }
}
