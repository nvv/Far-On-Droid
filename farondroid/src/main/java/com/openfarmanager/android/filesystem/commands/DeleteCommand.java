package com.openfarmanager.android.filesystem.commands;

import android.net.Uri;

import com.openfarmanager.android.filesystem.actions.DeleteTask;
import com.openfarmanager.android.fragments.MainPanel;

/**
 * @author Vlad Namashko.
 */
public class DeleteCommand extends AbstractPermissionCommand {

    private MainPanel mPanel;

    public DeleteCommand(MainPanel panel) {
        mPanel = panel;
    }

    @Override
    public void execute(final Object... args) {
        try {
            new DeleteTask(mPanel.getContext(), mPanel.getPanelLocation(), mPanel.getSelectedFiles()).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected boolean executeCommand(Uri uri) {
        return false;
    }

}
