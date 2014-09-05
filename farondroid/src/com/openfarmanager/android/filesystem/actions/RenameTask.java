package com.openfarmanager.android.filesystem.actions;

import com.openfarmanager.android.model.TaskStatusEnum;

import java.io.File;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

public class RenameTask {

    private String mDestinationFileName;
    private File mSrcFile;

    public RenameTask(File srcFile, String destinationFileName) {
        mSrcFile = srcFile;
        mDestinationFileName = destinationFileName;
    }

    public TaskStatusEnum execute() {
        if (mDestinationFileName == null || mDestinationFileName.trim().equals("")) {
            return ERROR_WRONG_DESTINATION_FILE_NAME;
        }

        if (mSrcFile == null) {
            return ERROR_RENAME_FILE;
        }

        String destinationFilePath = mSrcFile.getParent() + File.separator + mDestinationFileName;
        if (mSrcFile.getParentFile().canWrite()) {
            // due to stupid behaviour of 'renameTo' method we will do some tricks
            File newFile = new File(destinationFilePath);
            File tempFile = new File(mSrcFile.getParent() + File.separator + mDestinationFileName + "_____");

            // rename temp file to destination file
            return mSrcFile.renameTo(tempFile) && tempFile.renameTo(newFile) ? OK : ERROR_RENAME_FILE;
        } else {
            return RootTask.rename(mSrcFile.getAbsolutePath(), destinationFilePath) ? OK : ERROR_RENAME_FILE;
        }
    }

}
