package com.openfarmanager.android.filesystem.filter;

import com.openfarmanager.android.filesystem.FileProxy;

/**
 * Vlad Namashko
 */
public interface Filter {
    int doFilter(FileProxy file1, FileProxy file2);
}
