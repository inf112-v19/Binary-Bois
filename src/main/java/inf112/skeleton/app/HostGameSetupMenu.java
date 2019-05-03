package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import java.util.ArrayList;

public class HostGameSetupMenu implements Screen {
    private GameMap map;
    private ArrayList<Robot> player_robots;

    public HostGameSetupMenu() {
        try {
            map = new GameMap(180, 0, 300, 200, "map2.tmx");

            Gdx.input.setInputProcessor(map);
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
        map.render();

        for (Vector2Di player_pos : map.getTileClicks()) {
            Robot r = new Robot(player_pos.getX(), player_pos.getY());
            map.addDrawJob(r);
        }

        Renderable.updateAll();
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
}
