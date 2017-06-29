package com.openfarmanager.android.core.archive;

import com.openfarmanager.android.filesystem.ArchiveFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.filter.Sorter;
import com.openfarmanager.android.filesystem.filter.SorterFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

public class ArchiveScanner {

    private File mRoot;
    private LinkedList<Sorter> mSorters;

    public static final ArchiveScanner sInstance;

    static {
        sInstance = new ArchiveScanner();
    }

    private ArchiveScanner() {
        mRoot = File.createRoot();
        mSorters = new LinkedList<>();
        mSorters.add(SorterFactory.createDirectoryUpFilter());
        mSorters.add(SorterFactory.createAlphabeticFilter());
    }

    public final Comparator<FileProxy> mComparator = new Comparator<FileProxy>() {
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
        private List<FileProxy> mChildren;
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
                mChildren = new LinkedList<>();
            }

            mChildren.add(new ArchiveFile(child));
        }

        public void processFile(String path, long fileSize) {

            if (isNullOrEmpty(path)) {
                return;
            }

            String fullPath = path.replace("\\", "/");

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

            for (FileProxy child : mChildren) {
                if (!child.isDirectory()) {
                    mChildFilesCount++;
                } else {
                    mChildFilesCount += child.getChildren().size();
                }
            }

            return mChildFilesCount;
        }

        /**
         * Find file recursively.
         *
         * @param name name of file to find
         * @return file with name <code>fileName</code> or <code>null</code>, if file with specified name can't be found.
         */
        public File findFile(String name) {
            String fileName = name.replace("\\", "/");
            if (getName().equals(fileName)) {
                return this;
            }

            if (!isDirectory() && fileName.equals(getFullPath())) {
                return this;
            }

            if (mChildren == null) {
                return null;
            }

            for (FileProxy child : mChildren) {
                if (child.getFullPath().equals(fileName)) {
                    return (File) child;
                }

                if (child.isDirectory()) {
                    File childFile = ((File) child).findFile(fileName);
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

            for (FileProxy child : mChildren) {
                if (child.isDirectory() && child.getName().equals(directoryName)) {
                    return (File) child;
                }
            }

            return null;
        }

        public void sort(List<FileProxy> filesToSort) {
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

        public List<FileProxy> getChildren() {
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

            return fullPath.substring(fullSubPath.trim().length() > 0 ? fullSubPath.length() - 1 : 0);
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

        public List<FileProxy> getSortedChildren() {
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