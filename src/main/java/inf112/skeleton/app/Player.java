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
    private ArrayList<ICard> deck = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void giveDeck(ArrayList<ICard> newDeck) {
        deck.addAll(newDeck);
    }

    public ArrayList<ICard> getDeck() {
        return deck;
    }

    public int getMemoryHealth() {
        return memoryHealth;
    }

    public int getRobotLives() {
        return robotLives;
    }
}
