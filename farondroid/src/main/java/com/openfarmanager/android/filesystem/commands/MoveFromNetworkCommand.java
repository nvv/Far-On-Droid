package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.multi.network.MoveFromNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.network.MoveFromNetworkTask;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.fragments.NetworkPanel;

/**
 * @author Vlad Namashko
 */
public class MoveFromNetworkCommand implements AbstractCommand {

    private MainPanel mPanel;
    private NetworkPanel mNetworkPanel;

    public MoveFromNetworkCommand(NetworkPanel networkPanel, MainPanel panel) {
        mNetworkPanel = networkPanel;
        mPanel = panel;
    }

    @Override
    public void execute(Object... args) {
        try {
            if (App.sInstance.getSettings().isMultiThreadTasksEnabled(mNetworkPanel.getNetworkType())) {
                new MoveFromNetworkMultiTask(mNetworkPanel,
                        mNetworkPanel.getSelectedFileProxies(), mPanel.getCurrentPath()).execute();
            } else {
                new MoveFromNetworkTask(mNetworkPanel, mNetworkPanel.getSelectedFileProxies(), mPanel.getCurrentPath()).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
