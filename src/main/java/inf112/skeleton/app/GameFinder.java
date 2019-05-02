package inf112.skeleton.app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class GameFinder extends Thread {
    private int port;

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
            byte[] recv_data = new byte[2048];

            System.out.printf("Waiting for games on udp %s:%s",
                    InetAddress.getLocalHost().getHostAddress(), port);
            DatagramPacket rcv_pkt = new DatagramPacket(recv_data, recv_data.length);

            for (;;) {
                ssock.receive(rcv_pkt);
                String sentence = new String(rcv_pkt.getData(),0, rcv_pkt.getLength());
                System.out.printf("From %s: %s", rcv_pkt.getAddress(), sentence);
            }
        } catch (IOException e) {
            System.out.println("Error while trying to find a game: ");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GameFinder game_finder = new GameFinder();
        game_finder.start();
        try {
            game_finder.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
