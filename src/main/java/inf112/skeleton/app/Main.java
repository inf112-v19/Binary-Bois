package inf112.skeleton.app;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {



    public static void main(String[] args) {
        // FIXME: This actually has to be retrieved *after* the map is loaded,
        //        but works fine for now as we only use 12x12 maps with 32x32
        //        pixel tiles.
        final int MAP_HEIGHT = 12;
        final int MAP_WIDTH = 12;
        final int TILE_PX_DIM = 32;

        // Widths/heights of the other parts of the game UI.
        final int BOTTOM_UI_PX_HEIGHT = 320;
        final int LEFT_UI_PX_WIDTH = 320;
        final int RIGHT_UI_PX_WIDTH = 320;
        final int MAP_PX_WIDTH = TILE_PX_DIM * MAP_WIDTH;
        final int MAP_PX_HEIGHT = TILE_PX_DIM * MAP_HEIGHT;

        LwjglApplicationConfiguration cfg;
        cfg = new LwjglApplicationConfiguration();
        cfg.title = "Robo Rally";
        cfg.width = MAP_PX_WIDTH;
        cfg.height = MAP_PX_HEIGHT;
        //cfg.resizable = false;
        new LwjglApplication(new GameLoop(), cfg);
    }
}