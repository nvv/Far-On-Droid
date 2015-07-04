package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioGroup;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.fragments.MainPanel;

import static com.openfarmanager.android.utils.Extensions.isNullOrEmpty;

/**
 * author: Vlad Namashko
 */
public class CreateFileDialog extends BaseFileDialog {

    private boolean mIsNetworkPanel;

    public CreateFileDialog(Context context, Handler handler, MainPanel inactivePanel, boolean isNetworkPanel) {
        super(context, handler, inactivePanel);
        mIsNetworkPanel = isNetworkPanel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // network panel, no possibility to create file (only create folder)
        if (mIsNetworkPanel) {
            mDialogView.findViewById(R.id.create_file).setVisibility(View.GONE);
        }
    }

    @Override
    public int getContentView() {
        return R.layout.dialog_file_create_new;
    }

    protected boolean validate() {

        if (isNullOrEmpty(mDestination.getText().toString())) {
            setErrorMessage(getSafeString(R.string.error_enter_name));
            return false;
        }

        return true;
    }

    @Override
    protected void execute() {
        mHandler.sendMessage(mHandler.obtainMessage(MainPanel.FILE_CREATE,
                new NewFileResult(mInactivePanel, mDestination.getText().toString(),
                        ((RadioGroup) mDialogView.findViewById(R.id.new_item_type)).getCheckedRadioButtonId() == R.id.create_folder)));
    }

    public class NewFileResult {
        public MainPanel inactivePanel;
        public String destination;
        public boolean isFolder;

        public NewFileResult(MainPanel panel, String dest, boolean isFolder) {
            inactivePanel = panel;
            destination = dest;
            this.isFolder = isFolder;
        }
    }
}
