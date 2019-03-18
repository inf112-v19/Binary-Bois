package inf112.skeleton.app;

import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.fail;

/**
 * Unit test for simple App.
 */
public class CardDeckTest {
    static String card_file = "./resources/Programcards.csv";
    @Test
    public void loadCards() {
        CardDeck deck = null;
        try {
            deck = new CardDeck(card_file);
        } catch (IOException | CSV.CSVError e) {
            fail();
        }
    }
}
