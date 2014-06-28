package com.openfarmanager.android.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

        String searchText = App.sInstance.getSharedPreferences("action_dialog", 0).getString("select_pattern", "*");
        ((TextView) findViewById(R.id.selection_string)).setText(searchText);

        final Calendar calendar = Calendar.getInstance();

        final int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int todayMonth = calendar.get(Calendar.MONTH);
        final int todayYear = calendar.get(Calendar.YEAR);

        final SimpleWrapper<Boolean> isToday = new SimpleWrapper<Boolean>();

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.setBackgroundResource(R.color.main_grey);
                name.setBackgroundResource(R.color.selected_item);

                pages.setDisplayedChild(0);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date.setBackgroundResource(R.color.selected_item);
                name.setBackgroundResource(R.color.main_grey);

                pages.setDisplayedChild(1);
            }
        });

        dateFrom.setClickable(false);
        dateTo.setClickable(false);

        isToday.value = true;
        dateToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePeriod.setBackgroundResource(R.color.main_grey);
                dateToday.setBackgroundResource(R.color.selected_item);

                dateFrom.setClickable(false);
                dateTo.setClickable(false);

                isToday.value = true;
            }
        });

        datePeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateToday.setBackgroundResource(R.color.main_grey);
                datePeriod.setBackgroundResource(R.color.selected_item);

                dateFrom.setClickable(true);
                dateTo.setClickable(true);

                isToday.value = false;
            }
        });

        dateFrom.setText(String.format("%s/%s/%s", todayYear, todayMonth, todayDay));
        dateTo.setText(String.format("%s/%s/%s", todayYear, todayMonth, todayDay));

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isToday.value) {
                    return;
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateFrom.setText(String.format("%s/%s/%s", year, monthOfYear, dayOfMonth));
                    }
                }, todayYear, todayMonth, todayDay);
                datePickerDialog.show();
            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isToday.value) {
                    return;
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTo.setText(String.format("%s/%s/%s", year, monthOfYear, dayOfMonth));
                    }
                }, todayYear, todayMonth, todayDay);
                datePickerDialog.show();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectParams selectParams;
                if (pages.getDisplayedChild() == 0) { // select by name
                    String selectionString = ((EditText) findViewById(R.id.selection_string)).getText().toString();
                    boolean invertSelection = ((CheckBox) findViewById(R.id.invert_selection)).isChecked();

                    selectParams = new SelectParams(SelectParams.SelectionType.NAME, selectionString, invertSelection, false, null, null);
                } else {
                    if (isToday.value) {
                        selectParams = new SelectParams(SelectParams.SelectionType.MODIFICATION_DATE, "", false, true, null, null);
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

                            Date dateFrom = calendar.getTime();

                            calendar.set(Calendar.YEAR, Integer.parseInt(toStringParts[0]));
                            calendar.set(Calendar.MONTH, Integer.parseInt(toStringParts[1]));
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(toStringParts[2]));
                            calendar.set(Calendar.HOUR_OF_DAY, 23);
                            calendar.set(Calendar.MINUTE, 59);

                            Date dateTo = calendar.getTime();

                            selectParams = new SelectParams(SelectParams.SelectionType.MODIFICATION_DATE, "", false, false, dateFrom, dateTo);
                        } catch (Exception e) {
                            selectParams = new SelectParams(SelectParams.SelectionType.MODIFICATION_DATE, "", false, true, null, null);
                        }
                    }
                }

                if (mCommand != null) {
                    mCommand.execute(selectParams);
                }
                dismiss();
            }
        });

    }
}
