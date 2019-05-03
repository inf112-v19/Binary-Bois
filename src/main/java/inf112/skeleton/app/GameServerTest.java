package inf112.skeleton.app;

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
    public static void main(String args[]) throws IOException, CSV.CSVError, NoSuchResource {
        final String init_key = "abc123";
        //GameServer server = new GameServer(1, StaticConfig.DEFAULT_GAME_OPTIONS, init_key);
        //server.start();
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Robo Rally";
        cfg.width = 1600;
        cfg.height = 900;
        cfg.resizable = false;
        cfg.fullscreen = false;
        new LwjglApplication(new RoboRally(), cfg);
    }
}
