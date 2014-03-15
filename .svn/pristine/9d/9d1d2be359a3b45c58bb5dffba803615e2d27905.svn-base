package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.CancelableCommand;
import com.openfarmanager.android.utils.ParcelableWrapper;

/**
 * @author Vlad Namashko
 */
public class RequestPasswordDialog extends BaseDialog {

    private EditText mPassword;

    public static RequestPasswordDialog newInstance(CancelableCommand command) {
        RequestPasswordDialog dialog = new RequestPasswordDialog();
        Bundle args = new Bundle();
        args.putParcelable("command", new ParcelableWrapper<CancelableCommand>(command));
        dialog.setArguments(args);

        return dialog;
    }

    public static RequestPasswordDialog newInstance(CancelableCommand command, Object[] extraParams) {
        RequestPasswordDialog dialog = new RequestPasswordDialog();
        Bundle args = new Bundle();
        args.putParcelable("command", new ParcelableWrapper<CancelableCommand>(command));
        args.putParcelable("extraParams", new ParcelableWrapper<Object[]>(extraParams));
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Action_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getSafeString(R.string.app_name));

        View view = inflater.inflate(R.layout.dialog_request_password, container, false);

        mPassword = (EditText) view.findViewById(R.id.archive_password);

        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelableCommand command = getCommand();
                if (command != null) {
                    dismiss();
                    Parcelable extraParams = getArguments().getParcelable("extraParams");

                    //noinspection unchecked
                    command.execute(mPassword.getText().toString(), extraParams == null ? extraParams :
                            ((ParcelableWrapper<Object[]>) extraParams).value);
                }
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelableCommand command = getCommand();
                if (command != null) {
                    dismiss();
                    command.cancel();
                }
            }
        });

        return view;
    }

    private CancelableCommand getCommand() {
        //noinspection unchecked
        return ((ParcelableWrapper<CancelableCommand>)
                getArguments().getParcelable("command")).value;
    }

}
