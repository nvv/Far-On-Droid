package com.openfarmanager.android.filesystem.actions;

import com.openfarmanager.android.model.TaskStatusEnum;

/**
 * @author Vlad Namashko
 */
public interface OnActionListener {
    void onActionFinish(TaskStatusEnum status);
}
