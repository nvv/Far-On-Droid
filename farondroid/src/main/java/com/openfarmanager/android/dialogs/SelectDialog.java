package com.openfarmanager.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.model.SelectParams;
import com.openfarmanager.android.view.DatesPickerView;

import java.util.Date;

/**
 * author: Vlad Namashko
 */
public class SelectDialog extends Dialog {

    public static final String SELECT_DIALOG = "SELECT_DIALOG";

    public static final String SELECT_PATTERN = "select_pattern";

    public static final String DATE_BEFORE = "date_before";
    public static final String DATE_AFTER = "date_after";
    public static final String DATE_BEFORE_SEARCH = "date_before_search";
    public static final String DATE_AFTER_SEARCH = "date_after_search";

    public static final String CASE_SENSITIVE = "case_sensitive";
    public static final String INVESRT_SELECTION = "invert_selection";
    public static final String INCLUDE_FILES = "include_files";
    public static final String INCLUDE_FOLDERS = "include_folders";

    private AbstractCommand mCommand;

    private Button mOkButton;

    private TextView mSelectionString;

    private CheckBox mIncludeFiles;
    private CheckBox mIncludeFolders;
    private CheckBox mInvertSelection;
    private CheckBox mCaseSensitive;

    private DatesPickerView mDatesPickerView;

    public SelectDialog(Context context, AbstractCommand command) {
        super(context, R.style.Action_Dialog);
        mCommand = command;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_select);

        final View name = findViewById(R.id.select_name);
        final View date = findViewById(R.id.select_date);
        final ViewFlipper pages = (ViewFlipper) findViewById(R.id.pages);

        mOkButton = (Button) findViewById(R.id.ok);
        mSelectionString = (TextView) findViewById(R.id.selection_string);

        mDatesPickerView = (DatesPickerView) findViewById(R.id.dates_picker);
        mIncludeFiles = (CheckBox) findViewById(R.id.include_files);
        mIncludeFolders = (CheckBox) findViewById(R.id.include_folders);
        mInvertSelection = ((CheckBox) findViewById(R.id.invert_selection));
        mCaseSensitive = ((CheckBox) findViewById(R.id.case_sensitive));

        mIncludeFiles.setOnCheckedChangeListener((button, isChecked) -> checkOkEnable(mIncludeFiles, mIncludeFolders));

        mIncludeFolders.setOnCheckedChangeListener((button, isChecked) -> checkOkEnable(mIncludeFiles, mIncludeFolders));

        restoreSettings();

        name.setOnClickListener(v -> {
            date.setBackgroundResource(R.color.main_grey);
            name.setBackgroundResource(R.color.selected_item);

            pages.setDisplayedChild(0);
        });

        date.setOnClickListener(v -> {
            date.setBackgroundResource(R.color.selected_item);
            name.setBackgroundResource(R.color.main_grey);

            pages.setDisplayedChild(1);
        });

        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());

        mOkButton.setOnClickListener(v -> {

            SelectParams selectParams;

            boolean invertSelection = mInvertSelection.isChecked();

            boolean isIncludeFiles = mIncludeFiles.isChecked();
            boolean isIncludeFolders = mIncludeFolders.isChecked();

            if (pages.getDisplayedChild() == 0) { // select by name
                String selectionString = mSelectionString.getText().toString();
                boolean caseSensitive = mCaseSensitive.isChecked();

                selectParams = new SelectParams(selectionString, caseSensitive, invertSelection, isIncludeFiles, isIncludeFolders);
            } else {
                selectParams = new SelectParams(mDatesPickerView.getSelectedDateAfter(), mDatesPickerView.getSelectedDateBefore(), invertSelection, isIncludeFiles, isIncludeFolders);
            }

            saveSetting(selectParams);

            if (mCommand != null) {
                mCommand.execute(selectParams);
            }
            dismiss();
        });

    }

    protected void restoreSettings() {
        SharedPreferences preferences = App.sInstance.getSharedPreferences(SELECT_DIALOG, 0);

        String searchText = preferences.getString(SELECT_PATTERN, "*");
        mSelectionString.setText(searchText);

        mIncludeFiles.setChecked(preferences.getBoolean(INCLUDE_FILES, true));
        mIncludeFolders.setChecked(preferences.getBoolean(INCLUDE_FOLDERS, true));
        mInvertSelection.setChecked(preferences.getBoolean(INVESRT_SELECTION, false));
        mCaseSensitive.setChecked(preferences.getBoolean(CASE_SENSITIVE, false));

        mDatesPickerView.initWithValues(preferences.getLong(DATE_BEFORE, -1), preferences.getLong(DATE_AFTER, -1));
        ((CheckBox) findViewById(R.id.date_before_enabled)).setChecked(preferences.getBoolean(DATE_BEFORE_SEARCH, false));
        ((CheckBox) findViewById(R.id.date_after_enabled)).setChecked(preferences.getBoolean(DATE_AFTER_SEARCH, false));

    }

    protected void saveSetting(SelectParams params) {
        SharedPreferences.Editor editor = App.sInstance.getSharedPreferences(SELECT_DIALOG, 0).edit();
        if (params.getType() == SelectParams.SelectionType.NAME) {
            editor.putString(SELECT_PATTERN, params.getSelectionString()).putBoolean(CASE_SENSITIVE, params.isCaseSensitive());
        } else {
            Date dateBefore = params.getDateTo();
            Date dateAfter = params.getDateFrom();

            if (dateBefore != null) {
                editor.putLong(DATE_BEFORE, dateBefore.getTime());
            }
            if (dateAfter != null) {
                editor.putLong(DATE_AFTER, dateAfter.getTime());
            }

            editor.putBoolean(DATE_BEFORE_SEARCH, dateBefore != null);
            editor.putBoolean(DATE_AFTER_SEARCH, dateAfter != null);
        }

        editor.putBoolean(INVESRT_SELECTION, params.isInverseSelection());
        editor.putBoolean(INCLUDE_FILES, params.isIncludeFiles());
        editor.putBoolean(INCLUDE_FOLDERS, params.isIncludeFolders());

        editor.apply();
    }

    protected void checkOkEnable(CheckBox includeFiles, CheckBox includeFolders) {
        boolean enabled = includeFiles.isChecked() || includeFolders.isChecked();
        mOkButton.setFocusableInTouchMode(enabled);
        mOkButton.setFocusable(enabled);
        mOkButton.setEnabled(enabled);
    }
}
