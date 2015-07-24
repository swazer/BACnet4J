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
package com.serotonin.bacnet4j.type.constructed;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class PriorityArray extends SequenceOf<PriorityValue> {
    private static final long serialVersionUID = 8292702351986751796L;
    private static final int LENGTH = 16;

    public PriorityArray() {
        super(new ArrayList<PriorityValue>());
        ensureLength();
    }

    public PriorityArray(List<PriorityValue> priorityValues) {
        super(priorityValues);
        ensureLength();
    }

    public PriorityArray(ByteQueue queue, int contextId) throws BACnetException {
        super(queue, PriorityValue.class, contextId);
        ensureLength();
    }

    private void ensureLength() {
        while (getCount() < LENGTH)
            super.add(new PriorityValue(new Null()));
        while (getCount() > LENGTH)
            super.remove(getCount());
    }

    public PriorityArray put(int indexBase1, Encodable value) {
        set(indexBase1, new PriorityValue(value));
        return this;
    }

    @Override
    public void set(int indexBase1, PriorityValue value) {
        if (indexBase1 < 1 || indexBase1 > LENGTH)
            throw new RuntimeException("Invalid priority value");
        if (value == null)
            value = new PriorityValue(new Null());
        super.set(indexBase1, value);
    }

    @Override
    public void add(PriorityValue value) {
        throw new RuntimeException("Use set method instead");
    }

    @Override
    public void remove(int indexBase1) {
        throw new RuntimeException("Use set method instead");
    }

    @Override
    public void remove(PriorityValue value) {
        throw new RuntimeException("Use set method instead");
    }

    @Override
    public void removeAll(PriorityValue value) {
        throw new RuntimeException("Use set method instead");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PriorityArray[");

        boolean first = true;
        for (int i = 1; i < LENGTH + 1; i++) {
            Encodable e = get(i).getValue();
            if (!(e instanceof Null)) {
                if (first)
                    first = false;
                else
                    sb.append(',');
                sb.append(i).append('=').append(e);
            }
        }

        sb.append("]");
        return sb.toString();
    }
}
