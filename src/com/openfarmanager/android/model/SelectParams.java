package com.openfarmanager.android.model;

import java.util.Date;

/**
 * author: Vlad Namashko
 */
public class SelectParams {

    private SelectionType mType;
    private String mSelectionString;
    private boolean mInverseSelection;
    private boolean mTodayDate;
    private Date mDateFrom;
    private Date mDateTo;

    public SelectParams(SelectionType type, String selectionString, boolean inverseSelection, boolean todayDate, Date dateFrom, Date dateTo) {
        mType = type;
        mSelectionString = selectionString;
        mInverseSelection = inverseSelection;
        mTodayDate = todayDate;
        mDateFrom = dateFrom;
        mDateTo = dateTo;
    }

    public SelectionType getType() {
        return mType;
    }

    public String getSelectionString() {
        return mSelectionString;
    }

    public boolean isInverseSelection() {
        return mInverseSelection;
    }

    public boolean isTodayDate() {
        return mTodayDate;
    }

    public Date getDateFrom() {
        return mDateFrom;
    }

    public Date getDateTo() {
        return mDateTo;
    }


    public static enum SelectionType {
        NAME, MODIFICATION_DATE
    }
}
