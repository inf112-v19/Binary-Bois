package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * An action that can be executed on a game/robot.
 */
public class Card extends Renderable {
    private int amount;
    private int type_id;
    private int priority;
    private String name;
    private ICommand cmd;
    private Texture tx;

    public Card(ICommand cmd, String name, int amount, int type_id, int priority) {
        this.name = name;
        this.amount = amount;
        this.type_id = type_id;
        this.priority = priority;
        this.cmd = cmd;
    }

    public void initTexture(String sz) throws NoSuchResource {
        try {
            tx = Resources.getTexture("cards/" + sz + "/" + this.name + "_" + this.amount + "_" + this.priority + ".png");
            // TODO: Draw priority
        } catch (NoSuchResource e) {
            e.printStackTrace();
            tx = Resources.getTexture("cards/" + sz + "/unknown.png");
        }
    }

    public void initTexture() throws NoSuchResource {
        initTexture("175x250");
    }

    public Card(ICommand cmd, String name, int amount) {
        this.name = name;
        this.cmd = cmd;
        this.amount = amount;
        this.priority = -1;
        this.type_id = -1;
    }

    /**
     * @return The visual representation of the command as a card.
     */
    public Texture getTexture() {
        return tx;
    }

    public Vector2Di getPos() {
        return null;
    }

    /**
     * @return The "amount" to execute.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @return The id for the command, starts at 1 for each type of command.
     */
    public int getTypeID() {
        return type_id;
    }

    /**
     * @return Priority of the command.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @return Name of the command action, i.e "rotate", "move" etc.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("<%s(%d) p%d>", name, amount, priority);
    }

    /**
     * Run the card command.
     *
     * @param robot Robot to act upon.
     * @param game Game to act upon.
     * @return Whether or not the command succeeded fully.
     */
    public boolean exec(Robot robot, Game game) {
        return this.cmd.exec(this.amount, robot, game);
    }

    public static Card getNoneCard() {
        return new Card(Commands.none, "none", 0, 0, Integer.MAX_VALUE);
    }

    public JSONObject asJSON() {
        JSONObject obj = new JSONObject();
        obj.put("type", name);
        obj.put("type_id", type_id);
        obj.put("priority", priority);
        obj.put("amount", amount);
        return obj;
    }

    /**
     * Convert a JSON representation of a card into a Card object.
     *
     * @param card_obj The JSON representation.
     * @return The card being represented.
     * @throws NoSuchResource Thrown from Card::new()
     */
    public static Card fromJSON(JSONObject card_obj) throws NoSuchResource {
        return new Card(
                Commands.getComand(card_obj.getString("type")),
                card_obj.getString("type"),
                card_obj.getInt("amount"),
                card_obj.getInt("type_id"),
                card_obj.getInt("priority"));
    }

    /**
     * Call {@link #fromJSON(JSONArray)} to convert a JSON array of cards
     * into card instances.
     *
     * @param cards_obj Array of card information.
     * @return Card objects.
     * @throws NoSuchResource Thrown from Card::new()
     */
    public static ArrayList<Card> fromJSON(JSONArray cards_obj) throws NoSuchResource {
        ArrayList<Card> cards = new ArrayList<>();
        for (Object obj : cards_obj)
            cards.add(fromJSON((JSONObject) obj));
        return cards;
    }
}
