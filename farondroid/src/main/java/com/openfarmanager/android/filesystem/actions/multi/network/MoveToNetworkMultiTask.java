package com.openfarmanager.android.filesystem.actions.multi.network;

import android.content.Context;

import com.openfarmanager.android.filesystem.actions.OnActionListener;
import com.openfarmanager.android.fragments.BaseFileSystemPanel;
import com.openfarmanager.android.fragments.NetworkPanel;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.model.TaskStatusEnum;

import org.apache.commons.io.FileDeleteStrategy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_DELETE_FILE;
import static com.openfarmanager.android.model.TaskStatusEnum.ERROR_FILE_NOT_EXISTS;

/**
 * @author Vlad Namashko
 */
public class MoveToNetworkMultiTask extends CopyToNetworkMultiTask {

    public MoveToNetworkMultiTask(NetworkPanel panel, OnActionListener listener, List<File> items, String destination) {
        super(panel, listener, items, destination);
    }

    @Override
    public TaskStatusEnum doAction() {
        FileDeleteStrategy strategy = FileDeleteStrategy.FORCE;
        for (File file : mItems) {
            try {
                strategy.delete(file);
            } catch (NullPointerException e) {
                return ERROR_FILE_NOT_EXISTS;
            } catch (IOException e) {
                return ERROR_DELETE_FILE;
            } catch (Exception e) {
                return ERROR_DELETE_FILE;
            }
        }
        return TaskStatusEnum.OK;
    }

    protected Runnable getActionRunnable() {
        return mActionRunnable;
    }

    private Runnable mActionRunnable = new Runnable() {
        @Override
        public void run() {
            calculateSize();
            TaskStatusEnum status = MoveToNetworkMultiTask.super.doAction();

            if (hasSubTasks() && handleSubTasks(status)) {
                return;
            }

            onTaskDone(doAction());
        }
    };
}
