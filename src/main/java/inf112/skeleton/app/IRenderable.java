package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;

public interface IRenderable extends Comparable<IRenderable> {
    Vector2D getPos();
    Texture getTexture();
}
