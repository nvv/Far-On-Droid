package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import com.openfarmanager.android.R;
import com.openfarmanager.android.fragments.MainPanel;

/**
 * author: Vlad Namashko
 */
public class DeleteFileDialog extends BaseFileDialog {

    public DeleteFileDialog(Context context, Handler handler, MainPanel inactivePanel) {
        super(context, handler, inactivePanel);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDestination.setVisibility(View.GONE);
        mDialogView.setBackgroundResource(R.color.error_red);
        mDialogView.findViewById(R.id.current_action).setBackgroundResource(R.color.error_red);
        updateLabels(getSafeString(R.string.action_delete), getSafeString(R.string.delete_files), "");

    }

    @Override
    public int getContentView() {
        return R.layout.dialog_file_action_confirm;
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    protected void execute() {
        mHandler.sendMessage(mHandler.obtainMessage(MainPanel.FILE_DELETE,
                new DeleteFileResult(mInactivePanel, mDestination.getText().toString())));
    }

    public class DeleteFileResult {
        public MainPanel inactivePanel;
        public String destination;

        public DeleteFileResult(MainPanel panel, String dest) {
            inactivePanel = panel;
            destination = dest;
        }
    }
}
