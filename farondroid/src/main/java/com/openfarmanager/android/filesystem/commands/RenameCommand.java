package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.bus.RxBus;
import com.openfarmanager.android.core.bus.TaskErrorEvent;
import com.openfarmanager.android.core.bus.TaskOkEvent;
import com.openfarmanager.android.filesystem.actions.RenameTask;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.io.File;

/**
 * @author Vlad Namashko
 */
public class RenameCommand implements AbstractCommand {

    private MainPanel mPanel;
    private String mDestinationFileName;
    private File mLastSelectedFile;

    public RenameCommand(MainPanel panel, String destinationFileName, File lastSelectedFile) {
        mPanel = panel;
        mDestinationFileName = destinationFileName;
        mLastSelectedFile = lastSelectedFile;
    }

    @Override
    public void execute(Object... args) {
        TaskStatusEnum status = new RenameTask(mLastSelectedFile, mDestinationFileName).execute();
        if (status == TaskStatusEnum.OK) {
            RxBus.getInstance().postEvent(new TaskOkEvent(mPanel.getPanelLocation()));
        } else {
            RxBus.getInstance().postEvent(new TaskErrorEvent(mPanel.getPanelLocation()).setStatus(status));
        }
    }
}
