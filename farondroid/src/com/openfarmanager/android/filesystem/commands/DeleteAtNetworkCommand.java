package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
import com.openfarmanager.android.filesystem.actions.network.DeleteFromNetworkTask;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.TaskStatusEnum;

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
        FileActionTask task = null;
        try {
            task = new DeleteFromNetworkTask(((NetworkPanel) mNetworkPanel).getNetworkType(),
                    mNetworkPanel.fragmentManager(),
                    new FileActionTask.OnActionListener() {
                        @Override
                        public void onActionFinish(TaskStatusEnum status) {
                            mNetworkPanel.handleNetworkActionResult(status, args);
                        }
                    }, mNetworkPanel.getSelectedFileProxies());
        } catch (Exception e) {
            e.printStackTrace();
        }
        task.execute();
    }
}
