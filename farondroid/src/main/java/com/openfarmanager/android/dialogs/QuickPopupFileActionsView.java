package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.view.presenters.QuickActionViewPresenter;
import com.openfarmanager.android.view.presenters.QuickActionViewPresenterImpl;
import com.openfarmanager.android.view.presenters.view.WidgetOnPanelView;

import static com.openfarmanager.android.fragments.MainPanel.LEFT_PANEL;

/**
 * @author Vlad Namashko
 */
public class QuickPopupFileActionsView extends QuickPopupDialog implements WidgetOnPanelView {

    private QuickActionViewPresenter mPresenter;

    private int mPanelLocation;

    public QuickPopupFileActionsView(Context context, View view, int panelLocation) {
        super(context, view, R.layout.quick_action_popup);

        mPanelLocation = panelLocation;
        setPosition((panelLocation == LEFT_PANEL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP,
                (int) (50 * App.sInstance.getResources().getDisplayMetrics().density));

        View layout = getContentView();

        mPresenter = new QuickActionViewPresenterImpl(this);

        layout.findViewById(R.id.quick_action_copy).setOnClickListener(v -> mPresenter.copy());

        layout.findViewById(R.id.quick_action_delete).setOnClickListener(v -> mPresenter.delete());

        layout.findViewById(R.id.quick_action_select).setOnClickListener(v -> mPresenter.selectAll());

        layout.findViewById(R.id.quick_action_deselect).setOnClickListener(v -> mPresenter.unSelectAll());
    }

    @Override
    public int getPanelLocation() {
        return mPanelLocation;
    }
}
