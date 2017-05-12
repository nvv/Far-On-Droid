package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.network.ExportAsTask;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;

/**
 * @author Vlad Namashko
 */
public class ExportAsCommand implements AbstractCommand {

    private BaseFileSystemPanel mPanel;
    private String mDownloadLink;
    private String mDestination;

    public ExportAsCommand(BaseFileSystemPanel panel, String downloadLink, String destination) {
        mPanel = panel;
        mDownloadLink = downloadLink;
        mDestination = destination;
    }

    @Override
    public void execute(Object... args) {
        try {
            new ExportAsTask(mPanel, mDownloadLink, mDestination).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
