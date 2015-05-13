package com.serotonin.bacnet4j.service.unconfirmed;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.NotImplementedException;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.GroupChannelValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Unsigned32;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class WriteGroupRequest extends UnconfirmedRequestService {
    private static final long serialVersionUID = -7378234217541272001L;

    public static final byte TYPE_ID = 10;

    private final Unsigned32 groupNumber; // 0
    private final UnsignedInteger writePriority; // 1
    private final SequenceOf<GroupChannelValue> changeList; // 2
    private final Boolean inhibitDelay; // 3 optional

    public WriteGroupRequest(Unsigned32 groupNumber, UnsignedInteger writePriority,
            SequenceOf<GroupChannelValue> changeList, Boolean inhibitDelay) {
        this.groupNumber = groupNumber;
        this.writePriority = writePriority;
        this.changeList = changeList;
        this.inhibitDelay = inhibitDelay;
    }

    @Override
    public byte getChoiceId() {
        return TYPE_ID;
    }

    public Unsigned32 getGroupNumber() {
        return groupNumber;
    }

    public UnsignedInteger getWritePriority() {
        return writePriority;
    }

    public SequenceOf<GroupChannelValue> getChangeList() {
        return changeList;
    }

    public Boolean getInhibitDelay() {
        return inhibitDelay;
    }

    @Override
    public void handle(LocalDevice localDevice, Address from) throws BACnetException {
        throw new NotImplementedException();
    }

    @Override
    public void write(ByteQueue queue) {
        write(queue, groupNumber, 0);
        write(queue, writePriority, 1);
        write(queue, changeList, 1);
        writeOptional(queue, inhibitDelay, 3);
    }

    WriteGroupRequest(ByteQueue queue) throws BACnetException {
        groupNumber = read(queue, Unsigned32.class, 0);
        writePriority = read(queue, UnsignedInteger.class, 1);
        changeList = readSequenceOf(queue, GroupChannelValue.class, 2);
        inhibitDelay = readOptional(queue, Boolean.class, 3);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((changeList == null) ? 0 : changeList.hashCode());
        result = prime * result + ((groupNumber == null) ? 0 : groupNumber.hashCode());
        result = prime * result + ((inhibitDelay == null) ? 0 : inhibitDelay.hashCode());
        result = prime * result + ((writePriority == null) ? 0 : writePriority.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WriteGroupRequest other = (WriteGroupRequest) obj;
        if (changeList == null) {
            if (other.changeList != null)
                return false;
        }
        else if (!changeList.equals(other.changeList))
            return false;
        if (groupNumber == null) {
            if (other.groupNumber != null)
                return false;
        }
        else if (!groupNumber.equals(other.groupNumber))
            return false;
        if (inhibitDelay == null) {
            if (other.inhibitDelay != null)
                return false;
        }
        else if (!inhibitDelay.equals(other.inhibitDelay))
            return false;
        if (writePriority == null) {
            if (other.writePriority != null)
                return false;
        }
        else if (!writePriority.equals(other.writePriority))
            return false;
        return true;
    }
}
