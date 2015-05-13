package com.serotonin.bacnet4j.obj;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.CalendarEntry;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Date;
import com.serotonin.bacnet4j.util.ClockTimeSource;
import com.serotonin.bacnet4j.util.TimeSource;

public class CalendarObject extends BACnetObject {
    private static final long serialVersionUID = 4337001513572175841L;

    private int timeTolerance = 0;

    private TimeSource timeSource = new ClockTimeSource();

    // This timer task keeps the present value up to date in case other objects have registered
    // for COV on it.
    private Refresher presentValueRefersher;

    public CalendarObject(int instanceNumber, String name, SequenceOf<CalendarEntry> dateList) {
        super(ObjectType.calendar, instanceNumber, name);

        writePropertyImpl(PropertyIdentifier.dateList, dateList);
        updatePresentValue();

        addMixin(new CalendarMixin(this));
    }

    /**
     * Used by the test cases.
     */
    void setTimeSource(TimeSource timeSource) {
        this.timeSource = timeSource;
    }

    public int getTimeTolerance() {
        return timeTolerance;
    }

    /**
     * To compensate for clock variances if the time is close to midnight, pretend that it is already the next day.
     * This protects against schedules on devices that with clocks that are a bit ahead of ours, so that they get
     * the correct calendar value even if they ask for it a bit too early.
     */
    public void setTimeTolerance(int timeTolerance) {
        this.timeTolerance = timeTolerance;
    }

    @Override
    public void addedToDevice() {
        // Schedule a timer task to run every hour. This way we don't need to worry 
        // about daylight savings time changeovers.
        presentValueRefersher = new Refresher();

        // Calculate the amount of time until the next hour.
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(timeSource.currentTimeMillis());
        long elapsed = gc.get(Calendar.MILLISECOND) //
                + gc.get(Calendar.SECOND) * 1000 //
                + gc.get(Calendar.MINUTE) * 60 * 1000;
        long hour = 1000 * 60 * 60;
        long delay = hour - elapsed + 10; // Add a few milliseconds for fun.

        // Delay until the top of the next hour, and then run every hour.
        getLocalDevice().getTimer().scheduleAtFixedRate(presentValueRefersher, delay, hour);
    }

    @Override
    public void removedFromDevice() {
        if (presentValueRefersher != null) {
            presentValueRefersher.cancel();
            presentValueRefersher = null;
        }
    }

    class Refresher extends TimerTask {
        @Override
        public void run() {
            updatePresentValue();
        }
    }

    class CalendarMixin extends AbstractMixin {
        public CalendarMixin(BACnetObject bo) {
            super(bo);
        }

        @Override
        protected void beforeReadProperty(PropertyIdentifier pid) {
            if (pid.equals(PropertyIdentifier.presentValue))
                // Ensure that the present value gets updated before the read is performed.
                // TODO it could make a bit of sense to only run this again after some timeout, in
                // case a date range check takes a long time.
                updatePresentValue();
        }

        @Override
        protected boolean validateProperty(PropertyValue value) throws BACnetServiceException {
            if (PropertyIdentifier.presentValue.equals(value.getPropertyIdentifier()))
                throw new BACnetServiceException(ErrorClass.property, ErrorCode.writeAccessDenied);
            return false;
        }

        @Override
        protected void afterWriteProperty(PropertyIdentifier pid, Encodable oldValue, Encodable newValue) {
            if (PropertyIdentifier.dateList.equals(pid))
                updatePresentValue();
        }
    }

    synchronized void updatePresentValue() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(timeSource.currentTimeMillis());

        if (timeTolerance > 0) {
            // And on the compensatory time.
            gc.add(Calendar.MILLISECOND, timeTolerance);
        }

        updatePresentValue(new Date(gc));
    }

    private void updatePresentValue(Date date) {
        SequenceOf<CalendarEntry> dateList = get(PropertyIdentifier.dateList);

        boolean match = false;
        for (CalendarEntry e : dateList) {
            if (e.matches(date)) {
                match = true;
                break;
            }
        }

        writePropertyImpl(PropertyIdentifier.presentValue, match ? Boolean.TRUE : Boolean.FALSE);
    }
}
