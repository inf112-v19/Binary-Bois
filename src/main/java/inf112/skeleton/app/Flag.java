package inf112.skeleton.app;

public class Flag extends IItem {

    private int number;

    public Flag(int number) {
        this.number = number;
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
