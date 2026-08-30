package SokobanNoShinobi;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;
import java.io.File;
import java.lang.ClassLoader;
import java.nio.charset.StandardCharsets;
/**
 * 06/07/2024
 * A game of Sokoban, but you can move farther than one tile at a time.
 * @author moormonkey
 */
public class SokobanNoShinobi {
    private static GraphicsFrame graphics;
    private static final Board GAME = new Board();
    public static String title;
    public static String author;
    public static void main(String[] args) {
        graphics = new GraphicsFrame(GAME);
    }

    /**
     * Takes a String and returns only the wasd keys contained within, in the original order. 
     * The input is expected to be all lowercase; 'W' 'A' 'S' 'D' will NOT appear in the result.
     * @param str The String to strip non-"wasd" chars from.
     * @return The input, but with only "wasd" chars remaining.
     */
    public static String wasdOnly(String str) {
        String result = "";
        String validChars = "wasd";
        for (int i = 0; i < str.length(); i++) {
            if (validChars.indexOf(str.charAt(i)) != -1) result += str.charAt(i);
        }
        return result;
    }

    /**
     * Asks the player for a file and loads a board from that file.
     * Does nothing if no file is selected.
     * @param b the Board object to load into.
     * @return either the moveLogic of the new Board (>=0) or an error (-1: file format, -2: read error, -3: operation canceled, -4: unknown move-logic)
     */
    public static int loadNewBoard(Board b) {
        JFileChooser fileChooser = new JFileChooser(getCodeFolder() + "Levels");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt file containing level data", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Choose a level.");
        int picked = fileChooser.showOpenDialog(graphics);
        if (picked == JFileChooser.APPROVE_OPTION) {
            try {
                // read the puzzle layout from a file
                Scanner readFromFile = new Scanner(fileChooser.getSelectedFile());
                ArrayList<String> gameCode = new ArrayList<String>();
                String titl = "";
                String auth = "";
                int moveLogic = 0;
                while (readFromFile.hasNextLine()) {
                    // ignore comments and @data
                    String row = readFromFile.nextLine();
                    if (row.equals("")) continue; // ignore empty lines
                    if (row.charAt(0) != '/' && row.charAt(0) != '@') {
                        // ignore spaces
                        while (row.indexOf(' ') != -1) {
                            row = row.substring(0,row.indexOf(' ')) + row.substring(row.indexOf(' ') + 1);
                        }
                        gameCode.add(row);
                    } else if (row.length() > 7 && row.substring(0, 7).toLowerCase().equals("@title@")) {
                        titl = row.substring(7);
                    } else if (row.length() > 8 && row.substring(0, 8).toLowerCase().equals("@author@")) {
                        auth = row.substring(8);
                    } else if (row.toLowerCase().equals("@movelogic@simplebarriers")) {
                        moveLogic = 1;
                    } else if (row.toLowerCase().equals("@movelogic@barriers")) {
                        moveLogic = 2;
                    }
                }
                readFromFile.close();
                if (b.loadBoard(gameCode, moveLogic)) {
                    title = titl;
                    author = auth;
                    if (moveLogic < 0 || moveLogic > 2) return -4;
                    else return moveLogic;
                }
                else return -1;
            } catch (Exception e) {
                e.printStackTrace();
                return -2;
            }
        } else {
            return -3;
        }
    }

    /**
     * Resets the window title to "SokobanNoShinobi".
     */
    public static void resetWindowTitle() {
        graphics.setTitle("SokobanNoShinobi");
    }

    /**
     * @return the folder that contains SokobanNoShinobi.class, which should be the same as the folder that contains all of the other .class files for this game.
     */
    public static String getCodeFolder() {
        String pathToSoko = SokobanNoShinobi.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        pathToSoko = java.net.URLDecoder.decode(pathToSoko, StandardCharsets.UTF_8);
        return pathToSoko.substring(0, pathToSoko.lastIndexOf('/') + 1);
    }
}