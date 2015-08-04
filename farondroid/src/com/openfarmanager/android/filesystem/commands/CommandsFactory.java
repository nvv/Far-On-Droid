package com.openfarmanager.android.filesystem.commands;

import com.openfarmanager.android.core.AbstractCommand;
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


}
