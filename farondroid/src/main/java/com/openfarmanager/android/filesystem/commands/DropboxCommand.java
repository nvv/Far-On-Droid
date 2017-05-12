package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.DropboxFile;
import com.openfarmanager.android.filesystem.actions.network.DropboxTask;
import com.openfarmanager.android.fragments.MainPanel;

/**
 * @author Vlad Namashko
 */
public class DropboxCommand implements AbstractCommand {

    private MainPanel mPanel;
    private DropboxFile mFile;
    private int mDropboxTask;

    public DropboxCommand(MainPanel panel, DropboxFile file, int dropboxTask) {
        mPanel = panel;
        mFile = file;
        mDropboxTask = dropboxTask;
    }

    @Override
    public void execute(Object... args) {
        try {
            new DropboxTask(mPanel, mFile, mDropboxTask).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
