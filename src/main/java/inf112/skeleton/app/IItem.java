package inf112.skeleton.app;

/**
 * Represents all items on the board, like walls, holes, power-ups
 * and robots
 */
public abstract class IItem {

    public int compareTo(IItem other) {
        return Integer.compare(getSize(), other.getSize());
    }

    /**
     * 'Might' be used to choose layer in which the item is drawn
     * @return undefined
     */
    abstract int getSize();

    /**
     * Identifies the item type
     * @return String used for identification
     */
    abstract String getName();


    /** Todo
     * Representing the item. Used for graphics
     * @return 'type' used for graphics
     */
    //abstract String getSymbol();

}
