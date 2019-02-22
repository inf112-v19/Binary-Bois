package inf112.skeleton.app;

public class Robot extends IItem {

    private int size;
    private String name;
    private Vector2D pos;
    //Todo: private Texture texture;



    public Robot(String name, Vector2D pos) {
        //TODO: What do we want to store here, and what do we want in player class?
        this.name = name;
        this.pos = pos;
    }

    public Vector2D getPos() {
        return pos;
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
