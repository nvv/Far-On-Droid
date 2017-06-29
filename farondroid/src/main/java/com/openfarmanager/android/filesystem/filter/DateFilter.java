package com.openfarmanager.android.filesystem.filter;

import com.openfarmanager.android.model.SelectParams;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Vlad Namashko
 */
public class DateFilter extends FileFilter {

    public DateFilter(SelectParams params) {
        super(params);

        mPredicate = file -> {
            if (params.isTodayDate()) {
                Calendar today = Calendar.getInstance();
                Calendar currentDay = Calendar.getInstance();
                currentDay.setTime(new Date(file.lastModifiedDate()));
                return isSameDay(today, currentDay);
            } else {
                long startDate = params.getDateFrom().getTime();
                long endDate = params.getDateTo().getTime();
                return file.lastModifiedDate() > startDate && file.lastModifiedDate() < endDate;
            }
        };
    }

    /**
     * <p>Checks if two calendars represent the same day ignoring time.</p>
     * @param cal1  the first calendar, not altered, not null
     * @param cal2  the second calendar, not altered, not null
     * @return true if they represent the same day
     * @throws IllegalArgumentException if either calendar is <code>null</code>
     */
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

}
