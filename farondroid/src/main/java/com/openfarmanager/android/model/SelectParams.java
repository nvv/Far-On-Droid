package com.openfarmanager.android.model;

import java.util.Date;

/**
 * author: Vlad Namashko
 */
public class SelectParams {

    private SelectionType mType;
    private String mSelectionString;
    private boolean mInverseSelection;
    private boolean mCaseSensitive;
    private boolean mIncludeFiles;
    private boolean mIncludeFolders;
    private Date mDateFrom;
    private Date mDateTo;

    public SelectParams(String selectionString, boolean caseSensitive, boolean inverseSelection, boolean includeFiles, boolean includeFolders) {
        mType = SelectionType.NAME;
        mSelectionString = selectionString;
        mInverseSelection = inverseSelection;
        mCaseSensitive = caseSensitive;
        mIncludeFiles = includeFiles;
        mIncludeFolders = includeFolders;
    }

    public SelectParams(Date dateFrom, Date dateTo, boolean inverseSelection, boolean includeFiles, boolean includeFolders) {
        mType = SelectionType.MODIFICATION_DATE;
        mDateFrom = dateFrom;
        mDateTo = dateTo;
        mInverseSelection = inverseSelection;
        mIncludeFiles = includeFiles;
        mIncludeFolders = includeFolders;
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

    public Date getDateFrom() {
        return mDateFrom;
    }

    public Date getDateTo() {
        return mDateTo;
    }

    public boolean isIncludeFiles() {
        return mIncludeFiles;
    }

    public boolean isIncludeFolders() {
        return mIncludeFolders;
    }

    public boolean isCaseSensitive() {
        return mCaseSensitive;
    }


    public static enum SelectionType {
        NAME, MODIFICATION_DATE
    }
}
