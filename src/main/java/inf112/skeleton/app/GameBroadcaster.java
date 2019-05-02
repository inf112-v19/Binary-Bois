package inf112.skeleton.app;

import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class GameBroadcaster extends Thread {
    private int port;

    public GameBroadcaster(int port) {
        this.port = port;
    }

    public GameBroadcaster() {
        this(StaticConfig.UDP_BRD_PORT);
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
        byte[] buf = new byte[header.length + strbuf.length + 1];
        System.arraycopy(header, 0, buf, 0, header.length);
        System.arraycopy(strbuf, 0, buf, header.length, strbuf.length);
        buf[buf.length-1] = "\n".getBytes()[0];
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

    public void run() {
        final int WAIT_TIME = 1024;
        JSONObject obj = new JSONObject();
        obj.put("msg", "Hello World!");
        for (;;) {
            try {
                broadcastToAll(obj);
            } catch (IOException e) {
                System.out.println("Unable to broadcast:");
                e.printStackTrace();
            }

            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                ;
            }
        }
    }

    public static void main(String[] args) {
        GameBroadcaster gbcast = new GameBroadcaster();
        gbcast.start();
        try {
            gbcast.join();
        } catch (InterruptedException e) {
            System.out.println("Unable to join with thread.");
            e.printStackTrace();
        }
    }
}
