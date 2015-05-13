/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2006-2011 Serotonin Software Technologies Inc. http://serotoninsoftware.com
 * @author Matthew Lohbihler
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
 * When signing a commercial license with Serotonin Software Technologies Inc.,
 * the following extension to GPL is made. A special exception to the GPL is 
 * included to allow you to distribute a combined work that includes BAcnet4J 
 * without being obliged to provide the source code for any proprietary components.
 */
package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class ErrorCode extends Enumerated {
    private static final long serialVersionUID = -6627023845995429296L;

    public static final ErrorCode other = new ErrorCode(0);
    public static final ErrorCode configurationInProgress = new ErrorCode(2);
    public static final ErrorCode deviceBusy = new ErrorCode(3);
    public static final ErrorCode dynamicCreationNotSupported = new ErrorCode(4);
    public static final ErrorCode fileAccessDenied = new ErrorCode(5);
    public static final ErrorCode inconsistentParameters = new ErrorCode(7);
    public static final ErrorCode inconsistentSelectionCriterion = new ErrorCode(8);
    public static final ErrorCode invalidDataType = new ErrorCode(9);
    public static final ErrorCode invalidFileAccessMethod = new ErrorCode(10);
    public static final ErrorCode invalidFileStartPosition = new ErrorCode(11);
    public static final ErrorCode invalidParameterDataType = new ErrorCode(13);
    public static final ErrorCode invalidTimeStamp = new ErrorCode(14);
    public static final ErrorCode missingRequiredParameter = new ErrorCode(16);
    public static final ErrorCode noObjectsOfSpecifiedType = new ErrorCode(17);
    public static final ErrorCode noSpaceForObject = new ErrorCode(18);
    public static final ErrorCode noSpaceToAddListElement = new ErrorCode(19);
    public static final ErrorCode noSpaceToWriteProperty = new ErrorCode(20);
    public static final ErrorCode noVtSessionsAvailable = new ErrorCode(21);
    public static final ErrorCode propertyIsNotAList = new ErrorCode(22);
    public static final ErrorCode objectDeletionNotPermitted = new ErrorCode(23);
    public static final ErrorCode objectIdentifierAlreadyExists = new ErrorCode(24);
    public static final ErrorCode operationalProblem = new ErrorCode(25);
    public static final ErrorCode passwordFailure = new ErrorCode(26);
    public static final ErrorCode readAccessDenied = new ErrorCode(27);
    public static final ErrorCode serviceRequestDenied = new ErrorCode(29);
    public static final ErrorCode timeout = new ErrorCode(30);
    public static final ErrorCode unknownObject = new ErrorCode(31);
    public static final ErrorCode unknownProperty = new ErrorCode(32);
    public static final ErrorCode unknownVtClass = new ErrorCode(34);
    public static final ErrorCode unknownVtSession = new ErrorCode(35);
    public static final ErrorCode unsupportedObjectType = new ErrorCode(36);
    public static final ErrorCode valueOutOfRange = new ErrorCode(37);
    public static final ErrorCode vtSessionAlreadyClosed = new ErrorCode(38);
    public static final ErrorCode vtSessionTerminationFailure = new ErrorCode(39);
    public static final ErrorCode writeAccessDenied = new ErrorCode(40);
    public static final ErrorCode characterSetNotSupported = new ErrorCode(41);
    public static final ErrorCode invalidArrayIndex = new ErrorCode(42);
    public static final ErrorCode covSubscriptionFailed = new ErrorCode(43);
    public static final ErrorCode notCovProperty = new ErrorCode(44);
    public static final ErrorCode optionalFunctionalityNotSupported = new ErrorCode(45);
    public static final ErrorCode invalidConfigurationData = new ErrorCode(46);
    public static final ErrorCode datatypeNotSupported = new ErrorCode(47);
    public static final ErrorCode duplicateName = new ErrorCode(48);
    public static final ErrorCode duplicateObjectId = new ErrorCode(49);
    public static final ErrorCode propertyIsNotAnArray = new ErrorCode(50);
    public static final ErrorCode abortBufferOverflow = new ErrorCode(51);
    public static final ErrorCode abortInvalidApduInThisState = new ErrorCode(52);
    public static final ErrorCode abortPreemptedByHigherPriorityTask = new ErrorCode(53);
    public static final ErrorCode abortSegmentationNotSupported = new ErrorCode(54);
    public static final ErrorCode abortProprietary = new ErrorCode(55);
    public static final ErrorCode abortOther = new ErrorCode(56);
    public static final ErrorCode invalidTag = new ErrorCode(57);
    public static final ErrorCode networkDown = new ErrorCode(58);
    public static final ErrorCode rejectBufferOverflow = new ErrorCode(59);
    public static final ErrorCode rejectInconsistentParameters = new ErrorCode(60);
    public static final ErrorCode rejectInvalidParameterDataType = new ErrorCode(61);
    public static final ErrorCode rejectInvalidTag = new ErrorCode(62);
    public static final ErrorCode rejectMissingRequiredParameter = new ErrorCode(63);
    public static final ErrorCode rejectParameterOutOfRange = new ErrorCode(64);
    public static final ErrorCode rejectTooManyArguments = new ErrorCode(65);
    public static final ErrorCode rejectUndefinedEnumeration = new ErrorCode(66);
    public static final ErrorCode rejectUnrecognizedService = new ErrorCode(67);
    public static final ErrorCode rejectProprietary = new ErrorCode(68);
    public static final ErrorCode rejectOther = new ErrorCode(69);
    public static final ErrorCode unknownDevice = new ErrorCode(70);
    public static final ErrorCode unknownRoute = new ErrorCode(71);
    public static final ErrorCode valueNotInitialized = new ErrorCode(72);
    public static final ErrorCode invalidEventState = new ErrorCode(73);
    public static final ErrorCode noAlarmConfigured = new ErrorCode(74);
    public static final ErrorCode logBufferFull = new ErrorCode(75);
    public static final ErrorCode loggedValuePurged = new ErrorCode(76);
    public static final ErrorCode noPropertySpecified = new ErrorCode(77);
    public static final ErrorCode notConfiguredForTriggeredLogging = new ErrorCode(78);
    public static final ErrorCode unknownSubscription = new ErrorCode(79);
    public static final ErrorCode parameterOutOfRange = new ErrorCode(80);
    public static final ErrorCode listElementNotFound = new ErrorCode(81);
    public static final ErrorCode busy = new ErrorCode(82);
    public static final ErrorCode communicationDisabled = new ErrorCode(83);
    public static final ErrorCode success = new ErrorCode(84);
    public static final ErrorCode accessDenied = new ErrorCode(85);
    public static final ErrorCode badDestinationAddress = new ErrorCode(86);
    public static final ErrorCode badDestinationDeviceId = new ErrorCode(87);
    public static final ErrorCode badSignature = new ErrorCode(88);
    public static final ErrorCode badSourceAddress = new ErrorCode(89);
    public static final ErrorCode badTimestamp = new ErrorCode(90);
    public static final ErrorCode cannotUseKey = new ErrorCode(91);
    public static final ErrorCode cannotVerifyMessageId = new ErrorCode(92);
    public static final ErrorCode correctKeyRevision = new ErrorCode(93);
    public static final ErrorCode destinationDeviceIdRequired = new ErrorCode(94);
    public static final ErrorCode duplicateMessage = new ErrorCode(95);
    public static final ErrorCode encryptionNotConfigured = new ErrorCode(96);
    public static final ErrorCode encryptionRequired = new ErrorCode(97);
    public static final ErrorCode incorrectKey = new ErrorCode(98);
    public static final ErrorCode invalidKeyData = new ErrorCode(99);
    public static final ErrorCode keyUpdateInProgress = new ErrorCode(100);
    public static final ErrorCode malformedMessage = new ErrorCode(101);
    public static final ErrorCode notKeyServer = new ErrorCode(102);
    public static final ErrorCode securityNotConfigured = new ErrorCode(103);
    public static final ErrorCode sourceSecurityRequired = new ErrorCode(104);
    public static final ErrorCode tooManyKeys = new ErrorCode(105);
    public static final ErrorCode unknownAuthenticationType = new ErrorCode(106);
    public static final ErrorCode unknownKey = new ErrorCode(107);
    public static final ErrorCode unknownKeyRevision = new ErrorCode(108);
    public static final ErrorCode unknownSourceMessage = new ErrorCode(109);
    public static final ErrorCode notRouterToDnet = new ErrorCode(110);
    public static final ErrorCode routerBusy = new ErrorCode(111);
    public static final ErrorCode unknownNetworkMessage = new ErrorCode(112);
    public static final ErrorCode messageTooLong = new ErrorCode(113);
    public static final ErrorCode securityError = new ErrorCode(114);
    public static final ErrorCode addressingError = new ErrorCode(115);
    public static final ErrorCode writeBdtFailed = new ErrorCode(116);
    public static final ErrorCode readBdtFailed = new ErrorCode(117);
    public static final ErrorCode registerForeignDeviceFailed = new ErrorCode(118);
    public static final ErrorCode readFdtFailed = new ErrorCode(119);
    public static final ErrorCode deleteFdtEntryFailed = new ErrorCode(120);
    public static final ErrorCode distributeBroadcastFailed = new ErrorCode(121);
    public static final ErrorCode unknownFileSize = new ErrorCode(122);
    public static final ErrorCode abortApduTooLong = new ErrorCode(123);
    public static final ErrorCode abortApplicationExceededReplyTime = new ErrorCode(124);
    public static final ErrorCode abortOutOfResources = new ErrorCode(125);
    public static final ErrorCode abortTsmTimeout = new ErrorCode(126);
    public static final ErrorCode abortWindowSizeOutOfRange = new ErrorCode(127);
    public static final ErrorCode fileFull = new ErrorCode(128);
    public static final ErrorCode inconsistentConfiguration = new ErrorCode(129);
    public static final ErrorCode inconsistentObjectType = new ErrorCode(130);
    public static final ErrorCode internalError = new ErrorCode(131);
    public static final ErrorCode notConfigured = new ErrorCode(132);
    public static final ErrorCode outOfMemory = new ErrorCode(133);
    public static final ErrorCode valueTooLong = new ErrorCode(134);
    public static final ErrorCode abortInsufficientSecurity = new ErrorCode(135);
    public static final ErrorCode abortSecurityError = new ErrorCode(136);

    public static final ErrorCode[] ALL = { other, configurationInProgress, deviceBusy, dynamicCreationNotSupported,
            fileAccessDenied, inconsistentParameters, inconsistentSelectionCriterion, invalidDataType,
            invalidFileAccessMethod, invalidFileStartPosition, invalidParameterDataType, invalidTimeStamp,
            missingRequiredParameter, noObjectsOfSpecifiedType, noSpaceForObject, noSpaceToAddListElement,
            noSpaceToWriteProperty, noVtSessionsAvailable, propertyIsNotAList, objectDeletionNotPermitted,
            objectIdentifierAlreadyExists, operationalProblem, passwordFailure, readAccessDenied, serviceRequestDenied,
            timeout, unknownObject, unknownProperty, unknownVtClass, unknownVtSession, unsupportedObjectType,
            valueOutOfRange, vtSessionAlreadyClosed, vtSessionTerminationFailure, writeAccessDenied,
            characterSetNotSupported, invalidArrayIndex, covSubscriptionFailed, notCovProperty,
            optionalFunctionalityNotSupported, invalidConfigurationData, datatypeNotSupported, duplicateName,
            duplicateObjectId, propertyIsNotAnArray, abortBufferOverflow, abortInvalidApduInThisState,
            abortPreemptedByHigherPriorityTask, abortSegmentationNotSupported, abortProprietary, abortOther,
            invalidTag, networkDown, rejectBufferOverflow, rejectInconsistentParameters,
            rejectInvalidParameterDataType, rejectInvalidTag, rejectMissingRequiredParameter,
            rejectParameterOutOfRange, rejectTooManyArguments, rejectUndefinedEnumeration, rejectUnrecognizedService,
            rejectProprietary, rejectOther, unknownDevice, unknownRoute, valueNotInitialized, invalidEventState,
            noAlarmConfigured, logBufferFull, loggedValuePurged, noPropertySpecified, notConfiguredForTriggeredLogging,
            unknownSubscription, parameterOutOfRange, listElementNotFound, busy, communicationDisabled, success,
            accessDenied, badDestinationAddress, badDestinationDeviceId, badSignature, badSourceAddress, badTimestamp,
            cannotUseKey, cannotVerifyMessageId, correctKeyRevision, destinationDeviceIdRequired, duplicateMessage,
            encryptionNotConfigured, encryptionRequired, incorrectKey, invalidKeyData, keyUpdateInProgress,
            malformedMessage, notKeyServer, securityNotConfigured, sourceSecurityRequired, tooManyKeys,
            unknownAuthenticationType, unknownKey, unknownKeyRevision, unknownSourceMessage, notRouterToDnet,
            routerBusy, unknownNetworkMessage, messageTooLong, securityError, addressingError, writeBdtFailed,
            readBdtFailed, registerForeignDeviceFailed, readFdtFailed, deleteFdtEntryFailed, distributeBroadcastFailed,
            unknownFileSize, abortApduTooLong, abortApplicationExceededReplyTime, abortOutOfResources, abortTsmTimeout,
            abortWindowSizeOutOfRange, fileFull, inconsistentConfiguration, inconsistentObjectType, internalError,
            notConfigured, outOfMemory, valueTooLong, abortInsufficientSecurity, abortSecurityError, };

    public ErrorCode(int value) {
        super(value);
    }

    public ErrorCode(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == other.intValue())
            return "other";
        if (type == configurationInProgress.intValue())
            return "configurationInProgress";
        if (type == deviceBusy.intValue())
            return "deviceBusy";
        if (type == dynamicCreationNotSupported.intValue())
            return "dynamicCreationNotSupported";
        if (type == fileAccessDenied.intValue())
            return "fileAccessDenied";
        if (type == inconsistentParameters.intValue())
            return "inconsistentParameters";
        if (type == inconsistentSelectionCriterion.intValue())
            return "inconsistentSelectionCriterion";
        if (type == invalidDataType.intValue())
            return "invalidDataType";
        if (type == invalidFileAccessMethod.intValue())
            return "invalidFileAccessMethod";
        if (type == invalidFileStartPosition.intValue())
            return "invalidFileStartPosition";
        if (type == invalidParameterDataType.intValue())
            return "invalidParameterDataType";
        if (type == invalidTimeStamp.intValue())
            return "invalidTimeStamp";
        if (type == missingRequiredParameter.intValue())
            return "missingRequiredParameter";
        if (type == noObjectsOfSpecifiedType.intValue())
            return "noObjectsOfSpecifiedType";
        if (type == noSpaceForObject.intValue())
            return "noSpaceForObject";
        if (type == noSpaceToAddListElement.intValue())
            return "noSpaceToAddListElement";
        if (type == noSpaceToWriteProperty.intValue())
            return "noSpaceToWriteProperty";
        if (type == noVtSessionsAvailable.intValue())
            return "noVtSessionsAvailable";
        if (type == propertyIsNotAList.intValue())
            return "propertyIsNotAList";
        if (type == objectDeletionNotPermitted.intValue())
            return "objectDeletionNotPermitted";
        if (type == objectIdentifierAlreadyExists.intValue())
            return "objectIdentifierAlreadyExists";
        if (type == operationalProblem.intValue())
            return "operationalProblem";
        if (type == passwordFailure.intValue())
            return "passwordFailure";
        if (type == readAccessDenied.intValue())
            return "readAccessDenied";
        if (type == serviceRequestDenied.intValue())
            return "serviceRequestDenied";
        if (type == timeout.intValue())
            return "timeout";
        if (type == unknownObject.intValue())
            return "unknownObject";
        if (type == unknownProperty.intValue())
            return "unknownProperty";
        if (type == unknownVtClass.intValue())
            return "unknownVtClass";
        if (type == unknownVtSession.intValue())
            return "unknownVtSession";
        if (type == unsupportedObjectType.intValue())
            return "unsupportedObjectType";
        if (type == valueOutOfRange.intValue())
            return "valueOutOfRange";
        if (type == vtSessionAlreadyClosed.intValue())
            return "vtSessionAlreadyClosed";
        if (type == vtSessionTerminationFailure.intValue())
            return "vtSessionTerminationFailure";
        if (type == writeAccessDenied.intValue())
            return "writeAccessDenied";
        if (type == characterSetNotSupported.intValue())
            return "characterSetNotSupported";
        if (type == invalidArrayIndex.intValue())
            return "invalidArrayIndex";
        if (type == covSubscriptionFailed.intValue())
            return "covSubscriptionFailed";
        if (type == notCovProperty.intValue())
            return "notCovProperty";
        if (type == optionalFunctionalityNotSupported.intValue())
            return "optionalFunctionalityNotSupported";
        if (type == invalidConfigurationData.intValue())
            return "invalidConfigurationData";
        if (type == datatypeNotSupported.intValue())
            return "datatypeNotSupported";
        if (type == duplicateName.intValue())
            return "duplicateName";
        if (type == duplicateObjectId.intValue())
            return "duplicateObjectId";
        if (type == propertyIsNotAnArray.intValue())
            return "propertyIsNotAnArray";
        if (type == abortBufferOverflow.intValue())
            return "abortBufferOverflow";
        if (type == abortInvalidApduInThisState.intValue())
            return "abortInvalidApduInThisState";
        if (type == abortPreemptedByHigherPriorityTask.intValue())
            return "abortPreemptedByHigherPriorityTask";
        if (type == abortSegmentationNotSupported.intValue())
            return "abortSegmentationNotSupported";
        if (type == abortProprietary.intValue())
            return "abortProprietary";
        if (type == abortOther.intValue())
            return "abortOther";
        if (type == invalidTag.intValue())
            return "invalidTag";
        if (type == networkDown.intValue())
            return "networkDown";
        if (type == rejectBufferOverflow.intValue())
            return "rejectBufferOverflow";
        if (type == rejectInconsistentParameters.intValue())
            return "rejectInconsistentParameters";
        if (type == rejectInvalidParameterDataType.intValue())
            return "rejectInvalidParameterDataType";
        if (type == rejectInvalidTag.intValue())
            return "rejectInvalidTag";
        if (type == rejectMissingRequiredParameter.intValue())
            return "rejectMissingRequiredParameter";
        if (type == rejectParameterOutOfRange.intValue())
            return "rejectParameterOutOfRange";
        if (type == rejectTooManyArguments.intValue())
            return "rejectTooManyArguments";
        if (type == rejectUndefinedEnumeration.intValue())
            return "rejectUndefinedEnumeration";
        if (type == rejectUnrecognizedService.intValue())
            return "rejectUnrecognizedService";
        if (type == rejectProprietary.intValue())
            return "rejectProprietary";
        if (type == rejectOther.intValue())
            return "rejectOther";
        if (type == unknownDevice.intValue())
            return "unknownDevice";
        if (type == unknownRoute.intValue())
            return "unknownRoute";
        if (type == valueNotInitialized.intValue())
            return "valueNotInitialized";
        if (type == invalidEventState.intValue())
            return "invalidEventState";
        if (type == noAlarmConfigured.intValue())
            return "noAlarmConfigured";
        if (type == logBufferFull.intValue())
            return "logBufferFull";
        if (type == loggedValuePurged.intValue())
            return "loggedValuePurged";
        if (type == noPropertySpecified.intValue())
            return "noPropertySpecified";
        if (type == notConfiguredForTriggeredLogging.intValue())
            return "notConfiguredForTriggeredLogging";
        if (type == unknownSubscription.intValue())
            return "unknownSubscription";
        if (type == parameterOutOfRange.intValue())
            return "parameterOutOfRange";
        if (type == listElementNotFound.intValue())
            return "listElementNotFound";
        if (type == busy.intValue())
            return "busy";
        if (type == communicationDisabled.intValue())
            return "communicationDisabled";
        if (type == success.intValue())
            return "success";
        if (type == accessDenied.intValue())
            return "accessDenied";
        if (type == badDestinationAddress.intValue())
            return "badDestinationAddress";
        if (type == badDestinationDeviceId.intValue())
            return "badDestinationDeviceId";
        if (type == badSignature.intValue())
            return "badSignature";
        if (type == badSourceAddress.intValue())
            return "badSourceAddress";
        if (type == badTimestamp.intValue())
            return "badTimestamp";
        if (type == cannotUseKey.intValue())
            return "cannotUseKey";
        if (type == cannotVerifyMessageId.intValue())
            return "cannotVerifyMessageId";
        if (type == correctKeyRevision.intValue())
            return "correctKeyRevision";
        if (type == destinationDeviceIdRequired.intValue())
            return "destinationDeviceIdRequired";
        if (type == duplicateMessage.intValue())
            return "duplicateMessage";
        if (type == encryptionNotConfigured.intValue())
            return "encryptionNotConfigured";
        if (type == encryptionRequired.intValue())
            return "encryptionRequired";
        if (type == incorrectKey.intValue())
            return "incorrectKey";
        if (type == invalidKeyData.intValue())
            return "invalidKeyData";
        if (type == keyUpdateInProgress.intValue())
            return "keyUpdateInProgress";
        if (type == malformedMessage.intValue())
            return "malformedMessage";
        if (type == notKeyServer.intValue())
            return "notKeyServer";
        if (type == securityNotConfigured.intValue())
            return "securityNotConfigured";
        if (type == sourceSecurityRequired.intValue())
            return "sourceSecurityRequired";
        if (type == tooManyKeys.intValue())
            return "tooManyKeys";
        if (type == unknownAuthenticationType.intValue())
            return "unknownAuthenticationType";
        if (type == unknownKey.intValue())
            return "unknownKey";
        if (type == unknownKeyRevision.intValue())
            return "unknownKeyRevision";
        if (type == unknownSourceMessage.intValue())
            return "unknownSourceMessage";
        if (type == notRouterToDnet.intValue())
            return "notRouterToDnet";
        if (type == routerBusy.intValue())
            return "routerBusy";
        if (type == unknownNetworkMessage.intValue())
            return "unknownNetworkMessage";
        if (type == messageTooLong.intValue())
            return "messageTooLong";
        if (type == securityError.intValue())
            return "securityError";
        if (type == addressingError.intValue())
            return "addressingError";
        if (type == writeBdtFailed.intValue())
            return "writeBdtFailed";
        if (type == readBdtFailed.intValue())
            return "readBdtFailed";
        if (type == registerForeignDeviceFailed.intValue())
            return "registerForeignDeviceFailed";
        if (type == readFdtFailed.intValue())
            return "readFdtFailed";
        if (type == deleteFdtEntryFailed.intValue())
            return "deleteFdtEntryFailed";
        if (type == distributeBroadcastFailed.intValue())
            return "distributeBroadcastFailed";
        if (type == unknownFileSize.intValue())
            return "unknownFileSize";
        if (type == abortApduTooLong.intValue())
            return "abortApduTooLong";
        if (type == abortApplicationExceededReplyTime.intValue())
            return "abortApplicationExceededReplyTime";
        if (type == abortOutOfResources.intValue())
            return "abortOutOfResources";
        if (type == abortTsmTimeout.intValue())
            return "abortTsmTimeout";
        if (type == abortWindowSizeOutOfRange.intValue())
            return "abortWindowSizeOutOfRange";
        if (type == fileFull.intValue())
            return "fileFull";
        if (type == inconsistentConfiguration.intValue())
            return "inconsistentConfiguration";
        if (type == inconsistentObjectType.intValue())
            return "inconsistentObjectType";
        if (type == internalError.intValue())
            return "internalError";
        if (type == notConfigured.intValue())
            return "notConfigured";
        if (type == outOfMemory.intValue())
            return "outOfMemory";
        if (type == valueTooLong.intValue())
            return "valueTooLong";
        if (type == abortInsufficientSecurity.intValue())
            return "abortInsufficientSecurity";
        if (type == abortSecurityError.intValue())
            return "abortSecurityError";
        return "Unknown: " + type;
    }
}
