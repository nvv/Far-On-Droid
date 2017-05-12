package com.openfarmanager.android.filesystem.commands;

import android.widget.Toast;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.bus.RxBus;
import com.openfarmanager.android.core.bus.TaskErrorEvent;
import com.openfarmanager.android.core.bus.TaskOkAndPostEvent;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.model.NetworkAccount;
import com.openfarmanager.android.model.TaskStatusEnum;
import com.openfarmanager.android.view.ToastNotification;

/**
 * @author Vlad Namashko
 */
public class CreateBookmarkCommand implements AbstractCommand {

    private BaseFileSystemPanel mPanel;
    private final String mPath;
    private final String mLabel;
    private final NetworkAccount mAccount;

    public CreateBookmarkCommand(BaseFileSystemPanel panel, String path, String label, NetworkAccount account) {
        mPanel = panel;
        mPath = path;
        mLabel = label;
        mAccount = account;
    }

    @Override
    public void execute(Object... args) {
        TaskStatusEnum status = App.sInstance.getBookmarkManager().createBookmark(mPath,
                mLabel, mAccount);

        if (status == TaskStatusEnum.OK) {
            RxBus.getInstance().postEvent(new TaskOkAndPostEvent(mPanel.getPanelLocation(),
                    () -> ToastNotification.makeText(App.sInstance.getApplicationContext(),
                            App.sInstance.getString(R.string.bookmark_created), Toast.LENGTH_SHORT).show()));

        } else {
            RxBus.getInstance().postEvent(new TaskErrorEvent(mPanel.getPanelLocation()).setStatus(status));
        }
    }
}
