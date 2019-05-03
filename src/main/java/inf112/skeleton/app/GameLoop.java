package inf112.skeleton.app;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

abstract class AZucc extends Thread {
    public abstract ArrayList<Card> getCards() throws NoSuchResource;
    public abstract ArrayList<ArrayList<Card>> getRoundCards();
    public abstract void setActiveCards(ArrayList<Card> active_cards);
    public abstract void submitAnswer();
    public abstract void setUpState(boolean up_state);
    public abstract JSONObject getConfig();
    public abstract void reset();
}

class AiZucc extends AZucc {

    private boolean do_return_cards = true;
    private JSONObject cfg;
    private ArrayList<Card> active_cards;
    private RoboRallyGame game;
    private final int NUM_PLAYERS = 4;
    private JSONObject game_settings;
    private CardDeck deck;

    public void reset() {
    }

    public AiZucc(RoboRallyGame game) throws IOException, NoSuchResource, CSV.CSVError, CardDeck.NoMoreCards {
        deck = new CardDeck(StaticConfig.GAME_CARDS_SRC);
        JSONObject config = new JSONObject();
        config.put("robots_pos", StaticConfig.DEFAULT_ROBOTS_POS_JSON);
        config.put("flags_pos", new JSONArray());
        config.put("version", StaticConfig.VERSION);
        config.put("num_players", NUM_PLAYERS);
        config.put("map", StaticConfig.DEFAULT_GAME_OPTIONS.get("map"));
        config.put("num_starting_cards", StaticConfig.DEFAULT_GAME_OPTIONS.get("num_starting_cards"));
        config.put("choosing_cards_time",  StaticConfig.DEFAULT_GAME_OPTIONS.get("choosing_cards_time"));
        this.game_settings = game_settings;
        JSONObject game_init_cfg = new JSONObject();
        game_init_cfg.put("idx", NUM_PLAYERS);
        game_init_cfg.put("robots_pos", game_settings.get("robots_pos"));
        try {
            ArrayList<Card> cards = deck.get(9);
            JSONArray cards_jarr = new JSONArray();
            for (Card c : cards)
                cards_jarr.put(c.asJSON());
            game_init_cfg.put("cards", cards_jarr);
        } catch (CardDeck.NoMoreCards e) {
            SystemPanic.panic("loaded empty card set");
        }
        game_init_cfg.put("flags_pos", game_settings.get("flags_pos"));
        this.game = game;
    }

    public ArrayList<Card> getCards() throws NoSuchResource {
        if (!do_return_cards)
            return null;
        do_return_cards = false;
        return active_cards;
    }

    public ArrayList<ArrayList<Card>> getRoundCards() {
        ArrayList<ArrayList<Card>> round_cards = new ArrayList<>();
        round_cards.add(active_cards);

        for (int i = 1; i < NUM_PLAYERS; i++) {
            Player p = game.getPlayer(i);
            Robot robot = game.getRobot(p);
            Vector2Di to = new Vector2Di(12, 12);      //TODO: DUMMY DUMMY
            Vector2Di from = robot.getPos();
            ArrayList<Vector2Di> path = game.fromTo(from, to);
            ArrayList<Card> cards = AiPlayer.chooseCards(robot.getDir(), path, game.getActivePlayer().getHand(), 10);
            for (Card c : cards)
                System.out.println("   " + c);
            round_cards.add(cards);
        }
        return round_cards;

    }

    public void setActiveCards(ArrayList<Card> active_cards) {
        this.active_cards = active_cards;
    }

    public void submitAnswer() {
        do_return_cards = true;
    }

    public void setUpState(boolean up_state) {
        ;
    }


    public JSONObject getConfig() {
        return cfg;
    }

    public void run() {
        ;
    }
}

/**
 * Zucc sucks in commands from the server and handles them.
 */
class Zucc extends AZucc {
    private GameSocket gsock;
    private ArrayList<JSONArray> cards_json = new ArrayList<>();
    private JSONArray cards_answer = new JSONArray();
    private ArrayList<ArrayList<Card>> round_cards = null;
    private boolean has_final_answer = false;
    private JSONObject game_init_cfg = null;
    private final Object monitor = new Object();
    private boolean up_state = true;

