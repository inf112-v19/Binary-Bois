package inf112.skeleton.app;

public class Vector2D {
    private int x;
    private int y;

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void rotate(double deg){
        double rad = Math.toRadians(deg);
        double cs = Math.cos(rad);
        double sn = Math.sin(rad);
        double x = this.x * cs - this.y * sn;
        double y = this.x * sn + this.y * cs;
        this.x = (int) Math.round(x);
        this.y = (int) Math.round(y);
    }

    public void rotateRight() {
        this.rotate(90);
    }

    public void rotateLeft() {
        this.rotate(-90);
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public void mul(int d) {
        this.x *= d;
        this.y *= d;
    }

    public Vector2D copy() {
        return new Vector2D(x, y);
    }

    public void add(Vector2D vec) {
        x += vec.x;
        y += vec.y;
    }

    public void move(Vector2D dir, int d) {
        Vector2D new_dir = dir.copy();
        new_dir.mul(d);
        this.add(new_dir);
    }

    @Override
    public String toString() {
        return "<Vector2D: [" + x + ", " + y + "]>";
    }
}




