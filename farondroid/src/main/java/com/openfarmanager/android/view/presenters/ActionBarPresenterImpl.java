package com.openfarmanager.android.view.presenters;

import android.os.Handler;
import android.os.Message;

import com.openfarmanager.android.App;
import com.openfarmanager.android.view.presenters.view.WidgetOnPanelView;

import javax.inject.Inject;

import static com.openfarmanager.android.controllers.FileSystemController.*;

/**
 * @author Vlad Namashko
 */
public class ActionBarPresenterImpl implements ActionBarPresenter {

    WidgetOnPanelView mActionBarView;

    @Inject
    Handler mHandler;

    public ActionBarPresenterImpl(WidgetOnPanelView view) {
        mActionBarView = view;
        App.sInstance.getFileSystemControllerComponent().inject(this);
    }

    @Override
    public void changePath() {
        mHandler.sendMessage(mHandler.obtainMessage(CHANGE_PATH, mActionBarView.getPanelLocation()));
    }

    @Override
    public void addBookmark() {
        gainFocus();
        mHandler.sendMessage(mHandler.obtainMessage(CREATE_BOOKMARK));
    }

    @Override
    public void openNetwork() {
        gainFocus();
        mHandler.sendMessage(mHandler.obtainMessage(OPEN_NETWORK));
    }

    @Override
    public void gotoHome() {
        gainFocus();
        mHandler.sendMessage(mHandler.obtainMessage(GOTO_HOME));
    }

    @Override
    public void openDirectory(String fullPath) {
        gainFocus();
        mHandler.sendMessage(mHandler.obtainMessage(OPEN_DIRECTORY, fullPath));
    }

    protected void gainFocus() {
        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            message.what = GAIN_FOCUS;
            message.arg1 = mActionBarView.getPanelLocation();
            mHandler.sendMessage(message);
        }
    }

}
