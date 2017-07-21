package com.openfarmanager.android.filesystem.search;


import com.openfarmanager.android.utils.CustomFormatter;

import java.util.Date;

/**
 * @author Vlad Namashko
 */
public class SearchOptions {

    public String fileMask;
    public String keyword;
    public boolean caseSensitive;
    public boolean wholeWords;

    public boolean includeFiles;
    public boolean includeFolders;

    public long biggerThenSizeBytes = -1;
    public long smallerThenSizeBytes = -1;
    public Date dateBefore;
    public Date dateAfter;

    public boolean isNetworkPanel;

    public void setMaxSizeRestriction(int biggerThenSize, int biggerThenUnit) {
        biggerThenSizeBytes = CustomFormatter.convertToBytes(biggerThenSize, biggerThenUnit);
    }

    public void setMinSizeRestriction(int smallerThenSize, int smallerThenUnit) {
        smallerThenSizeBytes = CustomFormatter.convertToBytes(smallerThenSize, smallerThenUnit);
    }

    public SearchOptions setIsNetworkPanel(boolean isNetworkPanel) {
        this.isNetworkPanel = isNetworkPanel;
        return this;
    }
}
