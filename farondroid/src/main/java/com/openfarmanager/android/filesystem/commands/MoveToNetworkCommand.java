package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.multi.network.MoveToNetworkMultiTask;
import com.openfarmanager.android.filesystem.actions.network.MoveToNetworkTask;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;

/**
 * @author Vlad Namashko
 */
public class MoveToNetworkCommand implements AbstractCommand {

    private MainPanel mPanel;
    private NetworkPanel mNetworkPanel;

    public MoveToNetworkCommand(MainPanel panel, NetworkPanel networkPanel) {
        mPanel = panel;
        mNetworkPanel = networkPanel;
    }

    @Override
    public void execute(Object... args) {
        try {
            if (App.sInstance.getSettings().isMultiThreadTasksEnabled(mNetworkPanel.getNetworkType())) {
                new MoveToNetworkMultiTask(mNetworkPanel,
                        mPanel.getSelectedFiles(), mNetworkPanel.getCurrentPath()).execute();
            } else {
                new MoveToNetworkTask(mNetworkPanel, mPanel.getSelectedFiles(), mNetworkPanel.getCurrentPath()).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
