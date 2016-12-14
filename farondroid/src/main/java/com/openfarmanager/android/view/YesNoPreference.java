package com.openfarmanager.android.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;

/**
 * author: Vlad Namashko
 */
public class YesNoPreference extends Dialog {

    private YesNoAction mAction;
    private View mDialogView;
    private String mMessage;

    public YesNoPreference(Context context, String message, YesNoAction runnable) {
        super(context, R.style.Action_Dialog);
        mAction = runnable;
        mMessage = message;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.confirm_dialog, null);

        ((TextView) mDialogView.findViewById(R.id.message)).setText(mMessage);

        mDialogView.findViewById(R.id.button_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAction.onResult(true);
                dismiss();
            }
        });

        mDialogView.findViewById(R.id.button_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAction.onResult(false);
                dismiss();
            }
        });

        setContentView(mDialogView);
    }

    public static interface YesNoAction {

        void onResult(boolean result);
    }


}