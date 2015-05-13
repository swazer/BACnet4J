package com.serotonin.bacnet4j.type.constructed;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AccessRule extends BaseType {
    private static final long serialVersionUID = -6428991796769519973L;

    private final TimeRangeSpecifier timeRangeSpecifier;
    private final DeviceObjectPropertyReference timeRange;
    private final LocationSpecifier locationSpecifier;
    private final DeviceObjectReference location;
    private final Boolean enable;

    public AccessRule(Boolean enable) {
        this(TimeRangeSpecifier.always, null, LocationSpecifier.all, null, enable);
    }

    public AccessRule(DeviceObjectPropertyReference timeRange, Boolean enable) {
        this(TimeRangeSpecifier.specified, timeRange, LocationSpecifier.all, null, enable);
    }

    public AccessRule(DeviceObjectReference location, Boolean enable) {
        this(TimeRangeSpecifier.always, null, LocationSpecifier.specified, location, enable);
    }

    public AccessRule(DeviceObjectPropertyReference timeRange, DeviceObjectReference location, Boolean enable) {
        this(TimeRangeSpecifier.specified, timeRange, LocationSpecifier.specified, location, enable);
    }

    private AccessRule(TimeRangeSpecifier timeRangeSpecifier, DeviceObjectPropertyReference timeRange,
            LocationSpecifier locationSpecifier, DeviceObjectReference location, Boolean enable) {
        this.timeRangeSpecifier = timeRangeSpecifier;
        this.timeRange = timeRange;
        this.locationSpecifier = locationSpecifier;
        this.location = location;
        this.enable = enable;
    }

    @Override
    public void write(ByteQueue queue) {
        write(queue, timeRangeSpecifier, 0);
        writeOptional(queue, timeRange, 1);
        write(queue, locationSpecifier, 2);
        writeOptional(queue, location, 3);
        write(queue, enable, 4);
    }

    public AccessRule(ByteQueue queue) throws BACnetException {
        timeRangeSpecifier = read(queue, TimeRangeSpecifier.class, 0);
        timeRange = readOptional(queue, DeviceObjectPropertyReference.class, 1);
        locationSpecifier = read(queue, LocationSpecifier.class, 2);
        location = readOptional(queue, DeviceObjectReference.class, 3);
        enable = read(queue, Boolean.class, 4);
    }

    public TimeRangeSpecifier getTimeRangeSpecifier() {
        return timeRangeSpecifier;
    }

    public DeviceObjectPropertyReference getTimeRange() {
        return timeRange;
    }

    public LocationSpecifier getLocationSpecifier() {
        return locationSpecifier;
    }

    public DeviceObjectReference getLocation() {
        return location;
    }

    public Boolean getEnable() {
        return enable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((enable == null) ? 0 : enable.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((locationSpecifier == null) ? 0 : locationSpecifier.hashCode());
        result = prime * result + ((timeRange == null) ? 0 : timeRange.hashCode());
        result = prime * result + ((timeRangeSpecifier == null) ? 0 : timeRangeSpecifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccessRule other = (AccessRule) obj;
        if (enable == null) {
            if (other.enable != null)
                return false;
        }
        else if (!enable.equals(other.enable))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        }
        else if (!location.equals(other.location))
            return false;
        if (locationSpecifier == null) {
            if (other.locationSpecifier != null)
                return false;
        }
        else if (!locationSpecifier.equals(other.locationSpecifier))
            return false;
        if (timeRange == null) {
            if (other.timeRange != null)
                return false;
        }
        else if (!timeRange.equals(other.timeRange))
            return false;
        if (timeRangeSpecifier == null) {
            if (other.timeRangeSpecifier != null)
                return false;
        }
        else if (!timeRangeSpecifier.equals(other.timeRangeSpecifier))
            return false;
        return true;
    }

    public static class TimeRangeSpecifier extends Enumerated {
        private static final long serialVersionUID = -7473910477809643702L;

        public static final TimeRangeSpecifier specified = new TimeRangeSpecifier(0);
        public static final TimeRangeSpecifier always = new TimeRangeSpecifier(1);

        public static final TimeRangeSpecifier[] ALL = { specified, always, };

        public TimeRangeSpecifier(int value) {
            super(value);
        }

        public TimeRangeSpecifier(ByteQueue queue) {
            super(queue);
        }

        @Override
        public String toString() {
            int type = intValue();
            if (type == specified.intValue())
                return "specified";
            if (type == always.intValue())
                return "always";
            return "Unknown(" + type + ")";
        }
    }

    public static class LocationSpecifier extends Enumerated {
        private static final long serialVersionUID = 443515896869395401L;

        public static final LocationSpecifier specified = new LocationSpecifier(0);
        public static final LocationSpecifier all = new LocationSpecifier(1);

        public static final LocationSpecifier[] ALL = { specified, all, };

        public LocationSpecifier(int value) {
            super(value);
        }

        public LocationSpecifier(ByteQueue queue) {
            super(queue);
        }

        @Override
        public String toString() {
            int type = intValue();
            if (type == specified.intValue())
                return "specified";
            if (type == all.intValue())
                return "all";
            return "Unknown(" + type + ")";
        }
    }
}
