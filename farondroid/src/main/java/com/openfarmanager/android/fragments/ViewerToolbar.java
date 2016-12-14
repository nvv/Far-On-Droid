package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.LinesAdapter;

import static com.openfarmanager.android.controllers.EditViewController.*;

/**
 * Toolbar for file viewer
 */
public class ViewerToolbar extends Fragment {

    private Handler mHandler;
    private View mRootView;
    private float mDensity;
    private int mFontSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDensity = getResources().getDisplayMetrics().density;
        mFontSize = App.sInstance.getSettings().getBottomPanelFontSize();
        mRootView = inflater.inflate(R.layout.viewer_toolbar, container, false);

        mRootView.findViewById(R.id.save).setEnabled(false);
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        addClickHandler(R.id.search, SEARCH);
        addClickHandler(R.id.replace, REPLACE);
        addClickHandler(R.id.save, SAVE);
        addClickHandler(R.id.edit, EDIT);
        addClickHandler(R.id.go_to, GOTO);
        addClickHandler(R.id.encoding, ENCODING);
        mRootView.requestLayout();
    }

    public void addClickHandler(final int id, final int msg) {
        TextView view = (TextView) mRootView.findViewById(id);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(msg);
            }
        });
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, mFontSize);
        view.setHeight((int) ((6 + 2 * mFontSize) * mDensity));
        view.setTypeface(App.sInstance.getSettings().getMainPanelFontType());
    }

    public void setTextChanged(boolean isTextChanged) {
        String save = App.sInstance.getString(R.string.btn_save);
        ((TextView) getView().findViewById(R.id.save)).setText((isTextChanged ? "* " : "") + save);
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    private void sendMessage(int what) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(what);
        }
    }

    public void changeEditTextMode(int mode) {
        mRootView.findViewById(R.id.save).setEnabled(mode != LinesAdapter.MODE_VIEW);
        ((TextView) mRootView.findViewById(R.id.edit)).setText(App.sInstance.getString(mode == LinesAdapter.MODE_VIEW ?
                R.string.action_edit : R.string.action_view));
    }

    public void setBigFileMode(boolean visible) {
        mRootView.findViewById(R.id.edit).setVisibility(visible ? View.VISIBLE : View.GONE);
        mRootView.findViewById(R.id.save).setVisibility(visible ? View.VISIBLE : View.GONE);
        mRootView.findViewById(R.id.replace).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

}
