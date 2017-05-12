package com.openfarmanager.android.core.bus;

import com.openfarmanager.android.model.TaskStatusEnum;

/**
 * @author Vlad Namashko
 */
public class TaskErrorEvent extends PanelEvent {

    private TaskStatusEnum mStatusEnum;
    private Object mExtra;

    public TaskErrorEvent(int forPanel) {
        super(forPanel);
    }

    public TaskErrorEvent setStatus(TaskStatusEnum status) {
        mStatusEnum = status;
        return this;
    }

    public TaskErrorEvent setExtra(Object extra) {
        mExtra = extra;
        return this;
    }

    public TaskStatusEnum status() {
        return mStatusEnum;
    }

    public Object extra() {
        return mExtra != null ? mExtra : "";
    }
}
