import java.awt.Image;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class Enemy {
    private int x, y;
    private Image image;
    private int width;
    private int height;
    private int speed;
    private Random random;
    private int tileSize; 

    public Enemy(int x, int y, Image image, int width, int height, int tileSize) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = width;
        this.height = height;
        this.random = new Random();
        this.speed = random.nextInt(3) + 1;
        this.tileSize = tileSize; 
    }

    public void moveTowards(int targetX, int targetY, int[][] map) {
        if (x < targetX) {
            if (canMoveTo(x + speed, y, map)) {
                x += speed;
            }
        } else if (x > targetX) {
            if (canMoveTo(x - speed, y, map)) {
                x -= speed;
            }
        }

        if (y < targetY) {
            if (canMoveTo(x, y + speed, map)) {
                y += speed;
            }
        } else if (y > targetY) {
            if (canMoveTo(x, y - speed, map)) {
                y -= speed;
            }
        }
    }

    private boolean canMoveTo(int newX, int newY, int[][] map) {
        int mapX = newX / tileSize;
        int mapY = newY / tileSize;

        return (mapX >= 0 && mapX < map[0].length && mapY >= 0 && mapY < map.length && map[mapY][mapX] == 0);
    }

    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void moveRandomly(int[][] map) {
        int direction = (int) (Math.random() * 4);
        int newX = this.x;
        int newY = this.y;

        switch (direction) {
            case 0: 
                newY -= speed;
                break;
            case 1:
                newY += speed;
                break;
            case 2: 
                newX -= speed;
                break;
            case 3: 
                newX += speed;
                break;
        }

        if (!isCollision(newX, newY, map)) {
            this.x = newX;
            this.y = newY;
        }
    }

    private boolean isCollision(int newX, int newY, int[][] map) {
        int tileX = newX / tileSize;
        int tileY = newY / tileSize;

        if (tileX < 0 || tileX >= map[0].length || tileY < 0 || tileY >= map.length) {
            return true;
        }

        return map[tileY][tileX] == 1; 
    }
}
