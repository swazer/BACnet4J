package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AccessCredentialDisable extends Enumerated {
    private static final long serialVersionUID = -2484224934687566191L;

    public static final AccessCredentialDisable none = new AccessCredentialDisable(0);
    public static final AccessCredentialDisable disable = new AccessCredentialDisable(1);
    public static final AccessCredentialDisable disableManual = new AccessCredentialDisable(2);
    public static final AccessCredentialDisable disableLockout = new AccessCredentialDisable(3);

    public static final AccessCredentialDisable[] ALL = { none, disable, disableManual, disableLockout, };

    public AccessCredentialDisable(int value) {
        super(value);
    }

    public AccessCredentialDisable(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == none.intValue())
            return "none";
        if (type == disable.intValue())
            return "disable";
        if (type == disableManual.intValue())
            return "disableManual";
        if (type == disableLockout.intValue())
            return "disableLockout";
        return "Unknown(" + type + ")";
    }
}
