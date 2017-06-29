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
        final View dateToday = findViewById(R.id.date_today);
        final View datePeriod = findViewById(R.id.date_period);
        final TextView dateFrom = (TextView) findViewById(R.id.date_from);
        final TextView dateTo = (TextView) findViewById(R.id.date_to);
        final ViewFlipper pages = (ViewFlipper) findViewById(R.id.pages);

        mOkButton = (Button) findViewById(R.id.ok);

        CheckBox includeFiles = (CheckBox) findViewById(R.id.include_files);
        CheckBox includeFolders = (CheckBox) findViewById(R.id.include_folders);

        includeFiles.setOnCheckedChangeListener((button, isChecked) -> checkOkEnable(includeFiles, includeFolders));

        includeFolders.setOnCheckedChangeListener((button, isChecked) -> checkOkEnable(includeFiles, includeFolders));

        String searchText = App.sInstance.getSharedPreferences("action_dialog", 0).getString("select_pattern", "*");
        ((TextView) findViewById(R.id.selection_string)).setText(searchText);

        final Calendar calendar = Calendar.getInstance();

        final int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int todayMonth = calendar.get(Calendar.MONTH);
        final int todayYear = calendar.get(Calendar.YEAR);

        final SimpleWrapper<Boolean> isToday = new SimpleWrapper<Boolean>();

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

        dateFrom.setClickable(false);
        dateTo.setClickable(false);

        isToday.value = true;
        dateToday.setOnClickListener(v -> {
            datePeriod.setBackgroundResource(R.color.main_grey);
            dateToday.setBackgroundResource(R.color.selected_item);

            dateFrom.setClickable(false);
            dateTo.setClickable(false);

            isToday.value = true;
        });

        datePeriod.setOnClickListener(v -> {
            dateToday.setBackgroundResource(R.color.main_grey);
            datePeriod.setBackgroundResource(R.color.selected_item);

            dateFrom.setClickable(true);
            dateTo.setClickable(true);

            isToday.value = false;
        });

        dateFrom.setText(String.format("%s/%s/%s", todayYear, todayMonth, todayDay));
        dateTo.setText(String.format("%s/%s/%s", todayYear, todayMonth, todayDay));

        dateFrom.setOnClickListener(v -> {

            if (isToday.value) {
                return;
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) ->
                    dateFrom.setText(String.format("%s/%s/%s", year, monthOfYear, dayOfMonth)), todayYear, todayMonth, todayDay);
            datePickerDialog.show();
        });

        dateTo.setOnClickListener(v -> {

            if (isToday.value) {
                return;
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> dateTo.setText(String.format("%s/%s/%s", year, monthOfYear, dayOfMonth)), todayYear, todayMonth, todayDay);
            datePickerDialog.show();
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
                if (isToday.value) {
                    selectParams = new SelectParams(true, null, null, invertSelection, isIncludeFiles, isIncludeFolders);
                } else {
                    try {
                        String fromString = dateFrom.getText().toString();
                        String toString = dateTo.getText().toString();

                        String[] toStringParts = toString.split("\\/");
                        String[] fromStringParts = fromString.split("\\/");

                        calendar.set(Calendar.YEAR, Integer.parseInt(fromStringParts[0]));
                        calendar.set(Calendar.MONTH, Integer.parseInt(fromStringParts[1]));
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fromStringParts[2]));
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        Date dateFrom1 = calendar.getTime();

                        calendar.set(Calendar.YEAR, Integer.parseInt(toStringParts[0]));
                        calendar.set(Calendar.MONTH, Integer.parseInt(toStringParts[1]));
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(toStringParts[2]));
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);

                        Date dateTo1 = calendar.getTime();

                        selectParams = new SelectParams(false, dateFrom1, dateTo1, invertSelection, isIncludeFiles, isIncludeFolders);
                    } catch (Exception e) {
                        selectParams = new SelectParams(true, null, null, invertSelection, isIncludeFiles, isIncludeFolders);
                    }
                }
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
