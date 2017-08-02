package com.openfarmanager.android.core.bus;

/**
 * @author Vlad Namashko
 */
public class TaskOkAndPostEvent extends PanelEvent {

    private Runnable mPostAction;

    public TaskOkAndPostEvent(int forPanel, Runnable action) {
        super(forPanel);
        mPostAction = action;
    }

    public Runnable getPostAction() {
        return mPostAction == null ? () -> {} : mPostAction;
    }
}
