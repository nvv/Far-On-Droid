package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;

import com.openfarmanager.android.R;

/**
 * @author Vlad Namashko.
 */
public class MarginSetupDialog extends FontSetupDialog {

    protected final static int MIN_VALUE = 0;
    protected final static int MAX_VALUE = 10;

    public MarginSetupDialog(Context context, SaveAction runnable) {
        super(context, runnable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView) mDialogView.findViewById(R.id.dialog_label)).setText(R.string.margin_size);
    }

    @Override
    protected int getMinValue() {
        return MIN_VALUE;
    }

    @Override
    protected int getMaxValue() {
        return MAX_VALUE;
    }

    @Override
    protected void onValueChanged(int progress, TextView fontSizeSample) {
        mNewFontSize = progress;
        fontSizeSample.setText(Integer.toString(progress));
    }

    @Override
    protected void setInitFontSize(int fontSize, TextView fontSizeSample) {
        fontSizeSample.setText(Integer.toString(fontSize));
        fontSizeSample.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    }
}
