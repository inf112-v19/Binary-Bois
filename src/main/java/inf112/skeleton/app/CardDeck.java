package inf112.skeleton.app;

import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;

/**
 * A "deck" or stack of command cards.
 */
public class CardDeck {
    private ArrayList<Card> cards_orig = new ArrayList<>();
    private ArrayList<Card> cards = new ArrayList<>();

    public class NoMoreCards extends Exception {
        public NoMoreCards() {
            super();
        }
    }

    /**
     * Load a card deck from a CSV file.
     *
     * The CSV file must have the following header:
     *   Type,Amount,TypeID,Priority
     * They have the following types:
     *   Type: String
     *   Amount: int
     *   TypeID: int
     *   Priority: int
     *
     * After the cards are loaded, the list is shuffled.
     * If you want to keep the order of the cards from the
     * input file, use #{CardDeck.restore()} after instantiating
     * the deck.
     *
     * @param path Path to the csv file.
     * @throws IOException If the file is inaccessible.
     * @throws CSV.CSVError If the file is not valid CSV, or if a
     *                      column or column header is missing
     */
    public CardDeck(String path) throws IOException, CSV.CSVError {
        CSV csv = new CSV(path);
        for (CSV.Row row : csv) {
            String type = row.get("Type");
            ICommand cmd = Commands.getComand(type);
            cards.add(new Card(cmd,
                               type,
                               row.getInt("Amount"),
                               row.getInt("TypeID"),
                               row.getInt("Priority")));
        }
        cards_orig.addAll(cards);
        shuffle();
    }

    /**
     * Shuffle the deck.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * @return The card on top of the deck.
     */
    public Card get() throws NoMoreCards {
        if (cards.size() == 0)
            throw new NoMoreCards();
        Card card = cards.get(0);
        cards.remove(0);
        return card;
    }

    public ArrayList<Card> get(int n) throws NoMoreCards {
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < n; i++)
            try {
                cards.add(get());
            } catch (NoMoreCards e) {
                // Add cards back and rethrow exception.
                this.cards.addAll(cards);
                throw e;
            }
        return cards;
    }

    public void initTextures() throws NoSuchResource {
        for (Card c : cards)
            c.initTexture();
    }

    /**
     * Put a card on top of the deck.
     * @param c The card to place on top.
     */
    public void put(Card c) {
        cards.add(0, c);
    }

    /**
     * Restore the deck to its original state, meaning in the exact
     * same order as the cards appeared in the CSV file the deck was
     * loaded from.
     */
    public void restore() {
        cards.clear();
        cards.addAll(cards_orig);
    }

    public int size() {
        return cards.size();
    }
}
