package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.DeleteTask;
import com.openfarmanager.android.filesystem.actions.multi.network.CopyFromNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.network.CopyFromNetworkTask;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

/**
 * @author Vlad Namashko
 */
public class CopyFromNetworkCommand implements AbstractCommand {

    private MainPanel mPanel;
    private NetworkPanel mNetworkPanel;

    public CopyFromNetworkCommand(NetworkPanel networkPanel, MainPanel panel) {
        mNetworkPanel = networkPanel;
        mPanel = panel;
    }

    @Override
    public void execute(Object... args) {
        try {
            if (App.sInstance.getSettings().isMultiThreadTasksEnabled(mNetworkPanel.getNetworkType())) {
                new CopyFromNetworkMultiTask(mNetworkPanel,
                        mNetworkPanel.getFiles(), mPanel.getCurrentPath()).execute();
            } else {
                new CopyFromNetworkTask(mNetworkPanel, mNetworkPanel.getFiles(), mPanel.getCurrentPath()).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
