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
     * @param x value, starting at 0
     * @param y value, starting at 0
     * @return ArrayList of all items in grid position
     */
    ArrayList<IItem> get(int x, int y);

    /** See get(int, int) */
    default ArrayList<IItem> get(Vector2Di pos) {
        return get(pos.getX(), pos.getY());
    }

    /**
     *
     * Checks that the item is on the board.
     */
    default boolean isOnBoard(Vector2Di v) {
        return !(v.getY() < 0 || v.getY() >= getHeight() || v.getX() < 0 || v.getX() >= getWidth());
    }

    /**
     * Adds item to the list of items at specified location
     * on the board
     * @param item object to be placed
     * @param x
     * @param y
     */
    void set(IItem item, int x, int y);

    /** See set(IItem, int, int) */
    default void set(IItem item, Vector2Di pos) {
        set(item, pos.getX(), pos.getY());
    }

    /** Remove specified item from location */
    void remove(IItem item, int x, int y);

    /** See set(IItem, int, int) */
    default void remove(IItem item, Vector2Di pos) {remove(item, pos.getX(), pos.getY());}

    /**
     * @return height of board
     */
    int getHeight();


    /**
     * @return width of board
     */
    int getWidth();

    ArrayList<IItem> getAllItemsOnBoard();
}