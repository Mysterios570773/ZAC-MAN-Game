import javax.swing.JFrame;

public class GameWindow {
    private JFrame frame;
    private GamePanel panel;

    public GameWindow() {
        frame = new JFrame("Zackman Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        panel = new GamePanel(); 
        frame.add(panel); 

        frame.setSize(800, 600); 
        frame.setVisible(true); 
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}
