package com.openfarmanager.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.view.ToastNotification;

/**
 * @author Vlad Namashko
 */
public class YesNoDontAskAgainDialog extends Dialog {

    private View mDialogView;

    public YesNoDontAskAgainDialog(Context context) {
        super(context, R.style.Action_Dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.yes_no_dont_ask_again, null);

        mDialogView.findViewById(R.id.button_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastNotification.makeText(getContext(), App.sInstance.getString(R.string.recursive_delete_allowed), Toast.LENGTH_LONG).show();
                App.sInstance.getSettings().setFtpAllowRecursiveDelete(true);
                dismiss();
            }
        });

        mDialogView.findViewById(R.id.button_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mDialogView.findViewById(R.id.button_don_ask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.sInstance.getSettings().setDontAskAboutFtpPermission();
                dismiss();
            }
        });

        ((TextView) mDialogView.findViewById(R.id.text)).setText(R.string.allow_recursive_delete);

        setContentView(mDialogView);
    }

}
