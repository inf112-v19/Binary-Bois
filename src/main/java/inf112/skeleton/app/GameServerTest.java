package inf112.skeleton.app;

import java.io.IOException;

public class GameServerTest {
    public static void main(String args[]) {
        try {
            GameServer server = new GameServer("abc123");
            server.listen();
        } catch (IOException e) {
            System.out.println("Unable to start server: " + e);
            System.exit(1);
        }
    }
}
