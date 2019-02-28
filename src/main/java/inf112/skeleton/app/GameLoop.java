package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;

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
    // All positions are in board dimensions, not in pixel dimensions.
    private Music player;
    private Robot my_robot;
    private Game game;

    private Map map;

    int map_px_w, map_px_h;

    public GameLoop(int map_px_w, int map_px_h) {
        super();
        this.map_px_w = map_px_w;
        this.map_px_h = map_px_h;
    }

    @Override
    public void create () {
        FileHandle file = new FileHandle("resources/RoboLazer.mp3");
        player = Gdx.audio.newMusic(file);
        player.setLooping(true);

        map = new Map(180, 0, 320, 320, "./resources/map.tmx");

        ArrayList<Robot> robots = new ArrayList<>();
        for (int[] pos : robot_start_positions) {
            Robot robut = new Robot(pos[0], pos[1]);
            robots.add(robut);
            map.addDrawJob(robut);
        }
        my_robot = robots.get(robots.size() - 1);
        my_robot.rot(-90);

        Vector2D map_dim = map.getDimensions();
        System.out.println("Map Dimensions: " + map_dim);
        this.game = new Game(map_dim.getX(), map_dim.getY(), robots);

        Gdx.input.setInputProcessor(this);
    }

    public void render () {
        // Clear the screen with a black color.
        Gdx.gl.glClearColor( 0, 0, 0, 1 );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        map.render();

        // TODO: The UI will be drawn here later.
    }

    @Override
    public boolean keyDown(int keycode) {
        int dir = 1;
        switch (keycode) {
            case Input.Keys.DOWN:
                dir = -1;
            case Input.Keys.UP:
                Vector2D dir_v = my_robot.getDir().copy();
                dir_v.mul(dir);
                if (game.canMoveTo(my_robot.getPos(), dir_v, my_robot)) {
                    my_robot.move(dir);
                }
                /*
                System.out.println(my_robot.getPos());
                TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(0); // assuming the layer at index on contains tiles
                TiledMapTileLayer.Cell cell = layer.getCell(my_robot.getPos().getX(), my_robot.getPos().getY());
                System.out.println(cell.getTile().getProperties().get("MapObject", String.class));
                */
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
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
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
}