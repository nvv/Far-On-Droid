package com.openfarmanager.android.filesystem.filter;

import android.content.res.Resources;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.utils.Extensions;

import java.text.Collator;

/**
 * Vlad Namashko
 */
public class FilterFactory {

    private static Collator sCollator;

    static {
        sCollator = Collator.getInstance();
        sCollator.setStrength(Collator.SECONDARY);
    }

    public static Filter createDirectoryUpFilter() {
        return new Filter() {
            public int doFilter(FileProxy file1, FileProxy file2) {
                return -Boolean.valueOf(file1.isDirectory()).compareTo(file2.isDirectory());
            }
        };
    }

    public static Filter createAlphabeticFilter() {
        return new Filter() {
            public int doFilter(FileProxy file1, FileProxy file2) {
                return sCollator.compare(file1.getName(), file2.getName());
            }
        };
    }

    public static Filter createSizeFilter() {
        return new Filter() {
            public int doFilter(FileProxy file1, FileProxy file2) {
                return Long.valueOf(file1.getSize()).compareTo(file2.getSize());
            }
        };
    }

    public static Filter createExtensionFilter() {
        return new Filter() {
            public int doFilter(FileProxy file1, FileProxy file2) {
                String name1 = file1.getName();
                String name2 = file2.getName();

                final int name1Dot = name1.lastIndexOf('.');
                final int name2Dot = name2.lastIndexOf('.');

                if ((name1Dot == -1) == (name2Dot == -1)) { // both or neither
                    name1 = name1.substring(name1Dot + 1);
                    name2 = name2.substring(name2Dot + 1);
                    return name1.compareTo(name2);
                } else if (name1Dot == -1) { // only name2 has an extension, so name1 goes first
                    return -1;
                } else { // only name1 has an extension, so name1 goes second
                    return 1;
                }
            }
        };
    }

    public static Filter createModifiedDateFilter() {
        return new Filter() {
            public int doFilter(FileProxy file1, FileProxy file2) {
                return Long.valueOf(file1.lastModifiedDate()).compareTo(file2.lastModifiedDate());
            }
        };
    }

    public static Filter createPreferredFilter() {

        Resources resources = App.sInstance.getResources();
        int sortIndex = Extensions.tryParse(App.sInstance.getSettings().getFileSortValue(), 0);

        switch (sortIndex) {
            case 0: default: return createAlphabeticFilter();
            case 1: return createSizeFilter();
            case 2: return createModifiedDateFilter();
            case 3: return createExtensionFilter();
        }


    }

    public static ArchiveFilter createArchiveDirectoryUpFilter() {
        return new ArchiveFilter() {
            public int doFilter(ArchiveScanner.File file1, ArchiveScanner.File file2) {
                return -Boolean.valueOf(file1.isDirectory()).compareTo(file2.isDirectory());
            }
        };
    }

    public static ArchiveFilter createArchiveAlphabeticFilter() {
        return new ArchiveFilter() {
            public int doFilter(ArchiveScanner.File file1, ArchiveScanner.File file2) {
                return sCollator.compare(file1.getName(), file2.getName());
            }
        };
    }    
    
}
