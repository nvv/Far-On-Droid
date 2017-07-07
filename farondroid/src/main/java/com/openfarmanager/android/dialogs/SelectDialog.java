package com.openfarmanager.android.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.model.SelectParams;
import com.openfarmanager.android.utils.SimpleWrapper;
import com.openfarmanager.android.view.DatesPickerView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * author: Vlad Namashko
 */
public class SelectDialog extends Dialog {

    private AbstractCommand mCommand;

    private Button mOkButton;

    public SelectDialog(Context context, AbstractCommand command) {
        super(context, R.style.Action_Dialog);
        mCommand = command;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_select);

        final View name = findViewById(R.id.select_name);
        final View date = findViewById(R.id.select_date);
        final DatesPickerView datesPickerView = (DatesPickerView) findViewById(R.id.dates_picker);
        final ViewFlipper pages = (ViewFlipper) findViewById(R.id.pages);

        mOkButton = (Button) findViewById(R.id.ok);

        CheckBox includeFiles = (CheckBox) findViewById(R.id.include_files);
        CheckBox includeFolders = (CheckBox) findViewById(R.id.include_folders);

        includeFiles.setOnCheckedChangeListener((button, isChecked) -> checkOkEnable(includeFiles, includeFolders));

        includeFolders.setOnCheckedChangeListener((button, isChecked) -> checkOkEnable(includeFiles, includeFolders));

        String searchText = App.sInstance.getSharedPreferences("action_dialog", 0).getString("select_pattern", "*");
        ((TextView) findViewById(R.id.selection_string)).setText(searchText);

        final Calendar calendar = Calendar.getInstance();

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

            boolean invertSelection = ((CheckBox) findViewById(R.id.invert_selection)).isChecked();

            boolean isIncludeFiles = includeFiles.isChecked();
            boolean isIncludeFolders = includeFolders.isChecked();

            if (pages.getDisplayedChild() == 0) { // select by name
                String selectionString = ((EditText) findViewById(R.id.selection_string)).getText().toString();
                boolean caseSensitive = ((CheckBox) findViewById(R.id.case_sensitive)).isChecked();

                selectParams = new SelectParams(selectionString, caseSensitive, invertSelection, isIncludeFiles, isIncludeFolders);
            } else {
                selectParams = new SelectParams(datesPickerView.getSelectedDateAfter(), datesPickerView.getSelectedDateBefore(), invertSelection, isIncludeFiles, isIncludeFolders);
            }

            if (mCommand != null) {
                mCommand.execute(selectParams);
            }
            dismiss();
        });

    }

    protected void checkOkEnable(CheckBox includeFiles, CheckBox includeFolders) {
        boolean enabled = includeFiles.isChecked() || includeFolders.isChecked();
        mOkButton.setFocusableInTouchMode(enabled);
        mOkButton.setFocusable(enabled);
        mOkButton.setEnabled(enabled);
    }
}
