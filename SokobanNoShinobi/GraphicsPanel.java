package SokobanNoShinobi;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.time.LocalDateTime;
/**
 * 06/08/2024
 * The JPanel that makes all of the graphics for the game.
 * @author moormonkey
 */
public class GraphicsPanel extends JPanel {
    // the Board, passed in when this object is created.
    private Board b;
    // width/height of the board
    private int width;
    private int height;
    // how big one side of a single FloorTile is when displayed, in pixels.
    private int unitSize;
    // the camera's X and Y position.
    private int camX;
    private int camY;
    // the size of the area this Panel operates within.
    private int windowWidth;
    private int windowHeight;
    // the sequence of steps the player has lined up (WASD; to be executed when ENTER is pressed)
    private String moveSequence = "";
    // the height of the bottomText rectangle; -1 to disable bottom text
    private int bottomTextHeight;
    // to display the moveLogic at the start of the round
    private String forceBottomText = "";
    // is this for the Level Editor?
    private boolean levelEditor;
    // does this ignore (non-camera-related) key presses?
    private boolean ignoreKeyPresses;

    /**
     * Creates a new GraphicsPanel, with a reference to a Board object for rendering.
     * @param b the Board object, which contains all the information about the game forever.
     */
    GraphicsPanel(Board b) {
        levelEditor = false;
        bottomTextHeight = 30; // default
        camX = 0;
        camY = 0;
        this.setPreferredSize(new Dimension(720,720 + bottomTextHeight));
        windowWidth = this.getSize().width;
        windowHeight = this.getSize().height;
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        MyKeyAdapter m = new MyKeyAdapter();
        this.addKeyListener(m);
        m.setPanel(this);
        this.b = b;
        width = b.getWidth();
        height = b.getHeight();
    }

