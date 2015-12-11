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
package com.serotonin.bacnet4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import com.serotonin.bacnet4j.enums.MaxApduLength;
import com.serotonin.bacnet4j.event.DeviceEventHandler;
import com.serotonin.bacnet4j.event.ExceptionDispatcher;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.npdu.NetworkIdentifier;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.VendorServiceKey;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedEventNotificationRequest;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedRequestService;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.IAmRequest;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedEventNotificationRequest;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedRequestService;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.SequenceDefinition;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.AddressBinding;
import com.serotonin.bacnet4j.type.constructed.CovSubscription;
import com.serotonin.bacnet4j.type.constructed.Destination;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.ObjectTypesSupported;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.ServicesSupported;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.DeviceStatus;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Unsigned16;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;
import com.serotonin.bacnet4j.util.sero.Utils;

/**
 * Enhancements:
 * - default character string encoding
 * - BIBBs (B-OWS) (services to implement) - AE-N-A - AE-ACK-A -
 * AE-INFO-A - AE-ESUM-A - SCHED-A - T-VMT-A - T-ATR-A - DM-DDB-A,B - DM-DOB-A,B - DM-DCC-A - DM-TS-A - DM-UTC-A -
 * DM-RD-A - DM-BR-A - NM-CE-A
 * 
 * @author mlohbihler
 */
public class LocalDevice {
    private static final int VENDOR_ID = 236; // Serotonin Software

    private final Transport transport;
    private final BACnetObject configuration;
    private final List<BACnetObject> localObjects = new CopyOnWriteArrayList<BACnetObject>();
    private final List<RemoteDevice> remoteDevices = new CopyOnWriteArrayList<RemoteDevice>();
    private boolean initialized;

    /**
     * The local password of the device. Used in the ReinitializeDeviceRequest service.
     */
    private String password = "";

    // Event listeners
    private final DeviceEventHandler eventHandler = new DeviceEventHandler();
    private final ExceptionDispatcher exceptionDispatcher = new ExceptionDispatcher();

    private final Timer timer;

    public static final Map<VendorServiceKey, SequenceDefinition> vendorServiceRequestResolutions = new HashMap<VendorServiceKey, SequenceDefinition>();
    public static final Map<VendorServiceKey, SequenceDefinition> vendorServiceResultResolutions = new HashMap<VendorServiceKey, SequenceDefinition>();

