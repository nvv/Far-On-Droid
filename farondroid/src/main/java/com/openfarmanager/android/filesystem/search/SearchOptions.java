package com.openfarmanager.android.filesystem.search;

/**
 * @author Vlad Namashko
 */
public class SearchOptions {

    public String fileMask;
    public String keyword;
    public boolean caseSensitive;
    public boolean wholeWords;

    public boolean isNetworkPanel;

    public SearchOptions(String fileMask, String keyword, boolean caseSensitive, boolean wholeWords) {
        this.fileMask = fileMask;
        this.keyword = keyword;
        this.caseSensitive = caseSensitive;
        this.wholeWords = wholeWords;
    }

    public SearchOptions setIsNetowrkPanel(boolean isNetworkPanel) {
        this.isNetworkPanel = isNetworkPanel;
        return this;
    }
}
