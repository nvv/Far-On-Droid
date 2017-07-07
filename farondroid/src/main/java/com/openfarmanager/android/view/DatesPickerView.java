package com.openfarmanager.android.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.openfarmanager.android.R;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Vlad Namashko
 */
public class DatesPickerView extends FrameLayout {

    private CheckBox mDateBeforeEnabled;
    private CheckBox mDateAfterEnabled;

    private Date mSelectedDateBefore;
    private Date mSelectedDateAfter;

    public DatesPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.dates_picker_view, this);

        final Button dateBefore = (Button) findViewById(R.id.date_before);
        final Button dateAfter = (Button) findViewById(R.id.date_after);

        mDateBeforeEnabled = (CheckBox) findViewById(R.id.date_before_enabled);
        mDateAfterEnabled = (CheckBox) findViewById(R.id.date_after_enabled);

        final Calendar calendar = Calendar.getInstance();

        final int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int todayMonth = calendar.get(Calendar.MONTH);
        final int todayYear = calendar.get(Calendar.YEAR);

        dateBefore.setText(String.format("%s/%s/%s", todayYear, todayMonth + 1, todayDay));
        dateAfter.setText(String.format("%s/%s/%s", todayYear, todayMonth + 1, todayDay));

        mDateAfterEnabled.setChecked(true);
        selectDateAfter(calendar, todayYear, todayMonth, todayDay);

        dateBefore.setOnClickListener(v -> new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            dateBefore.setText(String.format("%s/%s/%s", year, monthOfYear + 1, dayOfMonth));
            mDateBeforeEnabled.setChecked(true);

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);

            mSelectedDateBefore = calendar.getTime();
        }, todayYear, todayMonth, todayDay).show());

        dateAfter.setOnClickListener(v -> new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            dateAfter.setText(String.format("%s/%s/%s", year, monthOfYear + 1, dayOfMonth));
            mDateAfterEnabled.setChecked(true);

            selectDateAfter(calendar, year, monthOfYear, dayOfMonth);
        }, todayYear, todayMonth, todayDay).show());

    }

    protected void selectDateAfter(Calendar calendar, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        mSelectedDateAfter = calendar.getTime();
    }


    public Date getSelectedDateBefore() {
        return mDateBeforeEnabled.isChecked() ? mSelectedDateBefore : null;
    }

    public Date getSelectedDateAfter() {
        return mDateAfterEnabled.isChecked() ? mSelectedDateAfter : null;
    }
}
