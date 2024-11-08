import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

public class Enemy {
    private int x, y;
    private final Image enemyImage;
    private final Map map;
    private final Zack player;
    private static final int MOVE_AMOUNT = 32; 
    public static final int WIDTH = 32; 
    public static final int HEIGHT = 32; 
    private int moveCounter = 0; 
    private static final int MOVE_INTERVAL = 2; 

    public Enemy(int startX, int startY, Map map, Zack player) {
        this.x = startX * MOVE_AMOUNT;
        this.y = startY * MOVE_AMOUNT;
        this.map = map;
        this.player = player;
        this.enemyImage = new ImageIcon("enemy.png").getImage();
    }

    public void draw(Graphics g) {
        g.drawImage(enemyImage, x, y, null);
    }

    public void moveTowardsPlayer(boolean gamePaused, List<Enemy> enemies) {
        if (gamePaused || !player.isAlive()) return;

        moveCounter++; 
        if (moveCounter < MOVE_INTERVAL) return; 
        moveCounter = 0; 

        int playerX = player.getX();
        int playerY = player.getY();
        Random random = new Random();
        boolean moved = false;

        if (Math.abs(playerX - x) > Math.abs(playerY - y)) {
            if (playerX > x && canMove(x + MOVE_AMOUNT, y, enemies)) {
                moved = moveRight(enemies);
            } else if (playerX < x && canMove(x - MOVE_AMOUNT, y, enemies)) {
                moved = moveLeft(enemies);
            }
        } else if (Math.abs(playerX - x) < Math.abs(playerY - y)) {
            if (playerY > y && canMove(x, y + MOVE_AMOUNT, enemies)) {
                moved = moveDown(enemies);
            } else if (playerY < y && canMove(x, y - MOVE_AMOUNT, enemies)) {
                moved = moveUp(enemies);
            }
        }

        if (!moved) {
            boolean randomDirection = random.nextBoolean();
            if (randomDirection) { 
                if (playerX > x && canMove(x + MOVE_AMOUNT, y, enemies)) {
                    moveRight(enemies);
                } else if (playerX < x && canMove(x - MOVE_AMOUNT, y, enemies)) {
                    moveLeft(enemies);
                }
            } else { 
                if (playerY > y && canMove(x, y + MOVE_AMOUNT, enemies)) {
                    moveDown(enemies);
                } else if (playerY < y && canMove(x, y - MOVE_AMOUNT, enemies)) {
                    moveUp(enemies);
                }
            }
        }

        handleCollisionWithPlayer();
    }    

    public boolean canMove(int newX, int newY, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy != this && enemy.getX() == newX && enemy.getY() == newY) {
                return false; 
            }
        }
        return map.isWalkable(newX / MOVE_AMOUNT, newY / MOVE_AMOUNT); 
    }

    public boolean moveUp(List<Enemy> enemies) { 
        if (canMove(x, y - MOVE_AMOUNT, enemies)) { 
            y -= MOVE_AMOUNT; 
            return true;
        }
        return false; 
    }

    public boolean moveDown(List<Enemy> enemies) { 
        if (canMove(x, y + MOVE_AMOUNT, enemies)) {
            y += MOVE_AMOUNT; 
            return true; 
        }
        return false;
    }

    public boolean moveLeft(List<Enemy> enemies) {
        if (canMove(x - MOVE_AMOUNT, y, enemies)) {
            x -= MOVE_AMOUNT; 
            return true; 
        }
        return false;
    }

    public boolean moveRight(List<Enemy> enemies) { 
        if (canMove(x + MOVE_AMOUNT, y, enemies)) {
            x += MOVE_AMOUNT;
            return true;
        }
        return false;
    }

    public void handleCollisionWithPlayer() {
        Rectangle playerBounds = player.getBounds();  
        Rectangle enemyBounds = getBounds();

        if (enemyBounds.intersects(playerBounds)) {
            player.takeDamage(1); 
            int directionX = (player.getX() > x) ? -MOVE_AMOUNT : MOVE_AMOUNT;
            int directionY = (player.getY() > y) ? -MOVE_AMOUNT : MOVE_AMOUNT;
            player.pushBack(2, directionX, directionY);  
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