    public LocalDevice(int deviceId, Transport transport) {
        this.transport = transport;
        transport.setLocalDevice(this);

        timer = new Timer("BACnet4J maintenance timer");

        configuration = new BACnetObject(ObjectType.device, deviceId, "Device " + deviceId);
        configuration.setLocalDevice(this);
        configuration.writeProperty(PropertyIdentifier.maxApduLengthAccepted, new UnsignedInteger(1476));
        configuration.writeProperty(PropertyIdentifier.vendorIdentifier, new Unsigned16(VENDOR_ID));
        configuration.writeProperty(PropertyIdentifier.vendorName,
                new CharacterString("Serotonin Software Technologies, Inc."));
        configuration.writeProperty(PropertyIdentifier.segmentationSupported, Segmentation.segmentedBoth);
        configuration.writeProperty(PropertyIdentifier.maxSegmentsAccepted, new UnsignedInteger(1000));
        configuration.writeProperty(PropertyIdentifier.apduSegmentTimeout,
                new UnsignedInteger(Transport.DEFAULT_SEG_TIMEOUT));
        configuration.writeProperty(PropertyIdentifier.apduTimeout, new UnsignedInteger(Transport.DEFAULT_TIMEOUT));
        configuration.writeProperty(PropertyIdentifier.numberOfApduRetries,
                new UnsignedInteger(Transport.DEFAULT_RETRIES));
        configuration.writeProperty(PropertyIdentifier.deviceAddressBinding, new SequenceOf<AddressBinding>());
        configuration.writeProperty(PropertyIdentifier.activeCovSubscriptions, new SequenceOf<CovSubscription>());

        SequenceOf<ObjectIdentifier> objectList = new SequenceOf<ObjectIdentifier>();
        objectList.add(configuration.getId());
        configuration.writeProperty(PropertyIdentifier.objectList, objectList);

        // Set up the supported services indicators. Remove lines as services get implemented.
        ServicesSupported servicesSupported = new ServicesSupported();
        servicesSupported.setAcknowledgeAlarm(true);
        servicesSupported.setConfirmedCovNotification(true);
        servicesSupported.setConfirmedEventNotification(true);
        servicesSupported.setGetAlarmSummary(true);
        servicesSupported.setGetEnrollmentSummary(true);
        servicesSupported.setSubscribeCov(true);
        //        servicesSupported.setAtomicReadFile(true);
        //        servicesSupported.setAtomicWriteFile(true);
        servicesSupported.setAddListElement(true);
        servicesSupported.setRemoveListElement(true);
        servicesSupported.setCreateObject(true);
        servicesSupported.setDeleteObject(true);
        servicesSupported.setReadProperty(true);
        servicesSupported.setReadPropertyMultiple(true);
        servicesSupported.setWriteProperty(true);
        servicesSupported.setWritePropertyMultiple(true);
        //        servicesSupported.setDeviceCommunicationControl(true);
        servicesSupported.setConfirmedPrivateTransfer(true);
        servicesSupported.setConfirmedTextMessage(true);
        //        servicesSupported.setReinitializeDevice(true);
        //        servicesSupported.setVtOpen(true);
        //        servicesSupported.setVtClose(true);
        //        servicesSupported.setVtData(true);
        servicesSupported.setIAm(true);
        servicesSupported.setIHave(true);
        servicesSupported.setUnconfirmedCovNotification(true);
        servicesSupported.setUnconfirmedEventNotification(true);
        servicesSupported.setUnconfirmedPrivateTransfer(true);
        servicesSupported.setUnconfirmedTextMessage(true);
        //        servicesSupported.setTimeSynchronization(true);
        servicesSupported.setWhoHas(true);
        servicesSupported.setWhoIs(true);
        //        servicesSupported.setReadRange(true);
        //        servicesSupported.setUtcTimeSynchronization(true);
        //        servicesSupported.setLifeSafetyOperation(true);
        servicesSupported.setSubscribeCovProperty(true);
        servicesSupported.setGetEventInformation(true);
        //        servicesSupported.setWriteGroup(true);
        configuration.writeProperty(PropertyIdentifier.protocolServicesSupported, servicesSupported);

        // Set up the object types supported.
        ObjectTypesSupported objectTypesSupported = new ObjectTypesSupported();
        objectTypesSupported.setAll(true);
        configuration.writeProperty(PropertyIdentifier.protocolObjectTypesSupported, objectTypesSupported);

        // Set some other required values to defaults
        configuration.writeProperty(PropertyIdentifier.objectName, new CharacterString("BACnet device"));
        configuration.writeProperty(PropertyIdentifier.systemStatus, DeviceStatus.operational);
        configuration.writeProperty(PropertyIdentifier.modelName, new CharacterString("BACnet4J"));
        configuration.writeProperty(PropertyIdentifier.firmwareRevision, new CharacterString("not set"));
        configuration.writeProperty(PropertyIdentifier.applicationSoftwareVersion, new CharacterString("1.0.1"));
        configuration.writeProperty(PropertyIdentifier.protocolVersion, new UnsignedInteger(1));
        configuration.writeProperty(PropertyIdentifier.protocolRevision, new UnsignedInteger(0));
        configuration.writeProperty(PropertyIdentifier.databaseRevision, new UnsignedInteger(0));
    }

    public Network getNetwork() {
        return transport.getNetwork();
    }

    public NetworkIdentifier getNetworkIdentifier() {
        return transport.getNetworkIdentifier();
    }

