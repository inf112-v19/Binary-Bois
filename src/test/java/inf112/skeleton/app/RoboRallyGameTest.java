package inf112.skeleton.app;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class RoboRallyGameTest {

    private RoboRallyGame game;

    @Test
    public void robotDoesNotGoThroughWall() throws NoSuchResource{
        ArrayList<Robot> robots = new ArrayList<>();
        Robot robot = new Robot(0,0);
        robots.add(robot);
        game = new RoboRallyGame(5, 5, robots, "TEST GAME");
        Wall fullWall = new Wall(new Vector2Di(1, 0),
                new Vector2Di(-1, 0),
                new Vector2Di(0, 1),
                new Vector2Di(0, -1));
        game.setOnBoard(fullWall, 1, 0);

        Vector2Di dir = robot.getDir();
        assertFalse(game.canMoveTo(robot.getPos(), dir, robot));
    }
}
