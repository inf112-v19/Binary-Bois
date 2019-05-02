package inf112.skeleton.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg;
        cfg = new LwjglApplicationConfiguration();
        cfg.title = "Robo Rally";
        cfg.width = 1600;
        cfg.height = 900;

        new LwjglApplication(new RoboRally(), cfg);

    }


}