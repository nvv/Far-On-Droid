package com.openfarmanager.android.model.exeptions;

import com.openfarmanager.android.model.TaskStatusEnum;

/**
 * author: vnamashko
 */
public class TaskExecutionException extends RuntimeException {

    private TaskStatusEnum status;

    public TaskExecutionException() {
    }

    public TaskExecutionException(TaskStatusEnum statusEnum) {
        this.status = statusEnum;
    }

    public TaskStatusEnum getErrorReasonEnum() {
        return status;
    }

}
