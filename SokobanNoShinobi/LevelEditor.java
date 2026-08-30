package SokobanNoShinobi;
import java.util.ArrayList;
/**
 * 12/28/2024
 * A tool for making custom levels for SokobanNoShinobi.
 * Should be easier than writing the txt file by hand.
 * @author moormonkey
 */
public class LevelEditor {
    // for editing GUI
    private static LevelEditorFrame graphics;
    private static final Board LEVEL = new Board(3);
    // for playing GUI
    private static GraphicsFrame gameFrame;
    public static void main(String[] args) {
        graphics = new LevelEditorFrame(LEVEL);
    }

    /**
     * What happens when you press the "PLAY" button.
     */
    public static void play() {
        final Board GAME = LEVEL.clone();
        GAME.setMoveLogic(graphics.getMoveLogic());
        gameFrame = new GraphicsFrame(GAME, false);
    }
}
