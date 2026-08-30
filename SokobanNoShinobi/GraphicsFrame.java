package SokobanNoShinobi;
import javax.swing.JFrame;
import java.awt.event.*;
/**
 * 06/08/2024
 * The JFrame containing the JPanel that makes all of the graphics for the game while playing.
 * @author moormonkey
 */
public class GraphicsFrame extends JFrame {
    GraphicsPanel panel;

    /**
     * Creates the window inside which the graphics are contained.
     * @param b the Board object representing the game.
     */
    GraphicsFrame(Board b) {
        panel = new GraphicsPanel(b);
        this.add(panel);
        this.setTitle("SokobanNoShinobi");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                panel.repaint();
            }
        });
    }

    /**
     * Creates the window inside which the graphics are contained.
     * @param b the Board object representing the game.
     * @param exitOnClose should the program stop when this window is closed?
     */
    GraphicsFrame(Board b, boolean exitOnClose) {
        panel = new GraphicsPanel(b, false);
        this.add(panel);
        this.setTitle("SokobanNoShinobi");
        this.setDefaultCloseOperation(exitOnClose? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                panel.repaint();
            }
        });
    }

    /**
     * @return the GraphicsPanel contained within this GraphicsFrame.
     */
    public GraphicsPanel getPanel() {
        return panel;
    }
}