    /**
     * Creates a new GraphicsPanel, with a reference to a Board object for rendering.
     * This constructor is used by the level editor, and ignores keyboard input.
     * @param b the Board object, which contains all the information about the game forever.
     * @param addMouseListener should a mouseListener be added?
     */
    GraphicsPanel(Board b, boolean addMouseListener) {
        this.ignoreKeyPresses = addMouseListener;
        levelEditor = true;
        camX = 0;
        camY = 0;
        bottomTextHeight = addMouseListener? -1 : 30;
        this.setPreferredSize(new Dimension(720,addMouseListener? 720 : 750));
        windowWidth = this.getSize().width;
        windowHeight = this.getSize().height;
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        MyKeyAdapter m = new MyKeyAdapter();
        this.addKeyListener(m);
        m.setPanel(this);
        if (addMouseListener) {
            this.addMouseListener(new MouseListener() {
                @Override public void mouseClicked(MouseEvent e) {
                    requestFocusInWindow();
                }
                @Override public void mouseExited(MouseEvent e) { }
                @Override public void mouseEntered(MouseEvent e) { }
                @Override public void mouseReleased(MouseEvent e) {
                    if (unitSize != 0) {
                        int x = (e.getX() + camX) / unitSize;
                        int y = (e.getY() + camY) / unitSize;
                        if (0 <= x && x < width && 0 <= y && y < height) {
                            b.setSelectX(x);
                            b.setSelectY(y);
                        }
                    }
                    repaint();
                    ((LevelEditorFrame) SwingUtilities.getAncestorOfClass(LevelEditorFrame.class, (GraphicsPanel) e.getSource())).refreshRightColumn(true);
                }
                @Override public void mousePressed(MouseEvent e) { }
            });
        }
        this.b = b;
        width = b.getWidth();
        height = b.getHeight();
        unitSize = 240;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /**
     * Draws the Board.
     * @param g the Graphics object, onto which the Board is drawn.
     */
    public void draw(Graphics g) {
        // figure out window size, board size, unitSize, and displaying a message if there is no board loaded.
        windowWidth = this.getSize().width;
        windowHeight = this.getSize().height;
        if (b == null || (width = b.getWidth()) == 0 || (height = b.getHeight()) == 0) {
            g.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 25));
            FontMetrics met = getFontMetrics(g.getFont());
            g.setColor(Color.WHITE);
            g.drawString("No board loaded.", (windowWidth - met.stringWidth("No board loaded.")) / 2, ((windowHeight - g.getFont().getSize()) / 2));
            g.drawString("Hit ESC to load from file.", (windowWidth - met.stringWidth("Hit ESC to load from file.")) / 2, ((windowHeight + g.getFont().getSize()) / 2));
            g.drawString("Hit R to generate a random board.", (windowWidth - met.stringWidth("Hit R to generate a random board.")) / 2, ((windowHeight + 3 * g.getFont().getSize()) / 2));
            return;
        }
        //unitSize = Math.min(windowWidth/width,(windowHeight-Math.max(bottomTextHeight, 0))/height);
        if (unitSize == 0) return; // force zoom in
        // drawString prep
        g.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, unitSize/5));
        FontMetrics met = getFontMetrics(g.getFont());
        // backdrop
        g.setColor(Color.GRAY);
        g.fillRect(-camX, -camY, unitSize * width, unitSize * height);
        // tiles, boxes
        FloorTile[][] floor = b.getFloor();
        boolean[][] boxes = b.getBoxes();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                renderTile(g, met, col, row, floor[row][col], boxes[row][col]);
            }
        }
        // player
        if (b.getPlayerX() >= 0) {
            g.setColor(Color.GREEN);
            g.fillRect(b.getPlayerX() * unitSize + unitSize / 6 - camX, b.getPlayerY() * unitSize + unitSize / 6 - camY, unitSize * 2 / 3, unitSize * 2 / 3);
        }
        // gridlines
        g.setColor(Color.WHITE);
        for (int i = 0; i <= width; i++) {
            g.drawLine(i * unitSize - camX, -camY, i * unitSize - camX, height * unitSize - camY);
        }
        for (int i = 0; i <= height; i++) {
            g.drawLine(-camX, i * unitSize - camY, width * unitSize - camX, i * unitSize - camY);
        }
        // bottom text
        g.setColor(Color.BLACK);
        g.fillRect(0, windowHeight - bottomTextHeight + 1, windowWidth, bottomTextHeight);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 20));
        met = getFontMetrics(g.getFont());
        if (bottomTextHeight >= 0) {
            if (!forceBottomText.equals("")) {
                // force text
                g.drawString(forceBottomText, (windowWidth - met.stringWidth(forceBottomText)) / 2, windowHeight - 12);
                forceBottomText = "";
            } else if (b.checkWin()) {
                // win message
                String msg = levelEditor? "You win!" : "You win! Hit ESC to load a new file or R to create a random board.";
                g.drawString(msg, (windowWidth - met.stringWidth(msg)) / 2, windowHeight - 12);
            } else {
                // re-render movement
                g.drawString(moveSequence, (windowWidth - met.stringWidth(moveSequence)) / 2, windowHeight - 12);
            }
        }
    }

    /**
     * Renders a single FloorTile of the Board, as well as boxes.
     * @param g the Graphics onto which the FloorTile + box will be rendered.
     * @param met the FontMetrics of the font that is being used.
     * @param tileX the x-coordinate of the tile.
     * @param tileY the y-coordinate of the tile.
     * @param tile the FloorTile, with all of its information.
     * @param box does this tile hold a box?
     */
    public void renderTile(Graphics g, FontMetrics met, int tileX, int tileY, FloorTile tile, boolean box) {
        // x and y in pixels
        int x = tileX * unitSize - camX;
        int y = tileY * unitSize - camY;
        // level-editor: is selected
        if (levelEditor && b.getSelectX() == tileX && b.getSelectY() == tileY) {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, unitSize, unitSize);
        }
        // through
        if (tile.isThrough()) {
            g.setColor(Color.RED);
            g.fillRect(x, y, met.stringWidth(tile.maxSteps() + ""), g.getFont().getSize());
        }
        // maxSteps
        g.setColor(Color.BLACK);
        if (tile.maxSteps() > 0) g.drawString(tile.maxSteps() + "", x, y+g.getFont().getSize() * 9/10);
        else if (tile.maxSteps() == 0) {
            if (tile.isThrough()) g.setColor(Color.RED);
            g.fillRect(x, y, unitSize, unitSize);
            // level-editor: wall selected
            if (levelEditor && b.getSelectX() == tileX && b.getSelectY() == tileY) {
                g.setColor(Color.YELLOW);
                g.fillRect(x + 4 * unitSize / 5, y + 4 * unitSize / 5, unitSize / 5, unitSize / 5);
            }
        }
        // boxDestination and box
        if (tile.isDestination()) {
            g.setColor(Color.BLACK);
            g.fillOval(x + unitSize / 3, y + unitSize / 3, unitSize / 3, unitSize / 3);
            g.setColor(Color.LIGHT_GRAY);
        } else {
            g.setColor(Color.ORANGE);
        }
        if (box) g.fillRect(x + unitSize / 6, y + unitSize / 6, unitSize * 2 / 3, unitSize * 2 / 3);
    }

    /**
     * Renders the current moveSequence over the bottom 30px of the screen.
     */
    public void renderBottomText() {
        renderBottomText(moveSequence);
    }

    /**
     * Renders text over the bottom 30px of the screen.
     * @param text the String of text to render.
     */
    public void renderBottomText(String text) {
        if (bottomTextHeight >= 0) {
            Graphics g = getGraphics();
            g.setFont(new Font("Sans serif", Font.ROMAN_BASELINE, 20));
            FontMetrics met = getFontMetrics(g.getFont());
            renderBottomText(text, g, met);
        }
    }

    /**
     * Renders text over the bottom 30px of the screen.
     * @param text the String of text to render.
     * @param g the Graphics to which to render.
     * @param met the FontMetrics that describes the current font.
     */
    public void renderBottomText(String text, Graphics g, FontMetrics met) {
        if (bottomTextHeight >= 0) {
            g.setColor(Color.BLACK);
            g.fillRect(0, windowHeight - 29, windowWidth, 30);
            g.setColor(Color.WHITE);
            g.drawString(text, (windowWidth - met.stringWidth(text)) / 2, windowHeight - 12);
        }
    }

    /**
     * For displaying move-logic.
     * @param s the String to set the bottom text to.
     */
    public void setBottomText(String s) {
        forceBottomText = s;
    }

    /**
     * Resets the camera to be at (0, 0) and zoomed to fit the whole board with minimal blank space.
     */
    public void resetCamera() {
        width = b.getWidth();
        height = b.getHeight();
        windowWidth = this.getSize().width;
        windowHeight = this.getSize().height;
        camX = 0;
        camY = 0;
        unitSize = Math.min(windowWidth/width,(windowHeight-Math.max(bottomTextHeight, 0))/height);
        forceBottomText = moveSequence;
        repaint();
    }

    /**
     * Handles keyboard input. Accepted keys:
     * <ul>
     * <li>WASD keys are buffered into the moveSequence.
     * <li>BACK_SPACE removes the last character from moveSequence.
     * <li>ENTER causes the player to move according to the moveSequence (or prints an error if it doesn't work).
     * <li>ESCAPE ends the game early, without (necessarily) winning, and opens the load file dialog.
     * <li>R ends the game early, without (necessarily) winning, and creates a random board.
     * <li>F12 takes a screenshot of this panel.
     * <li>+/- keys zoom the camera in/out.
     * <li>Arrow keys move the camera.
     * <li>0 (the zero key) resets the camera to the default camX, camY, and unitSize.
     * </ul>
     */
    public class MyKeyAdapter extends KeyAdapter {
        private GraphicsPanel panel;

        public void setPanel(GraphicsPanel p) {
            panel = p;
        }

        public void keyPressed(KeyEvent e) {
            if (!ignoreKeyPresses) {
                switch (e.getKeyCode()) {
                    // WASD
                    case KeyEvent.VK_A:
                        moveSequence += "A";
                        renderBottomText(moveSequence);
                        break;
                    case KeyEvent.VK_D:
                        moveSequence += "D";
                        renderBottomText(moveSequence);
                        break;
                    case KeyEvent.VK_W:
                        moveSequence += "W";
                        renderBottomText(moveSequence);
                        break;
                    case KeyEvent.VK_S:
                        moveSequence += "S";
                        renderBottomText(moveSequence);
                        break;
                    // backspace
                    case KeyEvent.VK_BACK_SPACE:
                        if (moveSequence.length() > 0) {
                            moveSequence = moveSequence.substring(0, moveSequence.length() - 1);
                            renderBottomText(moveSequence);
                        }
                        break;
                    // confirm sequence
                    case KeyEvent.VK_ENTER:
                        if (!b.movePlayer(moveSequence)) renderBottomText("Invalid move.");
                        else if (!moveSequence.equals("")) {
                            repaint();
                        }
                        moveSequence = "";
                        break;
                    // load new board: file or random (which makes no sense to do if this is the Level Editor)
                    case KeyEvent.VK_ESCAPE:
                        if (!levelEditor) {
                            int loadState = SokobanNoShinobi.loadNewBoard(b);
                            switch (loadState) {
                                case -4:
                                    renderBottomText("Unknown move-logic. Check file."); break;
                                case -3:
                                    renderBottomText("File operation canceled."); break;
                                case -2:
                                    renderBottomText("File could not be loaded."); break;
                                case -1:
                                    renderBottomText("File formatted improperly."); break;
                                case 0:
                                    forceBottomText = "Using standard move-logic."; break;
                                case 1:
                                    forceBottomText = "Using Simple Barriers move-logic."; break;
                                case 2:
                                    forceBottomText = "Using Barriers move-logic."; break;
                            }
                            if (loadState >= 0) {
                                width = b.getWidth();
                                height = b.getHeight();
                                unitSize = Math.min(windowWidth/width,(windowHeight-Math.max(bottomTextHeight, 0))/height);
                                repaint();
                                ((JFrame) SwingUtilities.getWindowAncestor(panel)).setTitle("SokobanNoShinobi - " + SokobanNoShinobi.title);
                            }
                            moveSequence = "";
                        }
                        break;
                    case KeyEvent.VK_R:
                        if (!levelEditor) {
                            SokobanNoShinobi.resetWindowTitle();
                            b.loadRandomBoard(8,12,8,12);
                            moveSequence = "";
                            width = b.getWidth();
                            height = b.getHeight();
                            unitSize = Math.min(windowWidth/width,(windowHeight-Math.max(bottomTextHeight, 0))/height);
                            repaint();
                        }
                        break;
                }
            }
            int oldSize;
            switch (e.getKeyCode()) {
                // screenshot
                case KeyEvent.VK_F12:
                    try {
                        BufferedImage im = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        panel.paint(im.getGraphics());
                        String path = LocalDateTime.now().toString();
                        if ((new File(SokobanNoShinobi.getCodeFolder() + "Screenshots/")).exists()) path = SokobanNoShinobi.getCodeFolder() + "Screenshots/" + path.substring(0,13) + "-" + path.substring(14,16) + "-" + path.substring(17) + ".png";
                        else path = SokobanNoShinobi.getCodeFolder() + path.substring(0,13) + "-" + path.substring(14,16) + "-" + path.substring(17) + ".png";
                        ImageIO.write(im, "PNG", new File(path)); 
                        if (!ignoreKeyPresses) panel.renderBottomText("Saved to " + path);
                    } catch (Exception error) {
                        if (!ignoreKeyPresses) panel.renderBottomText("Screenshot could not be taken.");
                        error.printStackTrace();
                    }
                    break;
                // camera controls
                case KeyEvent.VK_EQUALS:
                    windowWidth = getSize().width;
                    windowHeight = getSize().height;
                    oldSize = unitSize;
                    unitSize += 10;
                    camX = unitSize * (2 * camX + windowWidth) / 2 / oldSize - windowWidth / 2;
                    camY = unitSize * (2 * camY + windowHeight) / 2 / oldSize - windowHeight / 2;
                    forceBottomText = moveSequence;
                    repaint();
                    break;
                case KeyEvent.VK_MINUS:
                    if (unitSize > 10) {
                        windowWidth = getSize().width;
                        windowHeight = getSize().height;
                        oldSize = unitSize;
                        unitSize -= 10;
                        camX = unitSize * (2 * camX + windowWidth) / 2 / oldSize - windowWidth / 2;
                        camY = unitSize * (2 * camY + windowHeight) / 2 / oldSize - windowHeight / 2;
                        forceBottomText = moveSequence;
                        repaint();
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    camX -= 10;
                    forceBottomText = moveSequence;
                    repaint();
                    break;
                case KeyEvent.VK_RIGHT:
                    camX += 10;
                    forceBottomText = moveSequence;
                    repaint();
                    break;
                case KeyEvent.VK_UP:
                    camY -= 10;
                    forceBottomText = moveSequence;
                    repaint();
                    break;
                case KeyEvent.VK_DOWN:
                    camY += 10;
                    forceBottomText = moveSequence;
                    repaint();
                    break;
                case KeyEvent.VK_0:
                    resetCamera();
                    break;
            }
        }
    }
}
