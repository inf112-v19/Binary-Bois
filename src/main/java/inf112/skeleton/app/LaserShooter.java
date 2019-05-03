package inf112.skeleton.app;

public class LaserShooter extends Renderable implements IItem {

    private Vector2Di pos;
    private  Vector2Di dir;

    /** Lasers could produce a "field of laser" on the board that
     * damages robots, if they are first in the line of fire, instead
     * of firing lasers all the time*/
    public LaserShooter(Vector2Di pos, Vector2Di dir) {
        this.pos = pos;
        this.dir = dir;
    }
}
