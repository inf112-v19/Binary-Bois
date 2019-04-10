package inf112.skeleton.app;

import java.util.ArrayList;

public interface IPlayer {
    ArrayList<Card> organizeCards(ArrayList<Card> cards);
    Card getOption();
}
