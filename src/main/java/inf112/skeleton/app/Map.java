package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.Vector;

/**
 * Presents the map and the objects that live on it.
 *
 * TODO: Make the positioning work properly, currently it just centers the map at the top.
 */
public class Map {
    /** Enable/disable fun */
    private final boolean FUN_ENABLED = false;

    private OrthographicCamera cam;
    private static TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private SpriteBatch batch;
    private Vector<IRenderable> render_queue;
    private Vector2D dim;
    private Vector2D pos;
    private int pw, ph;

    // FIXME: This should be retrived from the map later on.
    private final int tile_dim = 32;

    public Map(int px, int py, int dim_pw, int dim_ph, String map_file) {
        pos = new Vector2D(px, py);

        tiledMap = new TmxMapLoader().load(map_file);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();
        render_queue = new Vector<>();
        dim = new Vector2D(
                tiledMap.getProperties().get("width", Integer.class),
                tiledMap.getProperties().get("height", Integer.class)
        );

        int map_pw = dim.getX() * tile_dim;
        int map_ph = dim.getY() * tile_dim;
        pw = Gdx.graphics.getWidth();
        ph = Gdx.graphics.getHeight();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, map_pw*2, map_ph*2);
        cam.position.y = 0f;
        cam.position.x -= map_pw/2f;

        System.out.println("Cam position: " + "(" + cam.position.x + ", " + cam.position.y + ")");
        System.out.println("Cam size: " + cam.viewportWidth + "x" + cam.viewportHeight);
        System.out.println("Cam zoom: " + cam.zoom);

        cam.update();

        pw = map_pw;
        ph = map_ph;
    }

    public void maximize() {
        cam.position.y = ph/2f;
        cam.zoom = 0.5f;
        cam.update();
    }

    public void minimize() {
        cam.position.y = 0f;
        cam.zoom = 1.0f;
        cam.update();
    }

    public Vector2D getDimensions() {
        return dim;
    }

    public static TiledMap getTiledMap() {
        return tiledMap;
    }

    public Vector2D toPixelCoordinate(Vector2D vec) {
        Vector2D pos = new Vector2D(0, 0); // TODO: Remove this, it is a test
        return new Vector2D(
                pos.getX() + vec.getX() * (pw / dim.getX()),
                pos.getY() + vec.getY() * (ph / dim.getY())
        );
    }

    public void render() {
        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        for (IRenderable r : render_queue) {
            Vector2D pos = r.getPos();
            Vector2D px_pos = toPixelCoordinate(pos);
            batch.draw(r.getTexture(), px_pos.getX(), px_pos.getY());
        }
        batch.end();

        // The ability to do this was a neat side effect of putting everything
        // together on a single viewport.
        if (FUN_ENABLED) {
            rotate(1.0f);
        }
    }

    public void rotate(float deg) {
        cam.rotateAround(
                new Vector3(pw/2f, ph/2f, 0),
                new Vector3(0, 0, 1),
                deg
        );
        cam.update();
    }

    public void addDrawJob(IRenderable obj) {
        render_queue.add(obj);
    }
}
