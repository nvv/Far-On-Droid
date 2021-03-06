package com.openfarmanager.android.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.openfarmanager.android.R;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.model.NetworkAccount;

import org.json.JSONObject;

/**
 * author: Vlad Namashko
 */
public class CreateBookmarkDialog extends BaseFileDialog {

    private String mDestinationText;
    private String mDestinationId;
    private NetworkAccount mNetworkAccount;

    public CreateBookmarkDialog(Context context, Handler handler, MainPanel inactivePanel, String destination) {
        this(context, handler, inactivePanel, destination, null, null);
    }

    public CreateBookmarkDialog(Context context, Handler handler, MainPanel inactivePanel, String destination, String destinationId, NetworkAccount account) {
        super(context, handler, inactivePanel);
        mDestinationText = destination;
        mDestinationId = destinationId;
        mNetworkAccount = account;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TextView) mDialogView.findViewById(R.id.current_action)).setText(getSafeString(R.string.create_bookmark));
        ((EditText) mDialogView.findViewById(R.id.destination)).setText(mDestinationText);

        String[] tokens = mDestinationText.split("/");
        String defaultLabel = tokens.length > 0 ? tokens[tokens.length - 1] : "";

        ((EditText) mDialogView.findViewById(R.id.bookmark_label)).setText(defaultLabel);

    }

    @Override
    public int getContentView() {
        return R.layout.dialog_file_action_create_bookmark;
    }

    @Override
    protected boolean validate() {
        EditText label = (EditText) mDialogView.findViewById(R.id.bookmark_label);
        if (TextUtils.isEmpty(label.getText().toString().trim())) {
            setErrorMessage(getSafeString(R.string.error_enter_label));
            return false;
        }

        return true;
    }

    @Override
    protected void execute() {
        String destination = mDestination.getText().toString().trim();
        if (mNetworkAccount != null) {
            try {
                JSONObject data = new JSONObject();
                data.put(Bookmark.ID, mDestinationId);
                data.put(Bookmark.PATH, mDestinationText);

                destination = data.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        mHandler.sendMessage(mHandler.obtainMessage(MainPanel.FILE_CREATE_BOOKMARK,
                new CreateBookmarkResult(mInactivePanel,
                        ((EditText) mDialogView.findViewById(R.id.bookmark_label)).getText().toString().trim(),
                        destination, mNetworkAccount)));
    }

    public class CreateBookmarkResult {
        public MainPanel inactivePanel;
        public String label;
        public String link;
        public NetworkAccount networkAccount;

        public CreateBookmarkResult(MainPanel panel, String label, String dest, NetworkAccount account) {
            inactivePanel = panel;
            this.label = label;
            link = dest;
            networkAccount = account;
        }
    }
}
