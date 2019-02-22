package inf112.skeleton.app;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    private final int height;
    private final int width;
    private IBoard board;
    private HashMap<Player, RobotTest> playersAndRobots;
    private RobotTest robot;
    private Player player1;
    

    public Game(int height, int width, RobotTest robot) {
        this.height = height;
        this.width = width;
        this.robot = robot;
        playersAndRobots = new HashMap<>();
        board = new Board(height, width);
        System.out.println("height+width" + height + width);
        setup();
    }

    private void setup() {
        player1 = new Player("Player1");
        playersAndRobots.put(player1, robot);
        horribleBoardSetup();
    }

    public void registerFlag(Vector2D pos, Vector2D dir) {
        Vector2D newpos = new Vector2D(pos.getX(), pos.getY());
        newpos.move(dir, 1);
        ArrayList<IItem> itemlist = board.get(newpos.getX(), newpos.getY());
        if (itemlist.isEmpty())
            return;
        IItem itemInFront = itemlist.get(0);
        if (itemInFront instanceof Flag) {
            player1.register(((Flag) itemInFront).getNumber());
        }
    }

    public void printFlags() {
        System.out.println("Flags: ");
        for (Integer flag : player1.getFlags())
            System.out.print(flag + " ");
        System.out.println();
    }

    public boolean canMoveTo(Vector2D pos, Vector2D dir){
        Vector2D newpos = new Vector2D(pos.getX(), pos.getY());
        newpos.move(dir, 1);
        ArrayList<IItem> itemlist = board.get(newpos.getX(), newpos.getY());
        if (itemlist.isEmpty()) {
            System.out.println("It was empty soooo.. OK");
            return true;
        }
        IItem itemInFront = itemlist.get(0);
        return !(itemInFront instanceof Wall);
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
    }

}
