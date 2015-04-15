package ipv6;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class TestSender {
    public static void main(String[] args) throws Exception {
        InetAddress addr = InetAddress.getByName("FF05::BAC0");
        System.out.println(addr.getClass());

        byte[] msg = "My IPv6 message".getBytes();
        DatagramPacket packet = new DatagramPacket(msg, msg.length, new InetSocketAddress(addr, 0xBAC0));
        DatagramSocket socket = new DatagramSocket();
        socket.send(packet);
        socket.close();
    }
}
