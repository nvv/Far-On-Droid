package com.openfarmanager.android.filesystem.filter;

import android.content.res.Resources;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.utils.Extensions;

import java.text.Collator;

/**
 * @author Vlad Namashko
 */
public class SorterFactory {

    private static Collator sCollator;

    static {
        sCollator = Collator.getInstance();
        sCollator.setStrength(Collator.SECONDARY);
    }

    public static Sorter createDirectoryUpFilter() {
        return (file1, file2) -> -Boolean.valueOf(file1.isDirectory()).compareTo(file2.isDirectory());
    }

    public static Sorter createAlphabeticFilter() {
        return (file1, file2) -> sCollator.compare(file1.getName(), file2.getName());
    }

    public static Sorter createSizeFilter() {
        return (file1, file2) -> Long.valueOf(file1.getSize()).compareTo(file2.getSize());
    }

    public static Sorter createExtensionFilter() {
        return (file1, file2) -> {
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
        };
    }

    public static Sorter createModifiedDateFilter() {
        return (file1, file2) -> Long.valueOf(file1.lastModifiedDate()).compareTo(file2.lastModifiedDate());
    }

    public static Sorter createPreferredFilter() {
        switch (Extensions.tryParse(App.sInstance.getSettings().getFileSortValue(), 0)) {
            case 0: default: return createAlphabeticFilter();
            case 1: return createSizeFilter();
            case 2: return createModifiedDateFilter();
            case 3: return createExtensionFilter();
        }


    }

}
