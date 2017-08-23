package com.openfarmanager.android.filesystem;

import android.os.Build;
import android.text.TextUtils;
import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.actions.RootTask;
import com.openfarmanager.android.filesystem.filter.Sorter;
import com.openfarmanager.android.filesystem.filter.SorterFactory;
import com.openfarmanager.android.model.exeptions.FileIsNotDirectoryException;
import com.openfarmanager.android.utils.StorageUtils;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.PrefixFileFilter;

import java.io.File;
import java.util.*;

/**
 * Vlad Namashko
 */
public class FileSystemScanner {

    public static String ROOT = "/";

    public static final FileSystemScanner sInstance;

    private LinkedList<Sorter> mSorters;

    static {
        sInstance = new FileSystemScanner();
    }

    private Comparator<FileProxy> mComparator = new Comparator<FileProxy>() {
        public int compare(FileProxy f1, FileProxy f2) {
            int result = 0;
            for (Sorter sorter : mSorters) {
                if ((result = sorter.doSort(f1, f2)) != 0) {
                    return result;
                }
            }
            return result;
        }
    };

    private FileSystemScanner() {
        initSorters();
    }

    public void initSorters() {
        ROOT = App.sInstance.getSettings().isSDCardRoot() ? StorageUtils.getSdPath() : "/";

        mSorters = new LinkedList<>();
        if (App.sInstance.getSettings().isFoldersFirst()) {
            mSorters.add(SorterFactory.createDirectoryUpFilter());
        }
        mSorters.add(SorterFactory.createPreferredFilter());
    }

    public static Collection<File> getTree(File... root) {
        ArrayList<File> tree = new ArrayList<>();
        for (File f : root) {
            addFilesRecursively(f, tree);
        }
        return tree;
    }

    private static void addFilesRecursively(File file, Collection<File> all) {
        final File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                all.add(child);
                addFilesRecursively(child, all);
            }
        }
    }

    public File getRoot() {
        return new File(ROOT);
    }

    public boolean isRoot(File node) {
        return node.getAbsolutePath().equals(ROOT);
    }

    public List<FileProxy> fallingDown(File currentNode, String mFilter) throws FileIsNotDirectoryException {
        if (!currentNode.isFile()) {
            String[] files = null;

            if (Build.VERSION.SDK_INT >= 24 && StorageUtils.getSdPath().startsWith(currentNode.getAbsolutePath())
                    && !StorageUtils.getSdPath().equals(currentNode.getAbsolutePath())) {

                files = RootTask.ls(currentNode);
                if (files != null) {
                    return buildFileList(currentNode, files, new LinkedList<>());
                }

                // don't allow to open subfolder of sd card root on SDK >= 24
                return null;
            }

            List<FileProxy> result = new LinkedList<FileProxy>();

            if (currentNode.canRead()) {
                if (!TextUtils.isEmpty(mFilter)) {
                    try {
                        files = currentNode.list(new PrefixFileFilter(mFilter, IOCase.INSENSITIVE));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (files == null) {
                    files = currentNode.list();
                }
            } else {
                files = RootTask.ls(currentNode);
            }

            if (files == null) {
                return Build.VERSION.SDK_INT >= 24 ? null : result;
            } else {
                return buildFileList(currentNode, files, result);
            }
        } else {
            throw new FileIsNotDirectoryException(currentNode.getAbsolutePath());
        }
    }

    private List<FileProxy> buildFileList(File currentNode, String[] files, List<FileProxy> result) {
        for (String f : files) {
            FileSystemFile file = new FileSystemFile(currentNode, f);
            if (App.sInstance.getSettings().isHideSystemFiles() && file.isHidden()) {
                continue;
            }
            result.add(file);
        }
        sort(result);
        return result;
    }

    public void sort(List<FileProxy> filesToSort) {
        Collections.sort(filesToSort, mComparator);
    }

}
