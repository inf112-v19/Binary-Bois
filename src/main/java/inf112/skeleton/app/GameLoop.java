package inf112.skeleton.app;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.HashMap;


public class GameLoop extends ApplicationAdapter implements InputProcessor {
    private static int[][] robot_start_positions = {
            {6, 5},
            {6, 6},
            {6, 7},
            {6, 8}
    };
    // All positions are in board dimensions, not in pixel dimensions.
    private Music musicPlayer;
    private Sound fxPlayer;
    private Robot current_robot;
    private Game game;

    private Map map;

    int map_px_w, map_px_h;
    private BitmapFont font;
    private SpriteBatch batch;
    private Color bgcolor;
    private CardManager card_queue;
    private HashMap<String, Sound> soundNametoFile = new HashMap<>();

    public GameLoop(int map_px_w, int map_px_h) {
        super();
        this.map_px_w = map_px_w;
        this.map_px_h = map_px_h;
        bgcolor = new Color(0.5f, 0.5f, 0.5f, 1);
    }

    @Override
    public void create () {
        try {
            addSounds();

            map = new Map(180, 0, 320, 320, "map.tmx");

            ArrayList<Robot> robots = new ArrayList<>();
            for (int[] pos : robot_start_positions) {
                Robot robut = new Robot(pos[0], pos[1]); //Robut
                robots.add(robut);
                map.addDrawJob(robut);
            }
            current_robot = robots.get(robots.size() - 1);
            current_robot.rot(-90);

            Vector2Di map_dim = map.getDimensions();
            System.out.println("Map Dimensions: " + map_dim);
            this.game = new Game(map_dim.getX(), map_dim.getY(), robots);

            try {
                this.game.handOutCards();
            } catch (CardDeck.NoMoreCards e) {
                // NOTE: There is a check for this in the Game() constructor, so this
                //       exception will never happen directly after the Game is instantiated.
            }

            card_queue = new CardManager(5);
            card_queue.setCards(game.getActivePlayer().getHand());

            InputMultiplexer input_multi = new InputMultiplexer();
            input_multi.addProcessor(card_queue.getStage());
            input_multi.addProcessor(this);
            Gdx.input.setInputProcessor(input_multi);

            batch = new SpriteBatch();
            font = new BitmapFont();
            font.setColor(Color.BLACK);

            game.appendToLogBuilder("Press h to hide all cards");
            game.appendToLogBuilder("Press q to show all cards");
            game.appendToLogBuilder("Press e to run selected cards");
        } catch (NoSuchResource e) {
            System.out.println("Unable to load: " + e.getMessage());
            System.exit(1);
        } catch (Game.InitError e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private void addSounds() throws NoSuchResource {
        soundNametoFile.put("Death", Resources.getSound("d.e.a.t.h.ogg"));
        soundNametoFile.put("Move1", Resources.getSound("Move1.ogg"));
        soundNametoFile.put("Move2", Resources.getSound("Move2.ogg"));
        soundNametoFile.put("Move3", Resources.getSound("Move3.ogg"));
        soundNametoFile.put("Flag", Resources.getSound("Flag.ogg"));
        soundNametoFile.put("Wrench", Resources.getSound( "Wrench.ogg"));
        musicPlayer = Resources.getMusic("iRobot.ogg");
        musicPlayer.setVolume(0.6f);
        musicPlayer.setLooping(true);
    }


    public void render () {
        // Clear the screen with the background color.
        ArrayList<String> sounds = game.checkPlaySound();
        for (String sound : sounds) {
            fxPlayer = soundNametoFile.get(sound);
            if (musicPlayer.isPlaying()) {
                musicPlayer.setVolume(0.4f);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        musicPlayer.setVolume(0.6f);
                    }
                }, 0.05f);
            }
            fxPlayer.play();
        }

        Gdx.gl.glClearColor(bgcolor.r, bgcolor.g, bgcolor.b, bgcolor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Renderable.updateAll();

        if (game.getPrintLog() != null) {
            batch.begin();
            font.draw(batch, game.getPrintLog(), 0, 750);
            batch.end();
        }

        map.render();

        card_queue.render(batch);

        // TODO: The UI will be drawn here later.
    }

    @Override
    public boolean keyDown(int keycode) {
        final int KEY_NUM_END = 16;
        final int KEY_NUM_BEGIN = 8;
        int move_amount = 1;
        if (keycode <= KEY_NUM_END && keycode >= KEY_NUM_BEGIN) {
            int number = keycode - KEY_NUM_BEGIN;
            try {
                current_robot = game.getRobot(number);
            } catch (IndexOutOfBoundsException e) {
                game.appendToLogBuilder("No such robot");
            }
            return true;
        }
        switch (keycode) {
            case Input.Keys.DOWN:
                move_amount = -1;
            case Input.Keys.UP:
                Commands.moveCommand.exec(move_amount, current_robot, game);
                break;
            case Input.Keys.RIGHT:
                Commands.rotateCommand.exec(-90, current_robot, game);
                break;
            case Input.Keys.LEFT:
                Commands.rotateCommand.exec(90, current_robot, game);
                break;
            case Input.Keys.M:
                if (!musicPlayer.isPlaying())
                    musicPlayer.play();
                else
                    musicPlayer.stop();
                break;

            case Input.Keys.S:
                current_robot.addAnimation(Animation.scaleTo(current_robot, 3, 1f));
                break;

            case Input.Keys.K:
                game.killRobot(current_robot);
                break;

            // Execute all cards that are queued
            case Input.Keys.E:
                card_queue.getSequenceAsCommand().exec(1, current_robot, game);
                break;

            case Input.Keys.Q:
                card_queue.showCards();
                break;

            case Input.Keys.H:
                card_queue.hideCards();
                break;

            default:
                return false;
        }
        return true;
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