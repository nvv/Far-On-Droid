package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.filesystem.actions.network.DeleteFromNetworkTask;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.util.List;

/**
 * @author Vlad Namashko.
 */
public class DeleteAtNetworkCommand implements AbstractCommand {

    private MainPanel mNetworkPanel;

    public DeleteAtNetworkCommand(MainPanel panel) {
        mNetworkPanel = panel;
    }

    @Override
    public void execute(final Object... args) {
        try {
            NetworkEnum type = ((NetworkPanel) mNetworkPanel).getNetworkType();
            List<FileProxy> files = mNetworkPanel.getSelectedFileProxies();
            if (App.sInstance.getSettings().isMultiThreadTasksEnabled(type)) {
                new com.openfarmanager.android.filesystem.actions.multi.network.DeleteFromNetworkTask(mNetworkPanel, files).execute();
            } else {
                new DeleteFromNetworkTask(mNetworkPanel, files).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
