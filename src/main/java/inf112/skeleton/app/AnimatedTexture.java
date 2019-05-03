package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;

/**
 * General class for animating textures.
 */
public class AnimatedTexture extends Renderable {
    private Texture tx;

    public AnimatedTexture(String src) throws NoSuchResource {
        tx = Resources.getTexture(src);
    }

    public Texture getTexture() {
        return tx;
    }
}
