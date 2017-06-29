package com.openfarmanager.android.filesystem.filter;

import com.openfarmanager.android.filesystem.FileProxy;

/**
 * Vlad Namashko
 */
public interface Sorter {
    int doSort(FileProxy file1, FileProxy file2);
}
