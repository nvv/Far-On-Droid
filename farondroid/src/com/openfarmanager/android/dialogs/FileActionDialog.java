package com.openfarmanager.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.fragments.MainPanel;
import com.openfarmanager.android.model.FileActionEnum;

/**
 * Context menu action list dialog.
 *
 * author: Vlad Namashko
 */
public class FileActionDialog extends Dialog {

    private FileActionEnum[] mActions;
    private String[] mActionNames;
    private Handler mHandler;

    public FileActionDialog(Context context, FileActionEnum[] actions, Handler handler) {
        super(context, R.style.Action_Dialog);
        mActions = actions;
        mActionNames = FileActionEnum.names(actions);
        mHandler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_file_action_menu, null);

        final ListView actionsList = (ListView) view.findViewById(R.id.action_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                App.sInstance.getApplicationContext(), android.R.layout.simple_list_item_1,
                android.R.id.text1, (String[]) mActionNames) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View item = super.getView(position, convertView, parent);
                item.setMinimumWidth(actionsList.getWidth());
                return item;
            }
        };

        actionsList.setAdapter(adapter);

        actionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dismiss();
                mHandler.sendMessage(Message.obtain(mHandler, MainPanel.SELECT_ACTION, mActions[i]));
            }
        });
        setContentView(view);
    }

}
