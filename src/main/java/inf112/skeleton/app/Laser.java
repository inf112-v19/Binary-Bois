package inf112.skeleton.app;

public class Laser implements IItem {

    private Vector2Di pos;

    public Laser(Vector2Di pos) {
        this.pos = pos;
    }

    @Override
    public String getName() {
        return "Laser";
    }
}
