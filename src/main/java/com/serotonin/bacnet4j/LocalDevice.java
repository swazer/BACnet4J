/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2006-2011 Serotonin Software Technologies Inc. http://serotoninsoftware.com
 * @author Matthew Lohbihler
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
 * When signing a commercial license with Serotonin Software Technologies Inc.,
 * the following extension to GPL is made. A special exception to the GPL is 
 * included to allow you to distribute a combined work that includes BAcnet4J 
 * without being obliged to provide the source code for any proprietary components.
 */
package com.serotonin.bacnet4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ObjectUtils;

import com.serotonin.bacnet4j.enums.MaxApduLength;
import com.serotonin.bacnet4j.event.DeviceEventHandler;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
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
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.type.primitive.Unsigned16;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;

/**
 * Enhancements: - default character string encoding - BIBBs (B-OWS) (services to implement) - AE-N-A - AE-ACK-A -
 * AE-INFO-A - AE-ESUM-A - SCHED-A - T-VMT-A - T-ATR-A - DM-DDB-A,B - DM-DOB-A,B - DM-DCC-A - DM-TS-A - DM-UTC-A -
 * DM-RD-A - DM-BR-A - NM-CE-A
 * 
 * @author mlohbihler
 */
public class LocalDevice {
    private static final int VENDOR_ID = 236; // Serotonin Software

    private final Transport transport;
    private BACnetObject configuration;
    private final List<BACnetObject> localObjects = new CopyOnWriteArrayList<BACnetObject>();
    private final List<RemoteDevice> remoteDevices = new CopyOnWriteArrayList<RemoteDevice>();
    private boolean initialized;
    private ExecutorService executorService;
    private boolean ownsExecutorService;

    /**
     * The local password of the device. Used in the ReinitializeDeviceRequest service.
     */
    private String password = "";

    // Event listeners
    private final DeviceEventHandler eventHandler = new DeviceEventHandler();

    //private final DeviceEventHandler eventHandler = new DeviceEventAsyncHandler();

    public static final Map<VendorServiceKey, SequenceDefinition> vendorServiceRequestResolutions = new HashMap<VendorServiceKey, SequenceDefinition>();
    public static final Map<VendorServiceKey, SequenceDefinition> vendorServiceResultResolutions = new HashMap<VendorServiceKey, SequenceDefinition>();

