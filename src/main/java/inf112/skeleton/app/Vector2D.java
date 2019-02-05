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
        this.x = (int) Math.ceil(x);
        this.y = (int) Math.ceil(y);
    }

    public void rotateRight() {
        this.rotate(90);
    }

    public void rotateLeft() {
        this.rotate(-90);
    }
}




