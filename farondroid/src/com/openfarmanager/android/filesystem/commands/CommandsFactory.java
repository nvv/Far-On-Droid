package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.fragments.NetworkPanel;

/**
 * @author Vlad Namashko.
 */
public class CommandsFactory {

    public static AbstractCommand getCreateNewCommand(MainPanel panel) {
        if (panel instanceof NetworkPanel) {
            return new CreateNewAtNetworkCommand(panel);
        }

        return new CreateNewCommand(panel);
    }

    public static AbstractCommand getDeleteCommand(MainPanel panel) {
        if (panel instanceof NetworkPanel) {
            return new DeleteAtNetworkCommand(panel);
        }

        FileProxy lastSelectedFile = panel.getLastSelectedFile();
        return (lastSelectedFile != null && lastSelectedFile.isBookmark()) ?
                new DeleteBookmarkCommand(panel) : new DeleteCommand(panel);
    }

}
