package com.openfarmanager.android.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import java.nio.charset.Charset;

import android.util.Pair;
import android.view.inputmethod.InputMethodManager;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.LinesAdapter;
import com.openfarmanager.android.dialogs.SelectEncodingDialog;
import com.openfarmanager.android.fragments.*;

/**
 * author: vnamashko
 */
public class EditViewController {

    public static final int SEARCH = 0;
    public static final int REPLACE = 1;
    public static final int SAVE = 2;
    public static final int EDIT = 3;
    public static final int GOTO = 4;
    public static final int ENCODING = 5;

    public static final int MSG_TEXT_CHANGED = 10;
    public static final int MSG_BIG_FILE = 11;
    public static final int MSG_SELECT_ENCODING = 12;

    private Viewer mViewer;
    private ViewerToolbar mViewerToolbar;

    private InputMethodManager mInputMethodManager;

    public EditViewController(Viewer viewer, ViewerToolbar viewerToolbar) {
        mViewer = viewer;
        mViewerToolbar = viewerToolbar;

        mViewer.setHandler(mViewerHandler);
        mViewerToolbar.setHandler(mToolbarHandler);

        mInputMethodManager = (InputMethodManager) mViewer.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    
    private Handler mToolbarHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEARCH:
                    doSearch();
                    break;
                case REPLACE:
                    doReplace();
                    break;
                case SAVE:
                    mViewer.save();
                    break;
                case GOTO:
                    goTo();
                    break;
                case EDIT:
                    mViewer.changeMode();
                    mViewerToolbar.changeEditText(App.sInstance.getString(mViewer.getMode() == LinesAdapter.MODE_VIEW ?
                            R.string.action_edit : R.string.action_view));

                    // hide virtual keyboard when viewer mode is changed.
                    mInputMethodManager.hideSoftInputFromWindow(mViewer.getView().getWindowToken(), 0);
                    break;
                case ENCODING:
                    mViewer.showSelectEncodingDialog();
                    break;
            }
        }
    };

    private Handler mViewerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TEXT_CHANGED:
                    boolean isTextChanged = (Boolean) msg.obj;
                    mViewerToolbar.setTextChanged(isTextChanged);
                    break;
                case MSG_BIG_FILE:
                    mViewerToolbar.setBigFileMode(false);
                    break;
                case MSG_SELECT_ENCODING:
                    SelectEncodingDialog.SelectedEncodingInfo info = (SelectEncodingDialog.SelectedEncodingInfo) msg.obj;
                    mViewer.setEncoding(info.charset);
                    if (info.saveAsDefault) {
                        // save default charset
                        App.sInstance.getSettings().setDefaultCharset(info.charset.name());
                    }
                    break;
            }
        }
    };

    public Handler getToolbarHandler() {
        return mToolbarHandler;
    }

    public void hideToolbar() {
        mViewerToolbar.getFragmentManager().beginTransaction().hide(mViewerToolbar).commit();
    }

    public void showToolbar() {
        mViewerToolbar.getFragmentManager().beginTransaction().show(mViewerToolbar).commit();
    }

    private void doReplace() {
        EditViewReplaceDialog.newInstance(listener).show(mViewer.getFragmentManager(), "edit_view_replace");
    }

    private void doSearch() {
        EditViewSearchDialog.newInstance(listener).show(mViewer.getFragmentManager(), "edit_view_search");
    }

    private void goTo() {
        EditViewGotoDialog.newInstance(listener).show(mViewer.getFragmentManager(), "edit_view_goto");
    }

    EditViewDialog.EditDialogListener listener = new EditViewDialog.EditDialogListener() {
        @Override
        public void doSearch(String pattern, boolean caseSensitive, boolean wholeWords, boolean regularExpression) {
            mViewer.search(pattern, caseSensitive, wholeWords, regularExpression);
        }

        @Override
        public void doReplace(String pattern, String replaceTo, boolean caseSensitive, boolean wholeWords, boolean regularExpression) {
            mViewer.replace(pattern, replaceTo, caseSensitive, wholeWords, regularExpression);
        }

        @Override
        public void goTo(int position, int unit) {
            mViewer.gotoLine(position, unit);
        }
    };
}
