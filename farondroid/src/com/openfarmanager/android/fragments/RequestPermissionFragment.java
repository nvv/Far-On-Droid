package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openfarmanager.android.R;
import com.openfarmanager.android.utils.ParcelableWrapper;

/**
 * @author Vlad Namashko
 */
public class RequestPermissionFragment extends YesNoDialog {

    public static DialogFragment newInstance(String string, YesNoDialogListener listener) {
        RequestPermissionFragment dialog = new RequestPermissionFragment();
        Bundle args = new Bundle();
        args.putString("message", string);
        args.putParcelable("listener", new ParcelableWrapper<>(listener));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((TextView) view.findViewById(R.id.text)).setText(Html.fromHtml(getArguments().getString("message")));
        view.findViewById(R.id.root_frame).setBackgroundResource(R.color.grey_button);
        ((TextView) view.findViewById(R.id.button_yes)).setText(R.string.btn_ok);
        ((TextView) view.findViewById(R.id.button_no)).setText(R.string.btn_cancel);
        return view;
    }

}
