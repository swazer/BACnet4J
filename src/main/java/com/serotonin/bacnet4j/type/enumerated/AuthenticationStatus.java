package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AuthenticationStatus extends Enumerated {
    private static final long serialVersionUID = -9153277407804672743L;

    public static final AuthenticationStatus notReady = new AuthenticationStatus(0);
    public static final AuthenticationStatus ready = new AuthenticationStatus(1);
    public static final AuthenticationStatus disabled = new AuthenticationStatus(2);
    public static final AuthenticationStatus waitingForAuthenticationFactor = new AuthenticationStatus(3);
    public static final AuthenticationStatus waitingForAccompaniment = new AuthenticationStatus(4);
    public static final AuthenticationStatus waitingForVerification = new AuthenticationStatus(5);
    public static final AuthenticationStatus inProgress = new AuthenticationStatus(6);

    public static final AuthenticationStatus[] ALL = { notReady, ready, disabled, waitingForAuthenticationFactor,
            waitingForAccompaniment, waitingForVerification, inProgress, };

    public AuthenticationStatus(int value) {
        super(value);
    }

    public AuthenticationStatus(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == notReady.intValue())
            return "notReady";
        if (type == ready.intValue())
            return "ready";
        if (type == disabled.intValue())
            return "disabled";
        if (type == waitingForAuthenticationFactor.intValue())
            return "waitingForAuthenticationFactor";
        if (type == waitingForAccompaniment.intValue())
            return "waitingForAccompaniment";
        if (type == waitingForVerification.intValue())
            return "waitingForVerification";
        if (type == inProgress.intValue())
            return "inProgress";
        return "Unknown(" + type + ")";
    }
}
