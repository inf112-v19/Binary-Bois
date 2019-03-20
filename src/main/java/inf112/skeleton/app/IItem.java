package inf112.skeleton.app;

/**
 * Represents all items on the board, like walls, holes, power-ups
 * and robots
 */
public interface IItem {

    /**
     * Identifies the item type
     * @return String used for identification
     */
    String getName();
}