package inf112.skeleton.app;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * $HOME of all statically configured options.
 */
public class StaticConfig {
    // Necessary for LAN games
    public static final String GAME_CARDS_SRC = "resources/Programcards.csv";

    public static final String VERSION = "0.9";

    public static final boolean DEBUG = true;

    public static final int UDP_BRD_PORT = 31337;

    private static int[][] DEFAULT_ROBOTS_POS = {
            {6, 6},
            {6, 7},
            {6, 8},
            {6, 9},
    };

    private static int[][] DEFAULT_FLAGS_POS = {
            {1, 1}
    };

    public static final JSONArray DEFAULT_ROBOTS_POS_JSON = new JSONArray();
    static {
        for (int[] pos : DEFAULT_ROBOTS_POS) {
            JSONArray pos_arr = new JSONArray();
            pos_arr.put(pos[0]);
            pos_arr.put(pos[1]);
            DEFAULT_ROBOTS_POS_JSON.put(pos_arr);
        }
    }

    public static final JSONArray DEFAULT_FLAGS_POS_JSON = new JSONArray();
    static {
        for (int[] pos : DEFAULT_FLAGS_POS) {
            JSONArray pos_arr = new JSONArray();
            pos_arr.put(pos[0]);
            pos_arr.put(pos[1]);
            DEFAULT_FLAGS_POS_JSON.put(pos_arr);
        }
    }

    // Warning: When you add something here make sure you also add it to JSONSpecs.game_options
    public static final JSONObject DEFAULT_GAME_OPTIONS = new JSONObject();
    static {
        DEFAULT_GAME_OPTIONS.put("version", StaticConfig.VERSION);
        DEFAULT_GAME_OPTIONS.put("num_players", DEFAULT_ROBOTS_POS.length);
        DEFAULT_GAME_OPTIONS.put("choosing_cards_time", 45);
        DEFAULT_GAME_OPTIONS.put("num_starting_cards", 9);
        DEFAULT_GAME_OPTIONS.put("map", "map2.tmx");
        DEFAULT_GAME_OPTIONS.put("robots_pos", DEFAULT_ROBOTS_POS_JSON);
        DEFAULT_GAME_OPTIONS.put("flags_pos", DEFAULT_FLAGS_POS_JSON);
    }
}
