package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import org.lwjgl.opengl.Display;

import javax.xml.soap.Text;
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
public abstract class Renderable {
    public static final float ANIMATION_TIMESTEP = 1.0f/60.0f;
    private static float time_acc = 0.0f;
    private static int ticks = 0;
    /** Useful when you want to know if an animation has finished.
     *  TODO: Implement animationHasFinished(Animation) */
    private static long total_ticks = 0;

    private Vector2Df pos = new Vector2Df(0, 0);
    private float angle = 0;
    private float scale = 1.0f;
    private float pos_scale = 1;

    private ArrayList<Animation> animations = new ArrayList<>();
    private Animation current_animation = null;

    private boolean do_draw = true;

    private static final int MAX_DRAW_JOBS = 1024;
    private static ArrayList<AnimationCallback> callbacks = new ArrayList<>();
    private static final Vector2Di FBO_DIM = new Vector2Di(2048, 2048);
    private static FrameBuffer fbo = null;
    private static SpriteBatch batch = null;
    private static Renderable draw_jobs[] = new Renderable[MAX_DRAW_JOBS];
    private static int num_draw_jobs = 0;

    public static void init() {
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, FBO_DIM.getX(), FBO_DIM.getY(), false);
        batch = new SpriteBatch();
    }

    public void setPosScale(int new_scale) {
        pos_scale = new_scale;
    }

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

    public Vector2Di getAnimatedDrawPos() {
        Vector2Df pos = this.pos.copy();
        if (current_animation != null)
            pos.sub(current_animation.getPosOffset());
        pos.mul(pos_scale);
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

    public Vector2Di getFinalAnimationPos() {
        Vector2Df pos = this.pos.copy();
        for (Animation a : animations)
            pos.add(a.getPosOffset());
        pos.mul(pos_scale);
        return pos.toi();
    }

    public float getFinalAnimationScale() {
        float scale = this.scale;
        for (Animation a : animations)
            scale += a.getScaleOffset();
        return scale;
    }

    public Vector2Di getDrawPos() {
        Vector2Df pos = this.pos.copy();
        pos.mul(pos_scale);
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

    /**
     * Override this to use renderFBO.
     *
     * Always render to (0,0)
     * See FBO_DIM for the maximum pixel size of things rendered to the FBO.
     *
     * @return The size of the rendered area on the FBO.
     */
    public Vector2Di render(SpriteBatch batch) {
        throw new UnsupportedOperationException();
    }

    public void render() {
        if (num_draw_jobs == MAX_DRAW_JOBS)
            throw new RuntimeException("Too many draw jobs");
        if (!do_draw)
            return;
        draw_jobs[num_draw_jobs++] = this;
    }

    public void renderNow(SpriteBatch batch) {
        if (!do_draw)
            return;

        Texture tx = getTexture();
        if (tx == null)
            return;

        TextureRegion tx_reg = new TextureRegion(tx);

        int rx = tx.getWidth() / 2,
            ry = tx.getHeight() / 2;

        Vector2Di dpos = getAnimatedDrawPos();
        batch.draw(tx_reg,
                   dpos.getX(), dpos.getY(),
                   rx, ry,
                   tx.getWidth(), tx.getHeight(),
                   getAnimatedScale(), getAnimatedScale(),
                   getAnimatedAngle());

        update();
    }

    private void update() {
        if (current_animation == null || !current_animation.update(ticks))
            nextAnimation();
    }

    private TextureRegion renderFBO(Vector2Di dim_ret) {
        // make the FBO the current buffer
        fbo.begin();

        SpriteBatch batch = new SpriteBatch();

        // Clear the FBO
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        batch.getProjectionMatrix().setToOrtho2D(0, 0, fbo.getWidth(), fbo.getHeight());

        Vector2Di dim;
        batch.begin();
        try {
            dim = render(batch);
        } catch (Exception e) {
            batch.end();
            fbo.end();
            throw e;
        }
        batch.end();

        Texture tex = fbo.getColorBufferTexture();
        TextureRegion tex_reg = new TextureRegion(tex, 0, 0, dim.getX(), dim.getY());
        tex_reg.flip(false, true);

        // now we can unbind the FBO, returning rendering back to the default back buffer (the Display)
        fbo.end();

        batch.getProjectionMatrix().setToOrtho2D(0, 0, Display.getWidth(), Display.getHeight());

        dim_ret.set(dim.getX(), dim.getY());
        return tex_reg;
    }

    public static void clearRenderQueue() {
        num_draw_jobs = 0;
    }

    /**
     * TODO: For FBO rendering a packing algorithm must be used.
     *
     * The algorithm:
     *   1. Collect fbo renderables until the sum of their area exceeds
     *      the area of the FBO (see FBO_DIM)
     *   2. Sort the renderables from tallest to shortest.
     *   3. Pack downwards until there is not fit, loop onwards until
     *      a short enough renderable is found.
     *   4. Increase x position with minimum width of renderables
     *   5. Try to fit next renderable
     *   6. If it does not fit, increase x by 10 until it does.
     *   7. Loop
     */
    public static void flushRenderQueue() {
        Vector2Di dim = new Vector2Di(0, 0);
        batch.begin();
        for (int i = 0; i < num_draw_jobs; i++) {
            Renderable rend = draw_jobs[i];
            Texture tx = rend.getTexture();
            Vector2Di pos = rend.getAnimatedDrawPos();
            TextureRegion tx_reg;
            int rx, ry;
            if (tx == null || rend.fboRenderEnabled()) {
                batch.end();
                tx_reg = rend.renderFBO(dim);
                batch.begin();
                rx = dim.getX() / 2;
                ry = dim.getY() / 2;
            } else {
                rx = tx.getWidth() / 2;
                ry = tx.getHeight() / 2;
                tx_reg = new TextureRegion(tx);
                dim.set(tx.getWidth(), tx.getHeight());
            }
            batch.draw(
                    tx_reg,
                    pos.getX(),
                    pos.getY(),
                    rx, ry,
                    dim.getX(),
                    dim.getY(),
                    rend.getAnimatedScale(),
                    rend.getAnimatedScale(),
                    rend.getAnimatedAngle());
            rend.update();
        }
        batch.end();
        clearRenderQueue();
    }

    public void hide() {
        do_draw = false;
    }

    public void show() {
        do_draw = true;
    }

    /**
     * Override this method to enable fbo rendering.
     *
     * @return Whether or not to render to FBO.
     */
    public boolean fboRenderEnabled() {
        return false;
    }
}
