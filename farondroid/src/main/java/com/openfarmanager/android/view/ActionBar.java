package com.openfarmanager.android.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.view.presenters.ActionBarPresenter;
import com.openfarmanager.android.view.presenters.ActionBarPresenterImpl;
import com.openfarmanager.android.view.presenters.view.WidgetOnPanelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.openfarmanager.android.fragments.MainPanel.LEFT_PANEL;

/**
 * author: Vlad Namashko
 */
public class ActionBar extends FrameLayout implements WidgetOnPanelView {

    protected int mPanelLocation;

    protected View mChangePath;
    protected View mAddToBookmarks;
    protected View mNetwork;
    protected View mHome;

    protected TextView mCurrentPathView;
    protected String mCurrentPath;

    private ActionBarPresenter mActionBarPresenter;

    public ActionBar(Context context) {
        super(context);
        mActionBarPresenter = new ActionBarPresenterImpl(this);
    }

    public void setPanelLocation(int location) {
        mPanelLocation = location;
        boolean isLeft = mPanelLocation == LEFT_PANEL;
        inflate(getContext(), getLayout(isLeft), this);
        bindViews();
        updateNavigationItemsVisibility(false, true, true);
    }

    protected void bindViews() {
        mChangePath = findViewById(R.id.change_folder);
        mAddToBookmarks = findViewById(R.id.add_to_bookmarks);
        mNetwork = findViewById(R.id.network);
        mHome = findViewById(R.id.home);

        mCurrentPathView = (TextView) findViewById(R.id.current_path);

        mChangePath.setOnClickListener(view -> mActionBarPresenter.changePath());
        mAddToBookmarks.setOnClickListener(view -> mActionBarPresenter.addBookmark());
        mNetwork.setOnClickListener(view -> mActionBarPresenter.openNetwork());
        mHome.setOnClickListener(view -> mActionBarPresenter.gotoHome());

        mCurrentPathView.setOnLongClickListener(view -> {
            final List<String> items = new ArrayList<>(Arrays.asList(mCurrentPath.split("/")));
            if (items.size() == 0) {
                return false;
            }
            items.set(0, "/");
            items.remove(items.size() - 1);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_selectable_list_item, items);
            final ListPopupWindow select = new ListPopupWindow(getContext());
            select.setBackgroundDrawable(getResources().getDrawable(R.drawable.panel_path_background));
            select.setAnchorView(view);
            select.setAdapter(adapter);
            select.setModal(true);
            select.setWidth(400);
            select.setOnItemClickListener((parent, view1, pos, id) -> {
                select.dismiss();
                mActionBarPresenter.openDirectory(TextUtils.join("/", items.subList(0, pos + 1)));
            });
            select.show();

            return false;
        });
    }

    protected int getLayout(boolean isLeft) {
        return isLeft ? R.layout.action_bar_left_side : R.layout.action_bar_right_side;
    }

    public void updateBackground() {
        int color = App.sInstance.getSettings().getMainPanelColor();
        mChangePath.setBackgroundColor(color);
        mAddToBookmarks.setBackgroundColor(color);
        mNetwork.setBackgroundColor(color);
        mHome.setBackgroundColor(color);
    }

    public void updateNavigationItemsVisibility(final boolean forceHide, boolean isCopyFolderSupported, boolean isBookmarksSupported) {
        mChangePath.setVisibility(!forceHide && isCopyFolderSupported ? View.VISIBLE : View.GONE);
        mAddToBookmarks.setVisibility(!forceHide && isBookmarksSupported ? View.VISIBLE : View.GONE);
        mNetwork.setVisibility(!forceHide && isCopyFolderSupported ? View.VISIBLE : View.GONE);
        boolean isHomeFolderEnabled = App.sInstance.getSettings().isEnableHomeFolder();
        mHome.setVisibility(!forceHide && isCopyFolderSupported && isHomeFolderEnabled ?
                View.VISIBLE : View.GONE);
    }

    public void setActive(boolean active) {
        if (mCurrentPathView != null) {
            mCurrentPathView.setSelected(active);

            mCurrentPathView.setBackgroundColor(active ?
                    App.sInstance.getSettings().getSecondaryColor() : App.sInstance.getSettings().getMainPanelColor());

        }
    }

    public void updateCurrentPath(String absolutePath) {
        mCurrentPath = absolutePath;
        mCurrentPathView.setText(absolutePath);
    }

    @Override
    public int getPanelLocation() {
        return mPanelLocation;
    }
}
