package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.view.presenters.view.WidgetOnPanelView;

/**
 * @author Vlad Namashko.
 */
public class QuickPopupDialog extends PopupWindow {

    private View mParentView;
    private int mGravity;
    private int mOffset;

    public QuickPopupDialog(Context context, View view, int layoutId) {
        super(context);

        mParentView = view;

        App app = App.sInstance;
        View layout = ((LayoutInflater) app.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutId, null);
        layout.setBackgroundColor(app.getSettings().getSecondaryColor());
        layout.getBackground().setAlpha(135);

        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        setAnimationStyle(R.style.QuickActionPopupAnimation);
        setContentView(layout);
    }

    public void setPosition(int gravity, int offset) {
        mGravity = gravity;
        mOffset = offset;
    }

    public void show() {
        try {
            if (!isShowing()) {
                showAtLocation(mParentView, mGravity, mOffset, mOffset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
