package inf112.skeleton.app;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    //public static StringBuilder printLogMessage;
    private final int height;
    private final int width;
    private IBoard board;
    private HashMap<Robot, Player> robotsToPlayers;
    private ArrayList<Robot> robots;
    private ArrayList<Player> players;

    public Game(int height, int width, ArrayList<Robot> robots) {
        this.height = height;
        this.width = width;
        this.robots = robots;
        this.players = new ArrayList<>();
        robotsToPlayers = new HashMap<>();
        board = new Board(height, width);
        setup();
    }

    private void setup() {
        int player_num = 0;
        for (Robot r : robots) {
            Player p = new Player("Player-" + player_num++);
            robotsToPlayers.put(r, p);
            players.add(p);
        }
        boardSetup();
    }

    public void registerFlagUpdateBackup(Vector2D pos, Vector2D dir, Robot robot) {
        Vector2D newpos = pos.copy();
        newpos.move(dir, 1);
        ArrayList<IItem> itemlist = board.get(newpos);
        if (itemlist.isEmpty())
            return;
        for (IItem item : itemlist) {
            if (item instanceof Flag) {
                robot.setArchiveMarker(newpos);
                Player robotOwner = robotsToPlayers.get(robot);
                robotOwner.register(((Flag) item).getNumber());
                return;
            } else if (item instanceof Wrench) {
                robot.setArchiveMarker(newpos);
                return;
            }
        }
    }

    public void printFlags(int player_id) {
        System.out.println("Flags: ");
        for (Integer flag : players.get(player_id).getFlags())
            System.out.print(flag + " ");
        System.out.println();
    }

    public void printFlags() {
        printFlags(0);
    }

    public void jumpOnBoard(Robot robot) {
        Vector2D currentPos = robot.getPos();
        Vector2D backupPos = robot.getArchiveMarkerPos();
        board.get(currentPos).remove(robot);
        board.set(robot, backupPos);
        robot.setPos(backupPos);
        robot.setArchiveMarker(backupPos);
    }

    public void moveOnBoard(Robot robot, Vector2D newpos, Vector2D dir) {
        Vector2D pos = robot.getPos();
        board.get(pos).remove(robot);
        board.set(robot, newpos);
        registerFlagUpdateBackup(pos, dir, robot);
    }

    public void isOnHole(Robot robot) {
        Vector2D currentPos = robot.getPos();
        ArrayList<IItem> itemsOnPos = board.get(currentPos);
        for (IItem item : itemsOnPos) {
            if (item instanceof Hole) {
                robot.death();
                jumpOnBoard(robot);
                return;
            }
        }
    }

    public boolean canMoveTo(Vector2D pos, Vector2D dir, Robot my_robot){
        Vector2D newpos = new Vector2D(pos.getX(), pos.getY());
        System.out.println("Checking for " + pos + " along " + dir + " for " + my_robot);
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
                //printLogMessage.append("Pushed other robot");
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

    /*public static String printLog(){
        return printLogMessage.toString();
    }*/

    private void boardSetup() {

        TiledMap tiledMap = Map.getTiledMap();
        int flagCounter = 1;
        for(int k = 0; k < tiledMap.getLayers().size(); k++){
            TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(k);
            for(int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(i, j);
                    try{
                        if (cell.getTile().getProperties().get("MapObject", String.class).equals("wall")) {
                            board.set(new Wall(), i, j);
                        }
                        if (cell.getTile().getProperties().get("MapObject", String.class).equals("flag")) {
                            board.set(new Flag(flagCounter, new Vector2D(i, j)), i, j);
                            flagCounter += 1;
                        }
                        if (cell.getTile().getProperties().get("MapObject", String.class).equals("hole")) {
                            board.set(new Hole(), i, j);
                        }
                        if (cell.getTile().getProperties().get("MapObject", String.class).equals("wrench")) {
                            board.set(new Wrench(), i, j);
                        }
                    } catch (Exception e){

                    }
                }
            }
        }
        for (Robot robot : robots) {
            board.set(robot, robot.getPos());
        }
    }

}
