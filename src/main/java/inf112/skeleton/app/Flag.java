package inf112.skeleton.app;

public class Flag extends IItem {

    private int number;
    private Vector2D pos;

    public Flag(int number, Vector2D pos) {
        this.number = number;
        this.pos = pos;
    }

    int getNumber() {
        return number;
    }

    @Override
    int getSize() {
        return 0;
    }

    @Override
    String getName() {
        return "Flag";
    }
}
