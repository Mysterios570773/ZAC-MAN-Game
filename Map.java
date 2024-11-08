import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.List;

public class Map extends JPanel {
    private BufferedImage wallImage;
    private BufferedImage floorImage;
    private int width;
    private int height;
    private int[][] grid;
    private Random random = new Random();
    private List<Enemy> enemies; 
    private Zack player; 

    public Map(int width, int height, List<Enemy> enemies, Zack player) {
        this.width = width;
        this.height = height;
        this.enemies = enemies;
        this.player = player;
        loadImages();
        generateMap();
    }

    private void loadImages() {
        try {
            wallImage = ImageIO.read(new File("wall.png"));
            floorImage = ImageIO.read(new File("floor.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateMap() {
        grid = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    grid[y][x] = 1; 
                } else {
                    grid[y][x] = 0;
                }
            }
        }
        for (int i = 0; i < (width * height) / 5; i++) {
            int x = random.nextInt(width - 2) + 1;
            int y = random.nextInt(height - 2) + 1;

            if (grid[y][x] == 0 && !isAdjacentToWall(x, y) && !(x == 1 && y == 1)) {
                grid[y][x] = 1;
            }
        }
    }

    private boolean isAdjacentToWall(int x, int y) {
        if (x <= 0 || x >= width - 1 || y <= 0 || y >= height - 1) {
            return false;
        }
        return (grid[y - 1][x] == 1 || grid[y + 1][x] == 1 ||
                grid[y][x - 1] == 1 || grid[y][x + 1] == 1);
    }

    public boolean isWalkable(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height && grid[y][x] == 0 && !isOccupied(x, y));
    }

    public boolean isWall(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return grid[y][x] == 1;
    }

    public boolean isOccupied(int x, int y) {
        if (isWall(x, y)) {
            return true;  
        }

        for (Enemy enemy : enemies) {
            if (enemy.getX() / 32 == x && enemy.getY() / 32 == y) {
                return true;
            }
        }
        if (player.getX() / 32 == x && player.getY() / 32 == y) {
            return true;
        }

        return false;
    }

    public boolean[][] getWalls() {
        boolean[][] walls = new boolean[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                walls[x][y] = grid[y][x] == 1;
            }
        }
        return walls;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == 1) {
                    g.drawImage(wallImage, x * 32, y * 32, null);
                } else {
                    g.drawImage(floorImage, x * 32, y * 32, null);
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
