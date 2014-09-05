package com.openfarmanager.android.model;

public class Bookmark {

    private long mBookmarkId;
    private String mBookmarkPath;
    private String mBookmarkLabel;

    public Bookmark(String bookmarkPath, String bookmarkLabel) {
        mBookmarkPath = bookmarkPath;
        mBookmarkLabel = bookmarkLabel;
    }

    public Bookmark(String bookmarkPath) {
        mBookmarkPath = bookmarkPath;
        String[] tokens = bookmarkPath.split("/");
        mBookmarkLabel = tokens[tokens.length - 1];
    }

    public String getBookmarkPath() {
        return mBookmarkPath;
    }

    public void setBookmarkPath(String bookmarkPath) {
        mBookmarkPath = bookmarkPath;
    }

    public String getBookmarkLabel() {
        return mBookmarkLabel;
    }

    public void setBookmarkLabel(String bookmarkLabel) {
        mBookmarkLabel = bookmarkLabel;
    }

    public long getBookmarkId() {
        return mBookmarkId;
    }

    public void setBookmarkId(long bookmarkId) {
        mBookmarkId = bookmarkId;
    }
}
