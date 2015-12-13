package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.filesystem.actions.network.CreateNewAtNetworkTask;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.TaskStatusEnum;

/**
 * @author Vlad Namashko.
 */
public class CreateNewAtNetworkCommand implements AbstractCommand {

    private MainPanel mNetworkPanel;

    public CreateNewAtNetworkCommand(MainPanel panel) {
        mNetworkPanel = panel;
    }

    @Override
    public void execute(final Object... args) {
        String currentPath = mNetworkPanel.getCurrentPath();
        String destination = currentPath + (currentPath.endsWith("/") ? "" : "/") + args[1];
        try {
            new CreateNewAtNetworkTask(((NetworkPanel) mNetworkPanel).getNetworkType(),
                    mNetworkPanel.fragmentManager(),
                    new OnActionListener() {
                        @Override
                        public void onActionFinish(TaskStatusEnum status) {
                            mNetworkPanel.handleNetworkActionResult(status, args);
                        }
                    }, destination).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
