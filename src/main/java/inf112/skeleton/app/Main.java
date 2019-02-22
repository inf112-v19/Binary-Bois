package inf112.skeleton.app;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {



    public static void main(String[] args) {
        final int HEIGHT = 12;
        final int WIDTH = 12;

        LwjglApplicationConfiguration cfg;
        cfg = new LwjglApplicationConfiguration();
        cfg.title = "Robo Rally";
        cfg.width = 32*WIDTH;
        cfg.height = 32*HEIGHT;
        new LwjglApplication(new TiledTest(), cfg);
    }
}