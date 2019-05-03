package inf112.skeleton.app;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The players.
 */
public class Player {

    static String name;
    private ArrayList<Card> hand = new ArrayList<>();
    private ArrayList<Flag> flags;
    private CardManager card_man;

    public Player(String name) throws NoSuchResource {
        if (name.length() < 1)
            throw new IllegalArgumentException("Names of players should be at least one");
        this.name = name;
        this.flags = new ArrayList<>();
        this.card_man = new CardManager();

    }

    public CardManager getCardManager() {
        return card_man;
    }

    public void registerFlag(Flag flag) {
        if (flags.isEmpty() && flag.getNumber() == 1) {
            flags.add(flag);
            return;
        }

        //Only add flags with one index higher
        if (!flags.contains(flag) && flags.get(flags.size()-1).getNumber() == flag.getNumber()-1)
            flags.add(flag);
    }

    public ArrayList<Flag> getFlags() {
        return flags;
    }

    public String getName() {
        return name;
    }

    public void giveDeck(ArrayList<Card> newHand, Robot my_robot) {
        hand.clear();
        for (int i = 0; i < my_robot.getHealth()-1; i++)
            hand.add(newHand.get(i));

        System.out.println(name + " Cards: " + Arrays.toString(newHand.toArray()));
        card_man.setCards(hand);
    }

    public ArrayList<Card> getHand() {
        return hand;
    }
}