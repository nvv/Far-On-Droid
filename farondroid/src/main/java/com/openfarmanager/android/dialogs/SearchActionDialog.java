package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.filesystem.search.SearchOptions;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.view.DatesPickerView;

import java.util.Date;

/**
 * @author Vlad Namashko.
 */
public class SearchActionDialog extends BaseFileDialog {

    public static final String SEARCH_DIALOG = "SEARCH_DIALOG";

    public static final String FILEMASK = "filemask";
    public static final String KEYWORD = "keyword";
    public static final String CASE_SENSITIVE = "case_sensitive";
    public static final String WHOLE_WORDS = "whole_words";
    public static final String INCLUDE_FILES = "include_files";
    public static final String INCLUDE_FOLDERS = "include_folders";

    public static final String BIGGER_THAN_SIZE = "bigger_then_size";
    public static final String SMALLER_THAN_SIZE = "bigger_then_unit";
    public static final String BIGGER_THAN_UNIT = "smaller_then_size";
    public static final String SMALLER_THAN_UNIT = "smaller_then_unit";
    public static final String DATE_BEFORE = "date_before";
    public static final String DATE_AFTER = "date_after";

    public static final String ADVANCED_SEARCH = "advanced_search";
    public static final String DATE_BEFORE_SEARCH = "date_before_search";
    public static final String DATE_AFTER_SEARCH = "date_after_search";
    public static final String SIZE_SMALLER_SEARCH = "size_smaller_search";
    public static final String SIZE_BIGGER_SEARCH = "size_bigger_search";

    private boolean mOnlyFileSearch;
    private DatesPickerView mDatesPickerView;

    private CheckBox mIncludeFiles;
    private CheckBox mIncludeFolders;

