package com.openfarmanager.android.filesystem.actions.multi;

import android.content.Context;

import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.filesystem.actions.RootTask;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;
import com.openfarmanager.android.utils.FileUtilsExt;
import com.openfarmanager.android.utils.SystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.List;

import static com.openfarmanager.android.model.TaskStatusEnum.CANCELED;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_COPY;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_COPY_TO_THE_SAME_FOLDER;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_FILE_NOT_EXISTS;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_STORAGE_PERMISSION_REQUIRED;
import static com.openfarmanager.android.utils.StorageUtils.checkForPermissionAndGetBaseUri;
import static com.openfarmanager.android.utils.StorageUtils.checkUseStorageApi;
import static com.openfarmanager.android.utils.StorageUtils.getStorageOutputFileStream;

/**
 * @author Vlad Namashko
 */
public class MultiCopyTask extends MultiActionTask {

    private File mDestinationFolder;

    public MultiCopyTask(Context context, OnActionListener listener, List<File> items, File destination) {
        super(context, listener, items);
        mDestinationFolder = destination;
    }

    public TaskStatusEnum doAction() {
        if (FileUtilsExt.isTheSameFolders(mItems, mDestinationFolder)) { // no need to copy.
            return ERROR_COPY_TO_THE_SAME_FOLDER;
        }

        try {
            mSdCardPath = SystemUtils.getExternalStorage(mDestinationFolder.getAbsolutePath());
            if (checkUseStorageApi(mSdCardPath)) {
                mUseStorageApi = true;
                mBaseUri = checkForPermissionAndGetBaseUri();
            }
        } catch (SdcardPermissionException e) {
            return ERROR_STORAGE_PERMISSION_REQUIRED;
        }

        for (File file : mItems) {
            if (isCancelled()) {
                onTaskDone(CANCELED);
            }
            try {
                copy(file, new File(mDestinationFolder, file.getName()));
            } catch (NullPointerException e) {
                return ERROR_FILE_NOT_EXISTS;
            } catch (InterruptedIOException e) {
                return CANCELED;
            } catch (Exception e) {
                return ERROR_COPY;
            }
        }
        return TaskStatusEnum.OK;
    }

    @Override
    public TaskStatusEnum handleSubTaskException(Exception e) {
        if (e instanceof NullPointerException ) {
            return ERROR_FILE_NOT_EXISTS;
        } else if (e instanceof InterruptedIOException) {
            return CANCELED;
        } else {
            return ERROR_COPY;
        }
    }

    private void copy(final File source, final File destination) throws IOException {
        if (isCancelled()) {
            throw new InterruptedIOException();
        }
        if (source.isDirectory()) {
            createDirectoryIfNotExists(destination);
            for (String file : source.list()) {
                copy(new File(source, file), new File(destination, file));
            }
        } else {
            copyFileRoutine(source, destination, BUFFER);
            /*
            runSubTaskAsynk(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    copyFileRoutine(source, destination, new byte[512 * 1024]);
                    return null;
                }
            });
            */
        }
    }

    private void copyFileRoutine(File file, File destination, byte[] buf) throws IOException {
        mCurrentFile = file.getName();
        File parentFile = destination.getParentFile();

        if (!parentFile.exists() && !createDirectoryIfNotExists(parentFile)) {
            throw new IOException("Cannot create directory " + parentFile.getAbsolutePath());
        }
        if (!parentFile.canWrite() || !file.canRead()) {
            if (!RootTask.copy(file, destination)) {
                throw new IOException("Cannot copy file to " + parentFile.getAbsolutePath());
            }
        } else {
            InputStream in = new FileInputStream(file);

            OutputStream out;
            if (mUseStorageApi) {
                out = getStorageOutputFileStream(destination, mBaseUri, mSdCardPath);
            } else {
                out = new FileOutputStream(destination);
            }

            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                mDoneSize += len;
                updateProgress();
            }
            in.close();
            out.close();
        }
    }
}
