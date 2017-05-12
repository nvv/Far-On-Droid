package com.openfarmanager.android.core.bus;

/**
 * @author Vlad Namashko
 */
public class TaskCancelledEvent extends PanelEvent {

    public TaskCancelledEvent(int forPanel) {
        super(forPanel);
    }
}
