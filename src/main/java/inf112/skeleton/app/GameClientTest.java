package inf112.skeleton.app;

public class GameClientTest {
    public static void main(String args[]) {
        String hostname = "localhost";
        try {
            GameClient client = new GameClient(hostname, "abc123");
            System.out.println(client.getGameConf());
        } catch (Exception e) {
            System.out.println("Unable to establish connection to " + hostname + ": " + e);
        }
    }
}
