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
import java.util.Locale;

/**
 * @author Vlad Namashko
 */
public class DatesPickerView extends FrameLayout {

    private CheckBox mDateBeforeEnabled;
    private CheckBox mDateAfterEnabled;

    private Button mDateBefore;
    private Button mDateAfter;

    private Date mSelectedDateBefore;
    private Date mSelectedDateAfter;

    public DatesPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.dates_picker_view, this);

        mDateBefore = (Button) findViewById(R.id.date_before);
        mDateAfter = (Button) findViewById(R.id.date_after);

        mDateBeforeEnabled = (CheckBox) findViewById(R.id.date_before_enabled);
        mDateAfterEnabled = (CheckBox) findViewById(R.id.date_after_enabled);

        final Calendar calendar = Calendar.getInstance();

        final int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int todayMonth = calendar.get(Calendar.MONTH);
        final int todayYear = calendar.get(Calendar.YEAR);

        mDateBefore.setText(formatDate(todayYear, todayMonth + 1, todayDay));
        mDateAfter.setText(formatDate(todayYear, todayMonth + 1, todayDay));

        selectDateAfter(calendar, todayYear, todayMonth, todayDay);

        mDateBefore.setOnClickListener(v -> new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            mDateBefore.setText(formatDate(year, monthOfYear + 1, dayOfMonth));
            mDateBeforeEnabled.setChecked(true);

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);

            mSelectedDateBefore = calendar.getTime();
        }, todayYear, todayMonth, todayDay).show());

        mDateAfter.setOnClickListener(v -> new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            mDateAfter.setText(formatDate(year, monthOfYear + 1, dayOfMonth));
            mDateAfterEnabled.setChecked(true);

            selectDateAfter(calendar, year, monthOfYear, dayOfMonth);
        }, todayYear, todayMonth, todayDay).show());

    }

    private String formatDate(int year, int month, int day) {
        return String.format(Locale.US, "%s/%02d/%02d", year, month, day);
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

    public void initWithValues(long preSelectedDateBefore, long preSelectedDateAfter) {
        Calendar calendar = Calendar.getInstance();

        if (preSelectedDateBefore != -1) {
            Date date = new Date(preSelectedDateBefore);
            calendar.setTime(date);
            mDateBefore.setText(formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            mSelectedDateBefore = date;
        }

        if (preSelectedDateAfter != -1) {
            Date date = new Date(preSelectedDateAfter);
            calendar.setTime(date);
            mDateAfter.setText(formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            mSelectedDateAfter = date;
        }
    }

}
