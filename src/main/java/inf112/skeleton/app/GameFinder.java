package inf112.skeleton.app;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

class GameSettings {
    private long time;
    private JSONObject settings;
    private InetAddress from_addr;
    public static final int GAME_TIMEOUT = 8192;

    public GameSettings(InetAddress from_addr, JSONObject settings) {
        this.settings = settings;
        this.from_addr = from_addr;
        time = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof GameSettings))
            return false;
        GameSettings other = (GameSettings) o;
        return other.from_addr.equals(from_addr);
    }

    @Override
    public int hashCode() {
        return this.from_addr.toString().hashCode();
    }

    public boolean isOutdated() {
        return (time + GAME_TIMEOUT) <= System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format("<GameSettings: %s>", from_addr);
    }

    public String getHost() {
        String addr_str = from_addr.toString();
        if (addr_str.startsWith("/"))
            addr_str = addr_str.substring(1, addr_str.length());
        return addr_str;
    }
}

public class GameFinder extends Thread {
    private int port;
    private Map<String, GameSettings> discovered_games = new HashMap<>();

    public GameFinder() {
        this(StaticConfig.UDP_BRD_PORT);
    }

    public GameFinder(int port) {
        super();
        this.port = port;
    }

    @Override
    public void run() {
        try {
            DatagramSocket ssock = new DatagramSocket(port);
            ssock.setBroadcast(true);
            byte[] recv_data = new byte[2048];

            System.out.printf("Waiting for games on udp %s:%s",
                    InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket rcv_pkt = new DatagramPacket(recv_data, recv_data.length);
            String header = "BinaryBois/RoboRally\n";

            for (;;) {
                ssock.receive(rcv_pkt);
                String data_full = new String(rcv_pkt.getData(),0, rcv_pkt.getLength());
                if (!data_full.startsWith(header)) {
                    System.out.println("Received UDP broadcast, but was not a RoborRally game");
                    continue;
                }
                String data = new String(rcv_pkt.getData(), header.length(), rcv_pkt.getLength());
                try {
                    synchronized (this) {
                        discovered_games.put(
                                rcv_pkt.getAddress().toString(),
                                new GameSettings(rcv_pkt.getAddress(), new JSONObject(data)));
                        //discovered_games.add(new GameSettings(rcv_pkt.getAddress(), new JSONObject(data)));
                    }
                } catch (JSONException e) {
                    System.out.println("JSON Decoding error: " + data);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Error while trying to find a game: ");
            e.printStackTrace();
        }
    }

    public ArrayList<GameSettings> getGames() {
        synchronized (this) {
            ArrayList<String> outdated_games = new ArrayList<>();
            ArrayList<GameSettings> games = new ArrayList<>();
            for (Map.Entry<String, GameSettings> entry : discovered_games.entrySet())
                if (entry.getValue().isOutdated())
                    outdated_games.add(entry.getKey());
                else
                    games.add(entry.getValue());
            for (String k : outdated_games)
                discovered_games.remove(k);
            return games;
        }
    }

    public static void main(String[] args) {
        GameFinder game_finder = new GameFinder();
        game_finder.start();
        for (;;) {
            System.out.println("Discovered: " + Arrays.toString(game_finder.getGames().toArray()));
            try {
                Thread.sleep(1024);
            } catch (InterruptedException e) {
                ;
            }
        }
    }
}
