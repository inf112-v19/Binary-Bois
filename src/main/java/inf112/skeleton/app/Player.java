package inf112.skeleton.app;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The players.
 * TODO: Either the player knows of their robot, vice versa or they they don't know of each other
 */
public class Player {

    static String name;
    private int robotLives = 3;
    private int memoryHealth = 10;
    private ArrayList<Card> hand = new ArrayList<>();
    private ArrayList<Flag> flags;

    public Player(String name) {
        if (name.length() < 1)
            throw new IllegalArgumentException("Names of players should be at least one");
        this.name = name;
        this.flags = new ArrayList<>();
    }

    public void registerFlag(Flag flag) {
        if (!flags.contains(flag)) {
            flags.add(flag);
            System.out.println("Flag " + flag.getNumber() + " registered by " + name);
        }
    }

    public ArrayList<Flag> getFlags() {
        return flags;
    }

    public String getName() {
        return name;
    }

    public void giveDeck(ArrayList<Card> newHand) {
        hand.clear();
        hand.addAll(newHand);
        System.out.println(name + " Cards: " + Arrays.toString(newHand.toArray()));
    }

    public Card popCard() {
        if (hand.size() == 0)
            return null;
        Card c = hand.get(0);
        hand.remove(0);
        return c;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public int getMemoryHealth() {
        return memoryHealth;
    }

    public int getRobotLives() {
        return robotLives;
    }
}