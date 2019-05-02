package inf112.skeleton.app;

import java.util.ArrayList;
import java.util.Collections;

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
    public static ArrayList<Card> chooseCards(Vector2Di orig_dir, ArrayList<Vector2Di> path, ArrayList<Card> hand) {
        ArrayList<Card> optimal = pointsToCards(orig_dir, path);
        System.out.println("Optimal route:");
        for (Card c : optimal)
            System.out.println(c);
        ArrayList<Card> chosen_cards = new ArrayList<>();
        ArrayList<Card> left_in_hand = new ArrayList<>(hand);
        System.out.println("Cards chosen:");
        for (Card c : optimal) {
            if (chosen_cards.size() >= 5)
                return chosen_cards;

            if (c.getName().equals("rotate")) {
                ArrayList<Card> found_rotation = findMatchingCards(c.getAmount(), left_in_hand, "rotate");
                chosen_cards.addAll(found_rotation);
                left_in_hand.removeAll(found_rotation);

            } else if (c.getName().equals("move")) {
                ArrayList<Card> found_move = findMatchingCards(c.getAmount(), left_in_hand, "move");
                chosen_cards.addAll(found_move);
                left_in_hand.removeAll(found_move);
            }
        }
        return chosen_cards;
    }

    public static ArrayList<Card> findMatchingCards(int amount, ArrayList<Card> left_in_hand, String type) {
        ArrayList<Card> only_of_type = new ArrayList<>();
        for (Card c : left_in_hand)
            if (c.getName().equals(type))
                only_of_type.add(c);

        ArrayList<Card> correct = new ArrayList<>();
        for (int r = 1; r < only_of_type.size(); r++) {

            ArrayList<Card[]> combinations = combinations(only_of_type, only_of_type.size(), r);

            for (Card[] combo : combinations) {
                int current_amount = 0;
                for (Card c : combo)
                    current_amount += c.getAmount();

                if (current_amount == amount) {
                    Collections.addAll(correct, combo);
                    return correct;
                }
            }
        }
        correct.add(left_in_hand.get(0));
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
