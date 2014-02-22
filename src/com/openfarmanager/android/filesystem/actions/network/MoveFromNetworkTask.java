package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.core.network.dropbox.DropboxAPI;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.util.List;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

/**
 * @author Vlad Namashko
 */
public class MoveFromNetworkTask extends CopyFromNetworkTask {

    public MoveFromNetworkTask(NetworkEnum networkType, FragmentManager fragmentManager, OnActionListener listener, List<FileProxy> items, String destination) {
        super(networkType, fragmentManager, listener, items, destination);
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... voids) {

        if (mItems.size() < 1) {
            return OK;
        }

        TaskStatusEnum copyResult = doCopy();

        NetworkApi api = getApi();
        for (FileProxy file : mItems) {
            try {
                api.delete(file);
            } catch (NullPointerException e) {
                return ERROR_FILE_NOT_EXISTS;
            } catch (Exception e) {
                return ERROR_DELETE_FILE;
            }
        }

        return copyResult;
    }

}
