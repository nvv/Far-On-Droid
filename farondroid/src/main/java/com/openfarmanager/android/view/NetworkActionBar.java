package com.openfarmanager.android.view;

import android.content.Context;
import android.view.View;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.model.NetworkEnum;
import com.openfarmanager.android.view.presenters.NetworkActionBarPresenter;
import com.openfarmanager.android.view.presenters.NetworkActionBarPresenterImpl;

/**
 * author: Vlad Namashko
 */
public class NetworkActionBar extends ActionBar {

    protected View mCharset;
    protected View mExit;

    private boolean mIsEncodingSupported;

    private NetworkActionBarPresenter mActionBarPresenter;

    public NetworkActionBar(Context context, boolean isEncodingSupported, NetworkEnum networkEnum) {
        super(context);
        mIsEncodingSupported = isEncodingSupported;
        mActionBarPresenter = new NetworkActionBarPresenterImpl(this, networkEnum);
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        mCharset = findViewById(R.id.charset);
        mExit = findViewById(R.id.exit);

        mCharset.setOnClickListener(view -> mActionBarPresenter.selectCharset());
        mExit.setOnClickListener(view -> mActionBarPresenter.exitNetwork());
    }

    @Override
    protected int getLayout(boolean isLeft) {
        return isLeft ? R.layout.action_bar_network_left_side: R.layout.action_bar_network_right_side;
    }

    @Override
    public void updateBackground() {
        super.updateBackground();
        int color = App.sInstance.getSettings().getMainPanelColor();
        mCharset.setBackgroundColor(color);
        mExit.setBackgroundColor(color);
    }

    @Override
    public void updateNavigationItemsVisibility(final boolean forceHide, boolean isCopyFolderSupported, boolean isBookmarksSupported) {
        super.updateNavigationItemsVisibility(forceHide, isCopyFolderSupported, isBookmarksSupported);
        mExit.setVisibility(View.VISIBLE);
        mCharset.setVisibility(mIsEncodingSupported ? View.VISIBLE : View.GONE);
    }

}