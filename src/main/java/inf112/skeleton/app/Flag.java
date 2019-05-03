package inf112.skeleton.app;

public class Flag implements IItem, Comparable<Flag> {

    private int number;
    private Vector2Di pos;

    public Flag(int number, Vector2Di pos) {
        this.number = number;
        this.pos = pos;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public int compareTo(Flag o) {
        return this.getNumber()-o.getNumber();
    }
}