    public Zucc(GameSocket gsock) throws IOException {
        this.gsock = gsock;
        try {
            game_init_cfg = gsock.recv();
            if (!JSONTools.checkSpec(game_init_cfg, JSONSpecs.game_init))
                throw new JSONException("game_init did not match spec");
            cards_json.add(game_init_cfg.getJSONArray("cards"));
        } catch (GameSocketException e) {
            SystemPanic.panic("fucc");
        }
    }

    /**
     * Get initial game configuration.
     */
    public JSONObject getConfig() {
        return game_init_cfg;
    }

    /**
     * Listen on the gamesocket and handle events.
     */
    public void run() {
        System.out.println("Starting Zucc.run() ...");
        for (;;) {
            try {
                //System.out.println("Waiting for command ...");
                JSONObject obj = gsock.recv();
                //System.out.println("Received command.");
                if (!JSONTools.checkSpec(obj, JSONSpecs.cmd_base))
                    throw new JSONException("Illegal JSON structure.");
                String cmd = obj.getString("cmd");
                switch (cmd) {
                    case "new_cards":
                        if (!JSONTools.checkSpec(obj, JSONSpecs.card_deal))
                            throw new JSONException("new_cards command did not match spec");

                        synchronized (this) {
                            cards_json.add(obj.getJSONArray("cards"));
                        }
                    break;

                    case "get_cards":
                        JSONObject cards_obj = new JSONObject();
                        synchronized (this) {
                            cards_obj.put("cards", cards_answer);
                            cards_obj.put("final", has_final_answer);
                        }
                        gsock.send(cards_obj);
                    break;

                    case "run_round":
                        System.out.println("RUNNING THE ROUND");
                        if (!JSONTools.checkSpec(obj, JSONSpecs.run_round_map))
                            throw new JSONException("run_round command did not match spec");
                        JSONArray player_cards_arr = obj.getJSONArray("player_cards");
                        ArrayList<ArrayList<Card>> round_cards = new ArrayList<>();
                        for (Object cards : player_cards_arr)
                            round_cards.add(Card.fromJSON((JSONArray) cards));

                        synchronized (this) {
                            this.round_cards = round_cards;
                        }

                        synchronized (monitor) {
                            try {
                                monitor.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        obj = new JSONObject();
                        obj.put("status", "OK");
                        gsock.send(obj);
                    break;

                    default:
                        throw new JSONException("Unrecognized command: " + cmd);
                }
            } catch (JSONException e) {
                System.out.println("JSON decoding error: " + e);
            } catch (IOException e) {
                // TODO: Handle connection-dropping exceptions properly.
                System.out.println("Exception: " + e);
            } catch (NoSuchResource e) {
                SystemPanic.panic("NoSuchResource");
            }

            try {
                Thread.sleep(1024);
            } catch (InterruptedException e) {
                ;
            }
        }
    }

    /**
     * Retrieve cards that have been received from the server.
     *
     * @return null if there are no new cards, otherwise an array
     *         of the new cards
     * @throws NoSuchResource Thrown by Cards::new.
     */
    public ArrayList<Card> getCards() throws NoSuchResource {
        ArrayList<JSONArray> cards_json;
        synchronized (this) {
            if (this.cards_json.isEmpty())
                return null;
            cards_json = this.cards_json;
            this.cards_json = new ArrayList<>();
        }

        ArrayList<Card> cards = new ArrayList<>();
        for (JSONArray arr : cards_json)
            for (Object jobj : arr)
                cards.add(Card.fromJSON((JSONObject) jobj));

        return cards;
    }

    /**
     * Get the cards to be executed this round.
     *
     * @return Array indexed by player index, where arr[1] = player-1s cards.
     *         Returns null if the round isn't ready yet.
     */
    public ArrayList<ArrayList<Card>> getRoundCards() {
        synchronized (this) {
            ArrayList<ArrayList<Card>> round_cards = this.round_cards;
            this.round_cards = null;
            return round_cards;
        }
    }

    /**
     * Set the cards that should be sent to the server.
     *
     * @param active_cards 5 cards.
     */
    public void setActiveCards(ArrayList<Card> active_cards) {
        synchronized (this) {
            cards_answer = new JSONArray();
            for (Card c : active_cards)
                cards_answer.put(c.asJSON());
        }
    }

    /**
     * Tell the server that the current answer is the final answer.
     */
    public void submitAnswer() {
        synchronized (this) {
            has_final_answer = true;
        }
    }

    /**
     * Set the power up/down state.
     *
     * @param up_state Big if true.
     */
    public void setUpState(boolean up_state) {
        synchronized (monitor) {
            this.up_state = up_state;
            monitor.notify();
        }
    }

    /**
     * Reset the Zucc to prepare for a new round.
     */
    public void reset() {
        has_final_answer = false;

        // Empty the selected cards
        cards_answer = new JSONArray();
    }
}

public class GameLoop extends ApplicationAdapter implements InputProcessor, Screen {
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
    private RoboRallyGame game;
    private Round round = null;
    private GameState state = GameState.GAME_START;
    /**In seconds */
    public static final int POWER_ON_TIMEOUT = 2;
    private double state_start_t = 0.0;

    private GameMap map;
    private BitmapFont font;
    private SpriteBatch batch;
    private Color bgcolor = new Color(0.5f, 0.5f, 0.5f, 1);
    private HashMap<String, Sound> soundNametoFile = new HashMap<>();
    private GameSocket gsock;
    private int local_player_idx = 0;
    
    private boolean ai_game;

    private AZucc zucc;
    private boolean autofill_cards = false;
    private String host;
    private String init_key;

    public GameLoop(String host, String init_key) throws IOException {
        super();
        this.batch = null;
        this.font = null;
        this.host = host;
        this.init_key = init_key;
    }

    public GameLoop(String host, String init_key, SpriteBatch batch, BitmapFont font) throws IOException {
        super();
        this.batch = batch;
        this.font = font;
        if (font != null)
            font.setColor(Color.BLACK);
        this.host = host;
        this.init_key = init_key;
        create();
    }

    public GameLoop(String host, String init_key, RoboRally robo_rally, boolean ai_game) throws IOException {
        super();
        batch = robo_rally.batch;
        font = robo_rally.font;
        font.setColor(Color.BLACK);
        this.host = host;
        this.init_key = init_key;
        this.ai_game = true;
        create();
    }

    /**
     * Set up the input processors.
     *
     * @param extra Extra input processors that run *before* the default ones.
     */
    public void setInputs(ArrayList<InputProcessor> extra) {
        InputMultiplexer mul = new InputMultiplexer();
        for (InputProcessor inp : extra)
            mul.addProcessor(inp);
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

    public void create () {
        robots = new ArrayList<>();
        try {
            if (batch == null) batch = new SpriteBatch();
            if (font == null)  font = new BitmapFont();

            if (ai_game) {
                zucc = new AiZucc(game);
            } else {
                gsock = new GameSocket(host, init_key);
                zucc = new Zucc(gsock);
                zucc.start();
                local_player_idx = zucc.getConfig().getInt("idx");
            }

            addSounds();

            map = new GameMap(180, 0, 300, 200, "map2.tmx");

            int[][] robot_start_positions =
                    JSONTools.toIntMatrix(zucc
                                          .getConfig()
                                          .getJSONArray("robots_pos"));
            for (int[] pos : robot_start_positions) {
                Robot robut = new Robot(pos[0], pos[1]);
                robut.initTextures();
                robots.add(robut);
                map.addDrawJob(robut);
            }

            Vector2Di map_dim = map.getDimensions();
            System.out.println("GameMap Dimensions: " + map_dim);
            this.game = new RoboRallyGame(map_dim.getX(), map_dim.getY(), robots);
            this.game.initTextures();

            updatePlayer(local_player_idx);

            giveCards();

            game.appendToLogBuilder("Click on the deck to show all cards");
            game.appendToLogBuilder("Press e to run selected cards");
            game.appendToLogBuilder("Use scrollwheel to scroll cards");

            // Make sure the card manager passes the card order to zucc.
            game.getActivePlayer().getCardManager().onChange((Card[] cards_arr) -> {
                ArrayList<Card> cards = new ArrayList<>();
                for (Card c : cards_arr)
                    if (c != null)
                        cards.add(c);
                zucc.setActiveCards(cards);
            });

            if (StaticConfig.DEBUG && autofill_cards) {
                game.forceActiveCards();
                zucc.setActiveCards(game.getActivePlayer().getCardManager().getActiveCards());
            }
        } catch (NoSuchResource e) {
            System.out.println("Unable to load: " + e.getMessage());
            System.exit(1);
        } catch (RoboRallyGame.InitError e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Failed to connect: " + e);
            System.exit(1);
        } catch (CSV.CSVError | CardDeck.NoMoreCards e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void giveCards() throws NoSuchResource {
        ArrayList<Card> my_cards = zucc.getCards();
        if (my_cards != null) {
            for (Card c : my_cards)
                c.initTexture();
            game.getActivePlayer().getCardManager().removeAllCards(current_robot);
            game.getActivePlayer().giveDeck(my_cards, current_robot);
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

    public void render() {
        render(0.0f);
    }

    public void render (float dt) {
        /**ArrayList<Vector2Di> vecs = map.getTileClicks();
        if (!vecs.isEmpty()) {
            Vector2Di to = vecs.get(0);
            Vector2Di from = current_robot.getPos();
            ArrayList<Vector2Di> path = game.fromTo(from, to);
            ArrayList<Card> cards = AiPlayer.chooseCards(current_robot.getDir(), path, game.getActivePlayer().getHand(), 10);
            for (Card c : cards)
                System.out.println("   " + c);
            ICommand cmd = CardManager.getSequenceAsCommand(cards);
            cmd.exec(1, current_robot, game);
        }*/

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

        if (RoboRallyGame.getWinCondition()) {
            batch.begin();
            font.getData().setScale(5.0f);
            font.draw(batch, "WINNER", 400, 750);
            font.getData().setScale(1);
            batch.end();
        }

        map.render();

        try {
            giveCards();
        } catch (NoSuchResource e) {
            System.out.println(e + " in GameLoop render(), case WAITING_FOR_ROUND_START");
        }

        switch (state) {
            case GAME_START:
                state = GameState.CHOOSING_CARDS;
                game.appendToLogBuilder("Press Enter to submit cards");
            break;

            case CHOOSING_CARDS:
                getCardManager().render(batch);
            break;

            case WAITING_FOR_ROUND_START:
                ArrayList<ArrayList<Card>> round_cards = zucc.getRoundCards();
                if (round_cards != null) {
                    round = new Round(robots, round_cards, game);
                    state = GameState.RUNNING_ROUND;
                    game.appendToLogBuilder("Round starting ...");
                }
            break;

            case RUNNING_ROUND:
                game.emptyHand(current_robot);
                if (round != null && !round.doStep()) {
                    round = null;
                    zucc.reset();
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
                double cur_time = System.currentTimeMillis() / 1000.0;
                if (state_start_t + POWER_ON_TIMEOUT <= cur_time) {
                    state = GameState.CHOOSING_CARDS;
                    zucc.setUpState(true);
                }
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

        if (StaticConfig.DEBUG && keycode <= KEY_NUM_END && keycode >= KEY_NUM_BEGIN) {
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
                if (!StaticConfig.DEBUG) break;

                ArrayList<ArrayList<Card>> active_cards = new ArrayList<>();
                for (int i = 0; i < num_players; i++)
                    active_cards.add(game.getPlayer(i).getCardManager().getActiveCards());
                round = new Round(robots, active_cards, game);
                state = GameState.RUNNING_ROUND;
            break;

            case Input.Keys.DOWN:
                if (!StaticConfig.DEBUG) break;
                Commands.moveCommand.exec(-1, current_robot, game);
            break;

            case Input.Keys.UP:
                if (!StaticConfig.DEBUG) break;
                Commands.moveCommand.exec(1, current_robot, game);
            break;

            case Input.Keys.RIGHT:
                if (!StaticConfig.DEBUG) break;
                Commands.rotateCommand.exec(-90, current_robot, game);
            break;

            case Input.Keys.LEFT:
                if (!StaticConfig.DEBUG) break;
                Commands.rotateCommand.exec(90, current_robot, game);
            break;

            case Input.Keys.Y:
                ;

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
                if (!StaticConfig.DEBUG) break;
                game.killRobot(current_robot);
            break;

            // Execute all cards that are queued
            case Input.Keys.E:
                if (!StaticConfig.DEBUG) break;
                getCardManager().getSequenceAsCommand().exec(1, current_robot, game);
            break;

            case Input.Keys.Q:
                getCardManager().showCards();
            break;

            case Input.Keys.H:
                getCardManager().hideCards();
            break;

            case Input.Keys.L:
                if (!StaticConfig.DEBUG) break;
                game.shootLaser(current_robot.getPos(), current_robot.getDir());
            break;

            case Input.Keys.R:
                if (!StaticConfig.DEBUG) break;
                current_robot.respawn(game);
            break;

            case Input.Keys.ENTER:
                game.appendToLogBuilder("Waiting for round start ...");
                state = GameState.WAITING_FOR_ROUND_START;
                zucc.submitAnswer();
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

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }
}