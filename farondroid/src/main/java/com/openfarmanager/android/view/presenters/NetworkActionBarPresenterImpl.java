package com.openfarmanager.android.view.presenters;

import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.view.presenters.view.WidgetOnPanelView;

import static com.openfarmanager.android.controllers.FileSystemController.EXIT_FROM_NETWORK_STORAGE;
import static com.openfarmanager.android.controllers.FileSystemController.OPEN_ENCODING_DIALOG;

/**
 * @author Vlad Namashko
 */
public class NetworkActionBarPresenterImpl extends ActionBarPresenterImpl implements NetworkActionBarPresenter {

    private NetworkEnum mNetworkType;

    public NetworkActionBarPresenterImpl(WidgetOnPanelView view, NetworkEnum networkEnum) {
        super(view);
        mNetworkType = networkEnum;
//        App.sInstance.getFileSystemControllerComponent().inject(this);
    }

    @Override
    public void exitNetwork() {
        gainFocus();
        mHandler.sendMessage(mHandler.obtainMessage(EXIT_FROM_NETWORK_STORAGE, mActionBarView.getPanelLocation()));
    }

    @Override
    public void selectCharset() {
        gainFocus();
        mHandler.sendMessage(mHandler.obtainMessage(OPEN_ENCODING_DIALOG, mNetworkType));
    }
}
