import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Game extends JPanel implements KeyListener {
    private Zack zack;
    private Map map;
    private List<Point> items;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Point> fireballPositions;
    private List<Point> lavaPositions;
    private List<Point> icePositions;
    private BufferedImage victoryImage, itemImage, holeImage, heartImage, gameOverImage, lavaImage, iceImage, fireballImage;
    private Image scaledFireballImage;
    private JButton retryButton, mainMenuButton;
    private int itemsCollected = 0, currentLevel, requiredItems = 15;
    private Point holePosition;
    private boolean victory = false, gameOver = false, keyListenerAdded = false, isFrozen = false;
    private long freezeStartTime, lastMoveTime = 0, lastFireballCollisionTime = 0;
    private Timer timer;
    private JButton mainMenuButtonTopRight;
    private static final long FIREBALL_COLLISION_COOLDOWN = 1000;

    public Game(int level) {
        this.currentLevel = level;
        items = new ArrayList<>();
        fireballPositions = new ArrayList<>();
        lavaPositions = new ArrayList<>();
        icePositions = new ArrayList<>();
        enemies = new ArrayList<>();
        loadImages();
        zack = new Zack(1, 1, map); 
        map = new Map(20, 15, enemies, zack); 
        initializeGame(level);
    }    


    private void loadImages() {
        try {
            victoryImage = ImageIO.read(new File("victory.png"));
            itemImage = ImageIO.read(new File("item.png"));
            holeImage = ImageIO.read(new File("hole.png"));
            heartImage = ImageIO.read(new File("heart.png"));
            gameOverImage = ImageIO.read(new File("gameover.png"));
            lavaImage = ImageIO.read(new File("lava.png"));
            iceImage = ImageIO.read(new File("ice.png"));
            fireballImage = ImageIO.read(new File("fireball.png"));
            scaledFireballImage = fireballImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }

    private void initializeGame(int level) {
        setPreferredSize(new Dimension(640, 480));
        setBackground(Color.BLACK);
        map = new Map(20, 15, enemies, zack);
        zack = new Zack(1, 1, map);  
        initializeLevelSettings(level);
        placeItems();
        placeHole();
        setupVictoryButtons();
        setupMainMenuButtonTopRight(); 
        setFocusable(true);
        if (!keyListenerAdded) {
            addKeyListener(this);
            keyListenerAdded = true;
        }
    
        if (timer == null) {
            int timerSpeed = 250; 
            if (level == 2) {
                timerSpeed = 300; 
            } else if (level == 3) {
                timerSpeed = 400; 
            } else if (level == 4) {
                timerSpeed = 450;
            }
            timer = new Timer(timerSpeed, e -> updateGame());
        }
        timer.start();
    }

    private void initializeLevelSettings(int level) {
        spawnEnemies(level);
        switch (level) {
            case 2 -> placeLava(30);
            case 3 -> {
                placeLava(30);
                placeIce(5);
            }
            case 4 -> {
                placeLava(30);
                placeIce(10);
                placeFireballs(8);
            }
        }
    }

    private void spawnEnemies(int level) {
        int numberOfEnemies = 0; 
        if (level == 1) {
            numberOfEnemies = 3; 
        } else if (level == 2) {
            numberOfEnemies = 4; 
        } else if (level == 3) {
            numberOfEnemies = 5; 
        } else if (level == 4) {
            numberOfEnemies = 6; 
        }
        enemies.clear(); 
        for (int i = 0; i < numberOfEnemies; i++) {
            enemies.add(createEnemy());
        }
    }
    
    private Enemy createEnemy() {
        Random random = new Random();
        Point spawnPoint;
        do {
            int x = random.nextInt(map.getWidth() - 2) + 1;
            int y = random.nextInt(map.getHeight() - 2) + 1;
            spawnPoint = new Point(x, y);
        } while (!map.isWalkable(spawnPoint.x, spawnPoint.y) 
                || spawnPoint.equals(new Point(zack.getX() / Zack.MOVE_AMOUNT, zack.getY() / Zack.MOVE_AMOUNT))
                || spawnPoint.distance(zack.getX() / Zack.MOVE_AMOUNT, zack.getY() / Zack.MOVE_AMOUNT) < 3);
    
        Enemy enemy = new Enemy(spawnPoint.x, spawnPoint.y, map, zack);  
        enemies.add(enemy); 
        return enemy;
    }
    
    private void updateGame() {
        if (!gameOver && !victory) {
            for (Enemy enemy : enemies) {
                enemy.moveTowardsPlayer(false, enemies);  
            }
            for (Point fireballPosition : fireballPositions) {
                moveFireball(fireballPosition);
            }
    
            if (isFrozen) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - freezeStartTime >= 2000) {
                    isFrozen = false;
                }
            } else {
                checkItemCollection();
                checkLavaCollision();
                checkVictoryCondition();
                checkIceCollision();
            }
        } else {
            if (timer != null) timer.stop();
        }
        repaint();
    }
   
    private void placeItems() {
        Random random = new Random();
        while (items.size() < requiredItems) {
            int x = random.nextInt(map.getWidth() - 2) + 1;
            int y = random.nextInt(map.getHeight() - 2) + 1;
            Point itemPosition = new Point(x, y);

            if (map.isWalkable(x, y) &&
                    !items.contains(itemPosition) &&
                    !lavaPositions.contains(itemPosition) &&
                    !itemPosition.equals(new Point(zack.getX() / Zack.MOVE_AMOUNT, zack.getY() / Zack.MOVE_AMOUNT))) {
                items.add(itemPosition);
            }
        }
    }

    private void placeHole() {
        Random random = new Random();
        do {
            int x = random.nextInt(map.getWidth() - 2) + 1;
            int y = random.nextInt(map.getHeight() - 2) + 1;
            holePosition = new Point(x, y);
        } while (!map.isWalkable(holePosition.x, holePosition.y));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        map.paintComponent(g);
        for (Point lava : lavaPositions) {
            g.drawImage(lavaImage, lava.x * 32, lava.y * 32, null);
        }
        for (Point ice : icePositions) {
            g.drawImage(iceImage, ice.x * 32, ice.y * 32, null);
        }
        for (Point item : items) {
            g.drawImage(itemImage, item.x * 32, item.y * 32, null);
        }
        if (itemsCollected >= requiredItems && !victory) {
            g.drawImage(holeImage, holePosition.x * 32, holePosition.y * 32, null);
        }
        zack.draw(g);
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (int i = 0; i < zack.getHealth(); i++) {
            g.drawImage(heartImage, 32 * i, 0, null);
        }
        if (zack.getHealth() <= 0) {
            gameOver = true;
            clearFireballs();
            g.drawImage(gameOverImage, 0, 0, getWidth(), getHeight(), null);
            showVictoryButtons(true);
        } else if (victory) {
            clearFireballs();
            g.drawImage(victoryImage, 0, 0, getWidth(), getHeight(), null);
            showVictoryButtons(true);
        } else {
            showVictoryButtons(false);
        }
        for (Point fireballPosition : fireballPositions) {
            g.drawImage(scaledFireballImage, fireballPosition.x * 32 + 8, fireballPosition.y * 32 + 8, null);
        }
        checkFireballCollisionWithPlayer();
    }
    
    private void showVictoryButtons(boolean visible) {
        retryButton.setVisible(visible);
        mainMenuButton.setVisible(visible);
        revalidate();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime >= 100) { 
            int key = e.getKeyCode();
            boolean moved = false;
            if (!isFrozen) { 
                switch (key) {
                    case KeyEvent.VK_UP:
                        moved = zack.moveUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        moved = zack.moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        moved = zack.moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moved = zack.moveRight();
                        break;
                }
            }

            if (moved) {
                lastMoveTime = currentTime; 
                checkItemCollection();
                checkVictoryCondition();
                repaint();
            }
        }
    }

    private void checkItemCollection() {
        Point zackPosition = new Point(zack.getX() / Zack.MOVE_AMOUNT, zack.getY() / Zack.MOVE_AMOUNT);
        Iterator<Point> iterator = items.iterator();
        while (iterator.hasNext()) {
            Point item = iterator.next();
            if (zackPosition.equals(item)) {
                itemsCollected++;
                iterator.remove();
                break;
            }
        }
    }

    private void checkVictoryCondition() {
        if (itemsCollected >= requiredItems && !victory) {
            Point zackPosition = new Point(zack.getX() / Zack.MOVE_AMOUNT, zack.getY() / Zack.MOVE_AMOUNT);
            if (zackPosition.equals(holePosition)) {
                victory = true;
            }
        }
    }

    private void setupVictoryButtons() {
        retryButton = new JButton("Retry");
        mainMenuButton = new JButton("Main Menu");
        setLayout(null);  
        int buttonWidth = 100;
        int buttonHeight = 30;
        retryButton.setBounds(250, 200, buttonWidth, buttonHeight);
        mainMenuButton.setBounds(250, 240, buttonWidth, buttonHeight);
        retryButton.addActionListener(e -> restartGame());
        mainMenuButton.addActionListener(e -> returnToMainMenu());
        add(retryButton);
        retryButton.setVisible(false);
    }    
    
    private void restartGame() {
        itemsCollected = 0;
        victory = false;
        gameOver = false;
        items.clear();
        lavaPositions.clear();
        fireballPositions.clear();
        icePositions.clear();
        enemies.clear();
        zack.getHealth(); 
        lastMoveTime = 0;
        lastFireballCollisionTime = 0;
        showVictoryButtons(false); 
        initializeGame(currentLevel); 
        repaint();
    }
    
    private void returnToMainMenu() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topFrame.dispose(); 
        JFrame frame = new JFrame("Main Menu");
        GameStartScreen mainMenu = new GameStartScreen(frame);
        frame.add(mainMenu);
        frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); 
        frame.setVisible(true);
    }    

    private void checkLavaCollision() {
        Point zackPosition = new Point(zack.getX() / Zack.MOVE_AMOUNT, zack.getY() / Zack.MOVE_AMOUNT);
        if (lavaPositions.contains(zackPosition)) {
            zack.takeDamage(1);
        }
    }

    private void setupMainMenuButtonTopRight() {
        mainMenuButtonTopRight = new JButton("Main Menu");
        mainMenuButtonTopRight.addActionListener(e -> returnToMainMenu());
        setLayout(null);
        int buttonWidth = 100;
        int buttonHeight = 30;
        int xPosition = getWidth() - buttonWidth - 10; 
        int yPosition = 10; 
        mainMenuButtonTopRight.setBounds(xPosition, yPosition, buttonWidth, buttonHeight);
        add(mainMenuButtonTopRight);
        mainMenuButtonTopRight.setVisible(true);
        
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int newXPosition = getWidth() - buttonWidth - 10;
                mainMenuButtonTopRight.setBounds(newXPosition, yPosition, buttonWidth, buttonHeight);
            }
        });
    }    

    public static void startGame(int level) {
        JFrame frame = new JFrame("Level " + level);
        Game game = new Game(level);

        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void checkIceCollision() {
        Point zackPosition = new Point(zack.getX() / Zack.MOVE_AMOUNT, zack.getY() / Zack.MOVE_AMOUNT);
        if (icePositions.contains(zackPosition) && !isFrozen) { 
            isFrozen = true;
            freezeStartTime = System.currentTimeMillis();
        }
    }  
    
    private void placeLava(int count) {
        spawnEntities(lavaPositions, count);
    }
    
    private void placeIce(int count) {
        spawnEntities(icePositions, count);
    }
    
    private void placeFireballs(int count) {
        spawnEntities(fireballPositions, count);
    }
    
    private void spawnEntities(List<Point> entityList, int count) {
        Random random = new Random();
        Point zackPosition = new Point(zack.getX() / Zack.MOVE_AMOUNT, zack.getY() / Zack.MOVE_AMOUNT);
    
        while (entityList.size() < count) {
            int x = random.nextInt(map.getWidth() - 2) + 1;
            int y = random.nextInt(map.getHeight() - 2) + 1;
            Point position = new Point(x, y);
            if (map.isWalkable(x, y) &&
                !entityList.contains(position) &&
                !items.contains(position) &&
                !lavaPositions.contains(position) &&
                !icePositions.contains(position) &&
                !fireballPositions.contains(position) &&
                !position.equals(zackPosition)) {
                entityList.add(position);
            }
        }
    }
    
    private void moveFireball(Point fireballPosition) {
        Random random = new Random();
        int direction = random.nextInt(4);

        int oldX = fireballPosition.x;
        int oldY = fireballPosition.y;
        
        switch (direction) {
            case 0: 
                fireballPosition.y -= 1; 
                break;
            case 1: 
                fireballPosition.y += 1; 
                break;
            case 2: 
                fireballPosition.x -= 1; 
                break;
            case 3: 
                fireballPosition.x += 1;
                break;
        }
        
        if (!map.isWalkable(fireballPosition.x, fireballPosition.y)) {
            fireballPosition.x = oldX;
            fireballPosition.y = oldY;
        }
    }

    private void checkFireballCollisionWithPlayer() {
        long currentTime = System.currentTimeMillis();
    
        for (Point fireballPosition : fireballPositions) {
            if (fireballPosition.equals(zack.getPosition())) {
                if (currentTime - lastFireballCollisionTime > FIREBALL_COLLISION_COOLDOWN) {
                    zack.takeDamage(1);
                    lastFireballCollisionTime = currentTime; 
                }
                break;
            }
        }
    }

    private void clearFireballs() {
        fireballPositions.clear(); 
    }    

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}