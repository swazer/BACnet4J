package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AccessCredentialDisableReason extends Enumerated {
    private static final long serialVersionUID = 5503231556407385733L;

    public static final AccessCredentialDisableReason disabled = new AccessCredentialDisableReason(0);
    public static final AccessCredentialDisableReason disabledNeedsProvisioning = new AccessCredentialDisableReason(1);
    public static final AccessCredentialDisableReason disabledUnassigned = new AccessCredentialDisableReason(2);
    public static final AccessCredentialDisableReason disabledNotYetActive = new AccessCredentialDisableReason(3);
    public static final AccessCredentialDisableReason disabledExpired = new AccessCredentialDisableReason(4);
    public static final AccessCredentialDisableReason disabledLockout = new AccessCredentialDisableReason(5);
    public static final AccessCredentialDisableReason disabledMaxDays = new AccessCredentialDisableReason(6);
    public static final AccessCredentialDisableReason disabledMaxUses = new AccessCredentialDisableReason(7);
    public static final AccessCredentialDisableReason disabledInactivity = new AccessCredentialDisableReason(8);
    public static final AccessCredentialDisableReason disabledManual = new AccessCredentialDisableReason(9);

    public static final AccessCredentialDisableReason[] ALL = { disabled, disabledNeedsProvisioning,
            disabledUnassigned, disabledNotYetActive, disabledExpired, disabledLockout, disabledMaxDays,
            disabledMaxUses, disabledInactivity, disabledManual, };

    public AccessCredentialDisableReason(int value) {
        super(value);
    }

    public AccessCredentialDisableReason(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == disabled.intValue())
            return "disabled";
        if (type == disabledNeedsProvisioning.intValue())
            return "disabledNeedsProvisioning";
        if (type == disabledUnassigned.intValue())
            return "disabledUnassigned";
        if (type == disabledNotYetActive.intValue())
            return "disabledNotYetActive";
        if (type == disabledExpired.intValue())
            return "disabledExpired";
        if (type == disabledLockout.intValue())
            return "disabledLockout";
        if (type == disabledMaxDays.intValue())
            return "disabledMaxDays";
        if (type == disabledMaxUses.intValue())
            return "disabledMaxUses";
        if (type == disabledInactivity.intValue())
            return "disabledInactivity";
        if (type == disabledManual.intValue())
            return "disabledManual";
        return "Unknown(" + type + ")";
    }
}
