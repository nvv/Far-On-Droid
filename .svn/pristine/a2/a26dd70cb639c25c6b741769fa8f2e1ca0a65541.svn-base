package com.openfarmanager.android.core.archive;

import com.openfarmanager.android.filesystem.filter.ArchiveFilter;
import com.openfarmanager.android.filesystem.filter.FilterFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

public class ArchiveScanner {

    private File mRoot;
    private LinkedList<ArchiveFilter> mFilters;

    public static final ArchiveScanner sInstance;

    static {
        sInstance = new ArchiveScanner();
    }

    private ArchiveScanner() {
        mRoot = File.createRoot();
        mFilters = new LinkedList<ArchiveFilter>();
        mFilters.add(FilterFactory.createArchiveDirectoryUpFilter());
        mFilters.add(FilterFactory.createArchiveAlphabeticFilter());
    }

    public final Comparator<File> mComparator = new Comparator<File>() {
        public int compare(File f1, File f2) {
            int result = 0;
            for (ArchiveFilter filter : mFilters) {
                if ((result = filter.doFilter(f1, f2)) != 0) {
                    return result;
                }
            }
            return result;
        }
    };

    public File root() {
        return mRoot;
    }

    public void clearArchive() {
        mRoot.mChildren = null;
    }

    public static class File {

        private String mName;
        private boolean mIsDirectory;
        private long mSize;
        private boolean mUp;

        private File mParent;
        private List<File> mChildren;
        private boolean mRoot;
        private int mChildFilesCount;

        public File(File file) {
            this (file.getName(), file.isDirectory(), file.getSize(), file.getParent(), file.isUpNavigator(), file.isRoot());
            mChildren = file.getChildren();
        }

        private File(String name, boolean isDirectory, long size, File parent) {
            mName = name;
            mIsDirectory = isDirectory;
            mSize = size;
            mParent = parent;

            mUp = false;
            mRoot = false;
            mChildFilesCount = -1;
        }

        private File(String name, boolean isDirectory, long size, File parent, boolean up, boolean root) {
            this(name, isDirectory, size, parent);
            mUp = up;
            mRoot = root;
        }

        public static File createRoot() {
            return new File("/", true, 0, null, false, true);
        }

        public static File createUpperNode(File parent) {
            return new File("..", false, 0, parent, true, false);
        }

        public void addNode(String name, boolean isDirectory, long size) {
            File child = new File(name, isDirectory, size, this);
            if (mChildren == null) {
                mChildren = new LinkedList<File>();
            }

            mChildren.add(child);
        }

        public void processFile(String fullPath, long fileSize) {

            if (isNullOrEmpty(fullPath)) {
                return;
            }

            if (fullPath.contains(java.io.File.separator)) {
                String[] names = fullPath.split(java.io.File.separator);
                String directoryName = names[0];
                if (findDirectory(directoryName) == null) {
                    addNode(directoryName, true, 0);
                }

                File node = findDirectory(directoryName);
                node.processFile(fullPath.substring(directoryName.length() + 1), fileSize);
            } else {
                addNode(fullPath, false, fileSize);
            }
        }

        /**
         * Count files (including current file and excluding directories).
         *
         * @return number of files in current file tree.
         */
        public int countFiles() {
            if (mChildFilesCount != -1) {
                return mChildFilesCount;
            }

            mChildFilesCount = 1;

            if (!isDirectory()) {
                return mChildFilesCount;
            }

            for (File child : mChildren) {
                if (!child.isDirectory()) {
                    mChildFilesCount++;
                } else {
                    mChildFilesCount += child.countFiles();
                }
            }

            return mChildFilesCount;
        }

        /**
         * Find file recursively.
         *
         * @param fileName name of file to find
         * @return file with name <code>fileName</code> or <code>null</code>, if file with specified name can't be found.
         */
        public File findFile(String fileName) {
            if (getName().equals(fileName)) {
                return this;
            }

            if (!isDirectory() && fileName.equals(getFullPath())) {
                return this;
            }

            if (mChildren == null) {
                return null;
            }

            for (File child : mChildren) {
                if (child.getFullPath().equals(fileName)) {
                    return child;
                }

                if (child.isDirectory()) {
                    File childFile = child.findFile(fileName);
                    if (childFile != null) {
                        return childFile;
                    }
                }
            }

            return null;
        }

        public File findInTree(String fileName) {

            fileName = fileName.replace("//", "");

            File parent = getParent();
            if (parent != null) {
                if (parent.getFullPath().equals(fileName)) {
                    return parent;
                } else {
                    return parent.findInTree(fileName);
                }
            }

            return null;
        }

        public File findDirectory(String directoryName) {
            if (mChildren == null) {
                return null;
            }

            for (File child : mChildren) {
                if (child.isDirectory() && child.getName().equals(directoryName)) {
                    return child;
                }
            }

            return null;
        }

        public void sort(List<File> filesToSort) {
            if (filesToSort != null) {
                Collections.sort(filesToSort, ArchiveScanner.sInstance.mComparator);
            }
        }

        public String getName() {
            return mName;
        }

        public boolean isDirectory() {
            return mIsDirectory;
        }

        public long getSize() {
            return mSize;
        }

        public File getParent() {
            return mParent;
        }

        public List<File> getChildren() {
            return mChildren;
        }

        /**
         * Get path diff when extracting sub folder from archive.
         *
         * @param file file to be extracted from archive.
         * @return sub-path within current working directory.
         */
        public String getSubDirectoryPath(File file) {
            String fullSubPath = getFullDirectoryPath();
            String fullPath = file.getFullDirectoryPath();

            return fullPath.substring(fullSubPath.length() - 1);
        }

        public String getFullDirectoryPath() {
            StringBuilder fullPath = new StringBuilder();
            File parent = getParent();
            while (parent != null && !parent.isRoot()) {
                fullPath.insert(0, parent.getName() + "/");
                parent = parent.getParent();
            }

            return fullPath.toString();
        }

        public String getFullPath() {
            StringBuilder fullPath = new StringBuilder(mName);
            File parent = getParent();
            while (parent != null && !parent.isRoot()) {
                fullPath.insert(0, parent.getName() + "/");
                parent = parent.getParent();
            }

            return fullPath.toString();
        }

        public List<File> getSortedChildren() {
            sort(mChildren);
            return mChildren;
        }

        public boolean isUpNavigator() {
            return mUp;
        }

        public boolean isRoot() {
            return mRoot;
        }

    }
}