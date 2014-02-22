package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.openfarmanager.android.R;
import com.openfarmanager.android.utils.ParcelableWrapper;

public class EditViewReplaceDialog extends EditViewSearchReplaceDialog {

    @Override
    protected int getLayout() {
        return R.layout.dialog_edit_view_replace;
    }

    @Override
    protected void handleAction(final View view) {
        getListener().doReplace(((EditText) view.findViewById(R.id.search_pattern)).getText().toString(),
                ((EditText) view.findViewById(R.id.replace_to)).getText().toString(),
                ((CheckBox) view.findViewById(R.id.case_sensitive)).isChecked(),
                ((CheckBox) view.findViewById(R.id.whole_words)).isChecked(),
                ((CheckBox) view.findViewById(R.id.regular_expression)).isChecked());
    }

    public static EditViewReplaceDialog newInstance(EditViewDialog.EditDialogListener listener) {
        EditViewReplaceDialog dialog = new EditViewReplaceDialog();
        Bundle args = new Bundle();
        args.putParcelable("listener", new ParcelableWrapper<EditDialogListener>(listener));
        dialog.setArguments(args);

        return dialog;
    }

}
