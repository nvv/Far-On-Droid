package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.openfarmanager.android.R;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.utils.Extensions;

/**
 * author: Vlad Namashko
 */
public class CopyMoveFileDialog extends BaseFileDialog {

    private boolean mIsCopy;
    private boolean mIsRename;
    private String mDefaultDestination;

    public CopyMoveFileDialog(Context context, Handler handler, MainPanel inactivePanel) {
        super(context, handler, inactivePanel);
        mIsCopy = true;
    }

    public CopyMoveFileDialog(Context context, Handler handler, MainPanel inactivePanel,
                              boolean isRename, String destination) {
        this(context, handler, inactivePanel);
        mIsCopy = false;
        mIsRename = isRename;
        mDefaultDestination = destination;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String destinationFolder = mDefaultDestination == null ? mInactivePanel.getCurrentPath() : mDefaultDestination;
        if (mIsCopy) {
            updateLabels(getSafeString(R.string.action_copy), getSafeString(R.string.copy_files), destinationFolder);
        } else {
            updateLabels(getSafeString(mIsRename ? R.string.action_rename : R.string.action_move),
                    getSafeString(mIsRename ? R.string.rename_file : R.string.move_files), destinationFolder);
        }
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
        if (mIsCopy) {
            mHandler.sendMessage(mHandler.obtainMessage(MainPanel.FILE_COPY,
                    new CopyMoveFileResult(mInactivePanel, mDestination.getText().toString())));
        } else {
            mHandler.sendMessage(mHandler.obtainMessage(MainPanel.FILE_MOVE,
                    new CopyMoveFileResult(mInactivePanel, mDestination.getText().toString(), mIsRename)));
        }
    }

    public class CopyMoveFileResult {
        public MainPanel inactivePanel;
        public String destination;
        public boolean isRename;

        public CopyMoveFileResult(MainPanel panel, String dest) {
            inactivePanel = panel;
            destination = dest;
        }

        public CopyMoveFileResult(MainPanel panel, String dest, boolean rename) {
            inactivePanel = panel;
            destination = dest;
            isRename = rename;
        }
    }
}
