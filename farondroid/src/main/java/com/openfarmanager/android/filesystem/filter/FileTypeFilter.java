package com.openfarmanager.android.filesystem.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Vlad Namashko
 */
public class FileTypeFilter implements java.io.FileFilter {

    private boolean mIsIncludeFiles;
    private boolean mIsIncludeFolders;

    private FileFilter mAdditionalFilter;

    public FileTypeFilter(boolean isIncludeFiles, boolean isIncludeFolders) {
        mIsIncludeFiles = isIncludeFiles;
        mIsIncludeFolders = isIncludeFolders;
    }

    public FileFilter addFilter(FileFilter fileFilter) {
        mAdditionalFilter = fileFilter;
        return this;
    }

    @Override
    public boolean accept(File file) {
        return (mAdditionalFilter == null || mAdditionalFilter.accept(file)) && (file.isDirectory() ? mIsIncludeFolders : mIsIncludeFiles);
    }
}
