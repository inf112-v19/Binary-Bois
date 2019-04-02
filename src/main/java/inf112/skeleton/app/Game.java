package inf112.skeleton.app;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Game {
    public static final String cards_src = "resources/Programcards.csv";
    public static final int NUM_CARDS_PER_PLAYER = 9;

    private int height = 0;
    private int width = 0;
    private IBoard board;
    private HashMap<Robot, Player> robotsToPlayers;
    private ArrayList<Robot> robots;
    private ArrayList<Player> players;
    private ArrayList<IItem> flags;
    private GameLog game_log;
    private CardDeck deck;
    private int active_player_num = 0;
    public static ArrayList<String> soundFx = new ArrayList<>();
    private Random rnd = new Random();
    private int numberOfFlags;
    private static boolean winCondition = false;


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
        } catch (NoSuchResource e) {
            throw new InitError("Unable to load resource: " + e.getMessage());
        }
        setup();

        if (NUM_CARDS_PER_PLAYER * players.size() > deck.size())
            throw new InitError("Not enough cards for " + players.size() + " players, have " + deck.size() + " cards");
    }

    //Made for testing
    public Game(int height, int width, ArrayList<Robot> robots, String empty_string) {
        this.height = height;
        this.width = width;
        this.robots = robots;
        this.players = new ArrayList<>();
        robotsToPlayers = new HashMap<>();
        board = new Board(height, width);
        game_log = new GameLog(5);

        int player_num = 0;
        for (Robot r : robots) {
            Player p = new Player("Player-" + player_num++);
            robotsToPlayers.put(r, p);
            players.add(p);
        }
        for (Robot robot : robots) {
            board.set(robot, robot.getPos());
        }
    }

    public Player getActivePlayer() {
        return players.get(active_player_num);
    }

    public int getActivePlayerNum() {
        return active_player_num;
    }

    public void setActivePlayerNum(int num) {
        active_player_num = num;
    }

    public void nextPlayer() {
        active_player_num = (active_player_num+1) % players.size();
    }

    public void handOutCards() throws CardDeck.NoMoreCards {
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            int robo_health = robots.get(i).getHealth();
            p.giveDeck(deck.get(robo_health-1));
        }
    }

    public ArrayList<String> checkPlaySound() {
        ArrayList<String> tmp = new ArrayList<>(soundFx);
        soundFx = new ArrayList<>();
        return tmp;
    }

    public Vector2Di shootLaser(Vector2Di pos, Vector2Di dir) {
        soundFx.add("Laser");
        Vector2Di current_pos = pos.copy();
        ArrayList<IItem> item_list = board.get(current_pos);
        for (IItem item : item_list) {
            if (item instanceof Wall && ((Wall) item).hasEdge(dir))
                return current_pos;
            if (item instanceof LaserShooter)
                return current_pos;
        }

        current_pos.add(dir);
        for (; board.isOnBoard(current_pos); current_pos.add(dir)) {
            item_list = board.get(current_pos);
            for (IItem item : item_list) {
                Vector2Di dir_opposite = dir.copy();
                dir_opposite.mul(-1);
                if (item instanceof Wall && ((Wall) item).hasEdge(dir_opposite))
                    return current_pos;

                if (item instanceof Robot) {
                    soundFx.add("Oof");
                    ((Robot) item).handleDamage(DamageType.LASER, board);
                    return current_pos;
                }
            }
        }
        return current_pos;
    }


    public Robot getRobot(int playerNumber) throws IndexOutOfBoundsException {
        return robots.get(playerNumber);
    }

    public Robot getRobot(Player player) {
        return robots.get(players.indexOf(player));
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

    public void registerFlagUpdateBackup(Vector2Di pos, Vector2Di dir, Robot robot) {
        Vector2Di newpos = pos.copy();
        newpos.move(dir, 1);
        ArrayList<IItem> itemlist = board.get(newpos);
        if (itemlist.isEmpty())
            return;
        for (IItem item : itemlist) {
            if (item instanceof Flag) {

                soundFx.add("Flag");
                robot.setArchiveMarker(newpos);
                Player robotOwner = robotsToPlayers.get(robot);

                //Keeping tack of which flags the player has visited so far
                robotOwner.registerFlag((Flag)item);

                //ArrayList of all Flag IItems that are on the map
                ArrayList<IItem> flagArrayList = new ArrayList<>();
                for(int i = 0; i < board.getAllItemsOnBoard().size(); i++){
                    if(board.getAllItemsOnBoard().get(i) instanceof Flag){
                        flagArrayList.add(board.getAllItemsOnBoard().get(i));
                    }
                }

                //Win condition
                if(robotOwner.getFlags().containsAll(flagArrayList)){
                    System.out.println(robotOwner.getName() + " registered all flags. Winner!");
                    winCondition = true;
                }


            } else if (item instanceof Wrench) {
                robot.setArchiveMarker(newpos);
                soundFx.add("Wrench");
                return;
            }
        }
    }

    public void killRobot(Robot robot) {
        robot.handleDamage(DamageType.FALL, board);
    }

    public static boolean getWinCondition(){
        return winCondition;
    }
    
    public void appendToLogBuilder(String string){
        game_log.append("", string);
    }

    public String getPrintLog(){
        return game_log.toString();
    }

    public void moveOnBoard(Robot robot, Vector2Di newpos, Vector2Di dir) {
        soundFx.add("Move" + (rnd.nextInt(3) + 1));
        // ^ It's a bit ugly. TODO: Suggestions?
        Vector2Di pos = robot.getPos();
        board.get(pos).remove(robot);
        board.set(robot, newpos);
        registerFlagUpdateBackup(pos, dir, robot);
    }

    public static void addSoundFX (String soundName){
        soundFx.add(soundName);
    }

    public void setOnBoard(IItem item, int x, int y) {
        board.set(item, x, y);
    }

    public void isOnHole(Robot robot) {
        Vector2Di currentPos = robot.getPos();
        ArrayList<IItem> itemsOnPos = board.get(currentPos);
        for (IItem item : itemsOnPos) {
            if (item instanceof Hole) {
                soundFx.add("Death");
                robot.handleDamage(DamageType.FALL, board);
                return;
            }
        }
    }

    public boolean canMoveTo(Vector2Di pos, Vector2Di dir, Robot my_robot){
        Vector2Di orig_pos = pos.copy();
        Vector2Di newpos = pos.copy();
        newpos.move(dir, 1);

        for (IItem item : board.get(orig_pos))
            if (item instanceof Wall && ((Wall) item).hasEdge(dir)) {
                appendToLogBuilder("Blocked by wall");
                soundFx.add("Oof");
                return false;
            }

        ArrayList<IItem> itemlist = board.get(newpos);
        if (itemlist.isEmpty()) {
            moveOnBoard(my_robot, newpos, dir);
            return true;
        }

        Vector2Di dir_opposite = dir.copy();
        dir_opposite.mul(-1);
        for (IItem itemInFront : itemlist)
            if (itemInFront instanceof Wall && ((Wall) itemInFront).hasEdge(dir_opposite)) {
                appendToLogBuilder("Blocked by wall");
                soundFx.add("Oof");
                return false;
            }
        ArrayList<IItem> tmp_list = new ArrayList<>(itemlist);
        for (IItem itemInFront : tmp_list) {
            if (itemInFront instanceof Robot) {
                Vector2Di otherBotPos = ((Robot) itemInFront).getPos();
                if (canMoveTo(otherBotPos, dir, (Robot) itemInFront)) {
                    appendToLogBuilder("Pushed other robot");
                    ((Robot) itemInFront).move(dir, 1);
                    isOnHole((Robot) itemInFront);
                } else {
                    appendToLogBuilder("Unable to push other robot!");
                    return false;
                }
            }
        }
        moveOnBoard(my_robot, newpos, dir);
        return true;
    }

    private void boardSetup() {

        TiledMap tiledMap = Map.getTiledMap();
        for(int k = 0; k < tiledMap.getLayers().size(); k++){
            TiledMapTileLayer layer = (TiledMapTileLayer)tiledMap.getLayers().get(k);
            for(int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(i, j);
                    if (cell == null)
                        continue;
                    if (cell.getTile().getProperties().get("MapObject", String.class).equals("wall")) {
                        ArrayList<Vector2Di> edgesArrayList = new ArrayList<>();
                        if(cell.getTile().getProperties().get("N", Boolean.class)){
                            edgesArrayList.add(new Vector2Di(0,1));
                        }
                        if(cell.getTile().getProperties().get("E", Boolean.class)){
                            edgesArrayList.add(new Vector2Di(1,0));
                        }
                        if(cell.getTile().getProperties().get("S", Boolean.class)){
                            edgesArrayList.add(new Vector2Di(0,-1));
                        }

                        if(cell.getTile().getProperties().get("W", Boolean.class)){
                            edgesArrayList.add(new Vector2Di(-1,0));
                        }
                        Vector2Di[] edgesArray = new Vector2Di[edgesArrayList.size()];
                        for(int l = 0; l < edgesArrayList.size(); l++){
                            edgesArray[l] = edgesArrayList.get(l);
                        }
                        board.set(new Wall(edgesArray), i, j);

                    }
                    if (cell.getTile().getProperties().get("MapObject", String.class).equals("flag")) {
                        numberOfFlags += 1;
                        Flag flag = new Flag(numberOfFlags, new Vector2Di(i, j));
                        board.set(flag, i, j);

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
