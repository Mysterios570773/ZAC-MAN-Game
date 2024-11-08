import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class LevelSelectionScreen extends JPanel {
    private JButton[] levelButtons;
    private Image logoImage;

    public LevelSelectionScreen(JFrame frame) {
        levelButtons = new JButton[4]; 
        loadLogoImage();
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK); 

        for (int i = 0; i < levelButtons.length; i++) {
            levelButtons[i] = new JButton("Level " + (i + 1));
            int level = i + 1;

            levelButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Game.startGame(level);
                }
            });

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = i + 1; 
            gbc.insets = new Insets(10, 10, 10, 10);  
            gbc.anchor = GridBagConstraints.CENTER;
            add(levelButtons[i], gbc);
        }
    }

    private void loadLogoImage() {
        try {
            logoImage = ImageIO.read(new File("logogame.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
        protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ImageIcon backgroundImage = new ImageIcon("bg.png");
        g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        if (logoImage != null) {
            int logoWidth = getWidth() * 25 / 100; 
            int logoHeight = logoImage.getHeight(null) * logoWidth / logoImage.getWidth(null); 
            Image scaledLogo = logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            int logoX = (getWidth() - logoWidth) / 2; 
            int logoY = 20; 
            g.drawImage(scaledLogo, logoX, logoY, null);
        }
    }
}
