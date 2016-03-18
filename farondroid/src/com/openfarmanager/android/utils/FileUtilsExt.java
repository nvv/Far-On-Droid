package com.openfarmanager.android.utils;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * author: vnamashko
 */
public class FileUtilsExt extends FileUtils {

    /**
     * Count all child files within <code>file</code> directory.
     *
     * @param file parent file.
     * @return child files count or <code>1</code> if file is not directory.
     */
    public static int getFilesCount(File file) {
        if (!file.isDirectory()) {
            return 1;
        }

        int count = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                count += getFilesCount(f);
            }
        }
        return count;
    }

    /**
     * Count all child files within <code>files</code> directories.
     *
     * @param files parent files.
     * @return child files count.
     * @see FileUtilsExt#getFilesCount
     */
    public static int getFilesCount(List<File> files) {
        int count = 0;
        for (File file : files) {
            count += getFilesCount(file);
        }

        return count;
    }

    /**
     * Determines if <code>items</code> actually already located in <code>destination</code>.
     *
     * @param items       list of files.
     * @param destination destination file.
     * @return <code>true</code> if first item has the same absolute path as <code>destination</code>,
     *         <code>false</code> otherwise.
     */
    public static boolean isTheSameFolders(List<File> items, File destination) {
        if (items == null || items.size() < 1) {
            return false;
        }

        return destination.getAbsolutePath().equals(items.get(0).getParentFile().getAbsolutePath());
    }

    /**
     * Creates word search pattern with specified characteristics.
     *
     * @param pattern       string search pattern.
     * @param wholeWords    indicates whether only whole words need to be searched.
     * @param caseSensitive indicates that case sensitive rule need to be applied to search.
     * @return word search pattern.
     */
    public static Pattern createWordSearchPattern(String pattern, boolean wholeWords, IOCase caseSensitive) {
        return Pattern.compile(wholeWords ? String.format("\\b%s\\b", Pattern.quote(pattern)) :
                Pattern.quote(pattern), caseSensitive == IOCase.INSENSITIVE ? Pattern.CASE_INSENSITIVE : 0);
    }

    /**
     * Search file by name from <code>files</code>.
     *
     * @param files src files.
     * @param file  to be found.
     * @return file with name equals to <code>file#name</code> or null.
     */
    public static File findFileByName(Set<File> files, File file) {
        for (File theFile : files) {
            if (theFile.getName().equals(file.getName())) {
                return theFile;
            }
        }

        return null;
    }

    /**
     * Get directory details based on <code>DirectoryScanResult</code>
     *
     * @param source directory to be scanned.
     * @return 'filled' <code>DirectoryScanResult</code>.
     * @see DirectoryScanResult
     */
    public static DirectoryScanResult getDirectoryDetails(File source) {

        if (source == null) {
            throw new NullPointerException(App.sInstance.getResources().getString(R.string.error_quick_view_null_file));
        }

        DirectoryScanResult directoryScanResult = new DirectoryScanResult();
        // very brutal and stupid step to exclude very parent directory from final result.
        directoryScanResult.directories = -1;
        listDirectoryItems(source, directoryScanResult);
        return directoryScanResult;
    }

    /**
     * Recursive directory scan.
     *
     * @param source              directory to be scanned.
     * @param directoryScanResult scan result to used for recursion.
     */
    private static void listDirectoryItems(File source, DirectoryScanResult directoryScanResult) {
        if (source.isDirectory()) {
            directoryScanResult.directories++;
            File[] files = source.listFiles();
            for (File file : files) {
                listDirectoryItems(file, directoryScanResult);
            }
        } else {
            directoryScanResult.files++;
            directoryScanResult.filesSize += source.length();
        }
    }

    /**
     * Extract parent path from the full path. <br>
     * Remove last path separator.
     *
     * @param path full path
     * @return parent path
     */
    public static String getParentPath(String path) {
        path = removeLastSeparator(path);
        path = path.equals("") ? path : path.substring(0, path.lastIndexOf("/"));
        return path.equals("") ? "/" : path;
    }

    /**
     * Extract file name from full path. <br>
     * Remove last path separator.
     *
     * @param path full path
     * @return file name
     */
    public static String getFileName(String path) {
        if (path.equals("/")) {
            return path;
        }

        path = path.substring(path.lastIndexOf("/") + 1, path.length());
        return removeLastSeparator(path);
    }

    /**
     * Remove last path separator.
     */
    public static String removeLastSeparator(String path) {
        return path.endsWith("/") && path.length() > 1 ? path.substring(0, path.length() - 1) : path;
    }

    public static class DirectoryScanResult {
        public long files = 0;
        public long directories = 0;
        public long filesSize = 0;
    }
}
