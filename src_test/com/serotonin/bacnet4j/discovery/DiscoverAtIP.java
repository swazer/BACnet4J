package com.serotonin.bacnet4j.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.enums.MaxApduLength;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetTimeoutException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.LimitedAsync;

public class DiscoverAtIP {
    static LocalDevice localDevice;

    public static void main(String[] args) throws Exception {
        String ip = "166.248.35.148";
        //        String ip = "dgbox";

        IpNetwork network = new IpNetwork();
        Transport transport = new Transport(network);
        localDevice = new LocalDevice(45679, transport);
        transport.setRetries(0);
        transport.setTimeout(3000);

        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            localDevice.setExecutorService(executorService);
            localDevice.initialize();

            Address address = new Address(ip, 0xbac0);
            List<IdTester> tests = new ArrayList<IdTester>();
            for (int i = 0; i < 0xffff; i++)
                tests.add(new IdTester(address, i));
            LimitedAsync<IdTester> async = new LimitedAsync<IdTester>(executorService, tests, 50);
            async.executeAndWait();
        }
        finally {
            localDevice.terminate();
            executorService.shutdown();
        }
    }

    static class IdTester implements Runnable {
        private final Address address;
        private final int deviceId;

        public IdTester(Address address, int deviceId) {
            this.address = address;
            this.deviceId = deviceId;
        }

        @Override
        public void run() {
            ObjectIdentifier deviceOid = new ObjectIdentifier(ObjectType.device, deviceId);
            ReadPropertyRequest req = new ReadPropertyRequest(deviceOid, PropertyIdentifier.maxApduLengthAccepted);
            try {
                localDevice.send(address, null, MaxApduLength.UP_TO_50, Segmentation.noSegmentation, req).get();
                System.out.println("Response at deviceId " + deviceId);
            }
            catch (BACnetException e) {
                if (!(e instanceof BACnetTimeoutException))
                    e.printStackTrace();
            }
        }
    }
}
