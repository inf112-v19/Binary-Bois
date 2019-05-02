package inf112.skeleton.app;

import java.util.ArrayList;

/**
 * Player action interface.
 */
public interface IPlayerControls {
    /**
     * Set the cards to be used for the round.
     *
     * @param cards List of cards in their chosen order.
     */
    void setCards(ArrayList<Card> cards);

    /**
     * Set the power up status for the round.
     *
     * @param do_power_up Whether or not to power up.
     */
    void setPoweredUp(boolean do_power_up);

    /**
     * Say something to all other players.
     *
     * @param message What to say.
     */
    void say(String message);
}
