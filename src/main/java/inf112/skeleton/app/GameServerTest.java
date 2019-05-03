package inf112.skeleton.app;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.io.IOException;

/**
 * Starts a game server, then fires up a game that connects to it.
 *
 * NOTE: This is only a test, it provides no method for setting game
 * options and has a static init_key
 */
public class GameServerTest {

    static Music music_player;

    public static void main(String args[]) throws IOException, CSV.CSVError, NoSuchResource {
        GameServer serv = new GameServer(1, StaticConfig.DEFAULT_GAME_OPTIONS, "abc123");
        serv.start();
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Robo Rally";
        cfg.width = 1600;
        cfg.height = 900;
        cfg.resizable = false;
        cfg.fullscreen = false;

        try {
            music_player = Resources.getMusic("iRobot.ogg");
        } catch (NoSuchResource e) {
            System.out.println("Couldn't find music in RoboRally class");
        }
        music_player.setVolume(0.3f);
        music_player.setLooping(true);
        music_player.play();
        new LwjglApplication(new GameLoop("localhost", "abc123", music_player), cfg);
    }
}
