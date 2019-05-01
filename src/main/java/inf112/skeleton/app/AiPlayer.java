package inf112.skeleton.app;

import java.util.ArrayList;

public class AiPlayer extends Player {

    public AiPlayer(String name) throws NoSuchResource {
        super(name);
    }

    public static ArrayList<Card> pointsToCards(Vector2Di orig_dir, ArrayList<Vector2Di> path) {
        Vector2Di dir = orig_dir;
        Vector2Di pos = path.get(0);
        ArrayList<Card> cards = new ArrayList<>();
        int dir_cnt = 0;

        for (int i = 1; i < path.size(); i++) {
            Vector2Di diff = path.get(i).copy();
            diff.sub(pos);
            if (!dir.equals(diff)) {
                if (dir_cnt > 0)
                    cards.add(new Card(Commands.moveCommand, "move", dir_cnt));
                dir_cnt = 0;
                //System.out.println("Angle: " + (int) Math.round(diff.angle(dir)));
                cards.add(new Card(Commands.rotateCommand, "rotate", (int) Math.round(diff.angle(dir))));
            }
            dir_cnt++;
            dir = diff;
            pos = path.get(i);
        }

        if (dir_cnt > 0)
            cards.add(new Card(Commands.moveCommand, "move", dir_cnt));

        return cards;
    }

    /**
     * The AI finds the smartest combination of it's cards
     * @return active cards
     */
    public Card[] chooseCards(Game game, Robot robot) {

        return null;
    }
}
