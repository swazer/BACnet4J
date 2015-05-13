package com.serotonin.bacnet4j.obj.mixin;

import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.objectIdentifier;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.objectName;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.objectType;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.propertyList;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.obj.AbstractMixin;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;

public class PropertyListMixin extends AbstractMixin {
    public PropertyListMixin(BACnetObject bo) {
        super(bo);
    }

    @Override
    public boolean writeProperty(PropertyValue value) throws BACnetServiceException {
        if (propertyList.equals(value.getPropertyIdentifier()))
            throw new BACnetServiceException(ErrorClass.property, ErrorCode.writeAccessDenied);
        return false;
    }

    public void update() {
        List<PropertyIdentifier> pids = new ArrayList<PropertyIdentifier>();
        for (PropertyIdentifier p : properties().keySet()) {
            if (!p.isOneOf(objectName, objectType, objectIdentifier, propertyList))
                pids.add(p);
        }

        writePropertyImpl(propertyList, new SequenceOf<PropertyIdentifier>(pids));
    }
}
