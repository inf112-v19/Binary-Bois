package inf112.skeleton.app;

import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class GameBroadcaster extends Thread {
    private int port;
    private boolean running = true;
    private JSONObject settings;

    public GameBroadcaster(JSONObject settings, int port) {
        this.port = port;
        this.settings = settings;
    }

    public GameBroadcaster(JSONObject settings) {
        this(settings, StaticConfig.UDP_BRD_PORT);
    }

    /**
     * UDP broadcast so that potential players can find this
     * server.
     */
    public void broadcastTo(JSONObject obj, InetAddress brd_addr) throws IOException {
        final byte[] header = "BinaryBois/RoboRally\n".getBytes();
        DatagramSocket dsock = new DatagramSocket();
        dsock.setBroadcast(true);
        byte[] strbuf = obj.toString().getBytes();
        byte[] buf = new byte[header.length + strbuf.length];
        System.arraycopy(header, 0, buf, 0, header.length);
        System.arraycopy(strbuf, 0, buf, header.length, strbuf.length);
        //buf[buf.length-1] = "\n".getBytes()[0];
        DatagramPacket pck = new DatagramPacket(buf, buf.length, brd_addr, port);
        dsock.send(pck);
        dsock.close();
    }

    public void broadcastToAll(JSONObject obj) throws IOException {
        for (InetAddress addr : getBroadcastAddresses()) {
            System.out.println("Broadcasting to: " + addr);
            broadcastTo(obj, addr);
        }
    }

    public static ArrayList<InetAddress> getBroadcastAddresses() throws SocketException {
        ArrayList<InetAddress> brd_addrs = new ArrayList<>();
        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface net_iface;
        while (ifaces.hasMoreElements()) {
            net_iface = ifaces.nextElement();

            if (net_iface.isLoopback() || !net_iface.isUp())
                continue;

            for (InterfaceAddress iface : net_iface.getInterfaceAddresses()) {
                InetAddress addr = iface.getBroadcast();
                if (addr != null)
                    brd_addrs.add(addr);
            }
        }
        return brd_addrs;
    }

    /**
     * This is a dumb broadcast method that sends packets to every address on the subnet.
     *
     * This allows us to play the game on Høytek and other networks that might not
     * allow UDP broadcasts.
     */
    public void epicHighTechWorkaround(JSONObject obj, int wait_time) {
        int err = 0;
        for (int i = 0; i <= 8192; i++) {
            byte[] addr = {10, 113, 0, 0};
            addr[2] = (byte) Math.abs((i & 0xff00) >> 8);
            addr[3] = (byte) Math.abs(i & 0xff);
            try {
                broadcastTo(obj, InetAddress.getByAddress(addr));
            } catch (IOException e) {
                err++;
            }
            try {
                Thread.sleep(wait_time);
            } catch (InterruptedException e) {
                ;
            }
        }
        System.out.println("DUN: " + err);
    }

    public void run() {
        final int WAIT_TIME = 1024;
        int htek_wait_time = 3;
        boolean running;
        synchronized (this) {
            running = this.running;
        }

        do {
            try {
                broadcastTo(settings, InetAddress.getByName("10.113.22.168"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                broadcastToAll(settings);
                epicHighTechWorkaround(settings, htek_wait_time++);
            } catch (IOException e) {
                System.out.println("Unable to broadcast:");
                e.printStackTrace();
            }

            if (htek_wait_time == 32)
                htek_wait_time = 3;

            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                ;
            }

            synchronized (this) {
                running = this.running;
            }
        } while (running);
    }

    public void done() {
        synchronized (this) {
            running = false;
        }
    }

    public static void main(String[] args) {
        GameBroadcaster gbcast = new GameBroadcaster(StaticConfig.DEFAULT_GAME_OPTIONS);
        gbcast.start();
        try {
            gbcast.join();
        } catch (InterruptedException e) {
            System.out.println("Unable to join with thread.");
            e.printStackTrace();
        }
    }
}
