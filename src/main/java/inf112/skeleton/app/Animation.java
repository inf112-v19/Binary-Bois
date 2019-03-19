package inf112.skeleton.app;

/**
 * Linear animation, for use with Renderable objects.
 *
 * How it works:
 *   When an animation is started on a renderable object, the position
 *   of the renderable object is updated immediately. The position held
 *   inside the animation works as an offset which counts down to [0, 0].
 */
public class Animation {
    /** Amount of movement in x/y direction. */
    private Vector2Df pos_offset;
    /** Velocity normal vector. */
    private Vector2Df vel_normal;
    /** Velocity (pixels per tick.) */
    private Vector2Df vel;
    /** Amount of rotation expressed as angle_offset. */
    private float angle_offset;
    private float angle_vel;
    /** How many animation ticks before the animation is finished. */
    private int num_ticks;

    /**
     * @param vec The movement vector.
     * @param time How long you want it to take.
     */
    Animation(Vector2Df vec, float rot, float time) {
        vel = vec.copy();
        vel.mul(-1.0f/time * Renderable.ANIMATION_TIMESTEP);

        vel_normal = vel.copy();
        vel_normal.normalize();

        pos_offset = vec.copy();

        angle_offset = rot;
        angle_vel = rot / time;

        num_ticks = Math.round(time / Renderable.ANIMATION_TIMESTEP);
    }

    /**
     * Idle animation, used for timing subsequent animations.
     * @param t How long to idle.
     * @return Idle-animation.
     */
    public static Animation idle(float t) {
        return new Animation(new Vector2Df(0, 0), 0, t);
    }

    /**
     * Create an animation for a renderable that moves it to a new position.
     *
     * FIXME: moveTo needs to take existing animations on `r` into account when calculating
     *        the movement vector.
     *
     * @param r The renderable to be move-animated.
     * @param newpos The destination position.
     * @param t The animation time.
     * @return Move-animation
     */
    public static Animation moveTo(Renderable r, Vector2Di newpos, float t) {
        Vector2Df vec = newpos.copy().tof();
        vec.sub(r.getDrawPos(1).tof());
        return new Animation(vec, 0, t);
    }

    public boolean update(int ticks) {
        while (ticks-- > 0 && num_ticks-- > 0) {
            pos_offset.add(vel);
            angle_offset -= angle_vel * Renderable.ANIMATION_TIMESTEP;
        }
        return num_ticks > 0;
    }

    public Vector2Df getPosOffset() {
        return pos_offset;
    }

    public float getAngleOffset() {
        return angle_offset;
    }
}
