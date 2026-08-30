package SokobanNoShinobi;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.time.LocalDateTime;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
/**
 * 12/28/2024
 * Contains the graphics for the Level Editor.
 * @author moormonkey
 */
public class LevelEditorFrame extends JFrame {
    GraphicsPanel boardPanel;
    private JTextField[] textFields; // 0 - title, 1 - author, 2 - width (int), 3 - height (int), 4 - maxSteps of a single tile (int), 5 - tile x (int), 6 - tile y (int)
    private JCheckBox[] checkBoxes; // both are for single tiles; 0 - boxDestination, 1 - through
    private JComboBox<String> epb; // empty/player/box
    private JComboBox<String> moveLogic;
    private Board b;

    /**
     * Creates the window inside which the Level Editor's graphics are contained.
     * @param b the Board object representing the level being made.
     */
    LevelEditorFrame(Board b) {
        // setup for adding stuff
        JButton button;
        JLabel label;
        JSeparator separator;
        textFields = new JTextField[7];
        checkBoxes = new JCheckBox[2];
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        Dimension textFieldSize = new Dimension(100, 20);
        this.b = b;

        // need to be defined early for selecting new tile behavior
        textFields[4] = new JTextField();
        epb = new JComboBox<String>(new String[] {"Empty", "Player", "Box"});
        checkBoxes[0] = new JCheckBox("Box Destination");
        checkBoxes[1] = new JCheckBox("Through");

        // display the Board as we edit it
        boardPanel = new GraphicsPanel(b, true);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridheight = 16;
        this.add(boardPanel, c);

        // side column elements
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridheight = 1;

        // level information: title, author, width, height, moveLogic
        label = new JLabel("Title");
        c.gridx = 1;
        this.add(label, c);

        textFields[0] = new JTextField();
        c.gridx = 2;
        this.add(textFields[0], c);

        c.gridy++;

        label = new JLabel("Author");
        c.gridx = 1;
        this.add(label, c);

        textFields[1]  = new JTextField();
        c.gridx = 2;
        this.add(textFields[1], c);

        c.gridy++;

        label = new JLabel("Width");
        c.gridx = 1;
        this.add(label, c);

        textFields[2] = new JTextField();
        textFields[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x = parseIntSafe(textFields[2].getText(), 1);
                if (x > 0) {
                    b.setWidth(x);
                    boardPanel.repaint();
                } else {
                    textFields[2].setText("" + b.getWidth());
                    JOptionPane.showMessageDialog(null, "Enter only integers > 0");
                }
            }
        });
        c.gridx = 2;
        this.add(textFields[2], c);

        c.gridy++;

        label = new JLabel("Height");
        c.gridx = 1;
        this.add(label, c);

        textFields[3] = new JTextField();
        textFields[3].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x = parseIntSafe(textFields[3].getText(), 1);
                if (x > 0) {
                    b.setHeight(x);
                    boardPanel.repaint();
                } else {
                    textFields[3].setText("" + b.getHeight());
                    JOptionPane.showMessageDialog(null, "Enter only integers > 0");
                }
            }
        });
        c.gridx = 2;
        this.add(textFields[3], c);

        moveLogic = new JComboBox<String>(new String[] {"Set move-logic...", "0: Standard", "1: SimpleBarriers", "2: Barriers"});
        c.gridx = 1;
        c.gridy++;
        c.gridwidth = 2;
        this.add(moveLogic, c);

        c.gridx = 1;
        c.gridwidth = 2;

        // selected tile information: position, empty/player/box, boxDestination, through, maxSteps
        separator = new JSeparator();
        c.gridy++;
        c.ipadx = 200;
        this.add(separator, c);
        
        c.ipadx = 0;

        label = new JLabel("Selected tile");
        c.gridy++;
        this.add(label, c);

        c.gridy++;
        c.gridwidth = 1;

        label = new JLabel("x");
        c.gridx = 1;
        this.add(label, c);

        textFields[5] = new JTextField();
        textFields[5].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x = parseIntSafe(textFields[5].getText(), 0);
                if (x >= 0 && x < b.getWidth()) {
                    b.setSelectX(x);
                    boardPanel.repaint();
                    refreshRightColumn(false);
                } else {
                    textFields[5].setText("" + b.getSelectX());
                    JOptionPane.showMessageDialog(null, "Enter only integers: 0 <= x < " + b.getWidth() + " (board width)");
                }
            }
        });
        c.gridx = 2;
        this.add(textFields[5], c);

        c.gridy++;

        label = new JLabel("y");
        c.gridx = 1;
        this.add(label, c);

        textFields[6] = new JTextField();
        textFields[6].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x = parseIntSafe(textFields[6].getText(), 0);
                if (x >= 0 && x < b.getHeight()) {
                    b.setSelectY(x);
                    boardPanel.repaint();
                    refreshRightColumn(false);
                } else {
                    textFields[6].setText("" + b.getSelectY());
                    JOptionPane.showMessageDialog(null, "Enter only integers: 0 <= x < " + b.getHeight() + " (board height)");
                }
            }
        });
        c.gridx = 2;
        this.add(textFields[6], c);

        c.gridwidth = 2;
        c.gridx = 1;

        epb.addActionListener(new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch ((String) epb.getSelectedItem()) {
                    case "Empty":
                        b.setSelectFill(false, false);
                        break;
                    case "Player":
                        b.setSelectFill(false, true);
                        break;
                    case "Box":
                        b.setSelectFill(true, false);
                        break;
                    default:
                        break;
                }
                boardPanel.repaint();
            }
        });
        c.gridy++;
        this.add(epb, c);

        checkBoxes[0].setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxes[0].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                b.getSelectTile().setDest(e.getStateChange() == ItemEvent.SELECTED);
                boardPanel.repaint();
            }
        });
        c.gridy++;
        this.add(checkBoxes[0], c);

        checkBoxes[1].setHorizontalTextPosition(SwingConstants.LEFT);
        checkBoxes[1].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                b.getSelectTile().setThrough(e.getStateChange() == ItemEvent.SELECTED);
                boardPanel.repaint();
            }
        });
        c.gridy++;
        this.add(checkBoxes[1], c);

        c.gridy++;
        c.gridwidth = 1;

        label = new JLabel("Max Steps");
        c.insets = new Insets(0,5,0,5);
        this.add(label, c);

        c.insets = new Insets(0,0,0,0);

        textFields[4].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x = parseIntSafe(textFields[4].getText(), -1);
                if (x >= 0) {
                    b.getSelectTile().setMaxSteps(x);
                    boardPanel.repaint();
                } else {
                    textFields[4].setText("no limit");
                    b.getSelectTile().setMaxSteps(-1);
                    boardPanel.repaint();
                }
            }
        });
        c.gridx = 2;
        this.add(textFields[4], c);

        c.gridx = 1;

        // buttons: save, load, play
        separator = new JSeparator();
        c.gridy++;
        c.gridwidth = 2;
        c.ipadx = 200;
        this.add(separator, c);

        c.gridy++;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.NORTH;

        button = new JButton("SAVE");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (b.getPlayerX() < 0 || b.getPlayerY() < 0) JOptionPane.showMessageDialog(null, "Place the player in a valid position on the board.");
                else if (moveLogic.getSelectedIndex() == 0) JOptionPane.showMessageDialog(null, "Select a valid move-logic.");
                else {
                    String code = (String) moveLogic.getSelectedItem();
                    code = "@Title@" + textFields[0].getText() + "\n@Author@" + textFields[1].getText() + "\n@Date@" + 
                        LocalDateTime.now().getMonthValue() + "/" + LocalDateTime.now().getDayOfMonth() + "/" + LocalDateTime.now().getYear() +
                        "\n@MoveLogic@" + code.substring(code.indexOf(' ') + 1) + "\n" + b.getWidth() + ", " + b.getHeight();
                    for (int i = 0; i < b.getHeight(); i++) {
                        code += "\n";
                        for (int j = 0; j < b.getWidth(); j++) {
                            // P/B/E
                            if (b.getBoxes()[i][j]) code += "B";
                            else if (b.getPlayerX() == j && b.getPlayerY() == i) code += "P";
                            else code += "E";
                            // FloorTile info
                            FloorTile current = b.getFloor()[i][j];
                            if (current.isDestination()) code += "T";
                            else code += "F";
                            if (current.isThrough()) code += "T";
                            else code += "F";
                            code += current.maxSteps() + " ";
                        }
                    }

                    JFileChooser fileChooser = new JFileChooser(SokobanNoShinobi.getCodeFolder() + "Levels");
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt file for level data", "txt");
                    fileChooser.setFileFilter(filter);
                    int picked = fileChooser.showSaveDialog(null);
                    if (picked == JFileChooser.APPROVE_OPTION) {
                        String selected = fileChooser.getSelectedFile().toString();
                        if (!selected.substring(selected.length() - 4).equals(".txt")) selected += ".txt";
                        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(selected), "utf-8"))) {
                            writer.write(code);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error with saving.");
                        }
                    }
                }
            }
        });
        c.insets = new Insets(0,0,0,80);
        this.add(button, c);

        button = new JButton("PLAY");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (b.getPlayerX() < 0 || b.getPlayerY() < 0) JOptionPane.showMessageDialog(null, "Place the player in a valid position on the board.");
                else if (moveLogic.getSelectedIndex() == 0) JOptionPane.showMessageDialog(null, "Select a valid move-logic.");
                else LevelEditor.play();
            }
        });
        c.insets = new Insets(0,80,0,0);
        this.add(button, c);

        button = new JButton("LOAD FROM FILE");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int loadState = SokobanNoShinobi.loadNewBoard(b);
                if (loadState >= 0) {
                    moveLogic.setSelectedIndex(loadState + 1);
                    textFields[0].setText(SokobanNoShinobi.title);
                    textFields[1].setText(SokobanNoShinobi.author);
                    textFields[2].setText("" + b.getWidth());
                    textFields[3].setText("" + b.getHeight());
                    boardPanel.resetCamera();
                    refreshRightColumn(true);
                    repaint();
                } else {
                    switch (loadState) {
                        case -4:
                            JOptionPane.showMessageDialog(null, "Unknown move-logic. Check file."); break;
                        case -2:
                            JOptionPane.showMessageDialog(null, "File could not be loaded."); break;
                        case -1:
                            JOptionPane.showMessageDialog(null, "File formatted improperly."); break;
                        default:
                            break;
                    }
                }
            }
        });
        c.insets = new Insets(0, 0, 0, 0);
        c.gridy++;
        this.add(button, c);

        // set sizes and initial text of text fields
        for (JTextField t : textFields) {
            t.setMaximumSize(textFieldSize);
            t.setMinimumSize(textFieldSize);
            t.setPreferredSize(textFieldSize);
        }
        refreshRightColumn(true);
        textFields[2].setText("3");
        textFields[3].setText("3");

        // finalize the frame
        this.setTitle("SokobanNoShinobi Level Editor");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                boardPanel.repaint();
            }
        });
        boardPanel.resetCamera();
    }

    public void refreshRightColumn(boolean refreshSelectXY) {
        FloorTile select = b.getSelectTile();
        // maxSteps, boxDest, through, player/box/empty, selected tile coordinates
        textFields[4].setText(select.maxSteps() >= 0? "" + select.maxSteps() : "no limit");
        checkBoxes[0].setSelected(select.isDestination());
        checkBoxes[1].setSelected(select.isThrough());
        epb.setSelectedIndex(b.selectTileItem());
        if (refreshSelectXY) {
            textFields[5].setText("" + b.getSelectX());
            textFields[6].setText("" + b.getSelectY());
        }
    }

    /**
     * Runs Integer.parseInt() but with a try-catch to prevent crashes and a specified minimum value.
     * @param s the String to parse the int from.
     * @param min the minimum value.
     * @return the parsed int if successful; otherwise -1 if min >= 0 or Integer.MIN_VALUE (-2 ^ 31) if min < 0.
     */
    public int parseIntSafe(String s, int min) {
        try {
            int result = Integer.parseInt(s);
            if (result >= min) return result;
            else return min >= 0? -1 : Integer.MIN_VALUE;
        } catch (Exception e) {
            return min >= 0? -1 : Integer.MIN_VALUE;
        }
    }

    /**
     * @return the selected move-logic (index).
     */
    public int getMoveLogic() {
        return moveLogic.getSelectedIndex() - 1;
    }
}
