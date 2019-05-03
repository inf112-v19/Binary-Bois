package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import java.io.IOException;
import java.util.ArrayList;

enum JoinState {
    WAITING_FOR_INPUT,
    WAITING_FOR_GAME,
}

public class JoinGameMenu implements Screen, InputProcessor {
    private String init_key = "";
    private RoboRally game;
    private JoinState state = JoinState.WAITING_FOR_INPUT;
    private GameFinder game_finder;

    public JoinGameMenu(RoboRally game) {
        this.game = game;
        Gdx.input.setInputProcessor(this);
        game_finder = new GameFinder();
    }

    @Override
    public void show() { }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0,0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch (state) {
            case WAITING_FOR_INPUT: {
                game.batch.begin();
                game.font.getData().setScale(3.0f);
                game.font.draw(game.batch, "Game code: " + init_key, 0, Gdx.graphics.getHeight()/2);
                game.font.getData().setScale(1.0f);
                game.batch.end();
            } break;

            case WAITING_FOR_GAME: {
                ArrayList<GameSettings> games = game_finder.getGames();
                for (GameSettings game_set : games) {
                    System.out.println("Found game: " + game_set);
                    try {
                        game.setScreen(new GameLoop(game_set.getHost(), init_key, game.batch, game.font));
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Unable to connect to: " + game_set);
                    }
                }
                dispose();
            } break;
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
        switch (key) {
            case Input.Keys.BACKSPACE:
                if (init_key.length() > 0) {
                    init_key = init_key.substring(0, init_key.length() - 1);
                    System.out.println(init_key);
                }
            break;

            case Input.Keys.ENTER:
                game_finder.start();
                state = JoinState.WAITING_FOR_GAME;
            break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        char ok_array[] = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

        boolean good = false;
        for (char ok_c : ok_array)
            if (ok_c == c)
                good = true;
        if (!good)
            return false;
        init_key += c;
        System.out.println(init_key);
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
