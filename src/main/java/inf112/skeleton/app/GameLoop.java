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
import java.util.Arrays;
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
    private Card[][] player_active_cards;
    private  final int NUM_ACTIVE_CARDS = 5;

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

            map = new Map(180, 0, 300, 200, "map.tmx");

            ArrayList<Robot> robots = new ArrayList<>();
            for (int[] pos : robot_start_positions) {
                Robot robut = new Robot(pos[0], pos[1]); //Robut
                robots.add(robut);
                map.addDrawJob(robut);
            }


            Vector2Di map_dim = map.getDimensions();
            System.out.println("Map Dimensions: " + map_dim);
            this.game = new Game(map_dim.getX(), map_dim.getY(), robots);
            current_robot = game.getRobot(game.getActivePlayer());
            current_robot.rot(-90);

            try {
                this.game.handOutCards();
            } catch (CardDeck.NoMoreCards e) {
                // NOTE: There is a check for this in the Game() constructor, so this
                //       exception will never happen directly after the Game is instantiated.
            }

            player_active_cards = new Card[robots.size()][NUM_ACTIVE_CARDS];
            card_queue = new CardManager(NUM_ACTIVE_CARDS);
            card_queue.setCards(game.getActivePlayer().getHand());

            InputMultiplexer input_multi = new InputMultiplexer();
            for (InputProcessor p : card_queue.getInputProcessors())
                input_multi.addProcessor(p);
            input_multi.addProcessor(map);
            input_multi.addProcessor(this);
            input_multi.addProcessor(map);
            Gdx.input.setInputProcessor(input_multi);

            batch = new SpriteBatch();
            font = new BitmapFont();
            font.setColor(Color.BLACK);

            game.appendToLogBuilder("Click on the deck to show all cards");
            game.appendToLogBuilder("Press e to run selected cards");
            game.appendToLogBuilder("Use scrollwheel to scroll cards");
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
        soundNametoFile.put("Oof", Resources.getSound( "Oof.ogg"));
        soundNametoFile.put("showCards", Resources.getSound("showCards.ogg"));
        soundNametoFile.put("snapCard", Resources.getSound("snapCard.ogg"));
        soundNametoFile.put("hideCards", Resources.getSound("hideCards.ogg"));
        soundNametoFile.put("Laser", Resources.getSound("Laser.mp3"));
        musicPlayer = Resources.getMusic("iRobot.ogg");
        musicPlayer.setVolume(0.5f);
        musicPlayer.setLooping(true);
    }

    public void render () {
        // Check for sounds to play
        for (String sound : game.checkPlaySound()) {
            fxPlayer = soundNametoFile.get(sound);
            if (musicPlayer.isPlaying()) {
                musicPlayer.setVolume(0.3f);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        musicPlayer.setVolume(0.5f);
                    }
                }, 0.05f);
            }
            fxPlayer.play();
        }

        ArrayList<Vector2Di> clicks = map.getTileClicks();
        if (!clicks.isEmpty())
            System.out.println(Arrays.toString(clicks.toArray()));

        // Clear the screen with the background color.
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
        if (keycode <= KEY_NUM_END && keycode >= KEY_NUM_BEGIN) {
            int number = keycode - KEY_NUM_BEGIN;
            try {
                System.out.println("Input  " + number);
                player_active_cards[game.getActivePlayerNum()] = card_queue.getActive_cards();
                System.out.println("Saving:");
                for (Card c :  player_active_cards[game.getActivePlayerNum()])
                    System.out.println(c);

                current_robot = game.getRobot(number);
                game.setActivePlayerNum(number);
                card_queue.setCards(game.getActivePlayer().getHand());
                System.out.println("Cards gotten from player_active_cards  " + game.getActivePlayerNum());
                for (Card c :  player_active_cards[game.getActivePlayerNum()])
                    System.out.println(c);

                card_queue.setActive_cards(player_active_cards[game.getActivePlayerNum()]);

            } catch (IndexOutOfBoundsException e) {
                game.appendToLogBuilder("No such robot");
            }
            return true;
        }
        switch (keycode) {
            case Input.Keys.DOWN:
                Commands.moveCommand.exec(-1, current_robot, game);
                break;
            case Input.Keys.UP:
                Commands.moveCommand.exec(1, current_robot, game);
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
            case Input.Keys.L:
                game.shootLaser(current_robot.getPos(), current_robot.getDir());
                break;
            case Input.Keys.R:
                current_robot.powerOn();
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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