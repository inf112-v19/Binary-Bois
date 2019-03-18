package inf112.skeleton.app;

public class Flag implements IItem {

    private int number;
    private Vector2Di pos;

    public Flag(int number, Vector2Di pos) {
        this.number = number;
        this.pos = pos;
    }

    int getNumber() {
        return number;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public String getName() {
        return "Flag";
    }
}
