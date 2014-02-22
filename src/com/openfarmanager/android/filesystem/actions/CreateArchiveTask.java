package com.openfarmanager.android.filesystem.actions;

import android.support.v4.app.FragmentManager;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.io.File;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class CreateArchiveTask extends FileActionTask {

    private String mArchiveName;
    private ArchiveUtils.ArchiveType mArchiveType;
    private boolean mCompressionEnabled;
    private ArchiveUtils.CompressionEnum mCompression;

    public CreateArchiveTask(FragmentManager fragmentManager, OnActionListener listener, List<File> items,
                             String archiveName, ArchiveUtils.ArchiveType archiveType,
                             boolean compressionEnabled, ArchiveUtils.CompressionEnum compression) {
        super(fragmentManager, listener, items);
        mArchiveName = archiveName;
        mArchiveType = archiveType;
        mCompressionEnabled = compressionEnabled;
        mCompression = compression;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {
        try {
            ArchiveUtils.addToArchive(mItems, mArchiveName, mArchiveType, mCompression, mCompressionEnabled, mListener);
        } catch (Exception e) {
            return TaskStatusEnum.ERROR_CREATE_ARCHIVE;
        }

        return TaskStatusEnum.OK;
    }

    private ArchiveUtils.AddToArchiveListener mListener = new ArchiveUtils.AddToArchiveListener() {

        @Override
        public void beforeStarted(int filesToArchive) {
            totalSize = filesToArchive;
        }

        @Override
        public void beforeCompressionStarted(int fileParts) {
            totalSize = fileParts;
            doneSize = 0;
            updateProgress();
        }

        @Override
        public void onFileAdded(File file) {
            doneSize++;
            updateProgress();
        }
    };

}
