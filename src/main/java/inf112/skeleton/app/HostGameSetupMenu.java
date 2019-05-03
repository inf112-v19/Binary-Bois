package inf112.skeleton.app;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class HostGameSetupMenu implements Screen, InputProcessor {
    private GameMap map;
    private ArrayList<Robot> player_robots = new ArrayList<>();
    private boolean is_done = false;
    private String init_key = GameServer.generateKey();
    private RoboRally game;
    private SpriteBatch batch;
    private BitmapFont font;
    private final float header_text_scale = 6.0f;
    private String header_text;
    private float header_text_width;

    public HostGameSetupMenu(RoboRally game) {
        try {
            this.font = game.font;
            this.batch = game.batch;
            this.game = game;

            map = new GameMap(180, 0, 300, 200, "map2.tmx");
            //this.game = game;

            GlyphLayout layout = new GlyphLayout();
            header_text = "Your code: " + init_key;
            layout.setText(game.font, header_text);
            header_text_width = layout.width * header_text_scale;
            System.out.println("header_text_width: " + header_text_width);

            InputMultiplexer mulplex = new InputMultiplexer();
            mulplex.addProcessor(map);
            mulplex.addProcessor(this);
            Gdx.input.setInputProcessor(mulplex);
        } catch (NoSuchResource e) {
            e.printStackTrace();
            SystemPanic.panic("Unable to find game resources for HostGameSetupMenu");
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
        map.render();

        for (Vector2Di player_pos : map.getTileClicks()) {
            System.out.println(player_pos);
            Robot r = new Robot(player_pos.getX(), player_pos.getY());
            try {
                r.initTextures();
            } catch (NoSuchResource e) {
                e.printStackTrace();
                break;
            }
            player_robots.add(r);
            map.addDrawJob(r);
        }

        game.batch.begin();
        game.font.getData().setScale(6.0f);
        game.font.draw(
                game.batch,
                header_text,
                (int) ((Gdx.graphics.getWidth()/2.0f)-header_text_width),
                Gdx.graphics.getHeight()/2+400);
        game.font.getData().setScale(1);
        game.font.draw(game.batch, "Press enter to begin!", 100, 100);
        game.batch.end();

        Renderable.updateAll();

        if (is_done) {
            try {
                Robot.resetNameIntCounter();

                JSONObject config = new JSONObject();
                JSONArray robots_pos = new JSONArray();
                for (Robot r : player_robots) {
                    JSONArray pos = new JSONArray();
                    pos.put(r.getPos().getX());
                    pos.put(r.getPos().getY());
                    robots_pos.put(pos);
                }

                config.put("robots_pos", robots_pos);
                config.put("flags_pos", new JSONArray());
                config.put("version", StaticConfig.VERSION);
                config.put("num_players", player_robots.size());
                config.put("map", StaticConfig.DEFAULT_GAME_OPTIONS.get("map"));
                config.put("num_starting_cards", StaticConfig.DEFAULT_GAME_OPTIONS.get("num_starting_cards"));
                config.put("choosing_cards_time",  StaticConfig.DEFAULT_GAME_OPTIONS.get("choosing_cards_time"));

                GameServer server = new GameServer(1, config, init_key);
                server.start();

                game.setScreen(new GameLoop("localhost", init_key, batch, font));
                dispose();
            } catch (IOException | NoSuchResource | CSV.CSVError e) {
                e.printStackTrace();
                SystemPanic.panic("Unable to start up GameServer");
            }
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int key) {
        System.out.println("DOWN");;
        switch (key) {
            case Input.Keys.ENTER:
                System.out.println("GOT ENTER");
                is_done = true;
            break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int i) {
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
}
