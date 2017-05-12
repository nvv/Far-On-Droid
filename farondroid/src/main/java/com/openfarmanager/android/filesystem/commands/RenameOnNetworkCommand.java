package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.actions.network.RenameOnNetworkTask;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;

/**
 * @author Vlad Namashko
 */
public class RenameOnNetworkCommand implements AbstractCommand {

    private BaseFileSystemPanel mPanel;
    private String mDestination;
    private FileProxy mFile;

    public RenameOnNetworkCommand(BaseFileSystemPanel panel, String destination, FileProxy fileProxy) {
        mPanel = panel;
        mDestination = destination;
        mFile = fileProxy;
    }

    @Override
    public void execute(Object... args) {
        new RenameOnNetworkTask(mPanel, mDestination, mFile).execute();
    }
}
