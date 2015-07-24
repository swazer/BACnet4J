/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
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
 * When signing a commercial license with Infinite Automation Software,
 * the following extension to GPL is made. A special exception to the GPL is
 * included to allow you to distribute a combined work that includes BAcnet4J
 * without being obliged to provide the source code for any proprietary components.
 *
 * See www.infiniteautomation.com for commercial license options.
 * 
 * @author Matthew Lohbihler
 */
package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class PropertyIdentifier extends Enumerated {
    private static final long serialVersionUID = 7289026444729646422L;
    public static final PropertyIdentifier ackedTransitions = new PropertyIdentifier(0);
    public static final PropertyIdentifier ackRequired = new PropertyIdentifier(1);
    public static final PropertyIdentifier action = new PropertyIdentifier(2);
    public static final PropertyIdentifier actionText = new PropertyIdentifier(3);
    public static final PropertyIdentifier activeText = new PropertyIdentifier(4);
    public static final PropertyIdentifier activeVtSessions = new PropertyIdentifier(5);
    public static final PropertyIdentifier alarmValue = new PropertyIdentifier(6);
    public static final PropertyIdentifier alarmValues = new PropertyIdentifier(7);
    public static final PropertyIdentifier all = new PropertyIdentifier(8);
    public static final PropertyIdentifier allWritesSuccessful = new PropertyIdentifier(9);
    public static final PropertyIdentifier apduSegmentTimeout = new PropertyIdentifier(10);
    public static final PropertyIdentifier apduTimeout = new PropertyIdentifier(11);
    public static final PropertyIdentifier applicationSoftwareVersion = new PropertyIdentifier(12);
    public static final PropertyIdentifier archive = new PropertyIdentifier(13);
    public static final PropertyIdentifier bias = new PropertyIdentifier(14);
    public static final PropertyIdentifier changeOfStateCount = new PropertyIdentifier(15);
    public static final PropertyIdentifier changeOfStateTime = new PropertyIdentifier(16);
    public static final PropertyIdentifier notificationClass = new PropertyIdentifier(17);
    public static final PropertyIdentifier controlledVariableReference = new PropertyIdentifier(19);
    public static final PropertyIdentifier controlledVariableUnits = new PropertyIdentifier(20);
    public static final PropertyIdentifier controlledVariableValue = new PropertyIdentifier(21);
    public static final PropertyIdentifier covIncrement = new PropertyIdentifier(22);
    public static final PropertyIdentifier dateList = new PropertyIdentifier(23);
    public static final PropertyIdentifier daylightSavingsStatus = new PropertyIdentifier(24);
    public static final PropertyIdentifier deadband = new PropertyIdentifier(25);
    public static final PropertyIdentifier derivativeConstant = new PropertyIdentifier(26);
    public static final PropertyIdentifier derivativeConstantUnits = new PropertyIdentifier(27);
    public static final PropertyIdentifier description = new PropertyIdentifier(28);
    public static final PropertyIdentifier descriptionOfHalt = new PropertyIdentifier(29);
    public static final PropertyIdentifier deviceAddressBinding = new PropertyIdentifier(30);
    public static final PropertyIdentifier deviceType = new PropertyIdentifier(31);
    public static final PropertyIdentifier effectivePeriod = new PropertyIdentifier(32);
    public static final PropertyIdentifier elapsedActiveTime = new PropertyIdentifier(33);
    public static final PropertyIdentifier errorLimit = new PropertyIdentifier(34);
    public static final PropertyIdentifier eventEnable = new PropertyIdentifier(35);
    public static final PropertyIdentifier eventState = new PropertyIdentifier(36);
    public static final PropertyIdentifier eventType = new PropertyIdentifier(37);
    public static final PropertyIdentifier exceptionSchedule = new PropertyIdentifier(38);
    public static final PropertyIdentifier faultValues = new PropertyIdentifier(39);
    public static final PropertyIdentifier feedbackValue = new PropertyIdentifier(40);
    public static final PropertyIdentifier fileAccessMethod = new PropertyIdentifier(41);
    public static final PropertyIdentifier fileSize = new PropertyIdentifier(42);
    public static final PropertyIdentifier fileType = new PropertyIdentifier(43);
    public static final PropertyIdentifier firmwareRevision = new PropertyIdentifier(44);
    public static final PropertyIdentifier highLimit = new PropertyIdentifier(45);
    public static final PropertyIdentifier inactiveText = new PropertyIdentifier(46);
    public static final PropertyIdentifier inProcess = new PropertyIdentifier(47);
    public static final PropertyIdentifier instanceOf = new PropertyIdentifier(48);
    public static final PropertyIdentifier integralConstant = new PropertyIdentifier(49);
    public static final PropertyIdentifier integralConstantUnits = new PropertyIdentifier(50);
    public static final PropertyIdentifier limitEnable = new PropertyIdentifier(52);
    public static final PropertyIdentifier listOfGroupMembers = new PropertyIdentifier(53);
    public static final PropertyIdentifier listOfObjectPropertyReferences = new PropertyIdentifier(54);
    public static final PropertyIdentifier localDate = new PropertyIdentifier(56);
    public static final PropertyIdentifier localTime = new PropertyIdentifier(57);
    public static final PropertyIdentifier location = new PropertyIdentifier(58);
    public static final PropertyIdentifier lowLimit = new PropertyIdentifier(59);
    public static final PropertyIdentifier manipulatedVariableReference = new PropertyIdentifier(60);
    public static final PropertyIdentifier maximumOutput = new PropertyIdentifier(61);
    public static final PropertyIdentifier maxApduLengthAccepted = new PropertyIdentifier(62);
    public static final PropertyIdentifier maxInfoFrames = new PropertyIdentifier(63);
    public static final PropertyIdentifier maxMaster = new PropertyIdentifier(64);
    public static final PropertyIdentifier maxPresValue = new PropertyIdentifier(65);
    public static final PropertyIdentifier minimumOffTime = new PropertyIdentifier(66);
    public static final PropertyIdentifier minimumOnTime = new PropertyIdentifier(67);
    public static final PropertyIdentifier minimumOutput = new PropertyIdentifier(68);
    public static final PropertyIdentifier minPresValue = new PropertyIdentifier(69);
    public static final PropertyIdentifier modelName = new PropertyIdentifier(70);
    public static final PropertyIdentifier modificationDate = new PropertyIdentifier(71);
    public static final PropertyIdentifier notifyType = new PropertyIdentifier(72);
    public static final PropertyIdentifier numberOfApduRetries = new PropertyIdentifier(73);
    public static final PropertyIdentifier numberOfStates = new PropertyIdentifier(74);
    public static final PropertyIdentifier objectIdentifier = new PropertyIdentifier(75);
    public static final PropertyIdentifier objectList = new PropertyIdentifier(76);
    public static final PropertyIdentifier objectName = new PropertyIdentifier(77);
    public static final PropertyIdentifier objectPropertyReference = new PropertyIdentifier(78);
    public static final PropertyIdentifier objectType = new PropertyIdentifier(79);
    public static final PropertyIdentifier optional = new PropertyIdentifier(80);
    public static final PropertyIdentifier outOfService = new PropertyIdentifier(81);
    public static final PropertyIdentifier outputUnits = new PropertyIdentifier(82);
    public static final PropertyIdentifier eventParameters = new PropertyIdentifier(83);
    public static final PropertyIdentifier polarity = new PropertyIdentifier(84);
    public static final PropertyIdentifier presentValue = new PropertyIdentifier(85);
    public static final PropertyIdentifier priority = new PropertyIdentifier(86);
    public static final PropertyIdentifier priorityArray = new PropertyIdentifier(87);
    public static final PropertyIdentifier priorityForWriting = new PropertyIdentifier(88);
    public static final PropertyIdentifier processIdentifier = new PropertyIdentifier(89);
    public static final PropertyIdentifier programChange = new PropertyIdentifier(90);
    public static final PropertyIdentifier programLocation = new PropertyIdentifier(91);
    public static final PropertyIdentifier programState = new PropertyIdentifier(92);
    public static final PropertyIdentifier proportionalConstant = new PropertyIdentifier(93);
    public static final PropertyIdentifier proportionalConstantUnits = new PropertyIdentifier(94);
    public static final PropertyIdentifier protocolObjectTypesSupported = new PropertyIdentifier(96);
    public static final PropertyIdentifier protocolServicesSupported = new PropertyIdentifier(97);
    public static final PropertyIdentifier protocolVersion = new PropertyIdentifier(98);
    public static final PropertyIdentifier readOnly = new PropertyIdentifier(99);
    public static final PropertyIdentifier reasonForHalt = new PropertyIdentifier(100);
    public static final PropertyIdentifier recipientList = new PropertyIdentifier(102);
    public static final PropertyIdentifier reliability = new PropertyIdentifier(103);
    public static final PropertyIdentifier relinquishDefault = new PropertyIdentifier(104);
    public static final PropertyIdentifier required = new PropertyIdentifier(105);
    public static final PropertyIdentifier resolution = new PropertyIdentifier(106);
    public static final PropertyIdentifier segmentationSupported = new PropertyIdentifier(107);
    public static final PropertyIdentifier setpoint = new PropertyIdentifier(108);
    public static final PropertyIdentifier setpointReference = new PropertyIdentifier(109);
    public static final PropertyIdentifier stateText = new PropertyIdentifier(110);
    public static final PropertyIdentifier statusFlags = new PropertyIdentifier(111);
    public static final PropertyIdentifier systemStatus = new PropertyIdentifier(112);
    public static final PropertyIdentifier timeDelay = new PropertyIdentifier(113);
    public static final PropertyIdentifier timeOfActiveTimeReset = new PropertyIdentifier(114);
    public static final PropertyIdentifier timeOfStateCountReset = new PropertyIdentifier(115);
    public static final PropertyIdentifier timeSynchronizationRecipients = new PropertyIdentifier(116);
    public static final PropertyIdentifier units = new PropertyIdentifier(117);
    public static final PropertyIdentifier updateInterval = new PropertyIdentifier(118);
    public static final PropertyIdentifier utcOffset = new PropertyIdentifier(119);
    public static final PropertyIdentifier vendorIdentifier = new PropertyIdentifier(120);
    public static final PropertyIdentifier vendorName = new PropertyIdentifier(121);
    public static final PropertyIdentifier vtClassesSupported = new PropertyIdentifier(122);
    public static final PropertyIdentifier weeklySchedule = new PropertyIdentifier(123);
    public static final PropertyIdentifier attemptedSamples = new PropertyIdentifier(124);
    public static final PropertyIdentifier averageValue = new PropertyIdentifier(125);
    public static final PropertyIdentifier bufferSize = new PropertyIdentifier(126);
    public static final PropertyIdentifier clientCovIncrement = new PropertyIdentifier(127);
    public static final PropertyIdentifier covResubscriptionInterval = new PropertyIdentifier(128);
    public static final PropertyIdentifier eventTimeStamps = new PropertyIdentifier(130);
    public static final PropertyIdentifier logBuffer = new PropertyIdentifier(131);
    public static final PropertyIdentifier logDeviceObjectProperty = new PropertyIdentifier(132);
    public static final PropertyIdentifier enable = new PropertyIdentifier(133);
    public static final PropertyIdentifier logInterval = new PropertyIdentifier(134);
    public static final PropertyIdentifier maximumValue = new PropertyIdentifier(135);
    public static final PropertyIdentifier minimumValue = new PropertyIdentifier(136);
    public static final PropertyIdentifier notificationThreshold = new PropertyIdentifier(137);
    public static final PropertyIdentifier protocolRevision = new PropertyIdentifier(139);
    public static final PropertyIdentifier recordsSinceNotification = new PropertyIdentifier(140);
    public static final PropertyIdentifier recordCount = new PropertyIdentifier(141);
    public static final PropertyIdentifier startTime = new PropertyIdentifier(142);
    public static final PropertyIdentifier stopTime = new PropertyIdentifier(143);
    public static final PropertyIdentifier stopWhenFull = new PropertyIdentifier(144);
    public static final PropertyIdentifier totalRecordCount = new PropertyIdentifier(145);
    public static final PropertyIdentifier validSamples = new PropertyIdentifier(146);
    public static final PropertyIdentifier windowInterval = new PropertyIdentifier(147);
    public static final PropertyIdentifier windowSamples = new PropertyIdentifier(148);
    public static final PropertyIdentifier maximumValueTimestamp = new PropertyIdentifier(149);
    public static final PropertyIdentifier minimumValueTimestamp = new PropertyIdentifier(150);
    public static final PropertyIdentifier varianceValue = new PropertyIdentifier(151);
    public static final PropertyIdentifier activeCovSubscriptions = new PropertyIdentifier(152);
    public static final PropertyIdentifier backupFailureTimeout = new PropertyIdentifier(153);
    public static final PropertyIdentifier configurationFiles = new PropertyIdentifier(154);
    public static final PropertyIdentifier databaseRevision = new PropertyIdentifier(155);
    public static final PropertyIdentifier directReading = new PropertyIdentifier(156);
    public static final PropertyIdentifier lastRestoreTime = new PropertyIdentifier(157);
    public static final PropertyIdentifier maintenanceRequired = new PropertyIdentifier(158);
    public static final PropertyIdentifier memberOf = new PropertyIdentifier(159);
    public static final PropertyIdentifier mode = new PropertyIdentifier(160);
    public static final PropertyIdentifier operationExpected = new PropertyIdentifier(161);
    public static final PropertyIdentifier setting = new PropertyIdentifier(162);
    public static final PropertyIdentifier silenced = new PropertyIdentifier(163);
    public static final PropertyIdentifier trackingValue = new PropertyIdentifier(164);
    public static final PropertyIdentifier zoneMembers = new PropertyIdentifier(165);
    public static final PropertyIdentifier lifeSafetyAlarmValues = new PropertyIdentifier(166);
    public static final PropertyIdentifier maxSegmentsAccepted = new PropertyIdentifier(167);
    public static final PropertyIdentifier profileName = new PropertyIdentifier(168);
    public static final PropertyIdentifier autoSlaveDiscovery = new PropertyIdentifier(169);
    public static final PropertyIdentifier manualSlaveAddressBinding = new PropertyIdentifier(170);
    public static final PropertyIdentifier slaveAddressBinding = new PropertyIdentifier(171);
    public static final PropertyIdentifier slaveProxyEnable = new PropertyIdentifier(172);
    public static final PropertyIdentifier lastNotifyRecord = new PropertyIdentifier(173);
    public static final PropertyIdentifier scheduleDefault = new PropertyIdentifier(174);
    public static final PropertyIdentifier acceptedModes = new PropertyIdentifier(175);
    public static final PropertyIdentifier adjustValue = new PropertyIdentifier(176);
    public static final PropertyIdentifier count = new PropertyIdentifier(177);
    public static final PropertyIdentifier countBeforeChange = new PropertyIdentifier(178);
    public static final PropertyIdentifier countChangeTime = new PropertyIdentifier(179);
    public static final PropertyIdentifier covPeriod = new PropertyIdentifier(180);
    public static final PropertyIdentifier inputReference = new PropertyIdentifier(181);
    public static final PropertyIdentifier limitMonitoringInterval = new PropertyIdentifier(182);
    public static final PropertyIdentifier loggingObject = new PropertyIdentifier(183);
    public static final PropertyIdentifier loggingRecord = new PropertyIdentifier(184);
    public static final PropertyIdentifier prescale = new PropertyIdentifier(185);
    public static final PropertyIdentifier pulseRate = new PropertyIdentifier(186);
    public static final PropertyIdentifier scale = new PropertyIdentifier(187);
    public static final PropertyIdentifier scaleFactor = new PropertyIdentifier(188);
    public static final PropertyIdentifier updateTime = new PropertyIdentifier(189);
    public static final PropertyIdentifier valueBeforeChange = new PropertyIdentifier(190);
    public static final PropertyIdentifier valueSet = new PropertyIdentifier(191);
    public static final PropertyIdentifier valueChangeTime = new PropertyIdentifier(192);
    public static final PropertyIdentifier alignIntervals = new PropertyIdentifier(193);
    public static final PropertyIdentifier intervalOffset = new PropertyIdentifier(195);
    public static final PropertyIdentifier lastRestartReason = new PropertyIdentifier(196);
    public static final PropertyIdentifier loggingType = new PropertyIdentifier(197);
    public static final PropertyIdentifier restartNotificationRecipients = new PropertyIdentifier(202);
    public static final PropertyIdentifier timeOfDeviceRestart = new PropertyIdentifier(203);
    public static final PropertyIdentifier timeSynchronizationInterval = new PropertyIdentifier(204);
    public static final PropertyIdentifier trigger = new PropertyIdentifier(205);
    public static final PropertyIdentifier utcTimeSynchronizationRecipients = new PropertyIdentifier(206);
    public static final PropertyIdentifier nodeSubtype = new PropertyIdentifier(207);
    public static final PropertyIdentifier nodeType = new PropertyIdentifier(208);
    public static final PropertyIdentifier structuredObjectList = new PropertyIdentifier(209);
    public static final PropertyIdentifier subordinateAnnotations = new PropertyIdentifier(210);
    public static final PropertyIdentifier subordinateList = new PropertyIdentifier(211);
    public static final PropertyIdentifier actualShedLevel = new PropertyIdentifier(212);
    public static final PropertyIdentifier dutyWindow = new PropertyIdentifier(213);
    public static final PropertyIdentifier expectedShedLevel = new PropertyIdentifier(214);
    public static final PropertyIdentifier fullDutyBaseline = new PropertyIdentifier(215);
    public static final PropertyIdentifier requestedShedLevel = new PropertyIdentifier(218);
    public static final PropertyIdentifier shedDuration = new PropertyIdentifier(219);
    public static final PropertyIdentifier shedLevelDescriptions = new PropertyIdentifier(220);
    public static final PropertyIdentifier shedLevels = new PropertyIdentifier(221);
    public static final PropertyIdentifier stateDescription = new PropertyIdentifier(222);
    public static final PropertyIdentifier doorAlarmState = new PropertyIdentifier(226);
    public static final PropertyIdentifier doorExtendedPulseTime = new PropertyIdentifier(227);
    public static final PropertyIdentifier doorMembers = new PropertyIdentifier(228);
    public static final PropertyIdentifier doorOpenTooLongTime = new PropertyIdentifier(229);
    public static final PropertyIdentifier doorPulseTime = new PropertyIdentifier(230);
    public static final PropertyIdentifier doorStatus = new PropertyIdentifier(231);
    public static final PropertyIdentifier doorUnlockDelayTime = new PropertyIdentifier(232);
    public static final PropertyIdentifier lockStatus = new PropertyIdentifier(233);
    public static final PropertyIdentifier maskedAlarmValues = new PropertyIdentifier(234);
    public static final PropertyIdentifier securedStatus = new PropertyIdentifier(235);
    public static final PropertyIdentifier absenteeLimit = new PropertyIdentifier(244);
    public static final PropertyIdentifier accessAlarmEvents = new PropertyIdentifier(245);
    public static final PropertyIdentifier accessDoors = new PropertyIdentifier(246);
    public static final PropertyIdentifier accessEvent = new PropertyIdentifier(247);
    public static final PropertyIdentifier accessEventAuthenticationFactor = new PropertyIdentifier(248);
    public static final PropertyIdentifier accessEventCredential = new PropertyIdentifier(249);
    public static final PropertyIdentifier accessEventTime = new PropertyIdentifier(250);
    public static final PropertyIdentifier accessTransactionEvents = new PropertyIdentifier(251);
    public static final PropertyIdentifier accompaniment = new PropertyIdentifier(252);
    public static final PropertyIdentifier accompanimentTime = new PropertyIdentifier(253);
    public static final PropertyIdentifier activationTime = new PropertyIdentifier(254);
    public static final PropertyIdentifier activeAuthenticationPolicy = new PropertyIdentifier(255);
    public static final PropertyIdentifier assignedAccessRights = new PropertyIdentifier(256);
    public static final PropertyIdentifier authenticationFactors = new PropertyIdentifier(257);
    public static final PropertyIdentifier authenticationPolicyList = new PropertyIdentifier(258);
    public static final PropertyIdentifier authenticationPolicyNames = new PropertyIdentifier(259);
    public static final PropertyIdentifier authenticationStatus = new PropertyIdentifier(260);
    public static final PropertyIdentifier authorizationMode = new PropertyIdentifier(261);
    public static final PropertyIdentifier belongsTo = new PropertyIdentifier(262);
    public static final PropertyIdentifier credentialDisable = new PropertyIdentifier(263);
    public static final PropertyIdentifier credentialStatus = new PropertyIdentifier(264);
    public static final PropertyIdentifier credentials = new PropertyIdentifier(265);
    public static final PropertyIdentifier credentialsInZone = new PropertyIdentifier(266);
    public static final PropertyIdentifier daysRemaining = new PropertyIdentifier(267);
    public static final PropertyIdentifier entryPoints = new PropertyIdentifier(268);
    public static final PropertyIdentifier exitPoints = new PropertyIdentifier(269);
    public static final PropertyIdentifier expiryTime = new PropertyIdentifier(270);
    public static final PropertyIdentifier extendedTimeEnable = new PropertyIdentifier(271);
    public static final PropertyIdentifier failedAttemptEvents = new PropertyIdentifier(272);
    public static final PropertyIdentifier failedAttempts = new PropertyIdentifier(273);
    public static final PropertyIdentifier failedAttemptsTime = new PropertyIdentifier(274);
    public static final PropertyIdentifier lastAccessEvent = new PropertyIdentifier(275);
    public static final PropertyIdentifier lastAccessPoint = new PropertyIdentifier(276);
    public static final PropertyIdentifier lastCredentialAdded = new PropertyIdentifier(277);
    public static final PropertyIdentifier lastCredentialAddedTime = new PropertyIdentifier(278);
    public static final PropertyIdentifier lastCredentialRemoved = new PropertyIdentifier(279);
    public static final PropertyIdentifier lastCredentialRemovedTime = new PropertyIdentifier(280);
    public static final PropertyIdentifier lastUseTime = new PropertyIdentifier(281);
    public static final PropertyIdentifier lockout = new PropertyIdentifier(282);
    public static final PropertyIdentifier lockoutRelinquishTime = new PropertyIdentifier(283);
    public static final PropertyIdentifier maxFailedAttempts = new PropertyIdentifier(285);
    public static final PropertyIdentifier members = new PropertyIdentifier(286);
    public static final PropertyIdentifier musterPoint = new PropertyIdentifier(287);
    public static final PropertyIdentifier negativeAccessRules = new PropertyIdentifier(288);
    public static final PropertyIdentifier numberOfAuthenticationPolicies = new PropertyIdentifier(289);
    public static final PropertyIdentifier occupancyCount = new PropertyIdentifier(290);
    public static final PropertyIdentifier occupancyCountAdjust = new PropertyIdentifier(291);
    public static final PropertyIdentifier occupancyCountEnable = new PropertyIdentifier(292);
    public static final PropertyIdentifier occupancyLowerLimit = new PropertyIdentifier(294);
    public static final PropertyIdentifier occupancyLowerLimitEnforced = new PropertyIdentifier(295);
    public static final PropertyIdentifier occupancyState = new PropertyIdentifier(296);
    public static final PropertyIdentifier occupancyUpperLimit = new PropertyIdentifier(297);
    public static final PropertyIdentifier occupancyUpperLimitEnforced = new PropertyIdentifier(298);
    public static final PropertyIdentifier passbackMode = new PropertyIdentifier(300);
    public static final PropertyIdentifier passbackTimeout = new PropertyIdentifier(301);
    public static final PropertyIdentifier positiveAccessRules = new PropertyIdentifier(302);
    public static final PropertyIdentifier reasonForDisable = new PropertyIdentifier(303);
    public static final PropertyIdentifier supportedFormats = new PropertyIdentifier(304);
    public static final PropertyIdentifier supportedFormatClasses = new PropertyIdentifier(305);
    public static final PropertyIdentifier threatAuthority = new PropertyIdentifier(306);
    public static final PropertyIdentifier threatLevel = new PropertyIdentifier(307);
    public static final PropertyIdentifier traceFlag = new PropertyIdentifier(308);
    public static final PropertyIdentifier transactionNotificationClass = new PropertyIdentifier(309);
    public static final PropertyIdentifier userExternalIdentifier = new PropertyIdentifier(310);
    public static final PropertyIdentifier userInformationReference = new PropertyIdentifier(311);
    public static final PropertyIdentifier userName = new PropertyIdentifier(317);
    public static final PropertyIdentifier userType = new PropertyIdentifier(318);
    public static final PropertyIdentifier usesRemaining = new PropertyIdentifier(319);
    public static final PropertyIdentifier zoneFrom = new PropertyIdentifier(320);
    public static final PropertyIdentifier zoneTo = new PropertyIdentifier(321);
    public static final PropertyIdentifier accessEventTag = new PropertyIdentifier(322);
    public static final PropertyIdentifier globalIdentifier = new PropertyIdentifier(323);
    public static final PropertyIdentifier verificationTime = new PropertyIdentifier(326);
    public static final PropertyIdentifier baseDeviceSecurityPolicy = new PropertyIdentifier(327);
    public static final PropertyIdentifier distributionKeyRevision = new PropertyIdentifier(328);
    public static final PropertyIdentifier doNotHide = new PropertyIdentifier(329);
    public static final PropertyIdentifier keySets = new PropertyIdentifier(330);
    public static final PropertyIdentifier lastKeyServer = new PropertyIdentifier(331);
    public static final PropertyIdentifier networkAccessSecurityPolicies = new PropertyIdentifier(332);
    public static final PropertyIdentifier packetReorderTime = new PropertyIdentifier(333);
    public static final PropertyIdentifier securityPduTimeout = new PropertyIdentifier(334);
    public static final PropertyIdentifier securityTimeWindow = new PropertyIdentifier(335);
    public static final PropertyIdentifier supportedSecurityAlgorithms = new PropertyIdentifier(336);
    public static final PropertyIdentifier updateKeySetTimeout = new PropertyIdentifier(337);
    public static final PropertyIdentifier backupAndRestoreState = new PropertyIdentifier(338);
    public static final PropertyIdentifier backupPreparationTime = new PropertyIdentifier(339);
    public static final PropertyIdentifier restoreCompletionTime = new PropertyIdentifier(340);
    public static final PropertyIdentifier restorePreparationTime = new PropertyIdentifier(341);
    public static final PropertyIdentifier bitMask = new PropertyIdentifier(342);
    public static final PropertyIdentifier bitText = new PropertyIdentifier(343);
    public static final PropertyIdentifier isUtc = new PropertyIdentifier(344);
    public static final PropertyIdentifier groupMembers = new PropertyIdentifier(345);
    public static final PropertyIdentifier groupMemberNames = new PropertyIdentifier(346);
    public static final PropertyIdentifier memberStatusFlags = new PropertyIdentifier(347);
    public static final PropertyIdentifier requestedUpdateInterval = new PropertyIdentifier(348);
    public static final PropertyIdentifier covuPeriod = new PropertyIdentifier(349);
    public static final PropertyIdentifier covuRecipients = new PropertyIdentifier(350);
    public static final PropertyIdentifier eventMessageTexts = new PropertyIdentifier(351);
    public static final PropertyIdentifier eventMessageTextsConfig = new PropertyIdentifier(352);
    public static final PropertyIdentifier eventDetectionEnable = new PropertyIdentifier(353);
    public static final PropertyIdentifier eventAlgorithmInhibit = new PropertyIdentifier(354);
    public static final PropertyIdentifier eventAlgorithmInhibitRef = new PropertyIdentifier(355);
    public static final PropertyIdentifier timeDelayNormal = new PropertyIdentifier(356);
    public static final PropertyIdentifier reliabilityEvaluationInhibit = new PropertyIdentifier(357);
    public static final PropertyIdentifier faultParameters = new PropertyIdentifier(358);
    public static final PropertyIdentifier faultType = new PropertyIdentifier(359);
    public static final PropertyIdentifier localForwardingOnly = new PropertyIdentifier(360);
    public static final PropertyIdentifier processIdentifierFilter = new PropertyIdentifier(361);
    public static final PropertyIdentifier subscribedRecipients = new PropertyIdentifier(362);
    public static final PropertyIdentifier portFilter = new PropertyIdentifier(363);
    public static final PropertyIdentifier authorizationExemptions = new PropertyIdentifier(364);
    public static final PropertyIdentifier allowGroupDelayInhibit = new PropertyIdentifier(365);
    public static final PropertyIdentifier channelNumber = new PropertyIdentifier(366);
    public static final PropertyIdentifier controlGroups = new PropertyIdentifier(367);
    public static final PropertyIdentifier executionDelay = new PropertyIdentifier(368);
    public static final PropertyIdentifier lastPriority = new PropertyIdentifier(369);
    public static final PropertyIdentifier writeStatus = new PropertyIdentifier(370);
    public static final PropertyIdentifier propertyList = new PropertyIdentifier(371);
    public static final PropertyIdentifier serialNumber = new PropertyIdentifier(372);
    public static final PropertyIdentifier blinkWarnEnable = new PropertyIdentifier(373);
    public static final PropertyIdentifier defaultFadeTime = new PropertyIdentifier(374);
    public static final PropertyIdentifier defaultRampRate = new PropertyIdentifier(375);
    public static final PropertyIdentifier defaultStepIncrement = new PropertyIdentifier(376);
    public static final PropertyIdentifier egressTime = new PropertyIdentifier(377);
    public static final PropertyIdentifier inProgress = new PropertyIdentifier(378);
    public static final PropertyIdentifier instantaneousPower = new PropertyIdentifier(379);
    public static final PropertyIdentifier lightingCommand = new PropertyIdentifier(380);
    public static final PropertyIdentifier lightingCommandDefaultPriority = new PropertyIdentifier(381);
    public static final PropertyIdentifier maxActualValue = new PropertyIdentifier(382);
    public static final PropertyIdentifier minActualValue = new PropertyIdentifier(383);
    public static final PropertyIdentifier power = new PropertyIdentifier(384);
    public static final PropertyIdentifier transition = new PropertyIdentifier(385);
    public static final PropertyIdentifier egressActive = new PropertyIdentifier(386);

    public static final PropertyIdentifier[] ALL = { ackedTransitions, ackRequired, action, actionText, activeText,
            activeVtSessions, alarmValue, alarmValues, all, allWritesSuccessful, apduSegmentTimeout, apduTimeout,
            applicationSoftwareVersion, archive, bias, changeOfStateCount, changeOfStateTime, notificationClass,
            controlledVariableReference, controlledVariableUnits, controlledVariableValue, covIncrement, dateList,
            daylightSavingsStatus, deadband, derivativeConstant, derivativeConstantUnits, description,
            descriptionOfHalt, deviceAddressBinding, deviceType, effectivePeriod, elapsedActiveTime, errorLimit,
            eventEnable, eventState, eventType, exceptionSchedule, faultValues, feedbackValue, fileAccessMethod,
            fileSize, fileType, firmwareRevision, highLimit, inactiveText, inProcess, instanceOf, integralConstant,
            integralConstantUnits, limitEnable, listOfGroupMembers, listOfObjectPropertyReferences, localDate,
            localTime, location, lowLimit, manipulatedVariableReference, maximumOutput, maxApduLengthAccepted,
            maxInfoFrames, maxMaster, maxPresValue, minimumOffTime, minimumOnTime, minimumOutput, minPresValue,
            modelName, modificationDate, notifyType, numberOfApduRetries, numberOfStates, objectIdentifier, objectList,
            objectName, objectPropertyReference, objectType, optional, outOfService, outputUnits, eventParameters,
            polarity, presentValue, priority, priorityArray, priorityForWriting, processIdentifier, programChange,
            programLocation, programState, proportionalConstant, proportionalConstantUnits,
            protocolObjectTypesSupported, protocolServicesSupported, protocolVersion, readOnly, reasonForHalt,
            recipientList, reliability, relinquishDefault, required, resolution, segmentationSupported, setpoint,
            setpointReference, stateText, statusFlags, systemStatus, timeDelay, timeOfActiveTimeReset,
            timeOfStateCountReset, timeSynchronizationRecipients, units, updateInterval, utcOffset, vendorIdentifier,
            vendorName, vtClassesSupported, weeklySchedule, attemptedSamples, averageValue, bufferSize,
            clientCovIncrement, covResubscriptionInterval, eventTimeStamps, logBuffer, logDeviceObjectProperty, enable,
            logInterval, maximumValue, minimumValue, notificationThreshold, protocolRevision, recordsSinceNotification,
            recordCount, startTime, stopTime, stopWhenFull, totalRecordCount, validSamples, windowInterval,
            windowSamples, maximumValueTimestamp, minimumValueTimestamp, varianceValue, activeCovSubscriptions,
            backupFailureTimeout, configurationFiles, databaseRevision, directReading, lastRestoreTime,
            maintenanceRequired, memberOf, mode, operationExpected, setting, silenced, trackingValue, zoneMembers,
            lifeSafetyAlarmValues, maxSegmentsAccepted, profileName, autoSlaveDiscovery, manualSlaveAddressBinding,
            slaveAddressBinding, slaveProxyEnable, lastNotifyRecord, scheduleDefault, acceptedModes, adjustValue,
            count, countBeforeChange, countChangeTime, covPeriod, inputReference, limitMonitoringInterval,
            loggingObject, loggingRecord, prescale, pulseRate, scale, scaleFactor, updateTime, valueBeforeChange,
            valueSet, valueChangeTime, alignIntervals, intervalOffset, lastRestartReason, loggingType,
            restartNotificationRecipients, timeOfDeviceRestart, timeSynchronizationInterval, trigger,
            utcTimeSynchronizationRecipients, nodeSubtype, nodeType, structuredObjectList, subordinateAnnotations,
            subordinateList, actualShedLevel, dutyWindow, expectedShedLevel, fullDutyBaseline, requestedShedLevel,
            shedDuration, shedLevelDescriptions, shedLevels, stateDescription, doorAlarmState, doorExtendedPulseTime,
            doorMembers, doorOpenTooLongTime, doorPulseTime, doorStatus, doorUnlockDelayTime, lockStatus,
            maskedAlarmValues, securedStatus, absenteeLimit, accessAlarmEvents, accessDoors, accessEvent,
            accessEventAuthenticationFactor, accessEventCredential, accessEventTime, accessTransactionEvents,
            accompaniment, accompanimentTime, activationTime, activeAuthenticationPolicy, assignedAccessRights,
            authenticationFactors, authenticationPolicyList, authenticationPolicyNames, authenticationStatus,
            authorizationMode, belongsTo, credentialDisable, credentialStatus, credentials, credentialsInZone,
            daysRemaining, entryPoints, exitPoints, expiryTime, extendedTimeEnable, failedAttemptEvents,
            failedAttempts, failedAttemptsTime, lastAccessEvent, lastAccessPoint, lastCredentialAdded,
            lastCredentialAddedTime, lastCredentialRemoved, lastCredentialRemovedTime, lastUseTime, lockout,
            lockoutRelinquishTime, maxFailedAttempts, members, musterPoint, negativeAccessRules,
            numberOfAuthenticationPolicies, occupancyCount, occupancyCountAdjust, occupancyCountEnable,
            occupancyLowerLimit, occupancyLowerLimitEnforced, occupancyState, occupancyUpperLimit,
            occupancyUpperLimitEnforced, passbackMode, passbackTimeout, positiveAccessRules, reasonForDisable,
            supportedFormats, supportedFormatClasses, threatAuthority, threatLevel, traceFlag,
            transactionNotificationClass, userExternalIdentifier, userInformationReference, userName, userType,
            usesRemaining, zoneFrom, zoneTo, accessEventTag, globalIdentifier, verificationTime,
            baseDeviceSecurityPolicy, distributionKeyRevision, doNotHide, keySets, lastKeyServer,
            networkAccessSecurityPolicies, packetReorderTime, securityPduTimeout, securityTimeWindow,
            supportedSecurityAlgorithms, updateKeySetTimeout, backupAndRestoreState, backupPreparationTime,
            restoreCompletionTime, restorePreparationTime, bitMask, bitText, isUtc, groupMembers, groupMemberNames,
            memberStatusFlags, requestedUpdateInterval, covuPeriod, covuRecipients, eventMessageTexts,
            eventMessageTextsConfig, eventDetectionEnable, eventAlgorithmInhibit, eventAlgorithmInhibitRef,
            timeDelayNormal, reliabilityEvaluationInhibit, faultParameters, faultType, localForwardingOnly,
            processIdentifierFilter, subscribedRecipients, portFilter, authorizationExemptions, allowGroupDelayInhibit,
            channelNumber, controlGroups, executionDelay, lastPriority, writeStatus, propertyList, serialNumber,
            blinkWarnEnable, defaultFadeTime, defaultRampRate, defaultStepIncrement, egressTime, inProgress,
            instantaneousPower, lightingCommand, lightingCommandDefaultPriority, maxActualValue, minActualValue, power,
            transition, egressActive,

    };

    public PropertyIdentifier(int value) {
        super(value);
    }

    public PropertyIdentifier(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == ackedTransitions.intValue())
            return "ackedTransitions";
        if (type == ackRequired.intValue())
            return "ackRequired";
        if (type == action.intValue())
            return "action";
        if (type == actionText.intValue())
            return "actionText";
        if (type == activeText.intValue())
            return "activeText";
        if (type == activeVtSessions.intValue())
            return "activeVtSessions";
        if (type == alarmValue.intValue())
            return "alarmValue";
        if (type == alarmValues.intValue())
            return "alarmValues";
        if (type == all.intValue())
            return "all";
        if (type == allWritesSuccessful.intValue())
            return "allWritesSuccessful";
        if (type == apduSegmentTimeout.intValue())
            return "apduSegmentTimeout";
        if (type == apduTimeout.intValue())
            return "apduTimeout";
        if (type == applicationSoftwareVersion.intValue())
            return "applicationSoftwareVersion";
        if (type == archive.intValue())
            return "archive";
        if (type == bias.intValue())
            return "bias";
        if (type == changeOfStateCount.intValue())
            return "changeOfStateCount";
        if (type == changeOfStateTime.intValue())
            return "changeOfStateTime";
        if (type == notificationClass.intValue())
            return "notificationClass";
        if (type == controlledVariableReference.intValue())
            return "controlledVariableReference";
        if (type == controlledVariableUnits.intValue())
            return "controlledVariableUnits";
        if (type == controlledVariableValue.intValue())
            return "controlledVariableValue";
        if (type == covIncrement.intValue())
            return "covIncrement";
        if (type == dateList.intValue())
            return "dateList";
        if (type == daylightSavingsStatus.intValue())
            return "daylightSavingsStatus";
        if (type == deadband.intValue())
            return "deadband";
        if (type == derivativeConstant.intValue())
            return "derivativeConstant";
        if (type == derivativeConstantUnits.intValue())
            return "derivativeConstantUnits";
        if (type == description.intValue())
            return "description";
        if (type == descriptionOfHalt.intValue())
            return "descriptionOfHalt";
        if (type == deviceAddressBinding.intValue())
            return "deviceAddressBinding";
        if (type == deviceType.intValue())
            return "deviceType";
        if (type == effectivePeriod.intValue())
            return "effectivePeriod";
        if (type == elapsedActiveTime.intValue())
            return "elapsedActiveTime";
        if (type == errorLimit.intValue())
            return "errorLimit";
        if (type == eventEnable.intValue())
            return "eventEnable";
        if (type == eventState.intValue())
            return "eventState";
        if (type == eventType.intValue())
            return "eventType";
        if (type == exceptionSchedule.intValue())
            return "exceptionSchedule";
        if (type == faultValues.intValue())
            return "faultValues";
        if (type == feedbackValue.intValue())
            return "feedbackValue";
        if (type == fileAccessMethod.intValue())
            return "fileAccessMethod";
        if (type == fileSize.intValue())
            return "fileSize";
        if (type == fileType.intValue())
            return "fileType";
        if (type == firmwareRevision.intValue())
            return "firmwareRevision";
        if (type == highLimit.intValue())
            return "highLimit";
        if (type == inactiveText.intValue())
            return "inactiveText";
        if (type == inProcess.intValue())
            return "inProcess";
        if (type == instanceOf.intValue())
            return "instanceOf";
        if (type == integralConstant.intValue())
            return "integralConstant";
        if (type == integralConstantUnits.intValue())
            return "integralConstantUnits";
        if (type == limitEnable.intValue())
            return "limitEnable";
        if (type == listOfGroupMembers.intValue())
            return "listOfGroupMembers";
        if (type == listOfObjectPropertyReferences.intValue())
            return "listOfObjectPropertyReferences";
        if (type == localDate.intValue())
            return "localDate";
        if (type == localTime.intValue())
            return "localTime";
        if (type == location.intValue())
            return "location";
        if (type == lowLimit.intValue())
            return "lowLimit";
        if (type == manipulatedVariableReference.intValue())
            return "manipulatedVariableReference";
        if (type == maximumOutput.intValue())
            return "maximumOutput";
        if (type == maxApduLengthAccepted.intValue())
            return "maxApduLengthAccepted";
        if (type == maxInfoFrames.intValue())
            return "maxInfoFrames";
        if (type == maxMaster.intValue())
            return "maxMaster";
        if (type == maxPresValue.intValue())
            return "maxPresValue";
        if (type == minimumOffTime.intValue())
            return "minimumOffTime";
        if (type == minimumOnTime.intValue())
            return "minimumOnTime";
        if (type == minimumOutput.intValue())
            return "minimumOutput";
        if (type == minPresValue.intValue())
            return "minPresValue";
        if (type == modelName.intValue())
            return "modelName";
        if (type == modificationDate.intValue())
            return "modificationDate";
        if (type == notifyType.intValue())
            return "notifyType";
        if (type == numberOfApduRetries.intValue())
            return "numberOfApduRetries";
        if (type == numberOfStates.intValue())
            return "numberOfStates";
        if (type == objectIdentifier.intValue())
            return "objectIdentifier";
        if (type == objectList.intValue())
            return "objectList";
        if (type == objectName.intValue())
            return "objectName";
        if (type == objectPropertyReference.intValue())
            return "objectPropertyReference";
        if (type == objectType.intValue())
            return "objectType";
        if (type == optional.intValue())
            return "optional";
        if (type == outOfService.intValue())
            return "outOfService";
        if (type == outputUnits.intValue())
            return "outputUnits";
        if (type == eventParameters.intValue())
            return "eventParameters";
        if (type == polarity.intValue())
            return "polarity";
        if (type == presentValue.intValue())
            return "presentValue";
        if (type == priority.intValue())
            return "priority";
        if (type == priorityArray.intValue())
            return "priorityArray";
        if (type == priorityForWriting.intValue())
            return "priorityForWriting";
        if (type == processIdentifier.intValue())
            return "processIdentifier";
        if (type == programChange.intValue())
            return "programChange";
        if (type == programLocation.intValue())
            return "programLocation";
        if (type == programState.intValue())
            return "programState";
        if (type == proportionalConstant.intValue())
            return "proportionalConstant";
        if (type == proportionalConstantUnits.intValue())
            return "proportionalConstantUnits";
        if (type == protocolObjectTypesSupported.intValue())
            return "protocolObjectTypesSupported";
        if (type == protocolServicesSupported.intValue())
            return "protocolServicesSupported";
        if (type == protocolVersion.intValue())
            return "protocolVersion";
        if (type == readOnly.intValue())
            return "readOnly";
        if (type == reasonForHalt.intValue())
            return "reasonForHalt";
        if (type == recipientList.intValue())
            return "recipientList";
        if (type == reliability.intValue())
            return "reliability";
        if (type == relinquishDefault.intValue())
            return "relinquishDefault";
        if (type == required.intValue())
            return "required";
        if (type == resolution.intValue())
            return "resolution";
        if (type == segmentationSupported.intValue())
            return "segmentationSupported";
        if (type == setpoint.intValue())
            return "setpoint";
        if (type == setpointReference.intValue())
            return "setpointReference";
        if (type == stateText.intValue())
            return "stateText";
        if (type == statusFlags.intValue())
            return "statusFlags";
        if (type == systemStatus.intValue())
            return "systemStatus";
        if (type == timeDelay.intValue())
            return "timeDelay";
        if (type == timeOfActiveTimeReset.intValue())
            return "timeOfActiveTimeReset";
        if (type == timeOfStateCountReset.intValue())
            return "timeOfStateCountReset";
        if (type == timeSynchronizationRecipients.intValue())
            return "timeSynchronizationRecipients";
        if (type == units.intValue())
            return "units";
        if (type == updateInterval.intValue())
            return "updateInterval";
        if (type == utcOffset.intValue())
            return "utcOffset";
        if (type == vendorIdentifier.intValue())
            return "vendorIdentifier";
        if (type == vendorName.intValue())
            return "vendorName";
        if (type == vtClassesSupported.intValue())
            return "vtClassesSupported";
        if (type == weeklySchedule.intValue())
            return "weeklySchedule";
        if (type == attemptedSamples.intValue())
            return "attemptedSamples";
        if (type == averageValue.intValue())
            return "averageValue";
        if (type == bufferSize.intValue())
            return "bufferSize";
        if (type == clientCovIncrement.intValue())
            return "clientCovIncrement";
        if (type == covResubscriptionInterval.intValue())
            return "covResubscriptionInterval";
        if (type == eventTimeStamps.intValue())
            return "eventTimeStamps";
        if (type == logBuffer.intValue())
            return "logBuffer";
        if (type == logDeviceObjectProperty.intValue())
            return "logDeviceObjectProperty";
        if (type == enable.intValue())
            return "enable";
        if (type == logInterval.intValue())
            return "logInterval";
        if (type == maximumValue.intValue())
            return "maximumValue";
        if (type == minimumValue.intValue())
            return "minimumValue";
        if (type == notificationThreshold.intValue())
            return "notificationThreshold";
        if (type == protocolRevision.intValue())
            return "protocolRevision";
        if (type == recordsSinceNotification.intValue())
            return "recordsSinceNotification";
        if (type == recordCount.intValue())
            return "recordCount";
        if (type == startTime.intValue())
            return "startTime";
        if (type == stopTime.intValue())
            return "stopTime";
        if (type == stopWhenFull.intValue())
            return "stopWhenFull";
        if (type == totalRecordCount.intValue())
            return "totalRecordCount";
        if (type == validSamples.intValue())
            return "validSamples";
        if (type == windowInterval.intValue())
            return "windowInterval";
        if (type == windowSamples.intValue())
            return "windowSamples";
        if (type == maximumValueTimestamp.intValue())
            return "maximumValueTimestamp";
        if (type == minimumValueTimestamp.intValue())
            return "minimumValueTimestamp";
        if (type == varianceValue.intValue())
            return "varianceValue";
        if (type == activeCovSubscriptions.intValue())
            return "activeCovSubscriptions";
        if (type == backupFailureTimeout.intValue())
            return "backupFailureTimeout";
        if (type == configurationFiles.intValue())
            return "configurationFiles";
        if (type == databaseRevision.intValue())
            return "databaseRevision";
        if (type == directReading.intValue())
            return "directReading";
        if (type == lastRestoreTime.intValue())
            return "lastRestoreTime";
        if (type == maintenanceRequired.intValue())
            return "maintenanceRequired";
        if (type == memberOf.intValue())
            return "memberOf";
        if (type == mode.intValue())
            return "mode";
        if (type == operationExpected.intValue())
            return "operationExpected";
        if (type == setting.intValue())
            return "setting";
        if (type == silenced.intValue())
            return "silenced";
        if (type == trackingValue.intValue())
            return "trackingValue";
        if (type == zoneMembers.intValue())
            return "zoneMembers";
        if (type == lifeSafetyAlarmValues.intValue())
            return "lifeSafetyAlarmValues";
        if (type == maxSegmentsAccepted.intValue())
            return "maxSegmentsAccepted";
        if (type == profileName.intValue())
            return "profileName";
        if (type == autoSlaveDiscovery.intValue())
            return "autoSlaveDiscovery";
        if (type == manualSlaveAddressBinding.intValue())
            return "manualSlaveAddressBinding";
        if (type == slaveAddressBinding.intValue())
            return "slaveAddressBinding";
        if (type == slaveProxyEnable.intValue())
            return "slaveProxyEnable";
        if (type == lastNotifyRecord.intValue())
            return "lastNotifyRecord";
        if (type == scheduleDefault.intValue())
            return "scheduleDefault";
        if (type == acceptedModes.intValue())
            return "acceptedModes";
        if (type == adjustValue.intValue())
            return "adjustValue";
        if (type == count.intValue())
            return "count";
        if (type == countBeforeChange.intValue())
            return "countBeforeChange";
        if (type == countChangeTime.intValue())
            return "countChangeTime";
        if (type == covPeriod.intValue())
            return "covPeriod";
        if (type == inputReference.intValue())
            return "inputReference";
        if (type == limitMonitoringInterval.intValue())
            return "limitMonitoringInterval";
        if (type == loggingObject.intValue())
            return "loggingObject";
        if (type == loggingRecord.intValue())
            return "loggingRecord";
        if (type == prescale.intValue())
            return "prescale";
        if (type == pulseRate.intValue())
            return "pulseRate";
        if (type == scale.intValue())
            return "scale";
        if (type == scaleFactor.intValue())
            return "scaleFactor";
        if (type == updateTime.intValue())
            return "updateTime";
        if (type == valueBeforeChange.intValue())
            return "valueBeforeChange";
        if (type == valueSet.intValue())
            return "valueSet";
        if (type == valueChangeTime.intValue())
            return "valueChangeTime";
        if (type == alignIntervals.intValue())
            return "alignIntervals";
        if (type == intervalOffset.intValue())
            return "intervalOffset";
        if (type == lastRestartReason.intValue())
            return "lastRestartReason";
        if (type == loggingType.intValue())
            return "loggingType";
        if (type == restartNotificationRecipients.intValue())
            return "restartNotificationRecipients";
        if (type == timeOfDeviceRestart.intValue())
            return "timeOfDeviceRestart";
        if (type == timeSynchronizationInterval.intValue())
            return "timeSynchronizationInterval";
        if (type == trigger.intValue())
            return "trigger";
        if (type == utcTimeSynchronizationRecipients.intValue())
            return "utcTimeSynchronizationRecipients";
        if (type == nodeSubtype.intValue())
            return "nodeSubtype";
        if (type == nodeType.intValue())
            return "nodeType";
        if (type == structuredObjectList.intValue())
            return "structuredObjectList";
        if (type == subordinateAnnotations.intValue())
            return "subordinateAnnotations";
        if (type == subordinateList.intValue())
            return "subordinateList";
        if (type == actualShedLevel.intValue())
            return "actualShedLevel";
        if (type == dutyWindow.intValue())
            return "dutyWindow";
        if (type == expectedShedLevel.intValue())
            return "expectedShedLevel";
        if (type == fullDutyBaseline.intValue())
            return "fullDutyBaseline";
        if (type == requestedShedLevel.intValue())
            return "requestedShedLevel";
        if (type == shedDuration.intValue())
            return "shedDuration";
        if (type == shedLevelDescriptions.intValue())
            return "shedLevelDescriptions";
        if (type == shedLevels.intValue())
            return "shedLevels";
        if (type == stateDescription.intValue())
            return "stateDescription";
        if (type == doorAlarmState.intValue())
            return "doorAlarmState";
        if (type == doorExtendedPulseTime.intValue())
            return "doorExtendedPulseTime";
        if (type == doorMembers.intValue())
            return "doorMembers";
        if (type == doorOpenTooLongTime.intValue())
            return "doorOpenTooLongTime";
        if (type == doorPulseTime.intValue())
            return "doorPulseTime";
        if (type == doorStatus.intValue())
            return "doorStatus";
        if (type == doorUnlockDelayTime.intValue())
            return "doorUnlockDelayTime";
        if (type == lockStatus.intValue())
            return "lockStatus";
        if (type == maskedAlarmValues.intValue())
            return "maskedAlarmValues";
        if (type == securedStatus.intValue())
            return "securedStatus";
        if (type == absenteeLimit.intValue())
            return "absenteeLimit";
        if (type == accessAlarmEvents.intValue())
            return "accessAlarmEvents";
        if (type == accessDoors.intValue())
            return "accessDoors";
        if (type == accessEvent.intValue())
            return "accessEvent";
        if (type == accessEventAuthenticationFactor.intValue())
            return "accessEventAuthenticationFactor";
        if (type == accessEventCredential.intValue())
            return "accessEventCredential";
        if (type == accessEventTime.intValue())
            return "accessEventTime";
        if (type == accessTransactionEvents.intValue())
            return "accessTransactionEvents";
        if (type == accompaniment.intValue())
            return "accompaniment";
        if (type == accompanimentTime.intValue())
            return "accompanimentTime";
        if (type == activationTime.intValue())
            return "activationTime";
        if (type == activeAuthenticationPolicy.intValue())
            return "activeAuthenticationPolicy";
        if (type == assignedAccessRights.intValue())
            return "assignedAccessRights";
        if (type == authenticationFactors.intValue())
            return "authenticationFactors";
        if (type == authenticationPolicyList.intValue())
            return "authenticationPolicyList";
        if (type == authenticationPolicyNames.intValue())
            return "authenticationPolicyNames";
        if (type == authenticationStatus.intValue())
            return "authenticationStatus";
        if (type == authorizationMode.intValue())
            return "authorizationMode";
        if (type == belongsTo.intValue())
            return "belongsTo";
        if (type == credentialDisable.intValue())
            return "credentialDisable";
        if (type == credentialStatus.intValue())
            return "credentialStatus";
        if (type == credentials.intValue())
            return "credentials";
        if (type == credentialsInZone.intValue())
            return "credentialsInZone";
        if (type == daysRemaining.intValue())
            return "daysRemaining";
        if (type == entryPoints.intValue())
            return "entryPoints";
        if (type == exitPoints.intValue())
            return "exitPoints";
        if (type == expiryTime.intValue())
            return "expiryTime";
        if (type == extendedTimeEnable.intValue())
            return "extendedTimeEnable";
        if (type == failedAttemptEvents.intValue())
            return "failedAttemptEvents";
        if (type == failedAttempts.intValue())
            return "failedAttempts";
        if (type == failedAttemptsTime.intValue())
            return "failedAttemptsTime";
        if (type == lastAccessEvent.intValue())
            return "lastAccessEvent";
        if (type == lastAccessPoint.intValue())
            return "lastAccessPoint";
        if (type == lastCredentialAdded.intValue())
            return "lastCredentialAdded";
        if (type == lastCredentialAddedTime.intValue())
            return "lastCredentialAddedTime";
        if (type == lastCredentialRemoved.intValue())
            return "lastCredentialRemoved";
        if (type == lastCredentialRemovedTime.intValue())
            return "lastCredentialRemovedTime";
        if (type == lastUseTime.intValue())
            return "lastUseTime";
        if (type == lockout.intValue())
            return "lockout";
        if (type == lockoutRelinquishTime.intValue())
            return "lockoutRelinquishTime";
        if (type == maxFailedAttempts.intValue())
            return "maxFailedAttempts";
        if (type == members.intValue())
            return "members";
        if (type == musterPoint.intValue())
            return "musterPoint";
        if (type == negativeAccessRules.intValue())
            return "negativeAccessRules";
        if (type == numberOfAuthenticationPolicies.intValue())
            return "numberOfAuthenticationPolicies";
        if (type == occupancyCount.intValue())
            return "occupancyCount";
        if (type == occupancyCountAdjust.intValue())
            return "occupancyCountAdjust";
        if (type == occupancyCountEnable.intValue())
            return "occupancyCountEnable";
        if (type == occupancyLowerLimit.intValue())
            return "occupancyLowerLimit";
        if (type == occupancyLowerLimitEnforced.intValue())
            return "occupancyLowerLimitEnforced";
        if (type == occupancyState.intValue())
            return "occupancyState";
        if (type == occupancyUpperLimit.intValue())
            return "occupancyUpperLimit";
        if (type == occupancyUpperLimitEnforced.intValue())
            return "occupancyUpperLimitEnforced";
        if (type == passbackMode.intValue())
            return "passbackMode";
        if (type == passbackTimeout.intValue())
            return "passbackTimeout";
        if (type == positiveAccessRules.intValue())
            return "positiveAccessRules";
        if (type == reasonForDisable.intValue())
            return "reasonForDisable";
        if (type == supportedFormats.intValue())
            return "supportedFormats";
        if (type == supportedFormatClasses.intValue())
            return "supportedFormatClasses";
        if (type == threatAuthority.intValue())
            return "threatAuthority";
        if (type == threatLevel.intValue())
            return "threatLevel";
        if (type == traceFlag.intValue())
            return "traceFlag";
        if (type == transactionNotificationClass.intValue())
            return "transactionNotificationClass";
        if (type == userExternalIdentifier.intValue())
            return "userExternalIdentifier";
        if (type == userInformationReference.intValue())
            return "userInformationReference";
        if (type == userName.intValue())
            return "userName";
        if (type == userType.intValue())
            return "userType";
        if (type == usesRemaining.intValue())
            return "usesRemaining";
        if (type == zoneFrom.intValue())
            return "zoneFrom";
        if (type == zoneTo.intValue())
            return "zoneTo";
        if (type == accessEventTag.intValue())
            return "accessEventTag";
        if (type == globalIdentifier.intValue())
            return "globalIdentifier";
        if (type == verificationTime.intValue())
            return "verificationTime";
        if (type == baseDeviceSecurityPolicy.intValue())
            return "baseDeviceSecurityPolicy";
        if (type == distributionKeyRevision.intValue())
            return "distributionKeyRevision";
        if (type == doNotHide.intValue())
            return "doNotHide";
        if (type == keySets.intValue())
            return "keySets";
        if (type == lastKeyServer.intValue())
            return "lastKeyServer";
        if (type == networkAccessSecurityPolicies.intValue())
            return "networkAccessSecurityPolicies";
        if (type == packetReorderTime.intValue())
            return "packetReorderTime";
        if (type == securityPduTimeout.intValue())
            return "securityPduTimeout";
        if (type == securityTimeWindow.intValue())
            return "securityTimeWindow";
        if (type == supportedSecurityAlgorithms.intValue())
            return "supportedSecurityAlgorithms";
        if (type == updateKeySetTimeout.intValue())
            return "updateKeySetTimeout";
        if (type == backupAndRestoreState.intValue())
            return "backupAndRestoreState";
        if (type == backupPreparationTime.intValue())
            return "backupPreparationTime";
        if (type == restoreCompletionTime.intValue())
            return "restoreCompletionTime";
        if (type == restorePreparationTime.intValue())
            return "restorePreparationTime";
        if (type == bitMask.intValue())
            return "bitMask";
        if (type == bitText.intValue())
            return "bitText";
        if (type == isUtc.intValue())
            return "isUtc";
        if (type == groupMembers.intValue())
            return "groupMembers";
        if (type == groupMemberNames.intValue())
            return "groupMemberNames";
        if (type == memberStatusFlags.intValue())
            return "memberStatusFlags";
        if (type == requestedUpdateInterval.intValue())
            return "requestedUpdateInterval";
        if (type == covuPeriod.intValue())
            return "covuPeriod";
        if (type == covuRecipients.intValue())
            return "covuRecipients";
        if (type == eventMessageTexts.intValue())
            return "eventMessageTexts";
        if (type == eventMessageTextsConfig.intValue())
            return "eventMessageTextsConfig";
        if (type == eventDetectionEnable.intValue())
            return "eventDetectionEnable";
        if (type == eventAlgorithmInhibit.intValue())
            return "eventAlgorithmInhibit";
        if (type == eventAlgorithmInhibitRef.intValue())
            return "eventAlgorithmInhibitRef";
        if (type == timeDelayNormal.intValue())
            return "timeDelayNormal";
        if (type == reliabilityEvaluationInhibit.intValue())
            return "reliabilityEvaluationInhibit";
        if (type == faultParameters.intValue())
            return "faultParameters";
        if (type == faultType.intValue())
            return "faultType";
        if (type == localForwardingOnly.intValue())
            return "localForwardingOnly";
        if (type == processIdentifierFilter.intValue())
            return "processIdentifierFilter";
        if (type == subscribedRecipients.intValue())
            return "subscribedRecipients";
        if (type == portFilter.intValue())
            return "portFilter";
        if (type == authorizationExemptions.intValue())
            return "authorizationExemptions";
        if (type == allowGroupDelayInhibit.intValue())
            return "allowGroupDelayInhibit";
        if (type == channelNumber.intValue())
            return "channelNumber";
        if (type == controlGroups.intValue())
            return "controlGroups";
        if (type == executionDelay.intValue())
            return "executionDelay";
        if (type == lastPriority.intValue())
            return "lastPriority";
        if (type == writeStatus.intValue())
            return "writeStatus";
        if (type == propertyList.intValue())
            return "propertyList";
        if (type == serialNumber.intValue())
            return "serialNumber";
        if (type == blinkWarnEnable.intValue())
            return "blinkWarnEnable";
        if (type == defaultFadeTime.intValue())
            return "defaultFadeTime";
        if (type == defaultRampRate.intValue())
            return "defaultRampRate";
        if (type == defaultStepIncrement.intValue())
            return "defaultStepIncrement";
        if (type == egressTime.intValue())
            return "egressTime";
        if (type == inProgress.intValue())
            return "inProgress";
        if (type == instantaneousPower.intValue())
            return "instantaneousPower";
        if (type == lightingCommand.intValue())
            return "lightingCommand";
        if (type == lightingCommandDefaultPriority.intValue())
            return "lightingCommandDefaultPriority";
        if (type == maxActualValue.intValue())
            return "maxActualValue";
        if (type == minActualValue.intValue())
            return "minActualValue";
        if (type == power.intValue())
            return "power";
        if (type == transition.intValue())
            return "transition";
        if (type == egressActive.intValue())
            return "egressActive";
        return "Unknown: " + type;
    }
}
