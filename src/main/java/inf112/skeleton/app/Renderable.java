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

    private ArrayList<Animation> animations = new ArrayList<>();
    private Animation current_animation = null;

    private static ArrayList<AnimationCallback> callbacks = new ArrayList<>();

    /**
     * @param new_anim Animation to add.
     * @return The tick number that finishes the animation.
     */
    public long addAnimation(Animation new_anim) {
        long ticks = total_ticks;
        animations.add(new_anim);
        if (current_animation != null)
            ticks += current_animation.getTicks();
        for (Animation a : animations)
            ticks += a.getTicks();
        return ticks;
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

    public Vector2Di getFinalAnimationPos(float scale) {
        Vector2Df pos = this.pos.copy();
        for (Animation a : animations)
            pos.add(a.getPosOffset());
        pos.mul(scale);
        return pos.toi();
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
        for (AnimationCallback cb : callbacks)
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
        Texture tx = getTexture();
        if (tx == null)
            return;
        int rx = tx.getWidth() / 2;
        int ry = tx.getHeight() / 2;
        TextureRegion rtx = new TextureRegion(tx);
        batch.draw(rtx, pos.getX(), pos.getY(), rx, ry, tx.getWidth(), tx.getHeight(), 1, 1, angle);
        update();
    }

    /*
    @Override
    public int compareTo(Renderable other) {
        return other.getPriority() - getPriority();
    }
    */
}
