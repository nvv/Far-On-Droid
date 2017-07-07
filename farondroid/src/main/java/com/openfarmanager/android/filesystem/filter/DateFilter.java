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
//            long startDate = params.getDateFrom().getTime();
//            long endDate = params.getDateTo().getTime();
//            return file.lastModifiedDate() > startDate && file.lastModifiedDate() < endDate;
            return (params.getDateFrom() == null || file.lastModifiedDate() > params.getDateFrom().getTime()) &&
                    (params.getDateTo() == null || file.lastModifiedDate() < params.getDateTo().getTime());

        };
    }

}
