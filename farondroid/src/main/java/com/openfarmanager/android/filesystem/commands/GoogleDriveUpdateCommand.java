package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.network.GoogleDriveUpdateTask;
import com.openfarmanager.android.fragments.MainPanel;

/**
 * @author Vlad Namashko
 */
public class GoogleDriveUpdateCommand implements AbstractCommand {

    private MainPanel mPanel;
    private String mFieId;
    private String mData;

    public GoogleDriveUpdateCommand(MainPanel panel, String fileId, String data) {
        mPanel = panel;
        mFieId = fileId;
        mData = data;
    }

    @Override
    public void execute(Object... args) {
        try {
            new GoogleDriveUpdateTask(mPanel, mFieId, mData).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
