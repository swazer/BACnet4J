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
package com.serotonin.bacnet4j.obj;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.ResponseConsumer;
import com.serotonin.bacnet4j.apdu.AckAPDU;
import com.serotonin.bacnet4j.enums.DayOfWeek;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.obj.mixin.HasStatusFlagsMixin;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.NoneAlgo;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.BACnetArray;
import com.serotonin.bacnet4j.type.constructed.DailySchedule;
import com.serotonin.bacnet4j.type.constructed.DateRange;
import com.serotonin.bacnet4j.type.constructed.DateTime;
import com.serotonin.bacnet4j.type.constructed.DeviceObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.SpecialEvent;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.constructed.TimeValue;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Reliability;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Date;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Primitive;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.ClockTimeSource;
import com.serotonin.bacnet4j.util.TimeSource;
import com.serotonin.bacnet4j.util.sero.Utils;

public class ScheduleObject<T extends Primitive> extends BACnetObject {
    private static final long serialVersionUID = 1660874501874089852L;
    static final Logger LOG = LoggerFactory.getLogger(ScheduleObject.class);

    private TimeSource timeSource = new ClockTimeSource();
    private Refresher presentValueRefersher;

    /**
     * A proprietary mechanism to periodically write the present value to all property references in case of power
     * failures, restarts, and the like.
     */
    private PeriodicWriter periodicWriter;

    public ScheduleObject(int instanceNumber, String name, DateRange effectivePeriod,
            BACnetArray<DailySchedule> weeklySchedule, SequenceOf<SpecialEvent> exceptionSchedule, T scheduleDefault,
            SequenceOf<DeviceObjectPropertyReference> listOfObjectPropertyReferences, int priorityForWriting,
            boolean outOfService) {
        super(ObjectType.schedule, instanceNumber, name);

        if (effectivePeriod == null)
            throw new BACnetRuntimeException("effectivePeriod cannot be null");
        if (weeklySchedule == null && exceptionSchedule == null)
            throw new BACnetRuntimeException("Both weeklySchedule and exceptionSchedule cannot be null");
        if (scheduleDefault == null)
            throw new BACnetRuntimeException("scheduleDefault cannot be null");
        if (listOfObjectPropertyReferences == null)
            throw new BACnetRuntimeException("listOfObjectPropertyReferences cannot be null");

        writePropertyImpl(PropertyIdentifier.effectivePeriod, effectivePeriod);
        if (weeklySchedule != null) {
            if (weeklySchedule.getCount() != 7)
                throw new BACnetRuntimeException("weeklySchedule must have 7 elements");
            writePropertyImpl(PropertyIdentifier.weeklySchedule, weeklySchedule);
        }
        if (exceptionSchedule != null)
            writePropertyImpl(PropertyIdentifier.exceptionSchedule, exceptionSchedule);
        writePropertyImpl(PropertyIdentifier.scheduleDefault, scheduleDefault);
        writePropertyImpl(PropertyIdentifier.presentValue, scheduleDefault);
        writePropertyImpl(PropertyIdentifier.listOfObjectPropertyReferences, listOfObjectPropertyReferences);
        writePropertyImpl(PropertyIdentifier.priorityForWriting, new UnsignedInteger(priorityForWriting));
        writePropertyImpl(PropertyIdentifier.reliability, Reliability.noFaultDetected);
        writePropertyImpl(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(
                outOfService));
        writePropertyImpl(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, outOfService));

        addMixin(new HasStatusFlagsMixin(this));
        addMixin(new ScheduleMixin(this));

