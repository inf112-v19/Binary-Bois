package inf112.skeleton.app;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON specs for all the network protocols used in the game.
 *
 * Note that the spec system is very basic and only checks for type information.
 */
public class JSONSpecs {
    public static Map<String, SpecTarget> cmd_base = new HashMap<>();
    static {
        cmd_base.put("cmd", new SpecTarget(String.class));
    }

    public static Map<String, SpecTarget> join_game_reply = new HashMap<>();
    static {
        join_game_reply.put("status", new SpecTarget(String.class));
        join_game_reply.put("id", new SpecTarget(String.class));
        join_game_reply.put("key", new SpecTarget(String.class));
    }

    public static Map<String, SpecTarget> encrypted_message = new HashMap<>();
    static {
        encrypted_message.put("iv", new SpecTarget(String.class));
        encrypted_message.put("ciphertext", new SpecTarget(String.class));
        encrypted_message.put("meta", new SpecTarget(JSONObject.class));
    }
}
