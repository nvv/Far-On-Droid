package com.openfarmanager.android.controllers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.fragments.*;
import com.openfarmanager.android.model.NetworkEnum;

import java.io.File;

import static com.openfarmanager.android.fragments.MainPanel.LEFT_PANEL;

/**
 * author: Vlad Namashko
 */
public class FileSystemControllerSmartphone extends FileSystemController {

    private ViewPager mViewPager;

    private BasePanel mPanelToChange;

    private View mLeftPanelSelector;
    private View mRightPanelSelector;

    public FileSystemControllerSmartphone(FragmentManager manager, View rootView) {
        mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        mLeftPanelSelector = rootView.findViewById(R.id.left_panel_selector);
        mRightPanelSelector = rootView.findViewById(R.id.right_panel_selector);

        Context appContext = App.sInstance.getApplicationContext();

        mLeftPanel = (MainPanel) Fragment.instantiate(appContext, MainPanel.class.getName());
        mRightPanel = (MainPanel) Fragment.instantiate(appContext, MainPanel.class.getName());

        mLeftVisibleFragment = mLeftPanel;
        mRightVisibleFragment = mRightPanel;

        TabsAdapter adapter = new TabsAdapter(manager);
        mViewPager.setAdapter(adapter);

        mMainToolbar = (MainToolbarPanel) manager.findFragmentById(R.id.toolbar);
        mDirectoryDetailsView = (DirectoryDetailsView) Fragment.instantiate(appContext, DirectoryDetailsView.class.getName());

        mLeftArchivePanel = (ArchivePanel) Fragment.instantiate(appContext, ArchivePanel.class.getName());
        mRightArchivePanel = (ArchivePanel) Fragment.instantiate(appContext, ArchivePanel.class.getName());

        mLeftNetworkPanel = (NetworkPanel) Fragment.instantiate(appContext, NetworkPanel.class.getName());
        mRightNetworkPanel = (NetworkPanel) Fragment.instantiate(appContext, NetworkPanel.class.getName());

        mLeftGenericPanel = (GenericPanel) Fragment.instantiate(appContext, GenericPanel.class.getName());
        mRightGenericPanel = (GenericPanel) Fragment.instantiate(appContext, GenericPanel.class.getName());

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                mLeftPanelSelector.setBackgroundResource(i == 0 ? R.color.yellow : R.color.main_grey);
                mRightPanelSelector.setBackgroundResource(i == 1 ? R.color.yellow : R.color.main_grey);
                mLeftPanel.setIsActivePanel(i == 0);
                mRightPanel.setIsActivePanel(i == 1);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        initPanels();
    }

    protected void openArchive(MainPanel activePanel, File file) {
        boolean isLeftPanel = activePanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        ArchivePanel archivePanel = isLeftPanel ? mLeftArchivePanel : mRightArchivePanel;

        setupPanelsVisibility(isLeftPanel, archivePanel);
        archivePanel.gainFocus();
        archivePanel.openArchive(file);
    }

    protected void openCompressedArchive(MainPanel activePanel, File file) {
        boolean isLeftPanel = activePanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        ArchivePanel archivePanel = isLeftPanel ? mLeftArchivePanel : mRightArchivePanel;

        setupPanelsVisibility(isLeftPanel, archivePanel);
        archivePanel.openCompressedArchive(file);
    }

    protected void exitFromArchive(ArchivePanel archivePanel) {
        boolean isLeftPanel = archivePanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        MainPanel activePanel = isLeftPanel ? mLeftPanel : mRightPanel;

        activePanel.gainFocus();
        setupPanelsVisibility(isLeftPanel, activePanel);
    }


    public void openAppLaucnher() {
        MainPanel activePanel = getActivePanel();

        if (activePanel == null) {
            return;
        }

        if (activePanel instanceof GenericPanel) {
            exitFromGenericPanel(activePanel);
            return;
        }

        boolean isLeftPanel = activePanel.getPanelLocation() == MainPanel.LEFT_PANEL;

        GenericPanel genericPanel = isLeftPanel ? mLeftGenericPanel : mRightGenericPanel;
        genericPanel.gainFocus();
        setupPanelsVisibility(isLeftPanel, genericPanel);
    }

    protected void exitFromGenericPanel(MainPanel genericPanel) {
        boolean isLeftPanel = genericPanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        MainPanel activePanel = isLeftPanel ? mLeftPanel : mRightPanel;

        activePanel.gainFocus();
        setupPanelsVisibility(isLeftPanel, activePanel);
    }

