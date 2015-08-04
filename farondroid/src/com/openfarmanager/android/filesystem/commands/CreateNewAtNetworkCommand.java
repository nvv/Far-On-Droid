package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
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
        FileActionTask task = null;
        String currentPath = mNetworkPanel.getCurrentPath();
        String destination = currentPath + (currentPath.endsWith("/") ? "" : "/") + args[1];
        try {
            task = new CreateNewAtNetworkTask(((NetworkPanel) mNetworkPanel).getNetworkType(),
                    mNetworkPanel.fragmentManager(),
                    new FileActionTask.OnActionListener() {
                        @Override
                        public void onActionFinish(TaskStatusEnum status) {
                            mNetworkPanel.handleNetworkActionResult(status, args);
                        }
                    }, destination);
        } catch (Exception e) {
            e.printStackTrace();
        }
        task.execute();
    }
}