        // Validations
        // 1) entries in the list of property references must reference properties of this type
        // 2) time value entries in the weekly and exception schedules must be of this type
        // 3) time values must have times that are fully specific.
    }

    public void supportIntrinsicReporting(int notificationClass, EventTransitionBits eventEnable, NotifyType notifyType) {
        // Prepare the object with all of the properties that intrinsic reporting will need.
        // User-defined properties
        writePropertyImpl(PropertyIdentifier.notificationClass, new UnsignedInteger(notificationClass));
        writePropertyImpl(PropertyIdentifier.eventEnable, eventEnable);
        writePropertyImpl(PropertyIdentifier.eventState, EventState.normal);
        writePropertyImpl(PropertyIdentifier.notifyType, notifyType);

        // Now add the mixin.
        addMixin(new IntrinsicReportingMixin(this, new NoneAlgo(this), null, new PropertyIdentifier[0],
                new PropertyIdentifier[0]));
    }

    /**
     * Starts the internal periodic writer.
     * 
     * @param delay
     *            the delay before the first execution, in milliseconds.
     * @param period
     *            the period between executions, in milliseconds.
     */
    public void startPeriodicWriter(long delay, long period) {
        if (delay < 0)
            throw new IllegalArgumentException("delay cannot be < 0");
        if (period < 1)
            throw new IllegalArgumentException("period cannot be < 1");

        cancelPeriodicWriter();
        periodicWriter = new PeriodicWriter();
        getLocalDevice().getTimer().scheduleAtFixedRate(periodicWriter, delay, period);
        LOG.debug("Periodic writer started");
    }

    public void stopPeriodicWriter() {
        cancelPeriodicWriter();
    }

    /**
     * Used by the test cases.
     */
    void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    @Override
    public void addedToDevice() {
        T oldValue = get(PropertyIdentifier.presentValue);
        updatePresentValue();
        T newValue = get(PropertyIdentifier.presentValue);
        // If the present value didn't change after the update, then no write would have been done. So, to ensure
        // initialization of the objects in the list, force a write.
        if (Utils.equals(oldValue, newValue))
            doWrites(newValue);
    }

    @Override
    public void removedFromDevice() {
        cancelRefresher();
        cancelPeriodicWriter();
    }

    class Refresher extends TimerTask {
        @Override
        public void run() {
            updatePresentValue();
        }
    }

    class PeriodicWriter extends TimerTask {
        @Override
        public void run() {
            forceWrites();
        }
    }

    synchronized public void forceWrites() {
        doWrites(get(PropertyIdentifier.presentValue));
    }

    class ScheduleMixin extends AbstractMixin {
        public ScheduleMixin(BACnetObject bo) {
            super(bo);
        }

        @Override
        protected boolean validateProperty(PropertyValue value) throws BACnetServiceException {
            if (PropertyIdentifier.presentValue.equals(value.getPropertyIdentifier())) {
                Boolean outOfService = get(PropertyIdentifier.outOfService);
                if (!outOfService.booleanValue())
                    throw new BACnetServiceException(ErrorClass.property, ErrorCode.writeAccessDenied);
            }
            return false;
        }

        @Override
        protected void afterWriteProperty(PropertyIdentifier pid, Encodable oldValue, Encodable newValue) {
            if (Utils.equals(newValue, oldValue))
                return;

            if (pid.isOneOf(PropertyIdentifier.effectivePeriod, PropertyIdentifier.weeklySchedule,
                    PropertyIdentifier.exceptionSchedule, PropertyIdentifier.scheduleDefault))
                updatePresentValue();
            if (pid.equals(PropertyIdentifier.presentValue))
                doWrites(newValue);
        }
    }

    private void cancelRefresher() {
        if (presentValueRefersher != null) {
            presentValueRefersher.cancel();
            presentValueRefersher = null;
        }
    }

    private void cancelPeriodicWriter() {
        if (periodicWriter != null) {
            periodicWriter.cancel();
            periodicWriter = null;
        }
    }

    synchronized void updatePresentValue() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(timeSource.currentTimeMillis());
        updatePresentValue(new DateTime(gc));
    }

    @SuppressWarnings("unchecked")
    private void updatePresentValue(DateTime now) {
        cancelRefresher();

        T newValue;
        long nextCheck;

        T scheduleDefault = get(PropertyIdentifier.scheduleDefault);
        DateRange effectivePeriod = get(PropertyIdentifier.effectivePeriod);
        if (!effectivePeriod.matches(now.getDate())) {
            // Not in the current effective date.
            newValue = scheduleDefault;
            nextCheck = nextDay(now);
        }
        else {
            SequenceOf<TimeValue> schedule = null;

            // Is there an exception schedule in effect?
            SpecialEvent specialEvent = findExceptionSchedule(now);
            if (specialEvent != null)
                schedule = specialEvent.getListOfTimeValues();
            else {
                DailySchedule dailySchedule = findDailySchedule(now);
                if (dailySchedule != null)
                    schedule = dailySchedule.getDaySchedule();
            }

            if (schedule == null) {
                newValue = scheduleDefault;
                nextCheck = nextDay(now);
            }
            else {
                // Find the schedule entry in effect at this time.
                TimeValue currentTv = null;
                int tvIndex = schedule.getCount();
                for (; tvIndex > 0; tvIndex--) {
                    TimeValue tv = schedule.get(tvIndex);

                    if (!tv.getTime().after(now.getTime())) {
                        // Found a time value entry that can be used.
                        currentTv = tv;
                        break;
                    }
                }

                // Determine the new present value.
                if (currentTv == null)
                    newValue = scheduleDefault;
                else
                    newValue = (T) currentTv.getValue();

                // Determine the next time this method should run.
                if (tvIndex < schedule.getCount()) {
                    TimeValue nextTv = schedule.get(tvIndex + 1);
                    nextCheck = timeOf(now.getDate(), nextTv);
                }
                else
                    nextCheck = nextDay(now);
            }
        }

        writePropertyImpl(PropertyIdentifier.presentValue, newValue);

        presentValueRefersher = new Refresher();
        java.util.Date nextRuntime = new java.util.Date(nextCheck);
        getLocalDevice().getTimer().schedule(presentValueRefersher, nextRuntime);
        LOG.debug("Timer scheduled to run at {}", nextRuntime);
    }

    private long nextDay(DateTime now) {
        GregorianCalendar gc = now.getGC();
        gc.add(Calendar.DATE, 1);
        gc.add(Calendar.HOUR_OF_DAY, -gc.get(Calendar.HOUR_OF_DAY));
        gc.add(Calendar.MINUTE, -gc.get(Calendar.MINUTE));
        gc.add(Calendar.SECOND, -gc.get(Calendar.SECOND));
        gc.add(Calendar.MILLISECOND, -gc.get(Calendar.MILLISECOND));
        return gc.getTimeInMillis();
    }

    private long timeOf(Date date, TimeValue tv) {
        DateTime dt = new DateTime(date, tv.getTime());
        return dt.getGC().getTimeInMillis();
    }

    private SpecialEvent findExceptionSchedule(DateTime now) {
        SequenceOf<SpecialEvent> exceptionSchedule = get(PropertyIdentifier.exceptionSchedule);
        if (exceptionSchedule == null)
            return null;

        SpecialEvent best = null;
        for (SpecialEvent e : exceptionSchedule) {
            boolean active;
            if (e.isCalendarReference()) {
                CalendarObject co = (CalendarObject) getLocalDevice().getObject(e.getCalendarReference());
                if (co != null) {
                    Boolean pv;
                    try {
                        // Getting the property this way ensures that the calendar's present value gets is calculated.
                        pv = co.getProperty(PropertyIdentifier.presentValue);
                        active = pv.booleanValue();
                    }
                    catch (BACnetServiceException e1) {
                        // Should never happen.
                        throw new RuntimeException(e1);
                    }
                }
                else
                    active = false;
            }
            else
                active = e.getCalendarEntry().matches(now.getDate());

            if (active) {
                if (best == null || best.getEventPriority().intValue() > e.getEventPriority().intValue())
                    best = e;
            }
        }
        return best;
    }

    private DailySchedule findDailySchedule(DateTime now) {
        BACnetArray<DailySchedule> weeklySchedule = get(PropertyIdentifier.weeklySchedule);
        if (weeklySchedule == null)
            return null;

        DayOfWeek dow = now.getDate().getDayOfWeek();
        if (!dow.isSpecific())
            dow = DayOfWeek.forDate(now.getDate());

        return weeklySchedule.get(dow.getId());
    }

    void doWrites(Encodable value) {
        SequenceOf<DeviceObjectPropertyReference> listOfObjectPropertyReferences = get(PropertyIdentifier.listOfObjectPropertyReferences);
        UnsignedInteger priorityForWriting = get(PropertyIdentifier.priorityForWriting);

        // Send the write requests.
        for (DeviceObjectPropertyReference dopr : listOfObjectPropertyReferences) {
            LOG.debug("Sending write request to {} in {}, value={}, priority={}", dopr.getObjectIdentifier(),
                    dopr.getDeviceIdentifier(), value, priorityForWriting);

            if (dopr.getDeviceIdentifier() == null) {
                // Local write.
                BACnetObject that = getLocalDevice().getObject(dopr.getObjectIdentifier());
                try {
                    that.writeProperty(new PropertyValue(dopr.getPropertyIdentifier(), dopr.getPropertyArrayIndex(),
                            value, priorityForWriting));
                }
                catch (BACnetServiceException e) {
                    LOG.warn("Schedule failed to write to local object {}", dopr.getObjectIdentifier(), e);
                }
            }
            else {
                final ObjectIdentifier devId = dopr.getDeviceIdentifier();
                final ObjectIdentifier oid = dopr.getObjectIdentifier();
                RemoteDevice d = getLocalDevice().getRemoteDeviceImpl(devId.getInstanceNumber());
                if (d == null)
                    LOG.warn("Schedule failed to write to unknown remote device {}", devId);
                else {
                    WritePropertyRequest req = new WritePropertyRequest(oid, dopr.getPropertyIdentifier(),
                            dopr.getPropertyArrayIndex(), value, priorityForWriting);
                    getLocalDevice().send(d, req, new ResponseConsumer() {
                        @Override
                        public void success(AcknowledgementService ack) {
                            // Whatever.
                        }

                        @Override
                        public void fail(AckAPDU ack) {
                            LOG.warn("Schedule failed to write to {} in {}, ack={}", oid, devId, ack);
                        }

                        @Override
                        public void ex(BACnetException e) {
                            LOG.error("Schedule failed to write to {} in {}", oid, devId, e);
                        }
                    });
                }
            }
        }
    }
}
