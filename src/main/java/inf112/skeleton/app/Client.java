package inf112.skeleton.app;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * GameClient
 */
public class Client {
    /**
     * Connect to a game server and perform initial handshake.
     *
     * @param host
     */
    public Client(String host, String key) {
        try (Socket sock = new Socket(host, GameServer.port)) {
            //sock.rec
            ;
        } catch (UnknownHostException e) {
            System.out.println("ERROR: No such host: " + e);
        } catch (IOException e) {
            System.out.println("ERROR: Connection to host " + e + " was dropped unexpectedly");
        }
    }
}