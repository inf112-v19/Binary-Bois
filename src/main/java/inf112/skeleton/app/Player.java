package inf112.skeleton.app;

import java.util.ArrayList;

/**
 * The players.
 * TODO: Either the player knows of their robot, vice versa or they they don't know of each other
 */
public class Player {

    private String name;
    private int robotLives = 3;
    private int memoryHealth = 10;
    private ArrayList<ICard> hand = new ArrayList<>();
    private ArrayList<Integer> flags;

    public Player(String name) {
        if (name.length() < 1)
                throw new IllegalArgumentException("Names of players should be at least one");
        this.name = name;
        this.flags = new ArrayList<>();
    }

    public void register(Integer flag) {
        if (!flags.contains(flag)) {
            flags.add(flag);
            System.out.println("Flag " + flag + " registered");
        }
    }

    public ArrayList<Integer> getFlags() {
        return flags;
    }

    public String getName() {
        return name;
    }

    public void giveDeck(ArrayList<ICard> newHand) {
        hand.clear();
        hand.addAll(newHand);
    }

    public ArrayList<ICard> getHand() {
        return hand;
    }

    public int getMemoryHealth() {
        return memoryHealth;
    }

    public int getRobotLives() {
        return robotLives;
    }
}