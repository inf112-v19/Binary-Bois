package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Presents the map and the objects that live on it.
 */
public class GameMap implements InputProcessor {
    /** Enable/disable fun */
    private final boolean FUN_ENABLED = false;
    private final static int TILE_CLICKS_OVERFLOW = 8192;

    private OrthographicCamera cam;
    private static TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private SpriteBatch batch;
    private Vector<Renderable> render_queue;
    private Vector2Di dim;
    private Vector2Di pos;
    private Vector2Df last_mouse_pos = null;
    private ArrayList<Vector2Di> tile_clicks = new ArrayList<>();
    private int pw, ph, map_pw, map_ph;

    // FIXME: This should be retrieved from the map later on.
    private final int tile_dim = 32;

    public GameMap(int px, int py, int dim_pw, int dim_ph, String map_file) throws NoSuchResource {
        pos = new Vector2Di(px, py);

        tiledMap = Resources.getTiledMap(map_file);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();
        render_queue = new Vector<>();
        dim = new Vector2Di(
                tiledMap.getProperties().get("width", Integer.class),
                tiledMap.getProperties().get("height", Integer.class)
        );

        int map_pw = dim.getX() * tile_dim;
        int map_ph = dim.getY() * tile_dim;
        pw = Gdx.graphics.getWidth();
        ph = Gdx.graphics.getHeight();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, map_pw, map_ph * (((float) ph) / ((float)pw)));
        cam.position.add(-300f, -50f, 0f);
        float z = 1.5f;
        cam.zoom += z;

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

    public Vector2Di getDimensions() {
        return dim;
    }

    public static TiledMap getTiledMap() {
        return tiledMap;
    }

    public Vector2Di toPixelCoordinate(Vector2Di vec) {
        Vector2Di pos = new Vector2Di(0, 0); // TODO: Remove this, it is a test
        return new Vector2Di(
                pos.getX() + vec.getX() * (pw / dim.getX()),
                pos.getY() + vec.getY() * (ph / dim.getY())
        );
    }

    public void render() {
        //tiledMapRenderer.setView(cam.combined, 0f, 0f, (float) pw, (float) ph);
        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        for (Renderable r : render_queue) {
            //Vector2Di pos = r.getPos();
            //Vector2Di px_pos = toPixelCoordinate(pos);
            r.render(batch, 32);
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

    public void addDrawJob(Renderable obj) {
        render_queue.add(obj);
    }

    public Vector2Di pixToTile(Vector2Df pix) {
        Vector2Df tmp_vec = pix.copy();
        tmp_vec.mul(1f/tile_dim);
        return tmp_vec.toi();
    }

    public ArrayList<Vector2Di> getTileClicks() {
        ArrayList<Vector2Di> tile_clicks_cpy = new ArrayList<>(tile_clicks);
        tile_clicks.clear();
        return tile_clicks_cpy;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    public Vector2Di screenToMapCoord(float x, float y) {
        Vector3 vec = cam.unproject(new Vector3(x, y, 0));
        return new Vector2Di((int) (vec.x / tile_dim), (int) (vec.y / tile_dim));
    }

    private void addClickEvent(Vector2Di pos) {
        if (tile_clicks.size() >= TILE_CLICKS_OVERFLOW)
            tile_clicks.clear();
        if((pos.getX() >= 0 && pos.getX() <= getDimensions().getX()) && (pos.getY() >= 0 && pos.getX() <= getDimensions().getY())){
            tile_clicks.add(pos);
        }

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            addClickEvent(screenToMapCoord(Gdx.input.getX(), Gdx.input.getY()));
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        last_mouse_pos = null;
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (last_mouse_pos == null) {
            last_mouse_pos = new Vector2Df(x, y);
            return false;
        }
        Vector2Df cur_pos = new Vector2Df(x, y);
        Vector2Df diff = last_mouse_pos.copy();
        diff.sub(cur_pos);
        last_mouse_pos = cur_pos;
        cam.position.x += diff.getX();
        cam.position.y -= diff.getY();
        cam.update();
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
