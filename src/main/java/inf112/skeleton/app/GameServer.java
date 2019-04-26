package inf112.skeleton.app;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

class GameClient {
    ;
}

/**
 * GameServer
 */
public class GameServer {
    public final static String host = "0.0.0.0";
    public final static int port = 1337;
    private ServerSocket serv_sock;

    public GameServer() throws IOException {
        serv_sock = new ServerSocket(port);
    }

    /**
     * UDP broadcast so that potential players can find this
     * server.
     */
    public void broadcast() {
    }

    public void listen() {
        for (;;) {
            try (Socket con = serv_sock.accept()) {
                PrintWriter con_writer = new PrintWriter(con.getOutputStream(), true);
                con_writer.println("{'status': 'ok'}");
            } catch (IOException e) {
                System.out.println("ERROR: Unable to accept socket connection: " + e);
            }
        }
    }
}
