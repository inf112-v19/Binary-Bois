package inf112.skeleton.app;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * GameServer
 */
public class GameServer {
    public final static String host = "0.0.0.0";
    public final static int port = 1337;
    private String init_key = null;
    private ServerSocket serv_sock;
    private Map<String, GameSocket> clients;

    public GameServer(String init_key) throws IOException {
        serv_sock = new ServerSocket(port);
        clients = new HashMap<>();
        this.init_key = init_key;
    }

    /**
     * UDP broadcast so that potential players can find this
     * server.
     */
    public void broadcast() {
    }

    public void listen() {
        System.out.println("Listening for connections ...");
        for (;;) {
            try (Socket con = serv_sock.accept()) {
                System.out.println("Got connection!");
                GameSocket gsock = new GameSocket(con, init_key);
                System.out.println("Finished handshake, transmitting game config ...");
                JSONObject obj = new JSONObject();
                obj.put("msg", "This is a test");
                gsock.send(obj);
            } catch (IOException e) {
                System.out.println("ERROR: Unable to accept socket connection: " + e);
            } catch (DecryptionException e) {
                System.out.println("WARNING: Connected with wrong key");
            } catch (GameSocketException e) {
                System.out.println("WARNING: Possible version mismatch between server and client.");
            }
        }
    }
}
