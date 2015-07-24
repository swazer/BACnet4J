/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * When signing a commercial license with Infinite Automation Software,
 * the following extension to GPL is made. A special exception to the GPL is
 * included to allow you to distribute a combined work that includes BAcnet4J
 * without being obliged to provide the source code for any proprietary components.
 *
 * See www.infiniteautomation.com for commercial license options.
 * 
 * @author Matthew Lohbihler
 */
package com.serotonin.bacnet4j.util;

import java.util.Map;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.ServicesSupported;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class DiscoveryUtils {
    public static RemoteDevice discoverDevice(LocalDevice localDevice, final int deviceId) {
        // Send a WhoIs to the device. Use a listener to notify of a response so that we can return as soon as possible.
        final Object lock = new Object();
        DeviceEventAdapter listener = new DeviceEventAdapter() {
            @Override
            public void iAmReceived(RemoteDevice d) {
                if (d.getInstanceNumber() == deviceId) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            }
        };

        try {
            localDevice.getEventHandler().addListener(listener);

            synchronized (lock) {
                // Send a WhoIs for the device.
                UnsignedInteger id = new UnsignedInteger(deviceId);
                localDevice.sendGlobalBroadcast(new WhoIsRequest(id, id));

                try {
                    lock.wait(Transport.DEFAULT_TIMEOUT);
                }
                catch (InterruptedException e) {
                    // Ignore
                }
            }
        }
        finally {
            localDevice.getEventHandler().removeListener(listener);
        }

        // Get the device from the local.
        RemoteDevice d;
        try {
            d = localDevice.getRemoteDevice(deviceId);
        }
        catch (BACnetException e) {
            return null;
        }

        try {
            Map<PropertyIdentifier, Encodable> map = RequestUtils.getProperties(localDevice, d, null,
                    PropertyIdentifier.protocolServicesSupported);
            d.setServicesSupported((ServicesSupported) map.get(PropertyIdentifier.protocolServicesSupported));
        }
        catch (BACnetException e) {
            // ignore
        }

        return d;
    }

    public static void getExtendedDeviceInformation(LocalDevice localDevice, RemoteDevice d) throws BACnetException {
        ObjectIdentifier oid = d.getObjectIdentifier();

        // Get the device's supported services
        if (d.getServicesSupported() == null) {
            ReadPropertyAck supportedServicesAck = (ReadPropertyAck) localDevice.send(d,
                    new ReadPropertyRequest(oid, PropertyIdentifier.protocolServicesSupported)).get();
            d.setServicesSupported((ServicesSupported) supportedServicesAck.getValue());
        }

        // Uses the readProperties method here because this list will probably be extended.
        PropertyReferences properties = new PropertyReferences();
        properties.add(oid, PropertyIdentifier.objectName);
        properties.add(oid, PropertyIdentifier.protocolVersion);
        properties.add(oid, PropertyIdentifier.vendorIdentifier);
        properties.add(oid, PropertyIdentifier.modelName);

        PropertyValues values = RequestUtils.readProperties(localDevice, d, properties, null);

        d.setName(values.getString(oid, PropertyIdentifier.objectName));
        d.setProtocolVersion((UnsignedInteger) values.getNullOnError(oid, PropertyIdentifier.protocolVersion));

        UnsignedInteger vendorIdentifier = (UnsignedInteger) values.getNullOnError(oid,
                PropertyIdentifier.vendorIdentifier);
        if (vendorIdentifier != null)
            d.setVendorId(vendorIdentifier.intValue());

        CharacterString modelName = (CharacterString) values.getNullOnError(oid, PropertyIdentifier.modelName);
        if (modelName != null)
            d.setModelName(modelName.getValue());
    }
}