    /**
     * @return the number of bytes sent by the transport
     */
    public long getBytesOut() {
        return transport.getBytesOut();
    }

    /**
     * @return the number of bytes received by the transport
     */
    public long getBytesIn() {
        return transport.getBytesIn();
    }

    public Timer getTimer() {
        return timer;
    }

    public synchronized void initialize() throws Exception {
        transport.initialize();
        initialized = true;
    }

    public synchronized void terminate() {
        timer.cancel();
        transport.terminate();
        initialized = false;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public BACnetObject getConfiguration() {
        return configuration;
    }

    public DeviceEventHandler getEventHandler() {
        return eventHandler;
    }

    public ExceptionDispatcher getExceptionDispatcher() {
        return exceptionDispatcher;
    }

    //
    //
    // Device configuration.
    //
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null)
            password = "";
        this.password = password;
    }

    //
    //
    // Local object management
    //
    public BACnetObject getObjectRequired(ObjectIdentifier id) throws BACnetServiceException {
        BACnetObject o = getObject(id);
        if (o == null)
            throw new BACnetServiceException(ErrorClass.object, ErrorCode.unknownObject);
        return o;
    }

    public List<BACnetObject> getLocalObjects() {
        return localObjects;
    }

    public BACnetObject getObject(ObjectIdentifier id) {
        if (id.getObjectType().intValue() == ObjectType.device.intValue()) {
            // Check if we need to look into the local device.
            if (id.getInstanceNumber() == 0x3FFFFF || id.getInstanceNumber() == configuration.getInstanceId())
                return configuration;
        }

        for (BACnetObject obj : localObjects) {
            if (obj.getId().equals(id))
                return obj;
        }
        return null;
    }

    public BACnetObject getObject(String name) {
        // Check if we need to look into the local device.
        if (name.equals(configuration.getObjectName()))
            return configuration;

        for (BACnetObject obj : localObjects) {
            if (name.equals(obj.getObjectName()))
                return obj;
        }
        return null;
    }

    public void addObject(BACnetObject obj) throws BACnetServiceException {
        if (getObject(obj.getId()) != null)
            throw new BACnetServiceException(ErrorClass.object, ErrorCode.objectIdentifierAlreadyExists);
        if (getObject(obj.getObjectName()) != null)
            throw new BACnetServiceException(ErrorClass.object, ErrorCode.duplicateName);
        obj.validate();
        localObjects.add(obj);
        obj.setLocalDevice(this);

        // Create a reference in the device's object list for the new object.
        getObjectList().add(obj.getId());

        // Notify the object that it was added.
        obj.addedToDevice();
    }

    public ObjectIdentifier getNextInstanceObjectIdentifier(ObjectType objectType) {
        return new ObjectIdentifier(objectType, getNextInstanceObjectNumber(objectType));
    }

    public int getNextInstanceObjectNumber(ObjectType objectType) {
        // Make a list of existing ids.
        List<Integer> ids = new ArrayList<Integer>();
        int type = objectType.intValue();
        ObjectIdentifier id;
        for (BACnetObject obj : localObjects) {
            id = obj.getId();
            if (id.getObjectType().intValue() == type)
                ids.add(id.getInstanceNumber());
        }

        // Sort the list.
        Collections.sort(ids);

        // Find the first hole in the list.
        int i = 0;
        for (; i < ids.size(); i++) {
            if (ids.get(i) != i)
                break;
        }

        return i;
    }

    public void removeObject(ObjectIdentifier id) throws BACnetServiceException {
        BACnetObject obj = getObject(id);
        if (obj != null) {
            localObjects.remove(obj);

            // Remove the reference in the device's object list for this id.
            getObjectList().remove(id);

            // Notify the object that it was removed.
            obj.removedFromDevice();
        }
        else
            throw new BACnetServiceException(ErrorClass.object, ErrorCode.unknownObject);
    }

    @SuppressWarnings("unchecked")
    private SequenceOf<ObjectIdentifier> getObjectList() {
        try {
            return (SequenceOf<ObjectIdentifier>) configuration.getProperty(PropertyIdentifier.objectList);
        }
        catch (BACnetServiceException e) {
            // Should never happen, so just wrap in a RuntimeException
            throw new RuntimeException(e);
        }
    }

    public ServicesSupported getServicesSupported() throws BACnetServiceException {
        return (ServicesSupported) getConfiguration().getProperty(PropertyIdentifier.protocolServicesSupported);
    }

    //
    //
    // Message sending
    //
    public ServiceFuture send(RemoteDevice d, ConfirmedRequestService serviceRequest) {
        //        validateSupportedService(d, serviceRequest);
        return transport.send(d.getAddress(), d.getMaxAPDULengthAccepted(), d.getSegmentationSupported(),
                serviceRequest);
    }

    public ServiceFuture send(Address address, ConfirmedRequestService serviceRequest) {
        RemoteDevice d = getRemoteDevice(address);
        if (d == null)
            // Just use some hopeful defaults.
            return transport.send(address, MaxApduLength.UP_TO_50.getMaxLength(), Segmentation.noSegmentation,
                    serviceRequest);
        return send(d, serviceRequest);
    }

    public <T extends AcknowledgementService> void send(RemoteDevice d, ConfirmedRequestService serviceRequest,
            ResponseConsumer consumer) {
        //        validateSupportedService(d, serviceRequest);
        transport.send(d.getAddress(), d.getMaxAPDULengthAccepted(), d.getSegmentationSupported(), serviceRequest,
                consumer);
    }

    public <T extends AcknowledgementService> void send(Address address, ConfirmedRequestService serviceRequest,
            ResponseConsumer consumer) {
        RemoteDevice d = getRemoteDevice(address);
        if (d == null)
            // Just use some hopeful defaults.
            transport.send(address, MaxApduLength.UP_TO_50.getMaxLength(), Segmentation.noSegmentation, serviceRequest,
                    consumer);
        else
            send(d, serviceRequest, consumer);
    }

    public void send(Address address, UnconfirmedRequestService serviceRequest) {
        transport.send(address, serviceRequest, false);
    }

    public void sendLocalBroadcast(UnconfirmedRequestService serviceRequest) {
        Address bcast = transport.getLocalBroadcastAddress();
        transport.send(bcast, serviceRequest, true);
    }

    public void sendGlobalBroadcast(UnconfirmedRequestService serviceRequest) {
        transport.send(Address.GLOBAL, serviceRequest, true);
    }

    public void sendBroadcast(Address address, UnconfirmedRequestService serviceRequest) {
        transport.send(address, serviceRequest, true);
    }

    // Doesn't work because the service choice id is not the same as the index in services supported.
    // Besides, this should be done in the transport, and errors indicated to the callback.
    //    private void validateSupportedService(RemoteDevice d, Service service) {
    //        if (d.getServicesSupported() != null) {
    //            if (!d.getServicesSupported().getValue()[service.getChoiceId()])
    //                throw new BACnetRuntimeException("Remote device does not support service " + service.getClass());
    //        }
    //    }

    //
    //
    // Remote device management
    //
    public RemoteDevice getRemoteDevice(int instanceId) throws BACnetException {
        RemoteDevice d = getRemoteDeviceImpl(instanceId);
        if (d == null)
            throw new BACnetException("Unknown device: instance id=" + instanceId);
        return d;
    }

    public RemoteDevice getRemoteDeviceCreate(int instanceId, Address address) {
        RemoteDevice d = getRemoteDeviceImpl(instanceId);
        if (d == null) {
            if (address == null)
                throw new NullPointerException("addr cannot be null");
            d = new RemoteDevice(instanceId, address);
            remoteDevices.add(d);
        }
        else
            d.setAddress(address);
        return d;
    }

    public void addRemoteDevice(RemoteDevice d) {
        remoteDevices.add(d);
    }

    public RemoteDevice getRemoteDeviceImpl(int instanceId) {
        for (RemoteDevice d : remoteDevices) {
            if (d.getInstanceNumber() == instanceId)
                return d;
        }
        return null;
    }

    public List<RemoteDevice> getRemoteDevices() {
        return remoteDevices;
    }

    public RemoteDevice getRemoteDevice(Address address) {
        for (RemoteDevice d : remoteDevices) {
            if (d.getAddress().equals(address))
                return d;
        }
        return null;
    }

    public RemoteDevice getRemoteDeviceByUserData(Object userData) {
        for (RemoteDevice d : remoteDevices) {
            if (Utils.equals(userData, d.getUserData()))
                return d;
        }
        return null;
    }

    //
    //
    // Intrinsic events
    //
    @SuppressWarnings("unchecked")
    public List<BACnetException> sendIntrinsicEvent(ObjectIdentifier eventObjectIdentifier, TimeStamp timeStamp,
            int notificationClassId, EventType eventType, CharacterString messageText, NotifyType notifyType,
            EventState fromState, EventState toState, NotificationParameters eventValues) throws BACnetException {

        // Try to find a notification class with the given id in the local objects.
        BACnetObject nc = null;
        for (BACnetObject obj : localObjects) {
            if (ObjectType.notificationClass.equals(obj.getId().getObjectType())) {
                try {
                    UnsignedInteger ncId = (UnsignedInteger) obj.getProperty(PropertyIdentifier.notificationClass);
                    if (ncId != null && ncId.intValue() == notificationClassId) {
                        nc = obj;
                        break;
                    }
                }
                catch (BACnetServiceException e) {
                    // Should never happen, so wrap in a RTE
                    throw new RuntimeException(e);
                }
            }
        }

        if (nc == null)
            throw new BACnetException("Notification class object not found for given id: " + notificationClassId);

        // Get the required properties from the notification class object.
        SequenceOf<Destination> recipientList = null;
        com.serotonin.bacnet4j.type.primitive.Boolean ackRequired = null;
        UnsignedInteger priority = null;
        try {
            recipientList = (SequenceOf<Destination>) nc.getPropertyRequired(PropertyIdentifier.recipientList);
            ackRequired = new com.serotonin.bacnet4j.type.primitive.Boolean(
                    ((EventTransitionBits) nc.getPropertyRequired(PropertyIdentifier.ackRequired)).contains(toState));

            // Determine which priority value to use based upon the toState.
            SequenceOf<UnsignedInteger> priorities = (SequenceOf<UnsignedInteger>) nc
                    .getPropertyRequired(PropertyIdentifier.priority);
            if (toState.equals(EventState.normal))
                priority = priorities.get(3);
            else if (toState.equals(EventState.fault))
                priority = priorities.get(2);
            else
                // everything else is offnormal
                priority = priorities.get(1);
        }
        catch (BACnetServiceException e) {
            // Should never happen, so wrap in a RTE
            throw new RuntimeException(e);
        }

        // Send the message to the destinations that are interested in it, while recording any exceptions in the result
        // list
        List<BACnetException> sendExceptions = new ArrayList<BACnetException>();
        for (Destination destination : recipientList) {
            if (destination.isSuitableForEvent(timeStamp, toState)) {
                if (destination.getIssueConfirmedNotifications().booleanValue()) {
                    RemoteDevice remoteDevice = null;
                    if (destination.getRecipient().isAddress())
                        remoteDevice = getRemoteDevice(destination.getRecipient().getAddress());
                    else
                        remoteDevice = getRemoteDevice(destination.getRecipient().getDevice().getInstanceNumber());

                    if (remoteDevice != null) {
                        ConfirmedEventNotificationRequest req = new ConfirmedEventNotificationRequest(
                                destination.getProcessIdentifier(), configuration.getId(), eventObjectIdentifier,
                                timeStamp, new UnsignedInteger(notificationClassId), priority, eventType, messageText,
                                notifyType, ackRequired, fromState, toState, eventValues);
                        send(remoteDevice, req);
                    }
                }
                else {
                    Address address = null;
                    if (destination.getRecipient().isAddress())
                        address = destination.getRecipient().getAddress();
                    else {
                        RemoteDevice remoteDevice = getRemoteDevice(
                                destination.getRecipient().getDevice().getInstanceNumber());
                        if (remoteDevice != null)
                            address = remoteDevice.getAddress();
                    }

                    if (address != null) {
                        UnconfirmedEventNotificationRequest req = new UnconfirmedEventNotificationRequest(
                                destination.getProcessIdentifier(), configuration.getId(), eventObjectIdentifier,
                                timeStamp, new UnsignedInteger(notificationClassId), priority, eventType, messageText,
                                notifyType, ackRequired, fromState, toState, eventValues);
                        transport.send(address, req, false);
                    }
                }
            }
        }

        return sendExceptions;
    }

    //
    //
    // Convenience methods
    //
    public Address[] getAllLocalAddresses() {
        return transport.getNetwork().getAllLocalAddresses();
    }

    public IAmRequest getIAm() {
        try {
            return new IAmRequest(configuration.getId(),
                    (UnsignedInteger) configuration.getProperty(PropertyIdentifier.maxApduLengthAccepted),
                    (Segmentation) configuration.getProperty(PropertyIdentifier.segmentationSupported),
                    (Unsigned16) configuration.getProperty(PropertyIdentifier.vendorIdentifier));
        }
        catch (BACnetServiceException e) {
            // Should never happen, so just wrap in a RuntimeException
            throw new RuntimeException(e);
        }
    }

    //
    //
    // Manual device discovery
    //
    public RemoteDevice findRemoteDevice(Address address, int deviceId) throws BACnetException {
        RemoteDevice d = getRemoteDeviceImpl(deviceId);

        if (d == null) {
            ObjectIdentifier deviceOid = new ObjectIdentifier(ObjectType.device, deviceId);
            ReadPropertyRequest req = new ReadPropertyRequest(deviceOid, PropertyIdentifier.maxApduLengthAccepted);
            ReadPropertyAck ack = (ReadPropertyAck) transport
                    .send(address, MaxApduLength.UP_TO_50.getMaxLength(), Segmentation.noSegmentation, req).get();

            // If we got this far, then we got a response. Now get the other required properties.
            d = new RemoteDevice(deviceOid.getInstanceNumber(), address);
            d.setMaxAPDULengthAccepted(((UnsignedInteger) ack.getValue()).intValue());
            d.setSegmentationSupported(Segmentation.noSegmentation);

            Map<PropertyIdentifier, Encodable> map = RequestUtils.getProperties(this, d, null,
                    PropertyIdentifier.segmentationSupported, PropertyIdentifier.vendorIdentifier,
                    PropertyIdentifier.protocolServicesSupported);
            d.setSegmentationSupported((Segmentation) map.get(PropertyIdentifier.segmentationSupported));
            d.setVendorId(((Unsigned16) map.get(PropertyIdentifier.vendorIdentifier)).intValue());
            d.setServicesSupported((ServicesSupported) map.get(PropertyIdentifier.protocolServicesSupported));

            addRemoteDevice(d);
        }
        else if (d.getServicesSupported() == null) {
            // Ensure the device has services supported.
            Map<PropertyIdentifier, Encodable> map = RequestUtils.getProperties(this, d, null,
                    PropertyIdentifier.protocolServicesSupported);
            d.setServicesSupported((ServicesSupported) map.get(PropertyIdentifier.protocolServicesSupported));
        }

        return d;
    }

    @Override
    public String toString() {
        return "" + configuration.getInstanceId() + ": " + configuration.getObjectName();
    }
}
