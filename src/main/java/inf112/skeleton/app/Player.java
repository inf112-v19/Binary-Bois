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
        card_man.setCards(hand);
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
}