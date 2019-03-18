package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface IRenderable extends Comparable<IRenderable> {
    Vector2D getPos();
    Texture getTexture();

    default void render(SpriteBatch batch) {
        render(batch, getPos());
    }

    default void render(SpriteBatch batch, Vector2D pos) {
        Texture tx = getTexture();
        if (tx == null)
            return;
        batch.draw(tx, pos.getX(), pos.getY());
    }
}
