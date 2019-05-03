package inf112.skeleton.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AiPlayer extends Player {

    int difficulty;   // 1 to 10, 10 being smartest/hardest
                      // 1 is random bot

    public AiPlayer(String name, int difficulty) throws NoSuchResource {
        super(name);
        this.difficulty = difficulty;
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
    public static ArrayList<Card> chooseCards(Vector2Di orig_dir, ArrayList<Vector2Di> path, ArrayList<Card> hand, int difficulty) {
        ArrayList<Card> optimal = pointsToCards(orig_dir, path);
        ArrayList<Card> chosen_cards = new ArrayList<>();
        ArrayList<Card> left_in_hand = new ArrayList<>(hand);
        int path_num = 0;
        Random rnd = new Random();
        for (Card c : optimal) {
            int goof = rnd.nextInt(difficulty);

            if (c.getName().equals("rotate")) {
                if (goof == 0 && difficulty != 10) {
                    System.out.println("Goofed!");
                    int rnd_index = rnd.nextInt(left_in_hand.size());
                    chosen_cards.add(left_in_hand.get(rnd_index));
                    left_in_hand.remove(left_in_hand.get(rnd_index));
                    continue;
                }
                ArrayList<Card> found_rotation = findMatchingCards(c.getAmount(), left_in_hand, "rotate");
                if (found_rotation.isEmpty()) {
                    Vector2Di dir = currentDir(orig_dir, chosen_cards);
                    Card filler = fillIn(left_in_hand, dir, path.get(path_num), path.get(path.size()-1));
                    chosen_cards.add(filler);
                    left_in_hand.remove(filler);
                    continue;
                }
                chosen_cards.addAll(found_rotation);
                left_in_hand.removeAll(found_rotation);

            } else if (c.getName().equals("move")) {
                path_num += c.getAmount();
                if (goof == 0 && difficulty != 10) {
                    System.out.println("Goofed!");
                    int rnd_index = rnd.nextInt(left_in_hand.size());
                    chosen_cards.add(left_in_hand.get(rnd_index));
                    left_in_hand.remove(left_in_hand.get(rnd_index));
                    continue;
                }
                ArrayList<Card> found_move = findMatchingCards(c.getAmount(), left_in_hand, "move");
                if (found_move.isEmpty()) {
                    Vector2Di dir = currentDir(orig_dir, chosen_cards);
                    Card filler = fillIn(left_in_hand, dir, path.get(path_num), path.get(path.size()-1));
                    chosen_cards.add(filler);
                    left_in_hand.remove(filler);
                    continue;
                }
                chosen_cards.addAll(found_move);
                left_in_hand.removeAll(found_move);
            }
            if (chosen_cards.size() >= CardManager.NUM_ACTIVE_SLOTS) {
                while (chosen_cards.size() > CardManager.NUM_ACTIVE_SLOTS) {
                    chosen_cards.remove(chosen_cards.size()-1);
                }
                return chosen_cards;
            }
        }
        while (chosen_cards.size() < 5) {
            Vector2Di dir = currentDir(orig_dir, chosen_cards);
            Card filler = fillIn(left_in_hand, dir, path.get(path_num), path.get(path.size()-1));
            chosen_cards.add(filler);
            left_in_hand.remove(filler);
        }
        return chosen_cards;
    }

    public static Vector2Di currentDir(Vector2Di orig_dir, ArrayList<Card> chosen_cards) {
        int rotation = 0;
        for (Card c : chosen_cards) {
            if (c != null && c.getName().equals("rotate")) {
                rotation += c.getAmount();
            }
        }
        Vector2Di rotated = orig_dir.copy();
        rotated.rotate(rotation);
        return rotated;
    }

    public static Card fillIn(ArrayList<Card> left_in_hand, Vector2Di dir, Vector2Di from, Vector2Di goal) {
        double euclid_dist = Float.MAX_VALUE;
        Card best_card = null;
        for (Card c : left_in_hand) {
            if (c.getName().equals("rotate")) {
                Vector2Di diff = goal.copy();
                diff.sub(from);
                double dist = diff.magnitude();
                if (dist < euclid_dist) {
                    euclid_dist = dist;
                    best_card = c;
                }
            } else if (c.getName().equals("move")) {
                Vector2Di diff = goal.copy();
                Vector2Di moved = from.copy();
                moved.move(dir, c.getAmount());
                diff.sub(moved);
                double dist = diff.magnitude();
                if (dist < euclid_dist) {
                    euclid_dist = dist;
                    best_card = c;
                }
            }
        }
        return best_card;
    }

    public static ArrayList<Card> findMatchingCards(int amount, ArrayList<Card> left_in_hand, String type) {
        ArrayList<Card> only_of_type = new ArrayList<>();
        int total_move_power = 0;
        for (Card c : left_in_hand)
            if (c.getName().equals(type)) {
                only_of_type.add(c);
                total_move_power += c.getAmount();
            }

        if (total_move_power < amount && type.equals("move"))
            return only_of_type;

        ArrayList<Card> correct = new ArrayList<>();
        for (int r = 1; r < only_of_type.size(); r++) {

            ArrayList<Card[]> combinations = combinations(only_of_type, only_of_type.size(), r);

            for (Card[] combo : combinations) {
                int current_amount = 0;
                for (Card c : combo)
                    current_amount += c.getAmount();

                if (current_amount == amount || current_amount == amount + 360) {  //Special case for -90 rotation
                    Collections.addAll(correct, combo);
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
