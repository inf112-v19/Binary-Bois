package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.Vector;

public class GameLoop extends ApplicationAdapter implements InputProcessor {
    private static int[][] robot_start_positions = {
            {6, 5},
            {6, 6},
            {6, 7},
            {6, 8}
    };
    TiledMap tiledMap;
    Vector2D map_dim;
    OrthographicCamera camera;
    TiledMapRenderer tiledMapRenderer;
    // All positions are in board dimensions, not in pixel dimensions.
    Vector<IRenderable> board_render_queue;
    private Music player;
    private SpriteBatch batch;
    private Robot my_robot;
    private Game game;

    @Override
    public void create () {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w, h);
        camera.update();
        tiledMap = new TmxMapLoader().load("./resources/map.tmx");
        System.out.println(tiledMap.getProperties());
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();
        board_render_queue = new Vector<>();
        map_dim = new Vector2D(
                tiledMap.getProperties().get("width", Integer.class),
                tiledMap.getProperties().get("height", Integer.class));
        FileHandle file = new FileHandle("resources/RoboLazer.mp3");
        player = Gdx.audio.newMusic(file);
        player.setLooping(true);

        ArrayList<Robot> robots = new ArrayList<>();
        for (int[] pos : robot_start_positions) {
            Robot robut = new Robot(pos[0], pos[1]);
            robots.add(robut);
            board_render_queue.add(robut);
        }
        my_robot = robots.get(robots.size() - 1);
        my_robot.rot(-90);

        System.out.println("Map Dimensions: " + map_dim);

        this.game = new Game(map_dim.getX(), map_dim.getY(), robots);

        Gdx.input.setInputProcessor(this);
    }

    public Vector2D toPixelCoordinate(Vector2D vec) {
        int pw = Gdx.graphics.getWidth();
        int ph = Gdx.graphics.getHeight();
        int w = map_dim.getX();
        int h = map_dim.getY();
        return new Vector2D(vec.getX() * (pw / w), vec.getY() * (ph / h));
    }

    public void render () {
        tiledMapRenderer.setView(camera);
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        tiledMapRenderer.render();
        batch.begin();
        for (IRenderable r : board_render_queue) {
            Vector2D pos = r.getPos();
            Vector2D px_pos = toPixelCoordinate(pos);
            batch.draw(r.getTexture(), px_pos.getX(), px_pos.getY());
        }
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        System.out.println("DOWN");
        System.out.flush();
        switch (keycode) {
            case Input.Keys.UP:
                System.out.println("UP");
                if (game.canMoveTo(my_robot.getPos(), my_robot.getDir(), my_robot)) {
                    my_robot.forward(1);
                } else {
                    System.out.println("There is a wall ahead!");
                }
                System.out.println(my_robot.getPos());
                TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0); // assuming the layer at index on contains tiles
                TiledMapTileLayer.Cell cell = layer.getCell(my_robot.getPos().getX(), my_robot.getPos().getY());
                System.out.println(cell.getTile().getProperties().get("MapObject", String.class));
                break;
            case Input.Keys.RIGHT:
                my_robot.rot(-90);
                break;
            case Input.Keys.LEFT:
                my_robot.rot(90);
                break;
            case Input.Keys.M:
                if (!player.isPlaying())
                    player.play();
                else
                    player.stop();
                break;
            case Input.Keys.F:
                game.printFlags();
                break;
            case Input.Keys.DOWN:
                System.out.println("UP");
                my_robot.backward(-1);
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        System.out.println("UP");
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
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

    public void addBoardDrawJob(IRenderable obj) {
        board_render_queue.add(obj);
    }

}