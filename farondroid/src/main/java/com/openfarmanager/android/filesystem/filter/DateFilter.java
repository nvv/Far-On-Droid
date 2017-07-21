package com.openfarmanager.android.filesystem.filter;

import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.SelectParams;

import java.util.Date;

/**
 * @author Vlad Namashko
 */
public class DateFilter extends FileFilter {

    public DateFilter(SelectParams params) {
        super(params);

//        mPredicate = file -> (params.getDateFrom() == null || file.lastModifiedDate() > params.getDateFrom().getTime()) &&
//                (params.getDateTo() == null || file.lastModifiedDate() < params.getDateTo().getTime());

        mPredicate = file -> fileFilter(file, params.getDateFrom(), params.getDateTo());
    }

    public static boolean fileFilter(FileProxy file, Date dateFrom, Date dateTo) {
        return (dateFrom == null || file.lastModifiedDate() > dateFrom.getTime()) &&
                (dateTo == null || file.lastModifiedDate() < dateTo.getTime());
    }

}
