package ipv6;

import java.nio.channels.DatagramChannel;

public class TestReceiver {
    public static void main(String[] args) throws Exception {
        DatagramChannel channel = DatagramChannel.open();
        System.out.println(channel.isBlocking());
        channel.configureBlocking(false);
        System.out.println(channel.isBlocking());

        //        
        //        InetAddress addr = InetAddress.getByName("FF05::BAC0");
        //        MulticastSocket socket = new MulticastSocket(0xBAC0);
        //        socket.joinGroup(addr);
        //
        //        //        DatagramSocket socket = new DatagramSocket(0xBAC0, addr);
        //
        //        byte[] buf = new byte[2048];
        //        DatagramPacket packet = new DatagramPacket(buf, 2048);
        //        socket.receive(packet);
        //
        //        String s = new String(buf, 0, packet.getLength(), Charset.forName("ASCII"));
        //        System.out.println(s);
        //        socket.close();
    }
}
