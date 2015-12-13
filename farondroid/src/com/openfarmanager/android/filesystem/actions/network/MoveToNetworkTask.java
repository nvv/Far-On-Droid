package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;

import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.filesystem.actions.network.CopyToNetworkTask;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;
import org.apache.commons.io.FileDeleteStrategy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

/**
 * @author Vlad Namashko
 */
public class MoveToNetworkTask extends CopyToNetworkTask {

    public MoveToNetworkTask(NetworkEnum networkType, FragmentManager fragmentManager, OnActionListener listener, List<File> items,
                             String destination) {

        super(networkType, fragmentManager, listener, items, destination);
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {

        if (mItems.size() < 1) {
            return OK;
        }

        TaskStatusEnum copyResult = doCopy();

        FileDeleteStrategy strategy = FileDeleteStrategy.FORCE;
        for (File file : mItems) {
            try {
                strategy.delete(file);
            } catch (NullPointerException e) {
                return ERROR_FILE_NOT_EXISTS;
            } catch (IOException e) {
                return ERROR_DELETE_FILE;
            } catch (Exception e) {
                return ERROR_DELETE_FILE;
            }
        }

        return copyResult;
    }
}