    public SearchActionDialog(Context context, Handler handler,
                              MainPanel inactivePanel, boolean onlyFileSearch) {
        super(context, handler, inactivePanel);
        mOnlyFileSearch = onlyFileSearch;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText text = (EditText) mDialogView.findViewById(R.id.destination);
        mDatesPickerView = (DatesPickerView) mDialogView.findViewById(R.id.dates_picker);
        mIncludeFiles = (CheckBox) findViewById(R.id.include_files);
        mIncludeFolders = (CheckBox) findViewById(R.id.include_folders);

        restoreSettings();
        text.setSelection(text.getText().length());
        text = (EditText) mDialogView.findViewById(R.id.keyword);
        text.setSelection(text.getText().length());

        if (mOnlyFileSearch) {
            mDialogView.findViewById(R.id.search_text_options_label).setVisibility(View.GONE);
            mDialogView.findViewById(R.id.keyword_label).setVisibility(View.GONE);
            mDialogView.findViewById(R.id.keyword).setVisibility(View.GONE);
            mDialogView.findViewById(R.id.case_sensitive).setVisibility(View.GONE);
            mDialogView.findViewById(R.id.whole_words).setVisibility(View.GONE);
        }

        mIncludeFiles.setOnCheckedChangeListener((button, isChecked) -> checkOkEnable(mIncludeFiles, mIncludeFolders));

        mIncludeFolders.setOnCheckedChangeListener((button, isChecked) -> checkOkEnable(mIncludeFiles, mIncludeFolders));


        View advancedSearchFrame = mDialogView.findViewById(R.id.advanced_search_frame);
        ((CheckBox) mDialogView.findViewById(R.id.advanced_search)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            advancedSearchFrame.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    private void restoreSettings() {
        SharedPreferences preferences = App.sInstance.getSharedPreferences(SEARCH_DIALOG, 0);
        ((EditText) mDialogView.findViewById(R.id.destination)).setText(preferences.getString(FILEMASK, "*"));
        ((EditText) mDialogView.findViewById(R.id.keyword)).setText(preferences.getString(KEYWORD, ""));
        ((CheckBox) mDialogView.findViewById(R.id.case_sensitive)).setChecked(preferences.getBoolean(CASE_SENSITIVE, false));
        ((CheckBox) mDialogView.findViewById(R.id.whole_words)).setChecked(preferences.getBoolean(WHOLE_WORDS, false));
        mIncludeFiles.setChecked(preferences.getBoolean(INCLUDE_FILES, true));
        mIncludeFolders.setChecked(preferences.getBoolean(INCLUDE_FOLDERS, true));

        ((EditText) mDialogView.findViewById(R.id.size_bigger_than)).setText(preferences.getString(BIGGER_THAN_SIZE, ""));
        ((Spinner) mDialogView.findViewById(R.id.size_bigger_than_unit)).setSelection(preferences.getInt(BIGGER_THAN_UNIT, 0));

        ((EditText) mDialogView.findViewById(R.id.size_smaller_than)).setText(preferences.getString(SMALLER_THAN_SIZE, ""));
        ((Spinner) mDialogView.findViewById(R.id.size_smaller_than_unit)).setSelection(preferences.getInt(SMALLER_THAN_UNIT, 0));

        mDatesPickerView.initWithValues(preferences.getLong(DATE_BEFORE, -1), preferences.getLong(DATE_AFTER, -1));

        ((CheckBox) mDialogView.findViewById(R.id.size_bigger_than_enabled)).setChecked(preferences.getBoolean(SIZE_BIGGER_SEARCH, false));
        ((CheckBox) mDialogView.findViewById(R.id.size_smaller_than_enabled)).setChecked(preferences.getBoolean(SIZE_SMALLER_SEARCH, false));
        ((CheckBox) mDialogView.findViewById(R.id.date_before_enabled)).setChecked(preferences.getBoolean(DATE_BEFORE_SEARCH, false));
        ((CheckBox) mDialogView.findViewById(R.id.date_after_enabled)).setChecked(preferences.getBoolean(DATE_AFTER_SEARCH, false));

        if (preferences.getBoolean(ADVANCED_SEARCH, false)) {
            ((CheckBox) mDialogView.findViewById(R.id.advanced_search)).setChecked(true);
            mDialogView.findViewById(R.id.advanced_search_frame).setVisibility(View.VISIBLE);
        }
    }

    private void saveSettings(String fileMask, String keyword, boolean caseSensitive, boolean wholeWords, boolean includeFiles, boolean includeFolders, boolean advancedSearch) {
        App.sInstance.getSharedPreferences(SEARCH_DIALOG, 0).edit()
                .putString(FILEMASK, fileMask)
                .putString(KEYWORD, keyword)
                .putBoolean(CASE_SENSITIVE, caseSensitive)
                .putBoolean(WHOLE_WORDS, wholeWords)
                .putBoolean(INCLUDE_FILES, includeFiles)
                .putBoolean(INCLUDE_FOLDERS, includeFolders)
                .putBoolean(ADVANCED_SEARCH, advancedSearch)
                .apply();
    }

    private void saveAdvancedSettings(int biggerThenSize, int biggerThenUnit, int smallerThenSize, int smallerThenUnit,
                                      Date dateBefore, Date dateAfter) {
        SharedPreferences.Editor editor = App.sInstance.getSharedPreferences(SEARCH_DIALOG, 0).edit();

        if (biggerThenSize != -1) {
            editor.putString(BIGGER_THAN_SIZE, String.valueOf(biggerThenSize));
        }
        if (biggerThenUnit != -1) {
            editor.putInt(BIGGER_THAN_UNIT, biggerThenUnit);
        }
        if (smallerThenSize != -1) {
            editor.putString(SMALLER_THAN_SIZE, String.valueOf(smallerThenSize));
        }
        if (smallerThenUnit != -1) {
            editor.putInt(SMALLER_THAN_UNIT, smallerThenUnit);
        }
        if (dateBefore != null) {
            editor.putLong(DATE_BEFORE, dateBefore.getTime());
        }
        if (dateAfter != null) {
            editor.putLong(DATE_AFTER, dateAfter.getTime());
        }

        editor.putBoolean(DATE_BEFORE_SEARCH, dateBefore != null);
        editor.putBoolean(DATE_AFTER_SEARCH, dateAfter != null);

        editor.putBoolean(SIZE_SMALLER_SEARCH, smallerThenSize != -1);
        editor.putBoolean(SIZE_BIGGER_SEARCH, biggerThenSize != -1);

        editor.apply();
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
        boolean includeFiles = mIncludeFiles.isChecked();
        boolean includeFolders = mIncludeFolders.isChecked();

        int biggerThenSize = -1;
        int biggerThenUnit = -1;
        int smallerThenSize = -1;
        int smallerThenUnit = -1;

        Date mDateBefore;
        Date mDateAfter;

        SearchOptions searchOptions = new SearchOptions();
        searchOptions.fileMask = fileMask;
        searchOptions.keyword = keyword;
        searchOptions.caseSensitive = caseSensitive;
        searchOptions.wholeWords = wholeWords;
        searchOptions.includeFiles = includeFiles;
        searchOptions.includeFolders = includeFolders;

        boolean advancedSearch = ((CheckBox) findViewById(R.id.advanced_search)).isChecked();
        if (advancedSearch) {
            if (((CheckBox) findViewById(R.id.size_bigger_than_enabled)).isChecked()) {
                biggerThenSize = Integer.parseInt(((EditText) mDialogView.findViewById(R.id.size_bigger_than)).getText().toString());
                biggerThenUnit = ((Spinner) mDialogView.findViewById(R.id.size_bigger_than_unit)).getSelectedItemPosition();

                searchOptions.setMaxSizeRestriction(biggerThenSize, biggerThenUnit);
            }
            if (((CheckBox) findViewById(R.id.size_smaller_than_enabled)).isChecked()) {
                smallerThenSize = Integer.parseInt(((EditText) mDialogView.findViewById(R.id.size_smaller_than)).getText().toString());
                smallerThenUnit = ((Spinner) mDialogView.findViewById(R.id.size_smaller_than_unit)).getSelectedItemPosition();

                searchOptions.setMinSizeRestriction(smallerThenSize, smallerThenUnit);
            }

            searchOptions.dateBefore = mDateBefore = mDatesPickerView.getSelectedDateBefore();
            searchOptions.dateAfter = mDateAfter = mDatesPickerView.getSelectedDateAfter();

            saveAdvancedSettings(biggerThenSize, biggerThenUnit, smallerThenSize, smallerThenUnit, mDateBefore, mDateAfter);
        }

        saveSettings(fileMask, keyword, caseSensitive, wholeWords, includeFiles, includeFolders, advancedSearch);

        mHandler.sendMessage(mHandler.obtainMessage(MainPanel.SEARCH_ACTION,
                searchOptions.setIsNetworkPanel(mOnlyFileSearch)));
    }

    protected void checkOkEnable(CheckBox includeFiles, CheckBox includeFolders) {
        boolean enabled = includeFiles.isChecked() || includeFolders.isChecked();
        mOkButton.setFocusableInTouchMode(enabled);
        mOkButton.setFocusable(enabled);
        mOkButton.setEnabled(enabled);
    }
}
