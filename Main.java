import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("ZAC-MAN Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        GameStartScreen startScreen = new GameStartScreen(frame);
        frame.add(startScreen);

        frame.setVisible(true);
    }
}
