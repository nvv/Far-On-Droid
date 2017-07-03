package com.openfarmanager.android.view.presenters;

import android.os.Handler;
import android.os.Message;

import com.openfarmanager.android.App;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.view.presenters.view.WidgetOnPanelView;

import javax.inject.Inject;

import static com.openfarmanager.android.controllers.FileSystemController.FILE_ACTION;
import static com.openfarmanager.android.controllers.FileSystemController.GAIN_FOCUS;
import static com.openfarmanager.android.controllers.FileSystemController.SELECT_ALL;
import static com.openfarmanager.android.controllers.FileSystemController.UNSELECT_ALL;

/**
 * @author Vlad Namashko
 */
public class QuickActionViewPresenterImpl implements QuickActionViewPresenter {

    private WidgetOnPanelView mView;

    public QuickActionViewPresenterImpl(WidgetOnPanelView view) {
        mView = view;
        App.sInstance.getFileSystemControllerComponent().inject(this);
    }

    @Inject
    Handler mHandler;

    @Override
    public void copy() {
        gainFocus();
        mHandler.sendMessage(mHandler.obtainMessage(FILE_ACTION, FileActionEnum.COPY));
    }

    @Override
    public void delete() {
        gainFocus();
        mHandler.sendMessage(mHandler.obtainMessage(FILE_ACTION, FileActionEnum.DELETE));
    }

    @Override
    public void selectAll() {
        gainFocus();
        mHandler.sendEmptyMessage(SELECT_ALL);
    }

    @Override
    public void unSelectAll() {
        gainFocus();
        mHandler.sendEmptyMessage(UNSELECT_ALL);
    }

    protected void gainFocus() {
        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            message.what = GAIN_FOCUS;
            message.arg1 = mView.getPanelLocation();
            mHandler.sendMessage(message);
        }
    }
}
