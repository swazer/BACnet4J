package com.serotonin.bacnet4j.npdu;

import org.junit.Test;

public class NetworkUtilsTest {
    @Test
    public void ipV6() throws Exception {
        System.out.println(NetworkUtils.toOctetString("[::1]:456").getDescription());
        System.out.println(NetworkUtils.toOctetString("192.168.0.123:47808").getDescription());

        //        
        //        System.out.println(InetAddress.getByName("[::1]"));
        //        System.out.println(InetAddress.getByName("::1"));
        //        System.out.println(InetAddress.getByName("::1").toString());
        //
        //        URI uri = new URI("my://" + s);
        //        System.out.println(uri.getHost());
        //        System.out.println(uri.getPort());
    }
}
