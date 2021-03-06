package com.openfarmanager.android.filesystem.actions;

import android.content.Context;

import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;

import java.io.File;
import java.util.List;

import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_CREATE_ARCHIVE;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_STORAGE_PERMISSION_REQUIRED;

/**
 * @author Vlad Namashko
 */
public class CreateArchiveTask extends FileActionTask {

    private String mArchiveName;
    private ArchiveUtils.ArchiveType mArchiveType;
    private boolean mCompressionEnabled;
    private ArchiveUtils.CompressionEnum mCompression;

    public CreateArchiveTask(Context context, int invokedOnPanel, List<File> items,
                             String archiveName, ArchiveUtils.ArchiveType archiveType,
                             boolean compressionEnabled, ArchiveUtils.CompressionEnum compression) {
        super(context, invokedOnPanel, items);
        mArchiveName = archiveName;
        mArchiveType = archiveType;
        mCompressionEnabled = compressionEnabled;
        mCompression = compression;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {
        try {
            ArchiveUtils.addToArchive(mItems, mArchiveName, mArchiveType, mCompression, mCompressionEnabled, mListener);
        } catch (SdcardPermissionException e) {
            return ERROR_STORAGE_PERMISSION_REQUIRED;
        } catch (Exception e) {
            return ERROR_CREATE_ARCHIVE;
        }

        return TaskStatusEnum.OK;
    }

    private ArchiveUtils.AddToArchiveListener mListener = new ArchiveUtils.AddToArchiveListener() {

        @Override
        public void beforeStarted(int filesToArchive) {
            mTotalSize = filesToArchive;
        }

        @Override
        public void beforeCompressionStarted(int fileParts) {
            mTotalSize = fileParts;
            mDoneSize = 0;
            updateProgress();
        }

        @Override
        public void onFileAdded(File file) {
            mDoneSize++;
            updateProgress();
        }
    };

}
