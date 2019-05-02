package inf112.skeleton.app;

import org.json.JSONArray;
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

    public static Map<String, SpecTarget> card_map = new HashMap<>();
    static {
        card_map.put("type", new SpecTarget(String.class));
        card_map.put("type_id", new SpecTarget(Integer.class));
        card_map.put("priority", new SpecTarget(Integer.class));
        card_map.put("amount", new SpecTarget(Integer.class));
    }

    // Positions are represented as [x, y] lists.
    public static SpecTarget pos = SpecTarget.newArraySpec(new SpecTarget(Integer.class));

    public static SpecTarget card = new SpecTarget(card_map);
    public static SpecTarget cards = SpecTarget.newArraySpec(card);

    public static Map<String, SpecTarget> card_deal_map = new HashMap<>();
    static {
        card_deal_map.put("reason", new SpecTarget(String.class));
        card_deal_map.put("cards", cards);
    }
    public static SpecTarget card_deal = new SpecTarget(card_deal_map);

    public static Map<String, SpecTarget> game_options_map = new HashMap<>();
    static {
        game_options_map.put("version", new SpecTarget(String.class));
        game_options_map.put("num_players", new SpecTarget(Integer.class));
        game_options_map.put("choosing_cards_time", new SpecTarget(Integer.class));
        game_options_map.put("num_starting_cards", new SpecTarget(Integer.class));
        game_options_map.put("map", new SpecTarget(String.class));
    }
    public static SpecTarget game_options = new SpecTarget(game_options_map);

    public static Map<String, SpecTarget> game_init_map = new HashMap<>();
    static {
        game_init_map.put("robots_pos", SpecTarget.newArraySpec(pos));
        game_init_map.put("flags_pos", SpecTarget.newArraySpec(pos));
        game_init_map.put("cards", cards);
        game_init_map.put("idx", new SpecTarget(Integer.class));
    }
    public static SpecTarget game_init = new SpecTarget(game_init_map);

    public static Map<String, SpecTarget> run_round_map = new HashMap<>();
    static {
        // Array of arrays of cards:
        run_round_map.put("player_cards", SpecTarget.newArraySpec(cards));
    }

    public static Map<String, SpecTarget> cards_order_map = new HashMap<>();
    static {
        cards_order_map.put("cards", cards);
        cards_order_map.put("final", new SpecTarget(Boolean.class));
    }
    public static SpecTarget cards_order = new SpecTarget(cards_order_map);
}
