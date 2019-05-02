package inf112.skeleton.app;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg;
        cfg = new LwjglApplicationConfiguration();
        cfg.title = "Robo Rally";
        cfg.width = 1600;
        cfg.height = 900;

        final String hostname = "localhost";

        try {
            new LwjglApplication(new GameLoop(hostname, "abc123"), cfg);
        } catch (GameSocketException | IOException e) {
            SystemPanic.panic("Unable to connect to server: " + hostname);
        } catch (DecryptionException e) {
            SystemPanic.panic("Wrong key");
        }
    }
}