package SokobanNoShinobi;
/**
 * 06/07/2024
 * A floor tile in my Sokoban game.
 * @author moormonkey
 */
public class FloorTile {
    private boolean through;
    private boolean boxDestination;
    private int maxSteps;

    /** 
     * Creates a Tile with default values.
     * through = false
     * boxDestination = false
     * maxSteps = -1
     */
    public FloorTile() {
        through = false;
        boxDestination = false;
        maxSteps = -1;
    }

    /**
     * Creates a Tile.
     * @param through Should maxSteps be considered during a movement (true), or only after (false)?
     * @param dest is this tile a destination for a box?
     * @param steps What is the maximum number of steps that can be taken to (through) this tile? 0 -> tile acts as wall, -1 -> no max.
     */
    public FloorTile(boolean through, boolean dest, int steps) {
        this.through = through;
        this.boxDestination = dest;
        maxSteps = steps;
    }

    /**
     * Checks if the given number of steps is valid for this tile.
     * Accounts for through-ness.
     * @param steps how many steps the player moved.
     * @param lastStep is it the last step? (false -> only consider through tiles)
     * @return If the move is invalid (true), meaning that this is 
     * a through tile or the final step and steps is greater than the maximum.
     */
    public boolean tooFar(int steps, boolean lastStep) {
        return maxSteps >= 0 && (through || lastStep) && steps > maxSteps;
    }

    /**
     * @return is this Tile a through-floor (true) or an end-only floor (false)?
     */
    public boolean isThrough() {
        return through;
    }

    /**
     * @return is this Tile a destination for boxes?
     */
    public boolean isDestination() {
        return boxDestination;
    }

    /**
     * @return the maxSteps of this Tile, or -1 for uncapped.
     */
    public int maxSteps() {
        return maxSteps;
    }

    /**
     * Sets this Tile's through attribute.
     * @param through what to set through to.
     */
    public void setThrough(boolean through) {
        this.through = through;
    }

    /**
     * Sets whether this Tile is a destination for a box.
     * @param dest what to set boxDestination to.
     */
    public void setDest(boolean dest) {
        this.boxDestination = dest;
    }

    /**
     * Sets the max number of steps that can be taken to (throught) this Tile.
     * 0 -> wall, -1 -> infinitely many steps
     * @param maxSteps what to set maxSteps to.
     */
    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    @Override
    public FloorTile clone() {
        return new FloorTile(through, boxDestination, maxSteps);
    }
}
