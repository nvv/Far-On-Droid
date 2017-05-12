package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.core.bus.RxBus;
import com.openfarmanager.android.core.bus.TaskOkEvent;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.model.Bookmark;

/**
 * @author Vlad Namashko.
 */
public class DeleteBookmarkCommand implements AbstractCommand {

    private MainPanel mPanel;

    public DeleteBookmarkCommand(MainPanel panel) {
        mPanel = panel;
    }

    @Override
    public void execute(Object... args) {
        Bookmark bookmark = ((FileProxy) args[2]).getBookmark();
        App.sInstance.getBookmarkManager().deleteBookmark(bookmark);
        RxBus.getInstance().postEvent(new TaskOkEvent(mPanel.getPanelLocation()));
    }
}
