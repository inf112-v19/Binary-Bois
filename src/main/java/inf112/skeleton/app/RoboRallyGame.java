package inf112.skeleton.app;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RoboRallyGame {
    public static final int NUM_CARDS_PER_PLAYER = 9;

    private int height = 0;
    private int width = 0;
    private IBoard board;
    private HashMap<Robot, Player> robotsToPlayers;
    private ArrayList<Robot> robots;
    private ArrayList<Player> players;
    private ArrayList<IItem> flags;
    private Log game_log;
    private CardDeck deck;
    private int active_player_num = 0;
    private Vector2Di northVector = new Vector2Di(0,1);
    private Vector2Di eastVector = new Vector2Di(1,0);
    private Vector2Di southVector = new Vector2Di(0,-1);
    private Vector2Di westVector = new Vector2Di(-1,0);
    public static ArrayList<String> soundFx = new ArrayList<>();
    private Random rnd = new Random();
    private int numberOfFlags;
    private static boolean winCondition = false;


    public class InitError extends Exception {
        public InitError(String msg) {
            super(msg);
        }
    }

    public RoboRallyGame(JSONObject settings) throws InitError, JSONException {
        ArrayList<Robot> robos = new ArrayList<>();
        JSONSpecs.game_options.check(settings);
        int[][] robots_pos = JSONTools.toIntMatrix(settings.getJSONArray("robots_pos"));
        for (int[] pos : robots_pos)
            robos.add(new Robot(pos[0], pos[1]));
        //Vector2Di dim;
        //try {
        //    TiledMap tmap = Resources.getTiledMap((String) settings.get("map"));
        //    dim = new Vector2Di(
        //            tmap.getProperties().get("width", Integer.class),
        //            tmap.getProperties().get("height", Integer.class)
        //    );
        //} catch (NoSuchResource e) {
        //    throw new InitError(e.toString());
        //}
        Vector2Di dim = new Vector2Di(16, 16);
        init(dim.getX(), dim.getY(), robos);
    }

    public RoboRallyGame(int width, int height, ArrayList<Robot> robots) throws InitError {
        init(width, height, robots);
    }

    private void init(int width, int height, ArrayList<Robot> robots) throws InitError {
        this.width = width;
        this.height = height;
        this.robots = robots;
        this.players = new ArrayList<>();
        robotsToPlayers = new HashMap<>();
        board = new Board(height, width);
        game_log = new Log(5);
        try {
            deck = new CardDeck(StaticConfig.GAME_CARDS_SRC);
            setup();
        } catch (CSV.CSVError e) {
            throw new InitError("Card source CSV file was incorrectly formed: " + StaticConfig.GAME_CARDS_SRC);
        } catch (IOException e) {
            throw new InitError("Unable to read card source CSV file: " + StaticConfig.GAME_CARDS_SRC);
        } catch (NoSuchResource e) {
            throw new InitError("Unable to load resource: " + e.getMessage());
        }

        if (NUM_CARDS_PER_PLAYER * players.size() > deck.size())
            throw new InitError("Not enough cards for " + players.size() + " players, have " + deck.size() + " cards");
    }

    public void initTextures() throws NoSuchResource {
        deck.initTextures();
    }

    //Made for testing
    public RoboRallyGame(int height, int width, ArrayList<Robot> robots, String empty_string) throws NoSuchResource {
        this.height = height;
        this.width = width;
        this.robots = robots;
        this.players = new ArrayList<>();
        robotsToPlayers = new HashMap<>();
        board = new Board(height, width);
        game_log = new Log(5);

        int player_num = 0;
        for (Robot r : robots) {
            Player p = new Player("Player-" + player_num++);
            robotsToPlayers.put(r, p);
            players.add(p);
        }
        for (Robot robot : robots)
            board.set(robot, robot.getPos());
    }

    public void emptyHand(Robot robot) {
        getActivePlayer().getCardManager().removeAllCards(robot);
        getActivePlayer().getHand().clear();
    }

    public ArrayList<Vector2Di> fromTo(Vector2Di from, Vector2Di to) {
        ArrayList<Vector2Di> path = Dijkstra.fromTo(board, from, to);
        path.add(to);  //This isn't added in the Dijkstra
        return path;
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

    /**
     * Retrieve a number of cards from the deck, Wraps {@link CardDeck#get(int)}
     *
     * @param num_cards
     * @return The cards.
     * @throws CardDeck.NoMoreCards
     */
    public ArrayList<Card> getCards(int num_cards) throws CardDeck.NoMoreCards {
        return deck.get(num_cards);
    }

    /**
     * This is a testing method for automatically setting the active cards of players.
     */
    public void forceActiveCards() {
        getActivePlayer().getCardManager().setAllActiveCards();
    }

    public Player getPlayer(int num) {
        return players.get(num);
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

    private void setup() throws NoSuchResource {
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

                //Keeping track of which flags the player has visited so far
                robotOwner.registerFlag((Flag)item);

                //ArrayList of all Flag IItems that are on the map
                ArrayList<IItem> flagArrayList = new ArrayList<>();
                for(int i = 0; i < board.getAllItemsOnBoard().size(); i++)
                    if(board.getAllItemsOnBoard().get(i) instanceof Flag)
                        flagArrayList.add(board.getAllItemsOnBoard().get(i));

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

    public void setOnBoard(IItem item, Vector2Di vec) {
        board.set(item, vec);
    }

    public void handlePlunge(Robot robot) {
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

    public void handleLaserTile(Robot robot) {
        Vector2Di currentPos = robot.getPos();
        ArrayList<IItem> itemsOnPos = board.get(currentPos);
        for (IItem item : itemsOnPos) {
            if (item instanceof Laser){
                soundFx.add("Laser");
                robot.handleDamage(DamageType.LASER, board);
                return;
            }
        }
    }

    public boolean handleConveyorTile(Robot robot) {
        Vector2Di currentPos = robot.getPos();
        ArrayList<IItem> itemsOnPos = board.get(currentPos);
        for (IItem item : itemsOnPos) {
            if (item instanceof ConveyorBelt){
                if (canMoveTo(robot.getPos(), ((ConveyorBelt) item).getDir(), robot)) {
                    if(((ConveyorBelt) item).is_express()){
                        robot.moveFast(((ConveyorBelt) item).getDir(), 1);
                        return true;
                    } else {
                        robot.move(((ConveyorBelt) item).getDir(), 1);
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public boolean handleGyroTile(Robot robot){
        Vector2Di currentPos = robot.getPos();
        ArrayList<IItem> itemsOnPos = board.get(currentPos);
        for (IItem item : itemsOnPos) {
            if (item instanceof Gyro){
                robot.rot(((Gyro) item).getRotation());
                return true;
            }
        }
        return false;
    }

    public boolean canMoveTo(Vector2Di pos, Vector2Di dir, Robot my_robot){
        Vector2Di orig_pos = pos.copy();
        Vector2Di newpos = pos.copy();
        newpos.move(dir, 1);

        assert board.isOnBoard(orig_pos);

        for (IItem item : board.get(orig_pos))
            if (item instanceof Wall && ((Wall) item).hasEdge(dir)) {
                appendToLogBuilder("Blocked by wall");
                Vector2Df blockedByWallAnimationVector = dir.tof();
                blockedByWallAnimationVector.mul(0.3f);
                my_robot.addAnimation(new Animation(blockedByWallAnimationVector, 0, 0, 0.1f));
                blockedByWallAnimationVector.mul(-1);
                my_robot.addAnimation(new Animation(blockedByWallAnimationVector, 0, 0, 0.1f));
                soundFx.add("Oof");
                return false;
            }

        if (!board.isOnBoard(newpos))
            return false;

        ArrayList<IItem> itemlist = board.get(newpos);
        if (itemlist.isEmpty()) {
            moveOnBoard(my_robot, newpos, dir);
            return true;
        }

        Vector2Di dir_opposite = dir.copy();
        dir_opposite.mul(-1);
        for (IItem itemInFront : itemlist)
            if (itemInFront instanceof Wall && ((Wall) itemInFront).hasEdge(dir_opposite)) {
                Vector2Df blockedByWallAnimationVector = dir.tof();
                blockedByWallAnimationVector.mul(0.3f);
                my_robot.addAnimation(new Animation(blockedByWallAnimationVector, 0, 0, 0.1f));
                blockedByWallAnimationVector.mul(-1);
                my_robot.addAnimation(new Animation(blockedByWallAnimationVector, 0, 0, 0.1f));
                appendToLogBuilder("Blocked by wall");
                soundFx.add("Oof");
                return false;
            }
        ArrayList<IItem> tmp_list = new ArrayList<>(itemlist);
        for (IItem itemInFront : tmp_list) {
            if (itemInFront instanceof Robot) {
                assert itemInFront != my_robot;
                Vector2Di otherBotPos = ((Robot) itemInFront).getPos();
                if (canMoveTo(otherBotPos, dir, (Robot) itemInFront)) {
                    appendToLogBuilder("Pushed other robot");
                    ((Robot) itemInFront).move(dir, 1);
                    handlePlunge((Robot) itemInFront);
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

        TiledMap tiledMap = GameMap.getTiledMap();
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
                            edgesArrayList.add(northVector);
                        }
                        if(cell.getTile().getProperties().get("E", Boolean.class)){
                            edgesArrayList.add(eastVector);
                        }
                        if(cell.getTile().getProperties().get("S", Boolean.class)){
                            edgesArrayList.add(southVector);
                        }

                        if(cell.getTile().getProperties().get("W", Boolean.class)){
                            edgesArrayList.add(westVector);
                        }
                        Vector2Di[] edgesArray = new Vector2Di[edgesArrayList.size()];
                        for(int l = 0; l < edgesArrayList.size(); l++){
                            edgesArray[l] = edgesArrayList.get(l);
                        }
                        board.set(new Wall(edgesArray), i, j);
                    }
                    if (cell.getTile().getProperties().get("MapObject", String.class).equals("conveyor_belt")) {
                        boolean is_express;
                        if(cell.getTile().getProperties().get("is_express", Boolean.class)){
                            is_express = true;
                        } else {
                            is_express = false;
                        }
                        if(cell.getTile().getProperties().get("N", Boolean.class)){
                            board.set(new ConveyorBelt(northVector, is_express), i, j);
                        } else if(cell.getTile().getProperties().get("E", Boolean.class)){
                            board.set(new ConveyorBelt(eastVector, is_express), i, j);
                        } else if(cell.getTile().getProperties().get("S", Boolean.class)){
                            board.set(new ConveyorBelt(southVector, is_express), i, j);
                        } else if(cell.getTile().getProperties().get("W", Boolean.class)){
                            board.set(new ConveyorBelt(westVector, is_express), i, j);
                        }

                    }
                    if(cell.getTile().getProperties().get("MapObject", String.class).equals("gyro")){
                        Gyro gyro = new Gyro(cell.getTile().getProperties().get("rotation", Integer.class));
                        board.set(gyro,i ,j);
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
                    if (cell.getTile().getProperties().get("MapObject", String.class).equals("laser")) {
                        board.set(new Laser(new Vector2Di(i, j)), i, j);
                    }

                }
            }
        }
        for (Robot robot : robots) {
            board.set(robot, robot.getPos());
        }
    }

}
