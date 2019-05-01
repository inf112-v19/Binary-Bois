package inf112.skeleton.app;

import java.lang.reflect.Array;
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
                    for (int c = 0; c < dir_cnt; c++)
                        cards.add(new Card(Commands.moveCommand, "move", 1));
                        //cards.add(new Card(Commands.moveCommand, "move", dir_cnt));
                dir_cnt = 0;
                cards.add(new Card(Commands.rotateCommand, "rotate", (int) Math.round(diff.angle(dir))));
            }
            dir_cnt++;
            dir = diff;
            pos = path.get(i);
        }

        if (dir_cnt > 0)
            for (int c = 0; c < dir_cnt; c++)
                cards.add(new Card(Commands.moveCommand, "move", 1));
            //cards.add(new Card(Commands.moveCommand, "move", dir_cnt));

        return cards;
    }

    /**
     * The AI finds the smartest combination of it's cards
     * @return active cards
     */
    public static ArrayList<Card> chooseCards(Vector2Di orig_dir, ArrayList<Vector2Di> path, ArrayList<Card> hand) {
        ArrayList<Card> optimal = pointsToCards(orig_dir, path);
        ArrayList<Card> chosen_cards = new ArrayList<>();
        for (Card c : optimal) {
            if (chosen_cards.size() >= 5)
                return chosen_cards;

            if (c.getName().equals("rotate")) {
                ArrayList<Card> rotates = new ArrayList<>(findRotate(c.getAmount(), hand));
                chosen_cards.addAll(rotates);
            } else if (c.getName().equals("move")) {

            } else {
                throw new IllegalArgumentException("Not a move or rotate card");
            }
        }
        return chosen_cards;
    }

    public static ArrayList<Card> findRotate(int rotation, ArrayList<Card> my_hand) {
        ArrayList<Card> hand = my_hand;
        ArrayList<Card> both_types = new ArrayList<>(hand);
        ArrayList<Card> only_rotation = new ArrayList<>();
        for (Card c : both_types)
            if (!c.getName().equals("rotate"))
                only_rotation.add(c);
        ArrayList<Card> correct = new ArrayList<>();
        for (int r = 0; r < only_rotation.size(); r++) {
            ArrayList<Card[]> combinations = combinations(only_rotation, only_rotation.size(), r);
            for (Card[] combo : combinations) {
                int my_rotation = 0;
                for (Card c : combo) {
                    my_rotation += c.getAmount();
                }
                if (my_rotation == rotation) {
                    System.out.println("Found matching rotation: " + my_rotation + "  = " + rotation);

                    for (Card c : combo)
                        correct.add(c);
                    return correct;
                }
            }
        }
        return correct;
    }

    static ArrayList<Card[]> combinations(ArrayList<Card> arr, int n, int r) {
        ArrayList<Card> data = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            data.add(new Card(Commands.moveCommand, "move", 0));
        }
        ArrayList<Card[]> all_combos = new ArrayList<>();

        combinationUtil(arr, data, 0, n-1, 0, r, all_combos);

        return all_combos;
    }

    static void combinationUtil(ArrayList<Card> arr, ArrayList<Card> data, int start,
                                int end, int index, int r, ArrayList<Card[]> all_combos) {
        if (index == r) {
            Card[] combo = new Card[r];
            for (int j=0; j<r; j++)
                combo[j] = data.get(j);
            all_combos.add(combo);
        }

        for (int i=start; i<=end && end-i+1 >= r-index; i++) {
            data.set(index, arr.get(i));
            combinationUtil(arr, data, i+1, end, index+1, r, all_combos);
        }
    }
}
