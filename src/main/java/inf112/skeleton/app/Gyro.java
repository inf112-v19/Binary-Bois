package inf112.skeleton.app;

public class Gyro extends TilePiece {

    private int rotation;

    /**
     * rotation = 90 is clockwise and = -90 is counter clockwise
     */
    public Gyro(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }
}
