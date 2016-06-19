package com.openfarmanager.android.filesystem.actions;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.common.io.Files;
import com.openfarmanager.android.BuildConfig;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;
import com.openfarmanager.android.utils.FileUtilsExt;
import com.openfarmanager.android.utils.SystemUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ConcurrentModificationException;
import java.util.List;

import static com.openfarmanager.android.utils.StorageUtils.*;
import static com.openfarmanager.android.model.TaskStatusEnum.*;

/**
 * User: sokhotnyi
 */
public class CopyTask extends FileActionTask {

    protected File mDestinationFolder;

    public CopyTask(FragmentManager fragmentManager, OnActionListener listener, List<File> items, File destination) {
        super(fragmentManager, listener, items);
        mDestinationFolder = destination;
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {
        if (BuildConfig.DEBUG) {
            mTaskStartTime = System.currentTimeMillis();
        }

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
                return CANCELED;
            }
            try {
                copy(file, new File(mDestinationFolder, file.getName()));
            } catch (NullPointerException e) {
                return ERROR_FILE_NOT_EXISTS;
            } catch (InterruptedIOException e) {
                return CANCELED;
            } catch (IOException e) {
                return ERROR_COPY;
            } catch (IllegalArgumentException e) {
                return ERROR_COPY;
            } catch (ConcurrentModificationException e) {
                return ERROR_COPY;
            } catch (Exception e) {
                return ERROR_COPY;
            }
        }

        if (BuildConfig.DEBUG) {
            Log.d("Copy Task", "execution time = " + (System.currentTimeMillis() - mTaskStartTime));
        }

        return TaskStatusEnum.OK;
    }

    private void copy(File source, File destination) throws IOException {
        if (isCancelled()) {
            throw new InterruptedIOException();
        }
        if (source.isDirectory()) {
            createDirectoryIfNotExists(destination);
            for (String file : source.list()) {
                copy(new File(source, file), new File(destination, file));
            }
        } else {
            copyFileRoutine(source, destination);
        }
    }

    private void copyFileRoutine(File file, File destination) throws IOException {
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
            while ((len = in.read(BUFFER)) > 0) {
                out.write(BUFFER, 0, len);
                doneSize += len;
                updateProgress();
            }
            in.close();
            out.close();
        }
    }
}
