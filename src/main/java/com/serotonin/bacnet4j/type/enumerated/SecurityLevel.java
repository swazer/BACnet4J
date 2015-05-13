package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class SecurityLevel extends Enumerated {
    private static final long serialVersionUID = 4872601808833629487L;

    public static final SecurityLevel incapable = new SecurityLevel(0);
    public static final SecurityLevel plain = new SecurityLevel(1);
    public static final SecurityLevel signed = new SecurityLevel(2);
    public static final SecurityLevel encrypted = new SecurityLevel(3);
    public static final SecurityLevel signedEndToEnd = new SecurityLevel(4);
    public static final SecurityLevel encryptedEndToEnd = new SecurityLevel(5);

    public static final SecurityLevel[] ALL = { incapable, plain, signed, encrypted, signedEndToEnd, encryptedEndToEnd, };

    public SecurityLevel(int value) {
        super(value);
    }

    public SecurityLevel(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == incapable.intValue())
            return "incapable";
        if (type == plain.intValue())
            return "plain";
        if (type == signed.intValue())
            return "signed";
        if (type == encrypted.intValue())
            return "encrypted";
        if (type == signedEndToEnd.intValue())
            return "signedEndToEnd";
        if (type == encryptedEndToEnd.intValue())
            return "encryptedEndToEnd";
        return "Unknown(" + type + ")";
    }
}
