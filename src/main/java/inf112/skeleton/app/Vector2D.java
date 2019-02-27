package inf112.skeleton.app;

/**
 * Mutable 2D integer vector class.
 */
public class Vector2D {
    private int x;
    private int y;

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(double deg) {
        this.x = 1;
        this.y = 0;
        this.rotate(deg);
    }

    /**
     * Perform "integer" rotation, this means that the only results from this rotation
     * on a unit vector are: {[0,1], [0,-1], [1,0], [1, 1], [1, -1], [-1,0], [-1,1], [-1,-1]}
     * Note that a rotation might change the magnitude of a vector, as they have to be rounded.
     * This introduces a lot of error and makes the class unsuitable for certain applications, but this 
     * suits our use case of moving discretely along grids.
     *
     * @param deg Degrees to rotate
     */
    public void rotate(double deg) {
        double rad = Math.toRadians(deg);
        double cs = Math.cos(rad);
        double sn = Math.sin(rad);
        double x = this.x * cs - this.y * sn;
        double y = this.x * sn + this.y * cs;
        this.x = (int) Math.round(x);
        this.y = (int) Math.round(y);
    }

    /**
     * Compute dot product.
     *
     * @param other The vector B in: this . B
     * @return A_x*B_x + A_y*B_y
     */
    public double dot(Vector2D other) {
        return x*other.getX() + y*other.getY();
    }

    /**
     * Compute the angle between the two vectors: self . other
     *
     * @param other The vector to compute angle according to.
     * @return The angle in degrees.
     */
    public double angle(Vector2D other) {
        /* TODO: This computation is fairly heavy and doesn't change every 1/60th of a second,
         *       so for a few common "other" vectors like [1, 0] the result should probably be
         *       cached until x/y changes.
         */
        return Math.toDegrees(Math.acos(this.dot(other) / (this.magnitude() * other.magnitude())));
    }

    /**
     * @return Angle between this vector and the vector [1, 0]
     */
    public double angle() {
        return angle(new Vector2D(1, 0));
    }

    /**
     * Return the geometric length of a vector.
     *
     * @return Length.
     */
    public double magnitude() {
        return Math.sqrt(x*x + y*y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

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