    public void openNetworkPanel(NetworkEnum networkType) {
        MainPanel activePanel = getActivePanel();
        boolean isLeftPanel = activePanel.getPanelLocation() == MainPanel.LEFT_PANEL;

        forceExitFromNetwork(networkType, activePanel);
        NetworkPanel networkPanel = isLeftPanel ? mLeftNetworkPanel : mRightNetworkPanel;
        networkPanel.setNetworkType(networkType);
        networkPanel.gainFocus();
        setupPanelsVisibility(isLeftPanel, networkPanel);
        networkPanel.openDirectory();
    }

    protected void exitFromNetworkStorage(NetworkPanel networkPanel) {
        boolean isLeftPanel = networkPanel.getPanelLocation() == MainPanel.LEFT_PANEL;
        MainPanel activePanel = isLeftPanel ? mLeftPanel : mRightPanel;

        activePanel.gainFocus();
        setupPanelsVisibility(isLeftPanel, activePanel);
    }

    protected MainPanel getLeftVisiblePanel() {
        return mLeftVisibleFragment instanceof MainPanel ? (MainPanel) mLeftVisibleFragment : null;
    }

    protected MainPanel getRightVisiblePanel() {
        return mRightVisibleFragment instanceof MainPanel ? (MainPanel) mRightVisibleFragment : null;
    }

    protected boolean showDetailsView(MainPanel activePanel, MainPanel inactivePanel) {
        if (activePanel == null || !mLeftVisibleFragment.isFileSystemPanel() || !mRightVisibleFragment.isFileSystemPanel()) {
            return false;
        }

        boolean isLeftPanel = activePanel.getPanelLocation() == LEFT_PANEL;

        mHiddenPanel = (MainPanel) (isLeftPanel ? mRightVisibleFragment : mLeftVisibleFragment);

        if (isLeftPanel) {
            mPanelToChange = mRightVisibleFragment;
            mRightVisibleFragment = mDirectoryDetailsView;
        } else {
            mPanelToChange = mLeftVisibleFragment;
            mLeftVisibleFragment = mDirectoryDetailsView;
        }

        mViewPager.getAdapter().notifyDataSetChanged();
        mViewPager.setCurrentItem(isLeftPanel ? 1 : 0, true);

        return true;
    }

    protected void hideDetailsView(MainPanel panel, MainPanel inactivePanel) {
        BasePanel panelToShow = mHiddenPanel;
        // brutal hack.
        boolean isLeftPanel = panel != null ?
                panel.getPanelLocation() == LEFT_PANEL :
                inactivePanel.getPanelLocation() == LEFT_PANEL;

        if (panelToShow == null) {
            panelToShow = isLeftPanel ? mLeftVisibleFragment : mRightVisibleFragment;
        }

        if (isLeftPanel) {
            mPanelToChange = mRightVisibleFragment;
            mRightVisibleFragment = panelToShow;
        } else {
            mPanelToChange = mLeftVisibleFragment;
            mLeftVisibleFragment = panelToShow;
        }

        mViewPager.getAdapter().notifyDataSetChanged();
    }

    private void setupPanelsVisibility(boolean isLeftCondition, BasePanel panelToShow) {
        if (isLeftCondition) {
            mPanelToChange = mLeftVisibleFragment;
            mLeftVisibleFragment = panelToShow;
        } else {
            mPanelToChange = mRightVisibleFragment;
            mRightVisibleFragment = panelToShow;
        }

        mViewPager.getAdapter().notifyDataSetChanged();
    }

    protected void showDetails(MainPanel panel) {
        if (panel == null || !isDetailsPanelVisible()) {
            return;
        }
        mDirectoryDetailsView.selectFile(panel.getCurrentDir());
    }


    protected boolean isDetailsPanelVisible() {
        return mLeftVisibleFragment instanceof DirectoryDetailsView || mRightVisibleFragment instanceof DirectoryDetailsView;
    }

    private class TabsAdapter extends FragmentStatePagerAdapter {

        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                return mLeftVisibleFragment;
            } else {
                return mRightVisibleFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object.equals(mPanelToChange)) {
                mPanelToChange = null;
                return POSITION_NONE;
            }

            return POSITION_UNCHANGED;
        }
    }

}
