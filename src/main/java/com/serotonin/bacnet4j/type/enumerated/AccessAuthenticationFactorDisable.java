package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AccessAuthenticationFactorDisable extends Enumerated {
    private static final long serialVersionUID = 3409339570414164518L;

    public static final AccessAuthenticationFactorDisable none = new AccessAuthenticationFactorDisable(0);
    public static final AccessAuthenticationFactorDisable disabled = new AccessAuthenticationFactorDisable(1);
    public static final AccessAuthenticationFactorDisable disabledLost = new AccessAuthenticationFactorDisable(2);
    public static final AccessAuthenticationFactorDisable disabledStolen = new AccessAuthenticationFactorDisable(3);
    public static final AccessAuthenticationFactorDisable disabledDamaged = new AccessAuthenticationFactorDisable(4);
    public static final AccessAuthenticationFactorDisable disabledDestroyed = new AccessAuthenticationFactorDisable(5);

    public static final AccessAuthenticationFactorDisable[] ALL = { none, disabled, disabledLost, disabledStolen,
            disabledDamaged, disabledDestroyed };

    public AccessAuthenticationFactorDisable(int value) {
        super(value);
    }

    public AccessAuthenticationFactorDisable(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == none.intValue())
            return "none";
        if (type == disabled.intValue())
            return "disabled";
        if (type == disabledLost.intValue())
            return "disabledLost";
        if (type == disabledStolen.intValue())
            return "disabledStolen";
        if (type == disabledDamaged.intValue())
            return "disabledDamaged";
        if (type == disabledDestroyed.intValue())
            return "disabledDestroyed";
        return "Unknown(" + type + ")";
    }
}
