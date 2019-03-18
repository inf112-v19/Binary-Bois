package inf112.skeleton.app;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    public static final String cards_src = "resources/Programcards.csv";
    public static final int NUM_CARDS_PER_PLAYER = 9;

    private int height = 0;
    private int width = 0;
    private IBoard board;
    private HashMap<Robot, Player> robotsToPlayers;
    private ArrayList<Robot> robots;
    private ArrayList<Player> players;
    private GameLog game_log;
    private CardDeck deck;
    private int active_player_num = 0;

    public class InitError extends Exception {
        public InitError(String msg) {
            super(msg);
        }
    }

    public Game(int height, int width, ArrayList<Robot> robots) throws InitError {
        this.height = height;
        this.width = width;
        this.robots = robots;
        this.players = new ArrayList<>();
        robotsToPlayers = new HashMap<>();
        board = new Board(height, width);
        game_log = new GameLog(5);
        try {
            deck = new CardDeck(cards_src);
        } catch (CSV.CSVError e) {
            throw new InitError("Card source CSV file was incorrectly formed: " + cards_src);
        } catch (IOException e) {
            throw new InitError("Unable to read card source CSV file: " + cards_src);
        }
        setup();

        if (NUM_CARDS_PER_PLAYER * players.size() > deck.size())
            throw new InitError("Not enough cards for " + players.size() + " players, have " + deck.size() + " cards");
    }

    public Player getActivePlayer() {
        return players.get(active_player_num);
    }

    public void nextPlayer() {
        active_player_num = (active_player_num+1) % players.size();
    }

    public void handOutCards() throws CardDeck.NoMoreCards {
        for (Player p : players)
            p.giveDeck(deck.get(NUM_CARDS_PER_PLAYER));
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
        // FIXME: I'm not actually sure what should happen if there is no backup position
        //        (according to the game rules.)
        //        For now I'll assume they just die.
        if (backupPos != null) {
            board.set(robot, backupPos);
            robot.setPos(backupPos);
            robot.setArchiveMarker(backupPos);
        } else {
            robot.death();
        }
    }
    
    public void appendToLogBuilder(String string){
        game_log.append("", string);
    }

    public String getPrintLog(){
        return game_log.toString();
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
        Vector2D orig_pos = pos.copy();
        Vector2D newpos = pos.copy();
        newpos.move(dir, 1);

        for (IItem item : board.get(orig_pos))
            if (item instanceof Wall && ((Wall) item).hasEdge(dir)) {
                appendToLogBuilder("Blocked by wall");
                return false;
            }

        ArrayList<IItem> itemlist = board.get(newpos);
        if (itemlist.isEmpty()) {
            moveOnBoard(my_robot, newpos, dir);
            return true;
        }

        int listLength = itemlist.size();
        IItem itemInFront = itemlist.get(listLength-1);
        Vector2D dir_opposite = dir.copy();
        dir_opposite.mul(-1);
        if (itemInFront instanceof Wall && ((Wall) itemInFront).hasEdge(dir_opposite)) {
            appendToLogBuilder("Blocked by wall");
            return false;
        } else if (itemInFront instanceof Robot) {
            Vector2D otherBotPos = ((Robot) itemInFront).getPos();
            if (canMoveTo(otherBotPos, dir, (Robot) itemInFront)) {
                appendToLogBuilder("Pushed other robot");
                otherBotPos.move(dir, 1);
            } else {
                appendToLogBuilder("Unable to push other robot!");
                return false;
            }
        }
        moveOnBoard(my_robot, newpos, dir);
        return true;
    }

    private void boardSetup() {

        TiledMap tiledMap = Map.getTiledMap();
        int flagCounter = 1;
        for(int k = 0; k < tiledMap.getLayers().size(); k++){
            TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(k);
            for(int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(i, j);
                    if (cell == null)
                        continue;
                    if (cell.getTile().getProperties().get("MapObject", String.class).equals("wall")) {
                        board.set(Wall.getFullWall(), i, j);
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
                }
            }
        }
        for (Robot robot : robots) {
            board.set(robot, robot.getPos());
        }
    }

}
