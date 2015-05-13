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
package com.serotonin.bacnet4j.type.constructed;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.serotonin.bacnet4j.enums.DayOfWeek;
import com.serotonin.bacnet4j.enums.Month;
import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.type.DateMatchable;
import com.serotonin.bacnet4j.type.primitive.Date;
import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class WeekNDay extends OctetString implements DateMatchable {
    private static final long serialVersionUID = -2836161294089567458L;

    public static class WeekOfMonth extends Enumerated {
        private static final long serialVersionUID = 1951617360223950570L;
        public static final WeekOfMonth days1to7 = new WeekOfMonth(1);
        public static final WeekOfMonth days8to14 = new WeekOfMonth(2);
        public static final WeekOfMonth days15to21 = new WeekOfMonth(3);
        public static final WeekOfMonth days22to28 = new WeekOfMonth(4);
        public static final WeekOfMonth days29to31 = new WeekOfMonth(5);
        public static final WeekOfMonth last7Days = new WeekOfMonth(6);
        public static final WeekOfMonth any = new WeekOfMonth(255);

        public static WeekOfMonth valueOf(byte b) {
            switch (b) {
            case 1:
                return days1to7;
            case 2:
                return days8to14;
            case 3:
                return days15to21;
            case 4:
                return days22to28;
            case 5:
                return days29to31;
            case 6:
                return last7Days;
            default:
                return any;
            }
        }

        private WeekOfMonth(int value) {
            super(value);
        }

        public WeekOfMonth(ByteQueue queue) {
            super(queue);
        }
    }

    public WeekNDay(Month month, WeekOfMonth weekOfMonth, DayOfWeek dayOfWeek) {
        super(new byte[] { month.getId(), weekOfMonth.byteValue(), (byte) dayOfWeek.getId() });
    }

    public Month getMonth() {
        return Month.valueOf(getBytes()[0]);
    }

    public WeekOfMonth getWeekOfMonth() {
        return WeekOfMonth.valueOf(getBytes()[1]);
    }

    public DayOfWeek getDayOfWeek() {
        return DayOfWeek.valueOf(getBytes()[2]);
    }

    public WeekNDay(ByteQueue queue) {
        super(queue);
    }

    @Override
    public boolean matches(Date that) {
        if (!that.isSpecific())
            throw new BACnetRuntimeException("Dates for matching must be completely specified: " + that);

        if (!getMonth().matches(that.getMonth()))
            return false;

        if (!matchWeekOfMonth(that))
            return false;

        if (!getDayOfWeek().matches(that))
            return false;

        return true;
    }

    private boolean matchWeekOfMonth(Date that) {
        WeekOfMonth wom = getWeekOfMonth();
        if (wom.equals(WeekOfMonth.any))
            return true;
        int day = that.getDay();
        if (wom.equals(WeekOfMonth.days1to7))
            return day >= 1 && day <= 7;
        if (wom.equals(WeekOfMonth.days8to14))
            return day >= 8 && day <= 14;
        if (wom.equals(WeekOfMonth.days15to21))
            return day >= 15 && day <= 21;
        if (wom.equals(WeekOfMonth.days22to28))
            return day >= 22 && day <= 28;
        if (wom.equals(WeekOfMonth.days29to31))
            return day >= 29 && day <= 31;

        // Calculate the last day of the month.
        GregorianCalendar gc = that.calculateGC();
        int lastDay = gc.getActualMaximum(Calendar.DATE);
        return day >= (lastDay - 6) && day <= lastDay;
    }
}
