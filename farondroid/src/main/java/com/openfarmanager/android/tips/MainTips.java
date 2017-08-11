package com.openfarmanager.android.tips;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.model.SelectParams;
import com.openfarmanager.android.view.panels.MainToolbar;

/**
 * @author Vlad Namashko
 */
public class MainTips {

    private ShowcaseView mShowCaseView;
    private int mCurrentStep = 0;

    private FileSystemController mFileSystemController;
    private MainToolbar mMainToolbarPanel;

    private View mRootAnchor;
    private Point mCurrentPathViewPoint;

    private Activity mActivity;

    public MainTips(Activity activity, FileSystemController controller, MainToolbar panel) {

        mActivity = activity;
        mFileSystemController = controller;
        mMainToolbarPanel = panel;

        mFileSystemController.setInitActivePanel();
        init();
    }

    private void init() {
        mRootAnchor = mActivity.findViewById(App.sInstance.getSettings().isMultiPanelMode() ? R.id.panel_left : R.id.panels_holder);
        mRootAnchor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRootAnchor.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mShowCaseView = new ShowcaseView.Builder(mActivity)
                        .setTarget(new ViewTarget(mRootAnchor))
                        .setOnClickListener(mClickListener)
                        .build();

                mCurrentPathViewPoint = new ViewTarget(mActivity.findViewById(R.id.current_path)).getPoint();

                mShowCaseView.setStyle(R.style.CustomShowcaseTheme);

                mShowCaseView.setContentTitle(mActivity.getString(R.string.main_panel));
                mShowCaseView.setContentText(mActivity.getString(R.string.main_panel_common));
            }
        });
    }

    private void nextStep() {

        MainPanel panel = mFileSystemController.getActivePanel();
        float density = mActivity.getResources().getDisplayMetrics().density;

        if (mCurrentStep == 0) {
            int selectedFiles = panel.select(new SelectParams("*", false, false, true, true));
            if (selectedFiles == 0) {
                // skip "selected files"
                mCurrentStep = 2;
                nextStep();
                return;
            }

            int offset = (int) (50 * density);
            mShowCaseView.setContentText(mActivity.getString(R.string.main_panel_quick_panel));
            mShowCaseView.setShowcase(new Point(offset + (int) (32 * density * 2), offset + (int) (32 * density)), true);
        } else if (mCurrentStep == 1) {
            mShowCaseView.setContentText(mActivity.getString(R.string.main_panel_size));
            mShowCaseView.setShowcase(new ViewTarget(R.id.selected_files_size, mActivity), true);
        } else if (mCurrentStep == 2) {

            panel.unselectAll();
            panel.invalidate();

            // tips for different mode
            if (App.sInstance.getSettings().isMultiPanelMode()) {
                mFileSystemController.expandPanel(true);
                mShowCaseView.setContentText(mActivity.getString(R.string.main_panel_expand));
            } else {
                ViewPager pager = (ViewPager) mActivity.findViewById(R.id.view_pager);
                if (pager != null) {
                    pager.scrollTo(300, 0);
                    mShowCaseView.setContentText(mActivity.getString(R.string.main_panel_swipe));
                }
            }

            mShowCaseView.setShowcase(new ViewTarget(mRootAnchor), true);
        } else if (mCurrentStep == 3) {

            // back operation
            if (App.sInstance.getSettings().isMultiPanelMode()) {
                mFileSystemController.expandPanel(false);
            } else {
                ViewPager pager = (ViewPager) mActivity.findViewById(R.id.view_pager);
                if (pager != null) {
                    pager.scrollTo(0, 0);
                }
            }

            mShowCaseView.setContentTitle(mActivity.getString(R.string.current_path));
            mShowCaseView.setContentText(mActivity.getString(R.string.current_path_summary));
            mShowCaseView.setShowcase(mCurrentPathViewPoint, true);
        } else if (mCurrentStep == 4) {
            mShowCaseView.setContentTitle(mActivity.getString(R.string.tools));
            mShowCaseView.setContentText(mActivity.getString(R.string.tools_summary));
            mShowCaseView.setShowcase(new ViewTarget(R.id.network, mActivity), true);
        } else if (mCurrentStep == 5) {
            mShowCaseView.setContentText(mActivity.getString(R.string.tools_change_directory));
            mShowCaseView.setShowcase(new ViewTarget(R.id.change_folder, mActivity), true);
        } else if (mCurrentStep == 6) {
            mShowCaseView.setContentTitle(mActivity.getString(R.string.bottom_panel));
            mShowCaseView.setContentText(mActivity.getString(R.string.bottom_panel_alt));
            mShowCaseView.setShowcase(new ViewTarget(mMainToolbarPanel.getAltView()), true);
        } else if (mCurrentStep == 7) {
            mShowCaseView.setContentTitle(mActivity.getString(R.string.bottom_panel));
            mShowCaseView.setContentText(mActivity.getString(R.string.bottom_panel_select));
            mShowCaseView.setShowcase(new ViewTarget(mMainToolbarPanel.getSelectView()), true);
        } else if (mCurrentStep == 8) {
            if (mMainToolbarPanel.getApplicationsView() == null) {
                mCurrentStep++;
                nextStep();
                return;
            }

            mShowCaseView.setContentText(mActivity.getString(R.string.bottom_panel_applications));
            mShowCaseView.setShowcase(new ViewTarget(mMainToolbarPanel.getApplicationsView()), true);
        } else if (mCurrentStep == 9) {
            if (mMainToolbarPanel.getQuickView() == null) {
                mCurrentStep++;
                nextStep();
                return;
            }

            mShowCaseView.setContentText(mActivity.getString(R.string.bottom_panel_quick_view));
            mShowCaseView.setShowcase(new ViewTarget(mMainToolbarPanel.getQuickView()), true);

            if (mMainToolbarPanel.getMoreView().getWidth() == 0) { // hided
                mShowCaseView.setButtonText(mActivity.getString(R.string.btn_finish));
                mCurrentStep++;
            }

        } else if (mCurrentStep == 10) {
            mShowCaseView.setContentText(mActivity.getString(R.string.bottom_panel_more));
            mShowCaseView.setShowcase(new ViewTarget(mMainToolbarPanel.getMoreView()), true);

            mShowCaseView.setButtonText(mActivity.getString(R.string.btn_finish));
        } else {
            mShowCaseView.hide();
        }

        mCurrentStep++;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                nextStep();
            } catch (Exception e) {
                // handle unexpected showcase crash
                mShowCaseView.hide();
            }
        }
    };
}
