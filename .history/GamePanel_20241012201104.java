import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements KeyListener {
   private Image floorImage;
   private Image zackImage;
   private Image enemyImage;
   private Image wallImage;
   private int[][] map;
   private int tileSize = 32;
   private int zackX = 1;
   private int zackY = 1;
   private List<Enemy> enemies;
   private Timer timer;
   private int playerHP = 200;

   public GamePanel() {
      this.setBackground(Color.white);
      this.map = new int[18][25];
      this.loadImages();
      this.initializeMap();
      this.initializeEnemies();
      this.setFocusable(true);
      this.addKeyListener(this);
      this.timer = new Timer(100, (var1) -> {
         this.updateEnemies();
         this.repaint();
      });
      this.timer.start();
   }

   private void loadImages() {
      try {
         this.floorImage = ImageIO.read(new File("floor.png"));
         this.zackImage = ImageIO.read(new File("zack.png"));
         this.enemyImage = ImageIO.read(new File("enemy.png"));
         this.wallImage = ImageIO.read(new File("wall.png"));
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }

   private void initializeMap() {
      for (int var1 = 0; var1 < this.map.length; ++var1) {
         for (int var2 = 0; var2 < this.map[var1].length; ++var2) {
            this.map[var1][var2] = 0;
         }
      }

      byte var5 = 40;
      int var2 = 0;

      while (var2 < var5) {
         int var3 = (int) (Math.random() * (this.map[0].length - 2)) + 1;
         int var4 = (int) (Math.random() * (this.map.length - 2)) + 1;
         if (this.map[var4][var3] == 0 && this.map[var4 - 1][var3] == 0 && this.map[var4 + 1][var3] == 0 && this.map[var4][var3 - 1] == 0 && this.map[var4][var3 + 1] == 0) {
            this.map[var4][var3] = 1;
            ++var2;
         }
      }
   }

   private void initializeEnemies() {
      this.enemies = new ArrayList<>();
      byte var1 = 55;
      byte var2 = 55;
      this.enemies.add(new Enemy(5 * this.tileSize, 5 * this.tileSize, this.enemyImage, var1, var2));
      this.enemies.add(new Enemy(10 * this.tileSize, 10 * this.tileSize, this.enemyImage, var1, var2));
   }

   private void handleKeyPress(KeyEvent var1) {
      int var2 = this.zackX;
      int var3 = this.zackY;
      switch (var1.getKeyCode()) {
         case KeyEvent.VK_LEFT:
            --var2;
            break;
         case KeyEvent.VK_UP:
            --var3;
            break;
         case KeyEvent.VK_RIGHT:
            ++var2;
            break;
         case KeyEvent.VK_DOWN:
            ++var3;
      }

      if (var2 >= 0 && var2 < this.map[0].length && var3 >= 0 && var3 < this.map.length && this.map[var3][var2] == 0) {
         this.zackX = var2;
         this.zackY = var3;
      }

      this.repaint();
   }

   private void updateEnemies() {
      for (Enemy enemy : enemies) {
          int targetX = zackX * tileSize;
          int targetY = zackY * tileSize;
          
          double randomMoveChance = Math.random(); 
          if (randomMoveChance < 0.3) {  
              enemy.moveRandomly(map);
          } else {
              enemy.moveTowards(targetX, targetY, map);
          }
  
          if (enemy.getBounds().intersects(new Rectangle(zackX * tileSize, zackY * tileSize, tileSize, tileSize)) && playerHP > 0) {
              handlePlayerKnockback(enemy);
              
              playerHP -= 10;
              System.out.println("HP ลดลง! เหลือ: " + playerHP);
              if (playerHP <= 0) {
                  System.out.println("Game Over!");
              }
          }
      }
  }
  

private void handlePlayerKnockback(Enemy enemy) {
    int enemyX = enemy.getX();
    int enemyY = enemy.getY();

    int knockbackX = zackX - (enemyX / tileSize - zackX);
    int knockbackY = zackY - (enemyY / tileSize - zackY);


    if (knockbackX >= 0 && knockbackX < map[0].length && knockbackY >= 0 && knockbackY < map.length) {
        if (map[knockbackY][knockbackX] == 0) { 
            zackX = knockbackX;
            zackY = knockbackY;
        }
    }
}

   @Override
   protected void paintComponent(Graphics var1) {
      super.paintComponent(var1);

      for (int var2 = 0; var2 < this.map.length; ++var2) {
         for (int var3 = 0; var3 < this.map[var2].length; ++var3) {
            var1.drawImage(this.floorImage, var3 * this.tileSize, var2 * this.tileSize, this.tileSize, this.tileSize, (ImageObserver)null);
            if (this.map[var2][var3] == 1) {
               var1.drawImage(this.wallImage, var3 * this.tileSize, var2 * this.tileSize, this.tileSize, this.tileSize, (ImageObserver)null);
            }
         }
      }

      var1.drawImage(this.zackImage, this.zackX * this.tileSize, this.zackY * this.tileSize, this.tileSize, this.tileSize, (ImageObserver)null);
      Iterator<Enemy> var4 = this.enemies.iterator();

      while (var4.hasNext()) {
         Enemy var5 = var4.next();
         var5.draw(var1);
      }

      var1.setColor(Color.RED);
      var1.drawString("HP: " + this.playerHP, 10, 20);
   }

   @Override
   public void keyPressed(KeyEvent e) {
      handleKeyPress(e);
   }

   @Override
   public void keyReleased(KeyEvent e) {
   }

   @Override
   public void keyTyped(KeyEvent e) {
   }
}
