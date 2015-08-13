package com.openfarmanager.android.filesystem.actions;

import android.net.Uri;
import android.provider.DocumentsContract;
import android.support.v4.app.FragmentManager;

import com.openfarmanager.android.App;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;
import com.openfarmanager.android.utils.SystemUtils;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

/**
 * author: vnamashko
 */
public class DeleteTask extends PermissionRequiredTask {

    public DeleteTask(FragmentManager fragmentManager, OnActionListener listener, List<File> items) {
        super(fragmentManager, listener, items);
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {

        if (mItems.size() == 0) {
            return OK;
        }

        FileDeleteStrategy strategy = FileDeleteStrategy.FORCE;
        List<File> items = new ArrayList<>(mItems);
        String sdCardPath = SystemUtils.getExternalStorage(mItems.get(0).getParent());

        if (checkUseStorageApi(sdCardPath)) {
            try {
                Uri baseUri = checkForPermissionAndGetBaseUri();
                for (File file : items) {
                    Uri uri = checkForPermissionAndGetDestinationUrl(baseUri, sdCardPath, file.getAbsolutePath());
                    doneSize += FileUtils.sizeOf(file);
                    if (!DocumentsContract.deleteDocument(App.sInstance.getContentResolver(), uri)) {
                        return ERROR_DELETE_FILE;
                    }

                    updateProgress();
                }
                return OK;
            } catch (SdcardPermissionException e) {
                return ERROR_STORAGE_PERMISSION_REQUIRED;
            } catch (Exception e) {
                return ERROR_DELETE_FILE;
            }
        } else {
            for (File file : items) {
                if (isCancelled()) {
                    break;
                }
                try {
                    if (file.getParentFile().canWrite()) {
                        doneSize += FileUtils.sizeOf(file);
                        strategy.delete(file);
                    } else {
                        RootTask.delete(file);
                    }
                    updateProgress();
                } catch (NullPointerException e) {
                    return ERROR_FILE_NOT_EXISTS;
                } catch (IOException e) {
                    return ERROR_DELETE_FILE;
                } catch (Exception e) {
                    return ERROR_DELETE_FILE;
                }
            }
        }

        return OK;
    }

}
