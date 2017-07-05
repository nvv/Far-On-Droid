package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.filesystem.search.SearchOptions;
import com.openfarmanager.android.fragments.MainPanel;

/**
 * @author Vlad Namashko.
 */
public class SearchActionDialog extends BaseFileDialog {

    public static final String SEARCH_DIALOG = "SEARCH_DIALOG";
    public static final String FILEMASK = "filemask";
    public static final String KEYWORD = "keyword";
    public static final String CASE_SENSITIVE = "case_sensitive";
    public static final String WHOLE_WORDS = "whole_words";
    private boolean mOnlyFileSearch;

    public SearchActionDialog(Context context, Handler handler,
                              MainPanel inactivePanel, boolean onlyFileSearch) {
        super(context, handler, inactivePanel);
        mOnlyFileSearch = onlyFileSearch;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText text = (EditText) mDialogView.findViewById(R.id.destination);
        restoreSettings();
        text.setSelection(text.getText().length());
        text = (EditText) mDialogView.findViewById(R.id.keyword);
        text.setSelection(text.getText().length());

        if (mOnlyFileSearch) {
            mDialogView.findViewById(R.id.keyword_label).setVisibility(View.GONE);
            mDialogView.findViewById(R.id.keyword).setVisibility(View.GONE);
            mDialogView.findViewById(R.id.case_sensitive).setVisibility(View.GONE);
            mDialogView.findViewById(R.id.whole_words).setVisibility(View.GONE);
        }
    }

    private void restoreSettings() {
        SharedPreferences preferences = App.sInstance.getSharedPreferences(SEARCH_DIALOG, 0);
        ((EditText) mDialogView.findViewById(R.id.destination)).setText(preferences.getString(FILEMASK, "*"));
        ((EditText) mDialogView.findViewById(R.id.keyword)).setText(preferences.getString(KEYWORD, ""));
        ((CheckBox) mDialogView.findViewById(R.id.case_sensitive)).setChecked(preferences.getBoolean(CASE_SENSITIVE, false));
        ((CheckBox) mDialogView.findViewById(R.id.whole_words)).setChecked(preferences.getBoolean(WHOLE_WORDS, false));
    }

    private void saveSettings(String fileMask, String keyword, boolean caseSensitive, boolean wholeWords) {
        App.sInstance.getSharedPreferences(SEARCH_DIALOG, 0).edit()
                .putString(FILEMASK, fileMask)
                .putString(KEYWORD, keyword)
                .putBoolean(CASE_SENSITIVE, caseSensitive)
                .putBoolean(WHOLE_WORDS, wholeWords).apply();
    }

    @Override
    public int getContentView() {
        return R.layout.dialog_search;
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    protected void execute() {
        String fileMask = ((EditText) mDialogView.findViewById(R.id.destination)).getText().toString();
        String keyword = ((EditText) mDialogView.findViewById(R.id.keyword)).getText().toString();
        boolean caseSensitive = ((CheckBox) mDialogView.findViewById(R.id.case_sensitive)).isChecked();
        boolean wholeWords = ((CheckBox) mDialogView.findViewById(R.id.whole_words)).isChecked();
        saveSettings(fileMask, keyword, caseSensitive, wholeWords);

        mHandler.sendMessage(mHandler.obtainMessage(MainPanel.SEARCH_ACTION,
                new SearchOptions(fileMask, keyword, caseSensitive, wholeWords).setIsNetowrkPanel(mOnlyFileSearch)));
    }
}
