package inf112.skeleton.app;

import java.util.ArrayList;
/**
 * The board itself. Knows what is on the board and where
 */
 public interface IBoard {

    /**
     * Gives you all items at specified location, including
     * floor tiles, holes and walls.
     * x and y are cartesian coordinates, (0,0) being
     * lower left corner so (1,2) = ( . x . )
     *                              ( . . . )
     *                              ( . . . )
     * @param x row number, starting at 0
     * @param y column number, starting at 0
     * @return ArrayList of all items in grid position
     */
    ArrayList<IItem> get(int x, int y);

    /**
     * Adds item to the list of items at specified location
     * on the board
     * @param item object to be placed
     * @param x
     * @param y
     */
    void set(IItem item, int x, int y);

    /**
     * @return height of board
     */
    int getHeight();

    /**
     * @return width of board
     */
    int getWidth();

    /**
     * Checks if moving to this location is valid
     * Holes are valid, but will destroy the robot
     * @param x
     * @param y
     * @return if location can be moved to
     */
    boolean validMove(int x, int y);

}