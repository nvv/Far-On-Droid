package com.openfarmanager.android.filesystem.actions;

import android.support.v4.app.FragmentManager;
import com.openfarmanager.android.model.TaskStatusEnum;
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
public class DeleteTask extends FileActionTask {

    public DeleteTask(FragmentManager fragmentManager, OnActionListener listener, List<File> items) {
        super(fragmentManager, listener, items);
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {

        FileDeleteStrategy strategy = FileDeleteStrategy.FORCE;

        List<File> items = new ArrayList<File>(mItems);
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

        return OK;
    }

}
