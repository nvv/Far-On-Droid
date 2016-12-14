package com.openfarmanager.android.core.network.datasource;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.FakeFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.googledrive.model.File;
import com.openfarmanager.android.model.exeptions.RestoreStoragePathException;
import com.openfarmanager.android.utils.Extensions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public abstract class IdPathDataSource extends DataSource {

    protected HashMap<String, String> mParentMapping = new HashMap<>();
    protected HashMap<String, String> mFileNameMapping = new HashMap<>();
    protected HashMap<String, String> mFilePathMapping = new HashMap<>();

    protected String mRootPathId;

    public String getDirectoryId(String path) {
        return mFilePathMapping.get(path);
    }

    public void putDirectoryId(String id, String path) {
        mFilePathMapping.put(path, id);
    }

    protected void cacheDirectoryInfo(FileProxy directory) {
        mParentMapping.put(directory.getId(), directory.getParentPath());
        mFileNameMapping.put(directory.getId(), directory.getName());
        mFilePathMapping.put(directory.getFullPathRaw(), directory.getId());
    }

    @Override
    public NetworkPanel.DirectoryScanInfo openDirectory(FileProxy directory) throws RuntimeException {
        String parentPath = directory.getParentPath();
        cacheDirectoryInfo(directory);

        String directoryParent;

        if (Extensions.isNullOrEmpty(parentPath)) {
            directoryParent = parentPath;
        } else {
            String cachedDirectoryParent = mParentMapping.get(parentPath);
            directoryParent = cachedDirectoryParent == null ?
                    requestFileInfo(parentPath).getParentPath() : cachedDirectoryParent;
        }

        List<FileProxy> directoryFiles = getDirectoryFiles(directory);

        if ((directory.isRoot() || "/".equals(directory.getFullPathRaw())) && mRootPathId == null && directoryFiles.size() > 0) {
            mRootPathId = directoryFiles.get(0).getParentPath();
        }

        return mDirectoryScanInfo.set(directoryFiles, directoryParent);
    }

    @Override
    public FileProxy createFakeDirectory(String path) throws RestoreStoragePathException {
        String id = mFilePathMapping.get(path);
        String parentId = mParentMapping.get(id);

        if (id == null || parentId == null || !mFileNameMapping.containsKey(id) || mRootPathId == null) {
            throw new RestoreStoragePathException();
        }

        return new FakeFile(id, mFileNameMapping.get(id), parentId, path, mRootPathId.equals(parentId));
    }

    public void createFakeDirectoryAsync(final FileProxy file, final CreateFakeDirectoryCallback callback) {
        Extensions.runAsync(new Runnable() {
            @Override
            public void run() {

                LinkedList<String> list = new LinkedList<>();
                String parentPath = file.getParentPath();

                while (parentPath != null && !parentPath.equals(mRootPathId)) {
                    FileProxy file = requestFileInfo(parentPath);
                    mFileNameMapping.put(file.getId(), file.getName());
                    mFilePathMapping.put(constructPath(list), file.getId());

                    if (file.getParentPath() != null) {
                        mParentMapping.put(parentPath, file.getParentPath());
                        list.addFirst(file.getName());
                    }
                    parentPath = file.getParentPath();
                }

                String parentId = mParentMapping.get(file.getParentPath());
                FakeFile fakeFile = new FakeFile(file.getParentPath(), mFileNameMapping.get(file.getParentPath()),
                        parentId, constructPath(list), mRootPathId.equals(parentId));

                callback.onCreated(fakeFile);
            }
        });
    }

    private String constructPath(LinkedList<String> list) {
        String fullPath = "";
        for (String item : list) {
            fullPath += "/" + item;
        }

        return fullPath;
    }

    public interface CreateFakeDirectoryCallback {
        void onCreated(FileProxy file);
    }

    protected abstract List<FileProxy> getDirectoryFiles(FileProxy directory);

    public abstract FileProxy requestFileInfo(String id);
}