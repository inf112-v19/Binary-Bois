package inf112.skeleton.app;

import org.json.JSONArray;
import org.junit.Test;

import java.util.ArrayList;

/**
 * An action that can be executed on a game/robot.
 */
class AltCard extends Renderable {
    private int amount;
    private int type_id;
    private int priority;
    private String name;
    private ICommand cmd;

    public AltCard(ICommand cmd, String name, int amount, int type_id, int priority) {
        this.name = name;
        this.amount = amount;
        this.type_id = type_id;
        this.priority = priority;
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return String.format("<%s(%d) p%d>", name, amount, priority);
    }
}

public class JSONSerializationTest {
    @Test
    public void serializeAltCards() {
        JSONArray arr = new JSONArray();
        arr.put(new AltCard(Commands.rotateCommand, "rotate", 180, 0, 10));
        arr.put(new AltCard(Commands.rotateCommand, "rotate", 180, 1, 10));
        arr.put(new AltCard(Commands.rotateCommand, "rotate", 180, 2, 10));
        arr.put(new AltCard(Commands.none, "none", 2, 0, 10));
        arr.put(new AltCard(Commands.moveCommand, "rotate", 3, 0, 10));
        System.out.println(arr.toString());
    }
}
