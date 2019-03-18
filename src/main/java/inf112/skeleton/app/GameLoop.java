package inf112.skeleton.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

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
    private BitmapFont font;
    private SpriteBatch batch;
    private Color bgcolor;

    public GameLoop(int map_px_w, int map_px_h) {
        super();
        this.map_px_w = map_px_w;
        this.map_px_h = map_px_h;
        bgcolor = new Color(1, 1, 1, 1);
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

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
    }


    public void render () {
        // Clear the screen with the background color.
        Gdx.gl.glClearColor(bgcolor.r, bgcolor.g, bgcolor.b, bgcolor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.getPrintLog() != null) {
            batch.begin();
            font.draw(batch, game.getPrintLog(), 0, 750);
            batch.end();
        }

        map.render();

        // TODO: The UI will be drawn here later.
    }

    @Override
    public boolean keyDown(int keycode) {
        int move_amount = 1;
        switch (keycode) {
            case Input.Keys.DOWN:
                move_amount = -1;
            case Input.Keys.UP:
                Commands.moveCommand.exec(move_amount, my_robot, game);
                break;
            case Input.Keys.RIGHT:
                Commands.rotateCommand.exec(-90, my_robot, game);
                break;
            case Input.Keys.LEFT:
                Commands.rotateCommand.exec(90, my_robot, game);
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