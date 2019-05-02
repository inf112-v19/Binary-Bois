package inf112.skeleton.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class RoboCard implements Comparable<RoboCard> {
    Robot robot;
    Card c;

    public static ArrayList<RoboCard> createDemiRound(ArrayList<Robot> robots, ArrayList<Card> cards) {
        ArrayList<RoboCard> dr = new ArrayList<>();

        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);
            dr.add(new RoboCard(robots.get(i), c == null ? Card.getNoneCard() : c));
        }
        Collections.sort(dr);
        return dr;
    }

    public static void printDR(ArrayList<RoboCard> drs[]) {
        for (int i = 0; i < drs.length; i++) {
            System.out.println(Arrays.toString(drs[i].toArray()));
        }
    }

    public RoboCard(Robot robot, Card c) {
        this.robot = robot;
        this.c = c;
    }

    public boolean exec(Game g, Runnable cb) {
        boolean ret = c.exec(robot, g);
        robot.addAnimationCallback(cb);
        return ret;
    }

    @Override
    public String toString() {
        return String.format("<%s does %s>", robot.getName(), c);
    }

    @Override
    public int compareTo(RoboCard o) {
        return this.c.getPriority() - o.c.getPriority();
    }
}

public class Round {
    public static final int NUM_CARDS = CardManager.NUM_ACTIVE_SLOTS;
    private ArrayList<RoboCard>[] demirounds;
    private int dmr_idx = 0;
    private int idx = 0;
    private boolean is_done = false;
    private Game game;
    private boolean is_animating = false;

    @SuppressWarnings("unchecked")
    public Round(ArrayList<Robot> robots, ArrayList<ArrayList<Card>> hands, Game game) {
        // Assertions

        this.game = game;
        demirounds = (ArrayList<RoboCard>[]) (new ArrayList[NUM_CARDS]);
        for (int i = 0;  i < NUM_CARDS; i++) {
            ArrayList<Card> cards = new ArrayList<>();
            for (int j = 0; j < robots.size(); j++)
                cards.add(hands.get(j).get(i));
            demirounds[i] = RoboCard.createDemiRound(robots, cards);
        }

        RoboCard.printDR(demirounds);
    }

    /**
     *
     * @return False if the round is finished.
     */
    public boolean doStep() {
        if (is_animating)
            return !is_done;

        is_animating = true;

        if (idx >= demirounds[dmr_idx].size()) {
            if (++dmr_idx >= demirounds.length) {
                is_done = true;
                return false;
            }
            idx = 0;
        }

        RoboCard rc = demirounds[dmr_idx].get(idx++);
        rc.exec(game, () -> is_animating = false);

        return true;
    }
}
