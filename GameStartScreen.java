import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameStartScreen extends JPanel {
    private JFrame frame;
    private Image logoImage;

    public GameStartScreen(JFrame frame) {
        this.frame = frame;
        initializeScreen();
        loadLogoImage();
    }

    private void initializeScreen() {
        setLayout(new BorderLayout());
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(200, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        setLayout(new GridBagLayout());
        add(startButton, gbc);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLevelSelectionScreen();
            }
        });
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


    private void showLevelSelectionScreen() {
        frame.getContentPane().removeAll();
        LevelSelectionScreen levelSelectionScreen = new LevelSelectionScreen(frame);
        frame.getContentPane().add(levelSelectionScreen);
        frame.revalidate();
        frame.repaint();
    }
}
