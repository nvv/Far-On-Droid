package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.actions.CopyTask;
import com.openfarmanager.android.fragments.MainPanel;

import java.io.File;

/**
 * @author Vlad Namashko
 */
public class CopyCommand implements AbstractCommand {

    private MainPanel mPanel;
    private File mDestination;

    public CopyCommand(MainPanel panel, File destination) {
        mPanel = panel;
        mDestination = destination;
    }
    @Override
    public void execute(Object... args) {
        try {
            new CopyTask(mPanel.getContext(), mPanel.getPanelLocation(), mPanel.getSelectedFiles(), mDestination).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
