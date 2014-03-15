package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.openfarmanager.android.R;
import com.openfarmanager.android.utils.ParcelableWrapper;

import static com.openfarmanager.android.utils.Extensions.tryParse;

public class EditViewGotoDialog extends EditViewDialog {

    public static final int GOTO_LINE_POSITION = 0;
    public static final int GOTO_PERSENTS = 1;

    @Override
    protected void restoreSettings(View view) {
    }

    @Override
    protected void saveSettings(View view) {
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_edit_view_goto;
    }

    @Override
    protected void handleAction(View view) {
        int position = tryParse(((EditText) view.findViewById(R.id.go_to)).getText().toString(), 0);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.go_to_type);
        int checkedType = radioGroup.getCheckedRadioButtonId();
        getListener().goTo(position, checkedType == R.id.line_number ? GOTO_LINE_POSITION : GOTO_PERSENTS);
    }

    public static EditViewGotoDialog newInstance(EditViewDialog.EditDialogListener listener) {
        EditViewGotoDialog dialog = new EditViewGotoDialog();
        Bundle args = new Bundle();
        args.putParcelable("listener", new ParcelableWrapper<EditDialogListener>(listener));
        dialog.setArguments(args);

        return dialog;
    }
}
