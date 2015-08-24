package com.openfarmanager.android.filesystem.commands;

import android.net.Uri;

import com.openfarmanager.android.filesystem.actions.DeleteTask;
import com.openfarmanager.android.filesystem.actions.FileActionTask;
import com.openfarmanager.android.fragments.ErrorDialog;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.model.exeptions.SdcardPermissionException;

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
        FileActionTask task = null;
        try {
            task = new DeleteTask(mPanel.fragmentManager(),
                    new FileActionTask.OnActionListener() {
                        @Override
                        public void onActionFinish(TaskStatusEnum status) {
                            if (status != TaskStatusEnum.OK) {

                                if (status == TaskStatusEnum.ERROR_STORAGE_PERMISSION_REQUIRED) {
                                    mPanel.requestSdcardPermission();
                                    return;
                                }

                                try {
                                    ErrorDialog.newInstance(TaskStatusEnum.getErrorString(status)).show(mPanel.fragmentManager(), "errorDialog");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            mPanel.invalidatePanels((MainPanel) args[0]);
                        }
                    }, mPanel.getSelectedFiles());
        } catch (Exception e) {
            e.printStackTrace();
        }
        task.execute();
    }

    @Override
    protected boolean executeCommand(Uri uri) {
        return false;
    }

}
