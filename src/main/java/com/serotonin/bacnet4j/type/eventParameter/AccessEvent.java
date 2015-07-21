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
package com.serotonin.bacnet4j.type.eventParameter;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.constructed.DeviceObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AccessEvent extends EventParameter {
    private static final long serialVersionUID = -4567970907776185151L;

    public static final byte TYPE_ID = 13;

    private final SequenceOf<AccessEvent> listOfAccessEvents;
    private final DeviceObjectPropertyReference accessEventTimeReference;

    public AccessEvent(SequenceOf<AccessEvent> listOfAccessEvents,
            DeviceObjectPropertyReference accessEventTimeReference) {
        this.listOfAccessEvents = listOfAccessEvents;
        this.accessEventTimeReference = accessEventTimeReference;
    }

    @Override
    protected void writeImpl(ByteQueue queue) {
        write(queue, listOfAccessEvents, 0);
        write(queue, accessEventTimeReference, 1);
    }

    public AccessEvent(ByteQueue queue) throws BACnetException {
        listOfAccessEvents = readSequenceOf(queue, AccessEvent.class, 0);
        accessEventTimeReference = read(queue, DeviceObjectPropertyReference.class, 1);
    }

    @Override
    protected int getTypeId() {
        return TYPE_ID;
    }

    public SequenceOf<AccessEvent> getListOfAccessEvents() {
        return listOfAccessEvents;
    }

    public DeviceObjectPropertyReference getAccessEventTimeReference() {
        return accessEventTimeReference;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessEventTimeReference == null) ? 0 : accessEventTimeReference.hashCode());
        result = prime * result + ((listOfAccessEvents == null) ? 0 : listOfAccessEvents.hashCode());
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
        AccessEvent other = (AccessEvent) obj;
        if (accessEventTimeReference == null) {
            if (other.accessEventTimeReference != null)
                return false;
        }
        else if (!accessEventTimeReference.equals(other.accessEventTimeReference))
            return false;
        if (listOfAccessEvents == null) {
            if (other.listOfAccessEvents != null)
                return false;
        }
        else if (!listOfAccessEvents.equals(other.listOfAccessEvents))
            return false;
        return true;
    }
}
