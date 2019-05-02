package inf112.skeleton.app;

import com.badlogic.gdx.utils.compression.lzma.Base;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Base64;

class GameSocketException extends Exception {
    public GameSocketException(String msg) {
        super(msg);
    }
}

/**
 * Wrapper around the regular Java socket library.
 *
 * Communicates using JSON.
 */
public class GameSocket {
    public static final int MAX_RECONNECTS = 512;
    public static final int RECONNECT_WAIT_TIME_MS = 128;
    public static final int SOCK_TIMEOUT = 16000;
    public static final int MAX_RECV_ATTEMPTS = 1;
    public static final int MAX_SEND_ATTEMPTS = 1;
    public static final int port = GameServer.port;
    private static Integer id_count = 1;
    Socket sock;
    BufferedReader sock_in;
    BufferedWriter sock_out;
    String key;
    String hostname;
    JSONObject meta;
    boolean do_reconnect = true;

    /**
     * Generate a secure key for communicating with a GameSocket.
     *
     * @return An alphanumeric string of length 16.
     */
    public static String genKey() {
        Base64.Encoder b64encoder = Base64.getEncoder();
        byte rand_bytes[] = new byte[32];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(rand_bytes);
        String str = b64encoder.encodeToString(rand_bytes);
        return str.substring(0, 16);
    }

    /**
     * Create a persistent GameSocket connection to a GameServer.
     *
     * @param hostname The host to connect to.
     * @param init_key The key to use for the handshake.
     */
    public GameSocket(String hostname, String init_key) throws IOException, DecryptionException, GameSocketException {
        sock = new Socket();
        sock.connect(new InetSocketAddress(hostname, port), SOCK_TIMEOUT);
        sock_in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        sock_out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        key = init_key;
        this.hostname = hostname;
        meta = new JSONObject();
        meta.put("id", "");

        // Perform handshake to retrieve new encryption key and ID
        System.out.println("Starting handshake ...");
        JSONObject obj = new JSONObject();
        obj.put("cmd", "join");
        System.out.println("Sending join command ...");
        send(obj);
        System.out.println("Waiting for join reply ...");
        JSONObject info = recv();
        if (!JSONTools.checkSpec(info, JSONSpecs.join_game_reply))
            throw new GameSocketException("Illegally formatted JSON returned");
        meta.put("id", (String) info.get("id"));
        key = (String) info.get("key");

        // Now the GameSocket connection is established.
        System.out.println("Connected.");
    }

    /**
     * Turn a socket connection into a GameSocket.
     * On reconnect, the same socket object is used, but with the new Socket.
     *
     * @param sock
     */
    public GameSocket(Socket sock, String init_key) throws IOException, DecryptionException, GameSocketException {
        this.sock = sock;
        sock_in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        sock_out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        key = init_key;
        this.hostname = null;
        // Clients are responsible for reconnecting to the server, not the other way around.
        do_reconnect = false;
        meta = new JSONObject();
        meta.put("id", "SERVER");

        // Perform handshake with client.
        System.out.println("Starting handshake ...");
        JSONObject cmd = recv();
        System.out.println("Received command.");
        if (!JSONTools.checkSpec(cmd, JSONSpecs.cmd_base))
            throw new GameSocketException("Illegally formatted JSON returned");
        if (!cmd.get("cmd").equals("join"))
            throw new GameSocketException("Illegally formatted JSON returned");
        System.out.println("Command was OK.");
        JSONObject obj = new JSONObject();
        int this_id_count;
        synchronized (GameSocket.class) {
            this_id_count = id_count++;
        }
        String key;
        obj.put("status", "OK");
        obj.put("id", String.format("PLAYER-%d", this_id_count));
        obj.put("key", key = genKey());
        System.out.println("Sending reply ...");
        send(obj);
        this.key = key;

        // Now the GameSocket connection is established.
        System.out.println("Connected.");
    }

    /**
     * This is the function used to handle a reconnect server-side.
     */
    public void handleReconnect(Socket sock) throws IOException {
        ;
    }

    /**
     * Attempt to reconnect to the server.
     */
    private void reconnect() throws IOException {
        if (!do_reconnect)
            return;
        throw new NotImplementedException();
    }

    public void send(JSONObject obj) throws IOException, DecryptionException {
        String json_msg = (new CryptoMessage(key, obj.toString(), meta)).asJSON();
        Exception exc = null;
        for (int i = 0; i < MAX_SEND_ATTEMPTS; i++) {
            try {
                //System.out.println("send(): " + json_msg);
                sock_out.write(json_msg);
                sock_out.write("\n");
                sock_out.flush();
                //CryptoMessage resp_enc = CryptoMessage.fromJSON(sock_in.readLine());
                //JSONObject resp = new JSONObject(resp_enc.decrypt(key));
                //Object status = resp.get("status");
                //if (!((status instanceof String) && status.equals("OK"))) {
                //    // Retry on soft error
                //    System.out.println("Soft error: " + resp.get("msg"));
                //    continue;
                //}
                break;
            } catch (IOException e) {
                System.out.println("GameSocket.send() ERROR: " + e);
                if (i+1 == MAX_SEND_ATTEMPTS)
                    throw e;
            }
        }
    }

    public JSONObject recv() throws IOException, DecryptionException, GameSocketException {
        JSONObject resp = null;
        for (int i = 0; i < MAX_RECV_ATTEMPTS; i++) {
            try {
                String line = sock_in.readLine();
                if (line == null)
                    throw new GameSocketException("sock_in.readLine() returned null.");
                CryptoMessage msg_enc = CryptoMessage.fromJSON(line);
                resp = new JSONObject(msg_enc.decrypt(key));
                System.out.println("recv(): " + resp);
                //CryptoMessage reply_enc = new CryptoMessage(key, "{\"status\": \"OK\"}");
                //sock_out.write(reply_enc.asJSON());
                //sock_out.write("\n");
                //sock_out.flush();
                break;
            } catch (IOException | DecryptionException | GameSocketException e) {
                System.out.println("GameSocket.recv() ERROR: " + e);
                if (i + 1 == MAX_RECV_ATTEMPTS)
                    throw e;
            }
        }

        return resp;
    }
}
