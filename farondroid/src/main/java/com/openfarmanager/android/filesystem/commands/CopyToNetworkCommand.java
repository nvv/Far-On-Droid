package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.multi.network.CopyToNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.network.CopyToNetworkTask;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

import java.io.File;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class CopyToNetworkCommand implements AbstractCommand {

    private MainPanel mPanel;
    private NetworkPanel mNetworkPanel;

    public CopyToNetworkCommand(MainPanel panel, NetworkPanel networkPanel) {
        mPanel = panel;
        mNetworkPanel = networkPanel;
    }

    @Override
    public void execute(Object... args) {
        try {
            if (App.sInstance.getSettings().isMultiThreadTasksEnabled(mNetworkPanel.getNetworkType())) {
                new CopyToNetworkMultiTask(mNetworkPanel, mPanel.getSelectedFiles(), mNetworkPanel.getCurrentPath()).execute();
            } else {
                new CopyToNetworkTask(mNetworkPanel, mPanel.getSelectedFiles(), mNetworkPanel.getCurrentPath()).execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
