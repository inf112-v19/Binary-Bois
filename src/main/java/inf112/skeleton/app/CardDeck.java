package inf112.skeleton.app;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

/**
 * A "deck" or stack of command cards.
 */
public class CardDeck {
    private Vector<Card> commands_orig = new Vector<>();
    private Vector<Card> commands = new Vector<>();

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
            commands.add(new Card(cmd,
                                  type,
                                  row.getInt("Amount"),
                                  row.getInt("TypeID"),
                                  row.getInt("Priority")));
        }
        commands_orig.addAll(commands);
    }

    /**
     * Shuffle the deck.
     */
    public void shuffle() {
        Collections.shuffle(commands);
    }

    /**
     * @return The card on top of the deck.
     */
    public Card get() {
        if (commands.size() == 0)
            return null;
        Card card = commands.get(0);
        commands.removeElementAt(0);
        return card;
    }

    /**
     * Put a card on top of the deck.
     * @param c The card to place on top.
     */
    public void put(Card c) {
        commands.insertElementAt(c, 0);
    }

    /**
     * Restore the deck to its original state.
     */
    public void restore() {
        commands.clear();
        commands.addAll(commands_orig);
    }
}
