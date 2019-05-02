package inf112.skeleton.app;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameLoop extends ApplicationAdapter implements InputProcessor {
    private static int[][] robot_start_positions = {
            {6, 6},
            {6, 7},
            {6, 8},
            {6, 9}
    };
    private static final int num_players = robot_start_positions.length;   //FIXME: Only for testing purposes
    private Music musicPlayer;
    private Sound fxPlayer;
    private Robot current_robot;
    private ArrayList<Robot> robots;
    private Game game;
    private Round round = null;
    private GameState state = GameState.GAME_START;
    /**In seconds */
    public static final int POWER_ON_TIMEOUT = 2;
    private double state_start_t = 0.0;

    private GameMap map;
    private BitmapFont font;
    private SpriteBatch batch;
    private Color bgcolor = new Color(0.5f, 0.5f, 0.5f, 1);;
    private HashMap<String, Sound> soundNametoFile = new HashMap<>();

    private boolean autofill_cards = false;

    public GameLoop() {
        super();
    }

    public GameLoop(boolean autofill) {
        super();
        this.autofill_cards = autofill;
    }

    /**
     * Set up the input processors.
     *
     * @param extra Extra input processors that run *before* the default ones.
     */
    public void setInputs(ArrayList<InputProcessor> extra) {
        InputMultiplexer mul = new InputMultiplexer();
        for (InputProcessor inp : extra) {
            mul.addProcessor(inp);
        }
        mul.addProcessor(map);
        mul.addProcessor(this);
        mul.addProcessor(map);
        Gdx.input.setInputProcessor(mul);
    }

    private void updatePlayer(int player_idx) {
        if (player_idx < 0 || player_idx >= num_players)
            throw new IllegalArgumentException("Illegal player-index: " + player_idx);
        current_robot = game.getRobot(player_idx);
        game.setActivePlayerNum(player_idx);
        Player p = game.getActivePlayer();
        setInputs(p.getCardManager().getInputProcessors());
    }



    @Override
    public void create () {
        robots = new ArrayList<>();
        try {
            addSounds();

            map = new GameMap(180, 0, 300, 200, "map2.tmx");

            for (int[] pos : robot_start_positions) {
                Robot robut = new Robot(pos[0], pos[1]); //Robut
                robots.add(robut);
                map.addDrawJob(robut);
            }

            Vector2Di map_dim = map.getDimensions();
            System.out.println("GameMap Dimensions: " + map_dim);
            this.game = new Game(map_dim.getX(), map_dim.getY(), robots);

            try {
                this.game.handOutCards();
            } catch (CardDeck.NoMoreCards e) {
                // NOTE: There is a check for this in the Game() constructor, so this
                //       exception will never happen directly after the Game is instantiated.
            }

            updatePlayer(0);

            batch = new SpriteBatch();
            font = new BitmapFont();
            font.setColor(Color.BLACK);

            game.appendToLogBuilder("Click on the deck to show all cards");
            game.appendToLogBuilder("Press e to run selected cards");
            game.appendToLogBuilder("Use scrollwheel to scroll cards");

            if (autofill_cards)
                 game.forceActiveCards();
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
        ArrayList<Vector2Di> vecs = map.getTileClicks();
        if (!vecs.isEmpty()) {
            Vector2Di to = vecs.get(0);
            Vector2Di from = current_robot.getPos();
            ArrayList<Vector2Di> path = game.fromTo(from, to);
            path.add(to);
            ArrayList<Card> cards = AiPlayer.chooseCards(current_robot.getDir(), path, game.getActivePlayer().getHand());
            for (Card c : cards)
                System.out.println("   " + c);
            ICommand cmd = CardManager.getSequenceAsCommand(cards);
            cmd.exec(1, current_robot, game);
        }


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

        if (Game.getWinCondition()) {
            batch.begin();
            font.getData().setScale(5.0f);
            font.draw(batch, "WINNER", 400, 750);
            font.getData().setScale(1);
            batch.end();
        }

        map.render();

        switch (state) {
            case GAME_START:
                state = GameState.CHOOSING_CARDS;
                break;
            case CHOOSING_CARDS:
                getCardManager().render(batch);
                break;
            case RUNNING_ROUND:
                if (round != null && !round.doStep()) {
                    round = null;
                    state = GameState.RESPAWNING;
                }
                break;
            case RESPAWNING:
                Robot last_robot = null;
                float wait = 0.0f;
                for (Robot r : getDeadRobots()) {
                    r.addAnimation(Animation.idle(wait += 0.5f));
                    r.respawn(game);
                    last_robot = r;
                }

                Runnable run = () -> {
                    System.out.println("CALLBACK");
                    state = GameState.CHECKING_POWER_ON;
                    state_start_t = System.currentTimeMillis() / 1000.0;
                };
                if (last_robot != null) {
                    last_robot.addAnimationCallback(run);
                } else {
                    run.run();
                }
                break;

            case CHECKING_POWER_ON:
                //System.out.println("Checking for power on");
                double cur_time = System.currentTimeMillis() / 1000.0;
                if (state_start_t + POWER_ON_TIMEOUT <= cur_time)
                    state = GameState.CHOOSING_CARDS;
                break;
        }

        // TODO: The UI will be drawn here later.
    }

    private ArrayList<Robot> getDeadRobots() {
        ArrayList<Robot> dead_robots = new ArrayList<>();
        for (Robot r : robots)
            if (r.hasDied())
                dead_robots.add(r);
        return dead_robots;
    }

    private CardManager getCardManager() {
        return game.getActivePlayer().getCardManager();
    }

    @Override
    public boolean keyDown(int keycode) {
        final int KEY_NUM_END = 16;
        final int KEY_NUM_BEGIN = 8;
        if (keycode <= KEY_NUM_END && keycode >= KEY_NUM_BEGIN) {
            int number = keycode - KEY_NUM_BEGIN;
            try {
                updatePlayer(number);

            } catch (IndexOutOfBoundsException e) {
                game.appendToLogBuilder("No such robot");
            }
            return true;
        }
        switch (keycode) {
            //Does a round with the active cards
            case Input.Keys.D:
                ArrayList<ArrayList<Card>> active_cards = new ArrayList<>();
                for (int i = 0; i < num_players; i++) {
                    active_cards.add(game.getPlayer(i).getCardManager().getActiveCards());
                }
                round = new Round(robots, active_cards, game);
                state = GameState.RUNNING_ROUND;
                break;
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
                getCardManager().getSequenceAsCommand().exec(1, current_robot, game);
                break;

            case Input.Keys.Q:
                getCardManager().showCards();
                break;
            case Input.Keys.H:
                getCardManager().hideCards();
                break;
            case Input.Keys.L:
                game.shootLaser(current_robot.getPos(), current_robot.getDir());
                break;
            case Input.Keys.R:
                current_robot.respawn(game);
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