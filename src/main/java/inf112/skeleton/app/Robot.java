package inf112.skeleton.app;

public class Robot extends IItem {

    private int size;
    private String name;

    //Todo: private Texture texture;

    private Vector2D direction;

    public Robot() {
        //TODO: What do we want to store here, and what do we want in player class?
    }

    public Vector2D getDirection() {
        return direction;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getName() {
        return name;
    }

}
