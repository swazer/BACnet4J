package com.serotonin.bacnet4j.obj;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.serotonin.bacnet4j.service.confirmed.AddListElementRequest;
import com.serotonin.bacnet4j.service.confirmed.RemoveListElementRequest;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.Destination;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.Recipient;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;

public class NotificationClassTest extends AbstractTest {
    NotificationClassObject nc;

    @Override
    public void before() throws Exception {
        nc = new NotificationClassObject(0, "notifClass", 100, 5, 200, new EventTransitionBits(true, true, true));
        d1.addObject(nc);
    }

    @Test
    public void listValues() throws Exception {
        // Add a few items to the list.
        Recipient recipient = new Recipient(new Address(new byte[] { 3 }));
        Boolean issueConfirmedNotifications = new Boolean(true);
        EventTransitionBits transitions = new EventTransitionBits(true, true, true);

        Destination dest1 = new Destination(recipient, new UnsignedInteger(1), issueConfirmedNotifications, transitions);
        Destination dest2 = new Destination(recipient, new UnsignedInteger(2), issueConfirmedNotifications, transitions);
        Destination dest3 = new Destination(recipient, new UnsignedInteger(3), issueConfirmedNotifications, transitions);
        AddListElementRequest aler = new AddListElementRequest(nc.getId(), PropertyIdentifier.recipientList, null,
                new SequenceOf<Destination>(dest1, dest2, dest3));
        d2.send(rd1, aler).get();

        // Read the whole list
        SequenceOf<Destination> list2 = RequestUtils.getProperty(d2, rd1, nc.getId(), PropertyIdentifier.recipientList);
        assertEquals(list2, new SequenceOf<Destination>(dest1, dest2, dest3));

        // Read at an index.
        Destination d = RequestUtils.getProperty(d2, rd1, nc.getId(), PropertyIdentifier.recipientList, 2);
        assertEquals(dest2, d);

        // Write to an index.
        RequestUtils.writeProperty(d2, rd1, nc.getId(), PropertyIdentifier.recipientList, 4, dest1);
        d = RequestUtils.getProperty(d2, rd1, nc.getId(), PropertyIdentifier.recipientList, 4);
        assertEquals(dest1, d);

        // Remove at an index.
        RemoveListElementRequest rler = new RemoveListElementRequest(nc.getId(), PropertyIdentifier.recipientList,
                null, new SequenceOf<Encodable>(dest2));
        d2.send(rd1, rler).get();

        // Read the whole list
        list2 = RequestUtils.getProperty(d2, rd1, nc.getId(), PropertyIdentifier.recipientList);
        assertEquals(list2, new SequenceOf<Destination>(dest1, dest3, dest1));
    }
}
