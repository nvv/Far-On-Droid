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
        String currentPath = ((NetworkPanel) mNetworkPanel).getCurrentPathId();
        try {
            new CreateNewAtNetworkTask(mNetworkPanel,
                    mNetworkPanel.fragmentManager(),
                    new OnActionListener() {
                        @Override
                        public void onActionFinish(TaskStatusEnum status) {
                            mNetworkPanel.handleNetworkActionResult(status, args);
                        }
                    }, currentPath, (String) args[1]).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
