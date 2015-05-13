package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AccessUserType extends Enumerated {
    private static final long serialVersionUID = -8774935820842554578L;

    public static final AccessUserType asset = new AccessUserType(0);
    public static final AccessUserType group = new AccessUserType(1);
    public static final AccessUserType person = new AccessUserType(2);

    public static final AccessUserType[] ALL = { asset, group, person, };

    public AccessUserType(int value) {
        super(value);
    }

    public AccessUserType(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == asset.intValue())
            return "asset";
        if (type == group.intValue())
            return "group";
        if (type == person.intValue())
            return "person";
        return "Unknown(" + type + ")";
    }
}
