package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.AbstractCommand;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.utils.ParcelableWrapper;

/**
 * General user prompt dialog
 */
public class ConfirmActionDialog extends BaseDialog {

    private View mView;
    private EditText mDestination;

    public static ConfirmActionDialog newInstance(FileActionEnum action, AbstractCommand command,
                                                  String destination, boolean isRename) {


        return newInstance(action, command, null, destination, isRename);
    }

    public static ConfirmActionDialog newInstance(FileActionEnum action, AbstractCommand command, Object extraParam, String destination, boolean isRename) {

        ConfirmActionDialog dialog = new ConfirmActionDialog();
        Bundle args = new Bundle();

        args.putParcelable("action", new ParcelableWrapper<FileActionEnum>(action));
        args.putParcelable("command", new ParcelableWrapper<AbstractCommand>(command));
        args.putParcelable("extraParam", new ParcelableWrapper<Object>(extraParam));
        args.putString("destination", destination);
        args.putBoolean("isRename", isRename);

        dialog.setArguments(args);

        return dialog;

    }

    private FileActionEnum getAction() {
        //noinspection unchecked
        return ((ParcelableWrapper<FileActionEnum>) getArguments().getParcelable("action")).value;
    }

    private AbstractCommand getCommand() {
        //noinspection unchecked
        return ((ParcelableWrapper<AbstractCommand>) getArguments().getParcelable("command")).value;
    }

   private Object getExtraParam() {
        //noinspection unchecked
        return ((ParcelableWrapper<Object>) getArguments().getParcelable("extraParam")).value;
    }

    public void updateLabels(String title, String destLabel, String destDefaultValue) {
        ((TextView) mView.findViewById(R.id.current_action)).setText(title);
        ((TextView) mView.findViewById(R.id.destination_label)).setText(destLabel);
        ((EditText) mView.findViewById(R.id.destination)).setText(destDefaultValue);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Action_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getSafeString(R.string.app_name));

        mView = inflater.inflate(getLayout(), container, false);

        mDestination = (EditText) mView.findViewById(R.id.destination);

        mView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate() && getCommand() != null) {
                    okClicked();
                }
            }
        });
        mDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mDestination.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        String destinationFolder = getArguments().getString("destination");
        boolean isRename = getArguments().getBoolean("isRename");

        //setup view for certain action
        switch (getAction()) {
            case DELETE:
                mDestination.setVisibility(View.GONE);
                mView.setBackgroundResource(R.color.error_red);
                mView.findViewById(R.id.current_action).setBackgroundResource(R.color.error_red);
                updateLabels(getSafeString(R.string.action_delete), getSafeString(R.string.delete_files), "");
                break;

            case NEW:
                updateLabels(getSafeString(R.string.make_folder), getSafeString(R.string.create_new), "");
                // network panel, no possibility to create file (only create folder)
                if (Boolean.parseBoolean(getExtraParam().toString())) {
                    mView.findViewById(R.id.create_file).setVisibility(View.GONE);
                }

                break;

            case MOVE:
                updateLabels(getSafeString(isRename ? R.string.action_rename : R.string.action_move),
                        getSafeString(isRename ? R.string.rename_file : R.string.move_files), destinationFolder);
                break;

            case COPY:
                updateLabels(getSafeString(R.string.action_copy), getSafeString(R.string.copy_files), destinationFolder);
                break;

            case ARCHIVE_EXTRACT:
                updateLabels(getSafeString(R.string.action_archive_extract), getSafeString(R.string.archive_extract_to), destinationFolder);
                break;

            case SELECT:
                mView.findViewById(R.id.destination_label).setVisibility(View.GONE);
                updateLabels(getSafeString(R.string.btn_select), "", destinationFolder);
                break;

            case CREATE_BOOKMARK:
                ((TextView) mView.findViewById(R.id.current_action)).setText(getSafeString(R.string.create_bookmark));
                ((EditText) mView.findViewById(R.id.destination)).setText(destinationFolder);

                String[] tokens = destinationFolder.split("/");
                String defaultLabel = tokens.length > 0 ? tokens[tokens.length - 1] : "";

                ((EditText) mView.findViewById(R.id.bookmark_label)).setText(defaultLabel);
                break;

        }
        mDestination.setSelection(mDestination.getText().length());
        return mView;
    }

    private int getLayout() {
        switch (getAction()) {
            case SELECT:
                return R.layout.dialog_file_action_select;

            case CREATE_BOOKMARK:
                return R.layout.dialog_file_action_create_bookmark;

            case NEW:
                return R.layout.dialog_file_create_new;

            default:
                return R.layout.dialog_file_action_confirm;
        }
    }

    private void okClicked() {
        String stringArg = mDestination.getText().toString().trim();
        boolean boolArg = false;
        Object extraParam = getExtraParam();

        switch (getAction()) {
            case SELECT:
                boolArg = ((CheckBox) mView.findViewById(R.id.invert_selection)).isChecked();
                break;
            case CREATE_BOOKMARK:
                extraParam = ((EditText) mView.findViewById(R.id.bookmark_label)).getText().toString().trim();
                break;
            case NEW:
                boolArg = ((RadioGroup) mView.findViewById(R.id.new_item_type)).getCheckedRadioButtonId() == R.id.create_folder;
                break;
        }

        dismiss();
        getCommand().execute(App.sInstance.getFileSystemController().getInactivePanel(), stringArg, boolArg, extraParam);
    }

    private boolean validate() {
        switch (getAction()) {
            case DELETE:
                break;

            case NEW:
                String s = mDestination.getText().toString().trim();
                if (TextUtils.isEmpty(s)) {
                    mDestination.setError(getSafeString(R.string.error_enter_name));
                    return false;
                }
                break;

            case CREATE_BOOKMARK:
                EditText label = (EditText) mView.findViewById(R.id.bookmark_label);
                if (TextUtils.isEmpty(label.getText().toString().trim())) {
                    label.setError(getSafeString(R.string.error_enter_label));
                    return false;
                }
                break;

        }

        return true;
    }

}
