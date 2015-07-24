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
package com.serotonin.bacnet4j.obj.mixin;

import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class ObjectCovSubscription {
    // Identifying properties.
    private final Address address;
    private final UnsignedInteger subscriberProcessIdentifier;
    private final PropertyIdentifier monitoredProperty; // Can be null.

    // Mutable properties.
    private boolean issueConfirmedNotifications;
    private long expiryTime;
    private Real covIncrement;

    // Runtime values.
    private Encodable lastCovIncrementValue;

    public ObjectCovSubscription(Address address, UnsignedInteger subscriberProcessIdentifier,
            PropertyIdentifier monitoredProperty) {
        this.address = address;
        this.subscriberProcessIdentifier = subscriberProcessIdentifier;
        this.monitoredProperty = monitoredProperty;
    }

    public Address getAddress() {
        return address;
    }

    public UnsignedInteger getSubscriberProcessIdentifier() {
        return subscriberProcessIdentifier;
    }

    public PropertyIdentifier getMonitoredProperty() {
        return monitoredProperty;
    }

    public boolean isIssueConfirmedNotifications() {
        return issueConfirmedNotifications;
    }

    public void setIssueConfirmedNotifications(boolean issueConfirmedNotifications) {
        this.issueConfirmedNotifications = issueConfirmedNotifications;
    }

    public Real getCovIncrement() {
        return covIncrement;
    }

    public void setCovIncrement(Real covIncrement) {
        this.covIncrement = covIncrement;
    }

    public void setExpiryTime(int seconds) {
        if (seconds == 0)
            expiryTime = -1;
        else
            expiryTime = System.currentTimeMillis() + seconds * 1000;
    }

    public boolean hasExpired(long now) {
        if (expiryTime == -1)
            return false;
        return expiryTime < now;
    }

    public Encodable getLastCovIncrementValue() {
        return lastCovIncrementValue;
    }

    public void setLastCovIncrementValue(Encodable lastCovIncrementValue) {
        this.lastCovIncrementValue = lastCovIncrementValue;
    }

    public int getTimeRemaining(long now) {
        if (expiryTime == -1)
            return 0;
        int left = (int) ((expiryTime - now) / 1000);
        if (left < 1)
            return 1;
        return left;
    }
}
