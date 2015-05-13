package com.serotonin.bacnet4j.obj;

import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;

public interface BACnetObjectListener {
    void propertyChange(PropertyIdentifier pid, Encodable oldValue, Encodable newValue);
}
