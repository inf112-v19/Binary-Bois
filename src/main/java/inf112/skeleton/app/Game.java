package inf112.skeleton.app;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private final int HEIGHT = 12;
    private final int WIDTH = 12;
    private IBoard board;
    private LwjglApplicationConfiguration cfg;
    private HashMap<Player, Robot> playersAndRobots;

    public Game() {
        playersAndRobots = new HashMap<>();
        board = new Board(HEIGHT, WIDTH);
        setup();
    }

    private void setup() {
        Player player1 = new Player("Player1");
        Robot robot1 = new Robot("Robot1", new Vector2D(4,4));
        playersAndRobots.put(player1, robot1);
        horribleBoardSetup();

        cfg = new LwjglApplicationConfiguration();
        cfg.title = "Robo Rally";
        cfg.width = 32*WIDTH;
        cfg.height = 32*HEIGHT;
        new LwjglApplication(new TiledTest(robot1), cfg);
    }

    private void horribleBoardSetup() {
        for (int i = 0; i < WIDTH; i++) {
            board.set(new Wall(), i, 0);
        }
        for (int i = 0; i < WIDTH; i++) {
            board.set(new Wall(), i, HEIGHT-1);
        }
        for (int i = 0; i < HEIGHT; i++) {
            board.set(new Wall(), 0, i);
        }
        for (int i = 0; i < HEIGHT; i++) {
            board.set(new Wall(), WIDTH-1, i);
        }
        board.set(new Wall(), 3, 2);
        board.set(new Wall(), 3, 3);
        board.set(new Wall(), 9, 3);
        board.set(new Wall(), 10, 3);
        board.set(new Wall(), 2, 4);
        board.set(new Wall(), 3, 4);
        board.set(new Wall(), 4, 4);
        board.set(new Wall(), 5, 4);
        board.set(new Wall(), 5, 5);
        board.set(new Wall(), 8, 5);
        board.set(new Wall(), 2, 6);
        board.set(new Wall(), 8, 6);
        board.set(new Wall(), 2, 7);
        board.set(new Wall(), 2, 9);
        board.set(new Wall(), 3, 9);
        board.set(new Wall(), 8, 9);
        board.set(new Wall(), 9, 9);
        board.set(new Wall(), 2, 10);
        board.set(new Flag(), 2, 2);
        board.set(new Flag(), 8, 2);
        board.set(new Flag(), 6, 7);
        board.set(new Flag(), 3, 10);
    }

}
