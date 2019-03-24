package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

class AnimationCallback {
    public Runnable r;
    public long ticks;

    public AnimationCallback(long ticks, Runnable r) {
        this.r = r;
        this.ticks = ticks;
    }
}

/**
 * Renderable things.
 *
 * This class has two separate use cases:
 *   1. Simple object that just has a texture:
 *     - In this case all one has to do is to override getTexture() and getPos()
 *   2. More complex objects using multiple textures:
 *     - Override render(SpriteBatch, Vector2Di) and getPos()
 *
 * When you want to render a Renderable you should always use either
 * render(SpriteBatch, Vector2Di) or render(SpriteBatch).
 */
public abstract class Renderable {//implements Comparable<Renderable> {
    public static final float ANIMATION_TIMESTEP = 1.0f/60.0f;
    private static float time_acc = 0.0f;
    private static int ticks = 0;
    /** Useful when you want to know if an animation has finished.
     *  TODO: Implement animationHasFinished(Animation) */
    private static long total_ticks = 0;

    private Vector2Df pos = new Vector2Df(0, 0);
    private float angle = 0;
    private float scale = 1.0f;

    private ArrayList<Animation> animations = new ArrayList<>();
    private Animation current_animation = null;

    private boolean do_draw = true;

    private static ArrayList<AnimationCallback> callbacks = new ArrayList<>();

    /**
     * @param new_anim Animation to add.
     * @return The tick number that finishes the animation.
     */
    public long addAnimation(Animation new_anim) {
        animations.add(new_anim);
        return getFinalAnimationTick();
    }

    public long getFinalAnimationTick() {
        long ticks = total_ticks;
        if (current_animation != null)
            ticks += current_animation.getTicks();
        for (Animation a : animations)
            ticks += a.getTicks();
        return ticks;
    }

    public static <T extends Renderable> long getFinalAnimationTick(ArrayList<T> rs) {
        long max_ticks = Long.MIN_VALUE, ticks;
        for (Renderable r : rs)
            if ((ticks = r.getFinalAnimationTick()) > max_ticks)
                max_ticks = ticks;
        return max_ticks;
    }

    /**
     * Add callback for when the final animation finishes, as per the calling
     * of the function. If there are more animations added after this function
     * returns they will not be taken into account.
     * @param fn The callback to run.
     */
    public void addAnimationCallback(Runnable fn) {
        addAnimationCallback(getFinalAnimationTick(), fn);
    }

    public static <T extends Renderable> void addAnimationCallback(ArrayList<T> rs, Runnable fn) {
        addAnimationCallback(getFinalAnimationTick(rs), fn);
    }

    public static void addAnimationCallback(long ticks, Runnable fn) {
        if (ticks <= total_ticks)
            fn.run();
        callbacks.add(new AnimationCallback(ticks, fn));
    }

    private void nextAnimation() {
        if (animations.size() == 0) {
            current_animation = null;
            return;
        }
        current_animation = animations.get(0);
        animations.remove(0);
        angle += current_animation.getAngleOffset();
        scale += current_animation.getScaleOffset();
        pos.add(current_animation.getPosOffset());
    }

    public void clearAnimations() {
        animations.clear();
    }

    public Vector2Di getAnimatedDrawPos(float scale) {
        Vector2Df pos = this.pos.copy();
        if (current_animation != null)
            pos.sub(current_animation.getPosOffset());
        pos.mul(scale);
        return pos.toi();
    }

    public float getAnimatedAngle() {
        if (current_animation == null)
            return angle;
        return angle - current_animation.getAngleOffset();
    }

    public float getAnimatedScale() {
        if (current_animation == null)
            return scale;
        return scale - current_animation.getScaleOffset();
    }

    public Vector2Di getFinalAnimationPos(float scale) {
        Vector2Df pos = this.pos.copy();
        for (Animation a : animations)
            pos.add(a.getPosOffset());
        pos.mul(scale);
        return pos.toi();
    }

    public float getFinalAnimationScale() {
        float scale = this.scale;
        for (Animation a : animations)
            scale += a.getScaleOffset();
        return scale;
    }

    public Vector2Di getDrawPos(float scale) {
        Vector2Df pos = this.pos.copy();
        pos.mul(scale);
        return pos.toi();
    }

    public void setDrawPos(Vector2Df pos) {
        this.pos = pos;
    }

    public float getAngle() {
        if (animations.size() == 0)
            return angle;
        return angle + animations.get(0).getAngleOffset();
    }

    /**
     * Should be called at the start of each render cycle.
     * This updates #{Renderable.ticks} so that each instance
     * knows how much to advance their animations.
     */
    public static void updateAll() {
        total_ticks += ticks;
        time_acc += Gdx.graphics.getDeltaTime();
        ticks = (int) (time_acc / ANIMATION_TIMESTEP);
        time_acc -= ticks * ANIMATION_TIMESTEP;

        ArrayList<AnimationCallback> dead_callbacks = new ArrayList<>();
        // Callbacks can also add new animation callbacks, therefore the list needs to be copied.
        ArrayList<AnimationCallback> callbacks_cpy = new ArrayList<>(callbacks);
        for (AnimationCallback cb : callbacks_cpy)
            if (cb.ticks <= total_ticks) {
                dead_callbacks.add(cb);
                cb.r.run();
            }
        callbacks.removeAll(dead_callbacks);
    }

    public Texture getTexture() {
        return null;
    }

    public int getPriority() {
        return 0;
    }

    public void render(SpriteBatch batch, float scale) {
        render(batch, getAnimatedDrawPos(scale));
    }

    private void update() {
        if (current_animation == null || !current_animation.update(ticks))
            nextAnimation();
    }

    public void render(SpriteBatch batch, Vector2Di pos) {
        if (!do_draw)
            return;
        Texture tx = getTexture();
        if (tx == null)
            return;
        int rx = tx.getWidth() / 2;
        int ry = tx.getHeight() / 2;
        TextureRegion rtx = new TextureRegion(tx);
        batch.draw(rtx, pos.getX(), pos.getY(), rx, ry, tx.getWidth(), tx.getHeight(), getAnimatedScale(), getAnimatedScale(), getAnimatedAngle());
        update();
    }

    public void hide() {
        do_draw = false;
    }

    public void show() {
        do_draw = true;
    }

    /*
    @Override
    public int compareTo(Renderable other) {
        return other.getPriority() - getPriority();
    }
    */
}
