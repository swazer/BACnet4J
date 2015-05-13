package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AccessEvent extends Enumerated {
    private static final long serialVersionUID = -7727581470800473291L;

    public static final AccessEvent none = new AccessEvent(0);
    public static final AccessEvent granted = new AccessEvent(1);
    public static final AccessEvent muster = new AccessEvent(2);
    public static final AccessEvent passbackDetected = new AccessEvent(3);
    public static final AccessEvent duress = new AccessEvent(4);
    public static final AccessEvent trace = new AccessEvent(5);
    public static final AccessEvent lockoutMaxAttempts = new AccessEvent(6);
    public static final AccessEvent lockoutOther = new AccessEvent(7);
    public static final AccessEvent lockoutRelinquished = new AccessEvent(8);
    public static final AccessEvent lockedByHigherPriority = new AccessEvent(9);
    public static final AccessEvent outOfService = new AccessEvent(10);
    public static final AccessEvent outOfServiceRelinquished = new AccessEvent(11);
    public static final AccessEvent accompanimentBy = new AccessEvent(12);
    public static final AccessEvent authenticationFactorRead = new AccessEvent(13);
    public static final AccessEvent authorizationDelayed = new AccessEvent(14);
    public static final AccessEvent verificationRequired = new AccessEvent(15);
    public static final AccessEvent noEntryAfterGrant = new AccessEvent(16);
    public static final AccessEvent deniedDenyAll = new AccessEvent(128);
    public static final AccessEvent deniedUnknownCredential = new AccessEvent(129);
    public static final AccessEvent deniedAuthenticationUnavailable = new AccessEvent(130);
    public static final AccessEvent deniedAuthenticationFactorTimeout = new AccessEvent(131);
    public static final AccessEvent deniedIncorrectAuthenticationFactor = new AccessEvent(132);
    public static final AccessEvent deniedZoneNoAccessRights = new AccessEvent(133);
    public static final AccessEvent deniedPointNoAccessRights = new AccessEvent(134);
    public static final AccessEvent deniedNoAccessRights = new AccessEvent(135);
    public static final AccessEvent deniedOutOfTimeRange = new AccessEvent(136);
    public static final AccessEvent deniedThreatLevel = new AccessEvent(137);
    public static final AccessEvent deniedPassback = new AccessEvent(138);
    public static final AccessEvent deniedUnexpectedLocationUsage = new AccessEvent(139);
    public static final AccessEvent deniedMaxAttempts = new AccessEvent(140);
    public static final AccessEvent deniedLowerOccupancyLimit = new AccessEvent(141);
    public static final AccessEvent deniedUpperOccupancyLimit = new AccessEvent(142);
    public static final AccessEvent deniedAuthenticationFactorLost = new AccessEvent(143);
    public static final AccessEvent deniedAuthenticationFactorStolen = new AccessEvent(144);
    public static final AccessEvent deniedAuthenticationFactorDamaged = new AccessEvent(145);
    public static final AccessEvent deniedAuthenticationFactorDestroyed = new AccessEvent(146);
    public static final AccessEvent deniedAuthenticationFactorDisabled = new AccessEvent(147);
    public static final AccessEvent deniedAuthenticationFactorError = new AccessEvent(148);
    public static final AccessEvent deniedCredentialUnassigned = new AccessEvent(149);
    public static final AccessEvent deniedCredentialNotProvisioned = new AccessEvent(150);
    public static final AccessEvent deniedCredentialNotYetActive = new AccessEvent(151);
    public static final AccessEvent deniedCredentialExpired = new AccessEvent(152);
    public static final AccessEvent deniedCredentialManualDisable = new AccessEvent(153);
    public static final AccessEvent deniedCredentialLockout = new AccessEvent(154);
    public static final AccessEvent deniedCredentialMaxDays = new AccessEvent(155);
    public static final AccessEvent deniedCredentialMaxUses = new AccessEvent(156);
    public static final AccessEvent deniedCredentialInactivity = new AccessEvent(157);
    public static final AccessEvent deniedCredentialDisabled = new AccessEvent(158);
    public static final AccessEvent deniedNoAccompaniment = new AccessEvent(159);
    public static final AccessEvent deniedIncorrectAccompaniment = new AccessEvent(160);
    public static final AccessEvent deniedLockout = new AccessEvent(161);
    public static final AccessEvent deniedVerificationFailed = new AccessEvent(162);
    public static final AccessEvent deniedVerificationTimeout = new AccessEvent(163);
    public static final AccessEvent deniedOther = new AccessEvent(164);

    public static final AccessEvent[] ALL = { none, granted, muster, passbackDetected, duress, trace,
            lockoutMaxAttempts, lockoutOther, lockoutRelinquished, lockedByHigherPriority, outOfService,
            outOfServiceRelinquished, accompanimentBy, authenticationFactorRead, authorizationDelayed,
            verificationRequired, noEntryAfterGrant, deniedDenyAll, deniedUnknownCredential,
            deniedAuthenticationUnavailable, deniedAuthenticationFactorTimeout, deniedIncorrectAuthenticationFactor,
            deniedZoneNoAccessRights, deniedPointNoAccessRights, deniedNoAccessRights, deniedOutOfTimeRange,
            deniedThreatLevel, deniedPassback, deniedUnexpectedLocationUsage, deniedMaxAttempts,
            deniedLowerOccupancyLimit, deniedUpperOccupancyLimit, deniedAuthenticationFactorLost,
            deniedAuthenticationFactorStolen, deniedAuthenticationFactorDamaged, deniedAuthenticationFactorDestroyed,
            deniedAuthenticationFactorDisabled, deniedAuthenticationFactorError, deniedCredentialUnassigned,
            deniedCredentialNotProvisioned, deniedCredentialNotYetActive, deniedCredentialExpired,
            deniedCredentialManualDisable, deniedCredentialLockout, deniedCredentialMaxDays, deniedCredentialMaxUses,
            deniedCredentialInactivity, deniedCredentialDisabled, deniedNoAccompaniment, deniedIncorrectAccompaniment,
            deniedLockout, deniedVerificationFailed, deniedVerificationTimeout, deniedOther, };

    public AccessEvent(int value) {
        super(value);
    }

    public AccessEvent(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == none.intValue())
            return "none";
        if (type == granted.intValue())
            return "granted";
        if (type == muster.intValue())
            return "muster";
        if (type == passbackDetected.intValue())
            return "passbackDetected";
        if (type == duress.intValue())
            return "duress";
        if (type == trace.intValue())
            return "trace";
        if (type == lockoutMaxAttempts.intValue())
            return "lockoutMaxAttempts";
        if (type == lockoutOther.intValue())
            return "lockoutOther";
        if (type == lockoutRelinquished.intValue())
            return "lockoutRelinquished";
        if (type == lockedByHigherPriority.intValue())
            return "lockedByHigherPriority";
        if (type == outOfService.intValue())
            return "outOfService";
        if (type == outOfServiceRelinquished.intValue())
            return "outOfServiceRelinquished";
        if (type == accompanimentBy.intValue())
            return "accompanimentBy";
        if (type == authenticationFactorRead.intValue())
            return "authenticationFactorRead";
        if (type == authorizationDelayed.intValue())
            return "authorizationDelayed";
        if (type == verificationRequired.intValue())
            return "verificationRequired";
        if (type == noEntryAfterGrant.intValue())
            return "noEntryAfterGrant";
        if (type == deniedDenyAll.intValue())
            return "deniedDenyAll";
        if (type == deniedUnknownCredential.intValue())
            return "deniedUnknownCredential";
        if (type == deniedAuthenticationUnavailable.intValue())
            return "deniedAuthenticationUnavailable";
        if (type == deniedAuthenticationFactorTimeout.intValue())
            return "deniedAuthenticationFactorTimeout";
        if (type == deniedIncorrectAuthenticationFactor.intValue())
            return "deniedIncorrectAuthenticationFactor";
        if (type == deniedZoneNoAccessRights.intValue())
            return "deniedZoneNoAccessRights";
        if (type == deniedPointNoAccessRights.intValue())
            return "deniedPointNoAccessRights";
        if (type == deniedNoAccessRights.intValue())
            return "deniedNoAccessRights";
        if (type == deniedOutOfTimeRange.intValue())
            return "deniedOutOfTimeRange";
        if (type == deniedThreatLevel.intValue())
            return "deniedThreatLevel";
        if (type == deniedPassback.intValue())
            return "deniedPassback";
        if (type == deniedUnexpectedLocationUsage.intValue())
            return "deniedUnexpectedLocationUsage";
        if (type == deniedMaxAttempts.intValue())
            return "deniedMaxAttempts";
        if (type == deniedLowerOccupancyLimit.intValue())
            return "deniedLowerOccupancyLimit";
        if (type == deniedUpperOccupancyLimit.intValue())
            return "deniedUpperOccupancyLimit";
        if (type == deniedAuthenticationFactorLost.intValue())
            return "deniedAuthenticationFactorLost";
        if (type == deniedAuthenticationFactorStolen.intValue())
            return "deniedAuthenticationFactorStolen";
        if (type == deniedAuthenticationFactorDamaged.intValue())
            return "deniedAuthenticationFactorDamaged";
        if (type == deniedAuthenticationFactorDestroyed.intValue())
            return "deniedAuthenticationFactorDestroyed";
        if (type == deniedAuthenticationFactorDisabled.intValue())
            return "deniedAuthenticationFactorDisabled";
        if (type == deniedAuthenticationFactorError.intValue())
            return "deniedAuthenticationFactorError";
        if (type == deniedCredentialUnassigned.intValue())
            return "deniedCredentialUnassigned";
        if (type == deniedCredentialNotProvisioned.intValue())
            return "deniedCredentialNotProvisioned";
        if (type == deniedCredentialNotYetActive.intValue())
            return "deniedCredentialNotYetActive";
        if (type == deniedCredentialExpired.intValue())
            return "deniedCredentialExpired";
        if (type == deniedCredentialManualDisable.intValue())
            return "deniedCredentialManualDisable";
        if (type == deniedCredentialLockout.intValue())
            return "deniedCredentialLockout";
        if (type == deniedCredentialMaxDays.intValue())
            return "deniedCredentialMaxDays";
        if (type == deniedCredentialMaxUses.intValue())
            return "deniedCredentialMaxUses";
        if (type == deniedCredentialInactivity.intValue())
            return "deniedCredentialInactivity";
        if (type == deniedCredentialDisabled.intValue())
            return "deniedCredentialDisabled";
        if (type == deniedNoAccompaniment.intValue())
            return "deniedNoAccompaniment";
        if (type == deniedIncorrectAccompaniment.intValue())
            return "deniedIncorrectAccompaniment";
        if (type == deniedLockout.intValue())
            return "deniedLockout";
        if (type == deniedVerificationFailed.intValue())
            return "deniedVerificationFailed";
        if (type == deniedVerificationTimeout.intValue())
            return "deniedVerificationTimeout";
        if (type == deniedOther.intValue())
            return "deniedOther";
        return "Unknown(" + type + ")";
    }
}
