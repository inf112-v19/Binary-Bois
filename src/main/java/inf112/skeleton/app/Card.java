package inf112.skeleton.app;

import com.badlogic.gdx.graphics.Texture;

/**
 * An action that can be executed on a game/robot.
 */
public class Card {
    private int amount;
    private int type_id;
    private int priority;
    private String name;
    private ICommand cmd;

    public Card(ICommand cmd, String name, int amount, int type_id, int priority) {
        this.name = name;
        this.amount = amount;
        this.type_id = type_id;
        this.priority = priority;
        this.cmd = cmd;
    }

    /**
     * @return The visual representation of the command as a card.
     */
    public Texture getCardTexture() {
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
}
