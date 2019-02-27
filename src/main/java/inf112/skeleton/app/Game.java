package inf112.skeleton.app;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private final int height;
    private final int width;
    private IBoard board;
    private HashMap<Robot, Player> robotsToPlayers;
    private ArrayList<Robot> robots;
    private Player player1;
    private Player player2;
    

    public Game(int height, int width, ArrayList<Robot> robots) {
        this.height = height;
        this.width = width;
        this.robots = robots;
        robotsToPlayers = new HashMap<>();
        board = new Board(height, width);
        setup();
    }

    private void setup() {
        player1 = new Player("Player1");
        robotsToPlayers.put(robots.get(0), player1);
        player2 = new Player("Player2");
        robotsToPlayers.put(robots.get(1), player2);
        horribleBoardSetup();
    }

    public void registerFlag(Vector2D pos, Vector2D dir, Robot robot) {
        Vector2D newpos = pos.copy();
        newpos.move(dir, 1);
        ArrayList<IItem> itemlist = board.get(newpos);
        if (itemlist.isEmpty())
            return;
        IItem itemInFront = itemlist.get(0);
        if (itemInFront instanceof Flag) {
            robot.setLastFlag((Flag) itemInFront);
            Player robotOwner = robotsToPlayers.get(robot);
            robotOwner.register(((Flag) itemInFront).getNumber());
        }
    }

    public void printFlags() {
        System.out.println("Flags: ");
        for (Integer flag : player1.getFlags())
            System.out.print(flag + " ");
        System.out.println();
    }

    public void moveOnBoard(Robot robot, Vector2D newpos, Vector2D dir) {
        Vector2D pos = robot.getPos();
        board.get(pos).remove(robot);
        board.set(robot, newpos);
        registerFlag(pos, dir, robot);
    }

    public boolean canMoveTo(Vector2D pos, Vector2D dir, Robot my_robot){
        Vector2D newpos = new Vector2D(pos.getX(), pos.getY());
        newpos.move(dir, 1);
        ArrayList<IItem> itemlist = board.get(newpos);
        if (itemlist.isEmpty()) {
            moveOnBoard(my_robot, newpos, dir);
            return true;
        }
        int listLength = itemlist.size();
        IItem itemInFront = itemlist.get(listLength-1);
        System.out.println("iteminfront was: " + itemInFront.getName());
        if (itemInFront instanceof Robot) {
            Vector2D otherBotPos = ((Robot) itemInFront).getPos();
            if (canMoveTo(otherBotPos, dir, (Robot) itemInFront)) {
                System.out.println("Pushed other robot");
                otherBotPos.move(dir, 1);
                moveOnBoard(my_robot, newpos, dir);
                return true;
            } else {
                System.out.println("Unable to push other robot!");
                return false;
            }
        }
        if (!(itemInFront instanceof Wall)) {
            moveOnBoard(my_robot, newpos, dir);
            return true;
        }
        return false;
    }

    private void horribleBoardSetup() {
        /* Hardcoded board */
        for (int i = 0; i < width; i++) {
            board.set(new Wall(), i, 0);
        }
        for (int i = 0; i < width; i++) {
            board.set(new Wall(), i, height-1);
        }
        for (int i = 0; i < height; i++) {
            board.set(new Wall(), 0, i);
        }
        for (int i = 0; i < height; i++) {
            board.set(new Wall(), width-1, i);
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
        board.set(new Flag(1), 2, 2);
        board.set(new Flag(2), 8, 2);
        board.set(new Flag(3), 6, 7);
        board.set(new Flag(4), 3, 10);

        for (Robot robot : robots) {
            board.set(robot, robot.getPos());
        }
    }

}
