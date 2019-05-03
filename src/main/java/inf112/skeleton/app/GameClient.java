package inf112.skeleton.app;

import org.json.JSONObject;

import java.io.IOException;

public class GameClient {
    GameSocket gsock;
    JSONObject game_conf;

    public GameClient(String hostname, String init_key) throws IOException {
        gsock = new GameSocket(hostname, init_key);
        game_conf = gsock.recv();
    }

    public JSONObject getGameConf() {
        return game_conf;
    }
}
