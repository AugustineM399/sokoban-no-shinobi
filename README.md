# sokoban-no-shinobi
A Sokoban spinoff, with a special gimmick that the player can move more than one tile at a time.

Created 06/24/2024, last updated 08/30/2026
## Use
### Building
Run `javac` and `java` commands from the working directory just outside the SokobanNoShinobi folder, so that any command would look like `javac SokobanNoShinobi.java`.
If turned into a .jar file, run with `java -jar <name>.jar`. If possible, I would suggest running the command from a terminal and then either hiding the terminal or detaching the program process from the terminal and closing the terminal so that the program can be run without a useless terminal tab cluttering the screen.
### Game
- Generate or load a level with R or ESC, respectively. This can be done at any time, including while playing a level.
- Use WASD and BACKSPACE to make a movement plan, then ENTER to execute it.
    - If the player goes off the edge of the board at any point, passes over a red wall/tile with a maximum step count lower than the current step, or lands on a black wall/tile with a step count lower than the current step, the move will be invalid.
    - If the player lands on a box, the box will move according to the same instructions as the player. Boxes have the same rules as players, plus an additional rule that a box cannot land on another box or on a player.
    - The rules of player/box motion are sometimes different if loading a level from file. When the level is loaded, the text on the bottom of the screen will indicate which move-logic the level uses: 
        - 0: Standard - player moves according to instructions, cancelling the move if anything invalid happens along the way. Then the box moves, again cancelling the move (and pushing the player back) if anything invalid happens.
        - 1: Simple Barriers - same as Standard, but boxes avoid cancelling moves whenever possible. This is done by (a) ignoring any single step that would cause the box to invalidly pass through a red wall/tile, and (b) going backward through the movement sequence AS IS (whether or not there was an invalid step going forward) if invalidly landing on a black wall/tile/box/player. The step count at any given point along the way equals the total number of steps taken, valid or invalid, forward or backward.
        - 2: Barriers - same as Simple Barriers, but only moves that were executed going forward are considered while going backward, and step count = total distance traveled (not affected by invalid steps).
### Level Editor
- Click on any text entry fields on the side bar to enter text. The program will only "react" to the entered text when ENTER (the return key) is pressed.
- Click on any tile on the board to select it. The selected tile will be yellow, or have a yellow square in the bottom-right corner if it is a wall. 
    - Tiles may also be selected by specifying coordinates in the text field associated with the selected tile's x and y position.
- The options within the "Selected tile" section depend on the selected tile, and any changes made there will only affect the selected tile. The top-left tile is at position x = 0, y = 0; the bottom right tile is at x = width - 1, y = height - 1. Lower rows have higher y-values; righter columns have higher x-values.
- Saving will send you to a default levels location. You can navigate to another folder to save somewhere else. Selecting an existing file will overwrite it; to make a new file, type the desired file name, press "Save", and it will be made automatically.
- The camera can be controlled in the level editor in the same way as it is controlled elsewhere. 
    - If the camera does not move when it should, click somewhere on the board and try again.

## Notes on running directory
- There must be a folder titled "Screenshots" in the same folder as the SokobanNoShinobi folder in order for screenshots to save to this folder. Otherwise, they will save to a default folder (probably somewhere undesired!)
- There must be a folder titled "Levels" in the same folder as the SokobanNoShinobi folder in order for the file-picker dialog to open to the correct folder. Otherwise, it will pick a default folder, and you will spend more time navigating in that dialog than necessary.

For the above, "Same folder" means something like this:

- Parent folder
   - SokobanNoShinobi [folder]
   - Levels [folder]
   - Screenshots [folder]
   - README.md
   - patch-notes.md

Or, in other words, the folder that holds the game files has a sibling folder called "Levels" and another called "Screenshots."
## Watch
 * https://youtu.be/nPsZ-izATx8
 * https://youtu.be/HMejdT6-knY
