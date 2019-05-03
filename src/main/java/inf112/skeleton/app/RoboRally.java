package inf112.skeleton.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RoboRally extends Game {

    SpriteBatch batch;
    BitmapFont font;
    Music music_player;

    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        try {
            music_player = Resources.getMusic("iRobot.ogg");
        } catch (NoSuchResource e) {
            System.out.println("Couldn't find music in RoboRally class");
        }
        music_player.setVolume(0.3f);
        music_player.setLooping(true);
        music_player.play();

        this.setScreen(new MainMenu(this, music_player));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}