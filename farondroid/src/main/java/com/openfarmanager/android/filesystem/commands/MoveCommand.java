package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.bus.RxBus;
import com.openfarmanager.android.core.bus.TaskErrorEvent;
import com.openfarmanager.android.core.bus.TaskOkEvent;
import com.openfarmanager.android.filesystem.actions.MoveTask;
import com.openfarmanager.android.filesystem.actions.RenameTask;
import com.openfarmanager.android.fragments.ErrorDialog;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.io.File;

/**
 * @author Vlad Namashko
 */
public class MoveCommand implements AbstractCommand {

    private MainPanel mPanel;
    private File mDestination;
    private String mDestinationFileName;
    private boolean mDoRename;
    private File mLastSelectedFile;

    public MoveCommand(MainPanel panel, File destination, String destinationFileName, boolean doRename, File lastSelectedFile) {
        mPanel = panel;
        mDestination = destination;
        mDestinationFileName = destinationFileName;
        mDoRename = doRename;
        mLastSelectedFile = lastSelectedFile;
    }

    @Override
    public void execute(Object... args) {
        if (mDoRename) {
            TaskStatusEnum status = new RenameTask(mLastSelectedFile, mDestinationFileName).execute();
            if (status == TaskStatusEnum.OK) {
                RxBus.getInstance().postEvent(new TaskOkEvent(mPanel.getPanelLocation()));
            } else {
                RxBus.getInstance().postEvent(new TaskErrorEvent(mPanel.getPanelLocation()).setStatus(status));
            }
        } else {
            try {
                new MoveTask(mPanel.getContext(), mPanel.getPanelLocation(), mPanel.getSelectedFiles(), mDestination, mDestinationFileName).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
