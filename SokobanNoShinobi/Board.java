package SokobanNoShinobi;
import java.util.ArrayList;
/**
 * 06/07/2024
 * A board in my Sokoban game.
 * @author moormonkey
 */
public class Board {
    // the width and height of the Board.
    private int width;
    private int height;

    // each tile of the board, and what's on it.
    private FloorTile[][] floor;
    // each box on the board (false = no box here, true = box here)
    private boolean[][] boxes;

    // the player's position on the board.
    private int playerX;
    private int playerY;
    
    // Level Editor: which tile is selected for editing?
    private int selectX;
    private int selectY;

    /**
     * The way the player and boxes move across the board. 
     * <ul>
     * <li> 0: Standard
     * <li> 1: Simple Barriers
     * <li> 2: Barriers
     */
    private int moveLogic;

    /**
     * Creates a Board with nothing.
     */
    public Board() {}

    /**
     * Creates an empty, square board, with the player at -1, 0.
     * @param size one side length of the square.
     */
    public Board(int size) {
        width = size;
        height = size;
        playerX = -1;
        playerY = 0;
        floor = new FloorTile[size][size];
        boxes = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                floor[i][j] = new FloorTile();
                boxes[i][j] = false;
            }
        }
        selectX = 0;
        selectY = 0;
    }

    /**
     * Resizes and fills this board randomly.
     * @param wMin Minimum possible width.
     * @param wMax Maximum possible width.
     * @param hMin Minimum possible height.
     * @param hMax Maximum possible height.
     */
    public void loadRandomBoard(int wMin, int wMax, int hMin, int hMax) {
        // random width, height, player position
        width = (int)(Math.random() * (wMax - wMin + 1)) + wMin;
        height = (int)(Math.random() * (hMax - hMin + 1)) + hMin;
        playerX = (int)(Math.random() * width);
        playerY = (int)(Math.random() * height);
        // set the size of these arrays
        floor = new FloorTile[height][width];
        boxes = new boolean[height][width];
        // standard movement logic
        moveLogic = 0;
        // fill floor and boxes out with random values
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                boolean isPlayerPos = playerX == col && playerY == row;
                int maxSteps = (int)(Math.random() * 11) - 1;
                if (isPlayerPos && maxSteps == 0) maxSteps++;
                floor[row][col] = new FloorTile(Math.random() < .5 && maxSteps != -1, Math.random() < .3 && maxSteps != 0, maxSteps);
                // not edge, not wall, not player, and 2/5 chance
                boxes[row][col] = !((row == 0 || row == height - 1) || (col == 0 || col == width - 1)) && maxSteps != 0 && !isPlayerPos && Math.random() < .4;
            }
        }
    }

    /**
     * Loads a Board onto this instance from a given code.
     * @param code An ArrayList<String> representation of a board. Each String is a row.
     * @return if the operation was successful (true) or encountered an error (false).
     */
    public boolean loadBoard(ArrayList<String> code, int moveLogic) {
        // counts the rows loaded; starts at -1 because the first line from file is the width, height.
        int count = -1;
        try {
            if (code.size() == 0) throw new Exception("No data could be loaded from the provided file.");
            for (String row : code) {
                // setup pre-line
                if (count == -1) {
                    width = Integer.parseInt(row.substring(0,row.indexOf(',')));
                    height = Integer.parseInt(row.substring(row.indexOf(',')+1));
                    floor = new FloorTile[height][width];
                    boxes = new boolean[height][width];
                    count++;
                    continue;
                }
                int startIdx = 0;
                for (int j = 0; j < width; j++) {
                    // get rid of null
                    floor[count][j] = new FloorTile();

                    // startIdx: player, box, or empty
                    if (row.charAt(startIdx) == 'P') {
                        playerX = j;
                        playerY = count;
                    } else if (row.charAt(startIdx) == 'B') {
                        boxes[count][j] = true;
                    }

                    // startIdx + 1: is this a destination tile?
                    floor[count][j].setDest(row.charAt(startIdx + 1) == 'T');

                    // startIdx + 2: through?
                    floor[count][j].setThrough(row.charAt(startIdx + 2) == 'T');

                    // startIdx + 3 to next P/B/E: max steps
                    int nextPBE = row.length();
                    for (int i = 0; i < 3; i++) {
                        int k = row.indexOf("PBE".charAt(i), startIdx + 3);
                        if (k != -1 && k < nextPBE) {
                            nextPBE = k;
                        }
                    }
                    floor[count][j].setMaxSteps(Integer.parseInt(row.substring(startIdx + 3, startIdx = nextPBE)));
                }
                // only reach this point if this line wasn't a comment :)
                count++;
            }
            this.moveLogic = moveLogic;
            if (selectX >= width) selectX = width - 1;
            if (selectY >= height) selectY = height - 1;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     * Decides which moveLogic to use, then moves accordingly.
     * @param sequence a String representing an ordered list of directions in WASD.
     * @return if the move could be executed/is valid.
    */
    public boolean movePlayer(String sequence) {
        switch (moveLogic) {
            case 0:
                return movePlayerStandard(sequence);
            case 1:
                return movePlayerSimpleBarriers(sequence);
            case 2:
                return movePlayerBarriers(sequence);
            default:
                System.out.println("Issue with moveLogic. Please troubleshoot level file.");
                return false;
        }
    }

    /**
     * Moves the player's position, according to WASD.
     * Cancels the whole sequence if anything invalid happens anywhere.
     * @param sequence a String representing an ordered list of directions in WASD. 
     * @return if the move could be executed/is valid.
     */
    public boolean movePlayerStandard(String sequence) {
        int prevX = playerX;
        int prevY = playerY;
        sequence = sequence.toLowerCase();
        // move the player, if possible
        for (int i = 0; i < sequence.length(); i++) {
            char thisStep = sequence.charAt(i);
            if (thisStep == 'w') {
                playerY -= 1;
            } else if (thisStep == 'a') {
                playerX -= 1;
            } else if (thisStep == 's') {
                playerY += 1;
            } else if (thisStep == 'd') {
                playerX += 1;
            }
            if (playerY < 0 || playerY >= height || playerX < 0 || playerX >= width || floor[playerY][playerX].tooFar(i + 1, i == sequence.length() - 1)) {
                // invalid
                playerX = prevX;
                playerY = prevY;
                return false;
            }
        }
        // move the box
        if (boxes[playerY][playerX]) {
            int boxX = playerX;
            int boxY = playerY;
            for (int i = 0; i < sequence.length(); i++) {
                char thisStep = sequence.charAt(i);
                if (thisStep == 'w') {
                    boxY -= 1;
                } else if (thisStep == 'a') {
                    boxX -= 1;
                } else if (thisStep == 's') {
                    boxY += 1;
                } else if (thisStep == 'd') {
                    boxX += 1;
                }
                boolean lastStep = i == sequence.length() - 1;
                if (boxY < 0 || boxY >= height || boxX < 0 || boxX >= width || floor[boxY][boxX].tooFar(i + 1, lastStep) || lastStep && boxes[boxY][boxX]) {
                    // invalid
                    boxX = playerX;
                    boxX = playerY;
                    playerX = prevX;
                    playerY = prevY;
                    return false;
                }
            }
            boxes[playerY][playerX] = false;
            boxes[boxY][boxX] = true;
        }
        return true;
    }

    /**
     * Moves the player's position, according to WASD.
     * If the player does anything invalid, automatically cancels the entire sequence.
     * If a box hits a through-wall or goes out of bounds, it cancels that single step.
     * If a box lands on a wall or another box, it follows the given sequence in reverse until it reaches an open tile.
     * Move count on the way back equals total steps forward PLUS total steps back (so total distance traveled, essentially)
     * If the box cannot land, the move is invalid.
     * @param sequence a String representing an ordered list of directions in WASD. 
     * @return if the move could be executed/is valid.
     */
    public boolean movePlayerSimpleBarriers(String sequence) {
        int prevX = playerX;
        int prevY = playerY;
        sequence = sequence.toLowerCase();
        // move the player, if possible
        for (int i = 0; i < sequence.length(); i++) {
            char thisStep = sequence.charAt(i);
            if (thisStep == 'w') {
                playerY -= 1;
            } else if (thisStep == 'a') {
                playerX -= 1;
            } else if (thisStep == 's') {
                playerY += 1;
            } else if (thisStep == 'd') {
                playerX += 1;
            }
            if (playerY < 0 || playerY >= height || playerX < 0 || playerX >= width || floor[playerY][playerX].tooFar(i + 1, i == sequence.length() - 1)) {
                // invalid
                playerX = prevX;
                playerY = prevY;
                return false;
            }
        }
        // move the box
        if (boxes[playerY][playerX]) {
            int boxX = playerX;
            int boxY = playerY;
            for (int i = 0; i < sequence.length(); i++) {
                char thisStep = sequence.charAt(i);
                // execute sequence but ignore through-invalid moves
                if (thisStep == 'w' && boxY > 0 && !floor[boxY - 1][boxX].tooFar(i + 1, false)) {
                    boxY -= 1;
                } else if (thisStep == 'a' && boxX > 0 && !floor[boxY][boxX - 1].tooFar(i + 1, false)) {
                    boxX -= 1;
                } else if (thisStep == 's' && boxY < height - 1 && !floor[boxY + 1][boxX].tooFar(i + 1, false)) {
                    boxY += 1;
                } else if (thisStep == 'd' && boxX < width - 1 && !floor[boxY][boxX + 1].tooFar(i + 1, false)) {
                    boxX += 1;
                }
            }
            // while we can't land here, move back a step
            int count = 0;
            while ((floor[boxY][boxX].tooFar(sequence.length() + count, true) || boxes[boxY][boxX] || boxX == playerX && boxY == playerY) && count < sequence.length()) {
                char thisStep = sequence.charAt(sequence.length() - count - 1);
                // execute sequence in reverse, ignore through-invalid moves
                if (thisStep == 's' && boxY > 0 && !floor[boxY - 1][boxX].tooFar(sequence.length() + count + 1, false)) {
                    boxY -= 1;
                } else if (thisStep == 'd' && boxX > 0 && !floor[boxY][boxX - 1].tooFar(sequence.length() + count + 1, false)) {
                    boxX -= 1;
                } else if (thisStep == 'w' && boxY < height - 1 && !floor[boxY + 1][boxX].tooFar(sequence.length() + count + 1, false)) {
                    boxY += 1;
                } else if (thisStep == 'a' && boxX < width - 1 && !floor[boxY][boxX + 1].tooFar(sequence.length() + count + 1, false)) {
                    boxX += 1;
                }
                count++;
            }
            // final position. Return false if we can't land here.
            if (boxes[boxY][boxX] || floor[boxY][boxX].tooFar(sequence.length() + count, true) || boxX == playerX && boxY == playerY) {
                // invalid
                boxX = playerX;
                boxX = playerY;
                playerX = prevX;
                playerY = prevY;
                return false;
            }
            boxes[playerY][playerX] = false;
            boxes[boxY][boxX] = true;
        }
        return true;
    }

    /**
     * Moves the player's position, according to WASD.
     * If the player does anything invalid, automatically cancels the entire sequence.
     * If a box hits a through-wall or goes out of bounds, it cancels that single step.
     * If a box lands on a wall or another box, it follows ONLY THE VALID STEPS TAKEN in reverse until it reaches an open tile.
     * Move count on the way back equals total steps forward PLUS total steps back (so total distance traveled, essentially)
     * If the box cannot land, the move is invalid.
     * @param sequence a String representing an ordered list of directions in WASD. 
     * @return if the move could be executed/is valid.
     */
    public boolean movePlayerBarriers(String sequence) {
        int prevX = playerX;
        int prevY = playerY;
        sequence = sequence.toLowerCase();
        // move the player, if possible
        for (int i = 0; i < sequence.length(); i++) {
            char thisStep = sequence.charAt(i);
            if (thisStep == 'w') {
                playerY -= 1;
            } else if (thisStep == 'a') {
                playerX -= 1;
            } else if (thisStep == 's') {
                playerY += 1;
            } else if (thisStep == 'd') {
                playerX += 1;
            }
            if (playerY < 0 || playerY >= height || playerX < 0 || playerX >= width || floor[playerY][playerX].tooFar(i + 1, i == sequence.length() - 1)) {
                // invalid
                playerX = prevX;
                playerY = prevY;
                return false;
            }
        }
        // move the box
        if (boxes[playerY][playerX]) {
            int boxX = playerX;
            int boxY = playerY;
            for (int i = 0; i < sequence.length(); i++) {
                char thisStep = sequence.charAt(i);
                // execute sequence but ignore through-invalid moves
                if (thisStep == 'w') {
                    if (boxY > 0 && !floor[boxY - 1][boxX].tooFar(i + 1, false)) boxY -= 1; // valid
                    else {
                        sequence = sequence.substring(0, i) + sequence.substring(i+1);
                        i--;
                    }
                } else if (thisStep == 'a') {
                    if (thisStep == 'a' && boxX > 0 && !floor[boxY][boxX - 1].tooFar(i + 1, false)) boxX -= 1;
                    else {
                        sequence = sequence.substring(0, i) + sequence.substring(i+1);
                        i--;
                    }
                } else if (thisStep == 's') {
                    if (boxY < height - 1 && !floor[boxY + 1][boxX].tooFar(i + 1, false)) boxY += 1;
                    else {
                        sequence = sequence.substring(0, i) + sequence.substring(i+1);
                        i--;
                    }
                } else if (thisStep == 'd') {
                    if (boxX < width - 1 && !floor[boxY][boxX + 1].tooFar(i + 1, false)) boxX += 1;
                    else {
                        sequence = sequence.substring(0, i) + sequence.substring(i+1);
                        i--;
                    }
                }
            }
            // while we can't land here, move back a step
            int count = 0;
            while ((floor[boxY][boxX].tooFar(sequence.length() + count, true) || boxes[boxY][boxX] || boxX == playerX && boxY == playerY) && count < sequence.length()) {
                char thisStep = sequence.charAt(sequence.length() - count - 1);
                // execute sequence in reverse, ignore through-invalid moves
                if (thisStep == 's' && boxY > 0 && !floor[boxY - 1][boxX].tooFar(sequence.length() + count + 1, false)) {
                    boxY -= 1;
                } else if (thisStep == 'd' && boxX > 0 && !floor[boxY][boxX - 1].tooFar(sequence.length() + count + 1, false)) {
                    boxX -= 1;
                } else if (thisStep == 'w' && boxY < height - 1 && !floor[boxY + 1][boxX].tooFar(sequence.length() + count + 1, false)) {
                    boxY += 1;
                } else if (thisStep == 'a' && boxX < width - 1 && !floor[boxY][boxX + 1].tooFar(sequence.length() + count + 1, false)) {
                    boxX += 1;
                }
                count++;
            }
            // final position. Return false if we can't land here.
            if (boxes[boxY][boxX] || floor[boxY][boxX].tooFar(sequence.length() + count, true) || boxX == playerX && boxY == playerY) {
                // invalid
                boxX = playerX;
                boxX = playerY;
                playerX = prevX;
                playerY = prevY;
                return false;
            }
            boxes[playerY][playerX] = false;
            boxes[boxY][boxX] = true;
        }
        return true;
    }

    /**
     * @return the board state, to be printed.
     */
    @Override
    public String toString() {
        String result = "";
        for (int row = 0; row < height*3; row++) { // actually row times three
            for (int col = 0; col < width; col++) {
                if (row % 3 == 0) {
                    // box/player and boxEats information
                    if (boxes[row/3][col]) result += "B";
                    else if (row/3 == playerY && col == playerX) result += "P";
                    else result += " ";
                    if (floor[row/3][col].isDestination()) result += "D";
                    else result += " ";
                } else if (row % 3 == 1) {
                    // through and maxSteps information
                    FloorTile current = floor[row/3][col];
                    if (current.isThrough()) result += "T";
                    else result += " ";
                    if (current.maxSteps() >= 0) result += current.maxSteps();
                    else result += " ";
                } else {
                    result += "__";
                }
                result += "|";
            }
            result += "\n";
        }
        return result;
    }

    /** 
     * Checks if the player won. A game is considered "won" if every box-destination tile is covered by a box OR every box is at a destination.
     */
    public boolean checkWin() {
        boolean floorNotCovered = false;
        boolean misplacedBoxes = false;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (boxes[row][col] && !floor[row][col].isDestination()) {
                    misplacedBoxes = true;
                    if (floorNotCovered) return false;
                } else if (!boxes[row][col] && floor[row][col].isDestination()) {
                    floorNotCovered = true;
                    if (misplacedBoxes) return false;
                }
            }
        }
        return true;
    }

    /**
     * @return the width of the Board.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height of the Board.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return A 2D array of booleans, representing if a given tile has a box (true) or not (false) on this Board.
     */
    public boolean[][] getBoxes() {
        return boxes;
    }

    /**
     * @return A 2D array of FloorTile objects, representing the floors/walls of this Board.
     */
    public FloorTile[][] getFloor() {
        return floor;
    }

    /**
     * @return the player's x-position (column) on the Board.
     */
    public int getPlayerX() {
        return playerX;
    }

    /**
     * @return the player's y-position (row) on the Board. Lower down == larger number.
     */
    public int getPlayerY() {
        return playerY;
    }

    /**
     * @return the moveLogic of this Board.
     */
    public int getMoveLogic() {
        return moveLogic;
    }
    
    /**
     * For the Level Editor.
     * @return the selected tile's X coordinate.
     */
    public int getSelectX() {
        return selectX;
    }

    /**
     * For the Level Editor.
     * @return the selected tile's Y coordinate.
     */
    public int getSelectY() {
        return selectY;
    }

    /**
     * For the Level Editor.
     * @return the selected floorTile.
     */
    public FloorTile getSelectTile() {
        return floor[selectY][selectX];
    }

    /**
     * Sets the move-logic of this Board.
     * @param m the value to set the moveLogic to.
     */
    public void setMoveLogic(int m) {
        moveLogic = m;
    }

    /**
     * For the Level Editor.
     * Sets the box- and player-presence of the selected tile.
     * @param box should there be a box here?
     * @param player should there be a player here?
     */
    public void setSelectFill(boolean box, boolean player) {
        boxes[selectY][selectX] = box;
        if (player) {
            playerX = selectX;
            playerY = selectY;
        } else if (playerX == selectX && playerY == selectY) {
            playerX = -1;
        }
    }

    /**
     * For the Level Editor.
     * @param x the value to set selectX to.
     */
    public void setSelectX(int x) {
        selectX = x;
    }

    /**
     * For the Level Editor.
     * @param y the value to set selectY to.
     */
    public void setSelectY(int y) {
        selectY = y;
    }

    /**
     * For the Level Editor.
     * Changes the width of this Board.
     * @param w the new width to use.
     */
    public void setWidth(int w) {
        if (width == w || w < 1) return;
        for (int i = 0; i < height; i++) {
            boolean[] newRowBoxes = new boolean[w];
            FloorTile[] newRowFloor = new FloorTile[w];
            for (int j = 0; j < w; j++) {
                newRowBoxes[j] = j < width && boxes[i][j];
                newRowFloor[j] = j < width? floor[i][j] : new FloorTile();
            }
            boxes[i] = newRowBoxes;
            floor[i] = newRowFloor;
        }
        if (playerX >= w) playerX = w - 1;
        if (selectX >= w) selectX = w - 1;
        width = w;
    }

    /**
     * For the Level Editor.
     * Changes the height of this Board.
     * @param h the new height to use.
     */
    public void setHeight(int h) {
        if (height == h || h < 1) return;
        FloorTile[][] newFloor = new FloorTile[h][width];
        boolean[][] newBoxes = new boolean[h][width];
        for (int i = 0; i < h && i < height; i++) {
            newFloor[i] = floor[i];
            newBoxes[i] = boxes[i];
        }
        for (int i = height; i < h; i++) {
            for (int j = 0; j < width; j++) {
                newBoxes[i][j] = false;
                newFloor[i][j] = new FloorTile();
            }
        }
        if (playerY >= h) playerY = h - 1;
        if (selectY >= h) selectY = h - 1;
        floor = newFloor;
        boxes = newBoxes;
        height = h;
    }

    /**
     * For the Level Editor.
     * @return what's on the tile (0: empty, 1: player, 2: box)
     */
    public int selectTileItem() {
        if (boxes[selectY][selectX]) return 2; // there is a box on the selected tile
        if (playerX == selectX && playerY == selectY) return 1; // there is a player on the selected tile
        return 0; // selected tile is empty
    }

    @Override
    public Board clone() {
        Board result = new Board();
        result.width = width;
        result.height = height;
        result.boxes = new boolean[height][width];
        result.floor = new FloorTile[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                result.boxes[i][j] = boxes[i][j];
                result.floor[i][j] = floor[i][j].clone();
            }
        }
        result.moveLogic = moveLogic;
        result.playerX = playerX;
        result.playerY = playerY;
        result.selectX = -1;
        result.selectY = 0;
        return result;
    }
}