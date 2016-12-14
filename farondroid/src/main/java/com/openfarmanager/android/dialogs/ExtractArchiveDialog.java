package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.openfarmanager.android.R;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.utils.Extensions;

/**
 * author: Vlad Namashko
 */
public class ExtractArchiveDialog extends BaseFileDialog {

    private String mDefaultPath;
    private boolean mIsCompressed;

    public ExtractArchiveDialog(Context context, Handler handler, MainPanel inactivePanel,
                                boolean isCompressed, String defaultPath) {
        super(context, handler, inactivePanel);
        mIsCompressed = isCompressed;
        mDefaultPath = defaultPath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLabels(getSafeString(R.string.action_archive_extract), getSafeString(R.string.archive_extract_to), mDefaultPath);
    }

    @Override
    public int getContentView() {
        return R.layout.dialog_file_action_confirm;
    }

    @Override
    protected boolean validate() {
        if (Extensions.isNullOrEmpty(mDestination.getText().toString())) {
            setErrorMessage(getSafeString(R.string.error_destination_empty));
            return false;
        }

        return true;
    }

    @Override
    protected void execute() {
        mHandler.sendMessage(mHandler.obtainMessage(MainPanel.FILE_EXTRACT_ARCHIVE,
                new ExtractArchiveResult(mInactivePanel, mDestination.getText().toString())));
    }

    public class ExtractArchiveResult {
        public MainPanel inactivePanel;
        public String destination;
        public boolean isCompressed;

        public ExtractArchiveResult(MainPanel panel, String dest) {
            inactivePanel = panel;
            destination = dest;
            isCompressed = mIsCompressed;
        }
    }
}
