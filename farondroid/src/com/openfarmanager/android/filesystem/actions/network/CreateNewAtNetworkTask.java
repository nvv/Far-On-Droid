package com.openfarmanager.android.filesystem.actions.network;

import android.support.v4.app.FragmentManager;
import com.dropbox.client2.exception.DropboxException;
import com.microsoft.live.LiveOperationException;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.NetworkException;
import com.yandex.disk.client.exceptions.WebdavException;

import java.io.File;
import java.util.ArrayList;

import static com.openfarmanager.android.model.TaskStatusEnum.*;

/**
 * @author Vlad Namashko
 */
public class CreateNewAtNetworkTask extends NetworkActionTask {

    protected String mDestinationFolder;
    protected String mName;

    public CreateNewAtNetworkTask(BaseFileSystemPanel panel, FragmentManager fragmentManager, OnActionListener listener,
                                 String destinationForlder, String name) {
        // TODO: temporary
        super.mItems = new ArrayList<>();

        mDestinationFolder = destinationForlder;
        mName = name;
        mFragmentManager = fragmentManager;
        mListener = listener;

        initNetworkPanelInfo(panel);
    }

    @Override
    protected TaskStatusEnum doInBackground(Void... params) {
        NetworkApi api = getApi();

        try {
            return api.createDirectory(mDestinationFolder, mName) != null ? OK : ERROR_CREATE_DIRECTORY;
        } catch (DropboxException e) {
            return createNetworkError(NetworkException.handleNetworkException(e));
        } catch (WebdavException e) {
            return createNetworkError(NetworkException.handleNetworkException(e));
        } catch (LiveOperationException e) {
            return ERROR_CREATE_DIRECTORY;
        } catch (Exception e) {
            return ERROR_CREATE_DIRECTORY;
        }
    }

}