    public LocalDevice(int deviceId, Transport transport) {
        this.transport = transport;
        transport.setLocalDevice(this);

        try {
            ObjectIdentifier deviceIdentifier = new ObjectIdentifier(ObjectType.device, deviceId);

            configuration = new BACnetObject(this, deviceIdentifier);
            configuration.setProperty(PropertyIdentifier.maxApduLengthAccepted, new UnsignedInteger(1476));
            configuration.setProperty(PropertyIdentifier.vendorIdentifier, new Unsigned16(VENDOR_ID));
            configuration.setProperty(PropertyIdentifier.vendorName, new CharacterString(
                    "Serotonin Software Technologies, Inc."));
            configuration.setProperty(PropertyIdentifier.segmentationSupported, Segmentation.segmentedBoth);

            SequenceOf<ObjectIdentifier> objectList = new SequenceOf<ObjectIdentifier>();
            objectList.add(deviceIdentifier);
            configuration.setProperty(PropertyIdentifier.objectList, objectList);

            // Set up the supported services indicators. Remove lines as services get implemented.
            ServicesSupported servicesSupported = new ServicesSupported();
            servicesSupported.setAll(true);
            servicesSupported.setAcknowledgeAlarm(false);
            servicesSupported.setGetAlarmSummary(false);
            servicesSupported.setGetEnrollmentSummary(false);
            servicesSupported.setAtomicReadFile(false);
            servicesSupported.setAtomicWriteFile(false);
            servicesSupported.setAddListElement(false);
            servicesSupported.setRemoveListElement(false);
            servicesSupported.setReadPropertyConditional(false);
            servicesSupported.setDeviceCommunicationControl(false);
            servicesSupported.setReinitializeDevice(false);
            servicesSupported.setVtOpen(false);
            servicesSupported.setVtClose(false);
            servicesSupported.setVtData(false);
            servicesSupported.setAuthenticate(false);
            servicesSupported.setRequestKey(false);
            servicesSupported.setTimeSynchronization(false);
            servicesSupported.setReadRange(false);
            servicesSupported.setUtcTimeSynchronization(false);
            servicesSupported.setLifeSafetyOperation(false);
            servicesSupported.setSubscribeCovProperty(false);
            servicesSupported.setGetEventInformation(false);
            configuration.setProperty(PropertyIdentifier.protocolServicesSupported, servicesSupported);

            // Set up the object types supported.
            ObjectTypesSupported objectTypesSupported = new ObjectTypesSupported();
            objectTypesSupported.setAll(true);
            configuration.setProperty(PropertyIdentifier.protocolObjectTypesSupported, objectTypesSupported);

            // Set some other required values to defaults
            configuration.setProperty(PropertyIdentifier.objectName, new CharacterString("BACnet device"));
            configuration.setProperty(PropertyIdentifier.systemStatus, DeviceStatus.operational);
            configuration.setProperty(PropertyIdentifier.modelName, new CharacterString("BACnet4J"));
            configuration.setProperty(PropertyIdentifier.firmwareRevision, new CharacterString("not set"));
            configuration.setProperty(PropertyIdentifier.applicationSoftwareVersion, new CharacterString("1.0.1"));
            configuration.setProperty(PropertyIdentifier.protocolVersion, new UnsignedInteger(1));
            configuration.setProperty(PropertyIdentifier.protocolRevision, new UnsignedInteger(0));
            configuration.setProperty(PropertyIdentifier.databaseRevision, new UnsignedInteger(0));
        }
        catch (BACnetServiceException e) {
            // Should never happen, but wrap in an unchecked just in case.
            throw new BACnetRuntimeException(e);
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        if (initialized)
            throw new IllegalStateException("Cannot set the executor service. Already initialized");
        this.executorService = executorService;
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

    public synchronized void initialize() throws Exception {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
            ownsExecutorService = true;
        }
        else
            ownsExecutorService = false;

        transport.initialize();
        initialized = true;
    }

    public synchronized void terminate() {
        transport.terminate();
        initialized = false;

        if (ownsExecutorService) {
            ExecutorService temp = executorService;
            executorService = null;
            if (temp != null) {
                temp.shutdown();
                try {
                    temp.awaitTermination(3, TimeUnit.SECONDS);
                }
                catch (InterruptedException e) {
                    // no op
                }
            }
        }
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

        // Create a reference in the device's object list for the new object.
        getObjectList().add(obj.getId());
    }

    public ObjectIdentifier getNextInstanceObjectIdentifier(ObjectType objectType) {
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
        return new ObjectIdentifier(objectType, i);
    }

    public void removeObject(ObjectIdentifier id) throws BACnetServiceException {
        BACnetObject obj = getObject(id);
        if (obj != null) {
            localObjects.remove(obj);

            // Remove the reference in the device's object list for this id.
            getObjectList().remove(id);
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
    public <T extends AcknowledgementService> ServiceFuture<T> send(RemoteDevice d,
            ConfirmedRequestService serviceRequest) {
        return transport.send(d.getAddress(), d.getLinkService(), d.getMaxAPDULengthAccepted(),
                d.getSegmentationSupported(), serviceRequest);
    }

    public <T extends AcknowledgementService> ServiceFuture<T> send(Address address, MaxApduLength maxAPDULength,
            Segmentation segmentationSupported, ConfirmedRequestService serviceRequest) {
        return transport.send(address, null, maxAPDULength.getMaxLength(), segmentationSupported, serviceRequest);
    }

    public <T extends AcknowledgementService> ServiceFuture<T> send(Address address, OctetString linkService,
            MaxApduLength maxAPDULength, Segmentation segmentationSupported, ConfirmedRequestService serviceRequest) {
        return transport
                .send(address, linkService, maxAPDULength.getMaxLength(), segmentationSupported, serviceRequest);
    }

    public <T extends AcknowledgementService> void send(RemoteDevice d, ConfirmedRequestService serviceRequest,
            ResponseConsumer<T> consumer) {
        transport.send(d.getAddress(), d.getLinkService(), d.getMaxAPDULengthAccepted(), d.getSegmentationSupported(),
                serviceRequest, consumer);
    }

    public <T extends AcknowledgementService> void send(Address address, MaxApduLength maxAPDULength,
            Segmentation segmentationSupported, ConfirmedRequestService serviceRequest, ResponseConsumer<T> consumer) {
        transport.send(address, null, maxAPDULength.getMaxLength(), segmentationSupported, serviceRequest, consumer);
    }

    public <T extends AcknowledgementService> void send(Address address, OctetString linkService,
            MaxApduLength maxAPDULength, Segmentation segmentationSupported, ConfirmedRequestService serviceRequest,
            ResponseConsumer<T> consumer) {
        transport.send(address, linkService, maxAPDULength.getMaxLength(), segmentationSupported, serviceRequest,
                consumer);
    }

    public void send(Address address, UnconfirmedRequestService serviceRequest) {
        transport.send(address, null, serviceRequest, false);
    }

    public void send(Address address, OctetString linkService, UnconfirmedRequestService serviceRequest) {
        transport.send(address, linkService, serviceRequest, false);
    }

    public void sendLocalBroadcast(UnconfirmedRequestService serviceRequest) {
        Address bcast = transport.getLocalBroadcastAddress();
        transport.send(bcast, null, serviceRequest, true);
    }

    public void sendGlobalBroadcast(UnconfirmedRequestService serviceRequest) {
        transport.send(Address.GLOBAL, null, serviceRequest, true);
    }

    public void sendBroadcast(Address address, OctetString linkService, UnconfirmedRequestService serviceRequest) {
        transport.send(address, linkService, serviceRequest, true);
    }

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

    public RemoteDevice getRemoteDeviceCreate(int instanceId, Address address, OctetString linkService) {
        RemoteDevice d = getRemoteDeviceImpl(instanceId);
        if (d == null) {
            if (address == null)
                throw new NullPointerException("addr cannot be null");
            d = new RemoteDevice(instanceId, address, linkService);
            remoteDevices.add(d);
        }
        else {
            d.setAddress(address);
            d.setLinkService(linkService);
        }
        return d;
    }

    public void addRemoteDevice(RemoteDevice d) {
        remoteDevices.add(d);
    }

    private RemoteDevice getRemoteDeviceImpl(int instanceId) {
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

    public RemoteDevice getRemoteDevice(Address address, OctetString linkService) {
        for (RemoteDevice d : remoteDevices) {
            if (d.getAddress().equals(address) && ObjectUtils.equals(d.getLinkService(), linkService))
                return d;
        }
        return null;
    }

    public RemoteDevice getRemoteDeviceByUserData(Object userData) {
        for (RemoteDevice d : remoteDevices) {
            if (ObjectUtils.equals(userData, d.getUserData()))
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
                        remoteDevice = getRemoteDevice(destination.getRecipient().getObjectIdentifier()
                                .getInstanceNumber());

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
                        RemoteDevice remoteDevice = getRemoteDevice(destination.getRecipient().getObjectIdentifier()
                                .getInstanceNumber());
                        if (remoteDevice != null)
                            address = remoteDevice.getAddress();
                    }

                    if (address != null) {
                        UnconfirmedEventNotificationRequest req = new UnconfirmedEventNotificationRequest(
                                destination.getProcessIdentifier(), configuration.getId(), eventObjectIdentifier,
                                timeStamp, new UnsignedInteger(notificationClassId), priority, eventType, messageText,
                                notifyType, ackRequired, fromState, toState, eventValues);
                        transport.send(address, null, req, false);
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
    public RemoteDevice findRemoteDevice(Address address, OctetString linkService, int deviceId) throws BACnetException {
        RemoteDevice d = getRemoteDeviceImpl(deviceId);

        if (d == null) {
            ObjectIdentifier deviceOid = new ObjectIdentifier(ObjectType.device, deviceId);
            ReadPropertyRequest req = new ReadPropertyRequest(deviceOid, PropertyIdentifier.maxApduLengthAccepted);
            ReadPropertyAck ack = (ReadPropertyAck) transport.send(address, linkService,
                    MaxApduLength.UP_TO_50.getMaxLength(), Segmentation.noSegmentation, req);

            // If we got this far, then we got a response. Now get the other required properties.
            d = new RemoteDevice(deviceOid.getInstanceNumber(), address, linkService);
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
}
