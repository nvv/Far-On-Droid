package com.openfarmanager.android.tips;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.controllers.FileSystemController;
import com.openfarmanager.android.fragments.MainToolbarPanel;
import com.openfarmanager.android.model.SelectParams;

/**
 * @author Vlad Namashko
 */
public class MainTips {

    private ShowcaseView mShowCaseView;
    private int mCurrentStep = 0;

    private FileSystemController mFileSystemController;
    private MainToolbarPanel mMainToolbarPanel;


    private Activity mActivity;

    public MainTips(Activity activity, FileSystemController controller, MainToolbarPanel panel) {
        mActivity = activity;
        mFileSystemController = controller;
        mMainToolbarPanel = panel;

        init();
    }

    private void init() {
        final View anchor = mActivity.findViewById(App.sInstance.getSettings().isMultiPanelMode() ? R.id.panel_left : R.id.panels_holder);
        anchor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                anchor.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mShowCaseView = new ShowcaseView.Builder(mActivity)
                        .setTarget(new ViewTarget(anchor))
                        .setOnClickListener(mClickListener)
                        .build();
            }
        });
    }

    private boolean nextStep() {

        boolean tipsFound = true;

        if (mCurrentStep == 0) {
            mFileSystemController.getActivePanel().select(new SelectParams(SelectParams.SelectionType.NAME, "*", false, false, null, null));

            int offset = (int) (50 * mActivity.getResources().getDisplayMetrics().density);
            mShowCaseView.setShowcaseX(offset + (int) (32 * mActivity.getResources().getDisplayMetrics().density * 2));
            mShowCaseView.setShowcaseY(offset + (int) (32 * mActivity.getResources().getDisplayMetrics().density));

        } else if (mCurrentStep == 1) {
            mFileSystemController.getActivePanel().unselectAll();
            mFileSystemController.getActivePanel().invalidate();
            mShowCaseView.setShowcase(new ViewTarget(R.id.network_left, mActivity), true);
        } else if (mCurrentStep == 2) {

            mFileSystemController.expandPanel(true);

            mShowCaseView.setContentText("current path, long tab also handled");
            mShowCaseView.setContentTitle("current path");
            mShowCaseView.setShowcase(new ViewTarget(R.id.current_path, mActivity), true);
        }

        /*
        else if (mCurrentStep == 2) {
            mShowCaseView.setContentText("current path, long tab also handled");
            mShowCaseView.setContentTitle("current path");
            mShowCaseView.setShowcase(new ViewTarget(R.id.current_path, mActivity), true);
        } else if (mCurrentStep == 3) {
            mShowCaseView.setShowcase(new ViewTarget(R.id.change_folder_to_left, mActivity), true);
        } else if (mCurrentStep == 4) {
            mShowCaseView.setContentText("Main toolbar");
            mShowCaseView.setShowcase(new ViewTarget(R.id.toolbar, mActivity), true);
        } else if (mCurrentStep == 5) {
            mShowCaseView.setShowcase(new ViewTarget(mMainToolbarPanel.getAltView()), true);
        } else if (mCurrentStep == 6 && mMainToolbarPanel.getApplicationsView() != null) {
            mShowCaseView.setShowcase(new ViewTarget(mMainToolbarPanel.getApplicationsView()), true);
        } else if (mCurrentStep == 7 && mMainToolbarPanel.getQuickView() != null) {
            mShowCaseView.setShowcase(new ViewTarget(mMainToolbarPanel.getQuickView()), true);
        } else if (mCurrentStep == 8) {
            mShowCaseView.setShowcase(new ViewTarget(mMainToolbarPanel.getMoreView()), true);
        } else if (mCurrentStep == 9) {
            mShowCaseView.hide();
        }
        */
        mCurrentStep++;

        return tipsFound;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nextStep();
        }
    };
}
