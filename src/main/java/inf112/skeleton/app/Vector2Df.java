package inf112.skeleton.app;

import org.lwjgl.Sys;

/**
 * Mutable 2D float vector.
 *
 * Note:
 * Unfortunately Java is dumb and does not allow generic parameters
 * to be primitive types. This means that Vector2Di and Vector2Df need
 * to be separate classes. You could use Integer/Double/Float, but that
 * comes at a performance cost.
 */
public class Vector2Df {
    private float x;
    private float y;

    public Vector2Df(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2Df(float deg) {
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
    public void rotate(float deg) {
        float rad = (float) Math.toRadians(deg);
        float cs = (float) Math.cos(rad);
        float sn = (float) Math.sin(rad);
        float x = this.x * cs - this.y * sn;
        float y = this.x * sn + this.y * cs;
        this.x = x;
        this.y = y;
    }

    /**
     * Compute dot product.
     *
     * @param other The vector B in: this . B
     * @return A_x*B_x + A_y*B_y
     */
    public float dot(Vector2Df other) {
        return x*other.getX() + y*other.getY();
    }

    public float crossProduct(Vector2Df other) {
        return this.x*other.getY() - this.y*other.getX();
    }

    /**
     * Compute the angle between the two vectors: self . other
     *
     * @param other The vector to compute angle according to.
     * @return The angle in degrees.
     */
    public float angle(Vector2Df other) {
        /* TODO: This computation is fairly heavy and doesn't change every 1/60th of a second,
         *       so for a few common "other" vectors like [1, 0] the result should probably be
         *       cached until x/y changes.
         */

        float angle = (float) Math.toDegrees(Math.acos(this.dot(other) / (this.magnitude() * other.magnitude())));
        if (crossProduct(other) > 0)
            angle = angle * -1;
        return angle;
    }

    /**
     * @return Angle between this vector and the vector [1, 0]
     */
    public float angle() {
        return angle(new Vector2Df(1, 0));
    }

    /**
     * Return the geometric length of a vector.
     *
     * @return Length.
     */
    public float magnitude() {
        return (float) Math.sqrt(x*x + y*y);
    }

    public float getX() {
        return x;
    }

    public void normalize() {
        float mag = magnitude();
        if (mag < 0.00001f)
            return;
        mul(1.0f/mag);
    }

    public float getY() {
        return y;
    }

    public void mul(float d) {
        this.x *= d;
        this.y *= d;
    }

    public Vector2Df copy() {
        return new Vector2Df(x, y);
    }

    public void add(Vector2Df vec) {
        x += vec.x;
        y += vec.y;
    }

    public void sub(Vector2Df vec) {
        x -= vec.x;
        y -= vec.y;
    }

    public void move(Vector2Df dir, float d) {
        Vector2Df new_dir = dir.copy();
        new_dir.mul(d);
        this.add(new_dir);
    }

    @Override
    public String toString() {
        return "<Vector2Df: [" + x + ", " + y + "]>";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Vector2Df) {
            Vector2Df other = (Vector2Df) obj;
            return other.x == x && other.y == y;
        }
        return false;
    }

    public Vector2Di toi() {
        return new Vector2Di(Math.round(x), Math.round(y));
    }
}
