package com.openfarmanager.android.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.model.FileActionEnum;
import com.openfarmanager.android.utils.ParcelableWrapper;

import java.io.Serializable;

/**
 * Context menu action list dialog
 */
public class FileActionDialog extends BaseDialog {

    public static FileActionDialog newInstance(FileActionEnum[] actions, OnActionSelectedListener listener) {
        FileActionDialog dialog = new FileActionDialog();
        Bundle args = new Bundle();
        args.putParcelableArray("actions", actions);
        args.putStringArray("actionNames", FileActionEnum.names(actions));
        args.putParcelable("listener", new ParcelableWrapper<OnActionSelectedListener>(listener));

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Action_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getSafeString(R.string.app_name));
        View view = inflater.inflate(R.layout.dialog_file_action_menu, container, false);

        final ListView actionsList = (ListView) view.findViewById(R.id.action_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                App.sInstance.getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1,
                (String[]) getArguments().getStringArray("actionNames")) {

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
                //noinspection unchecked
                OnActionSelectedListener listener = ((ParcelableWrapper<OnActionSelectedListener>)
                        getArguments().getParcelable("listener")).value;
                if (listener != null) {
                    listener.onActionSelected((FileActionEnum) getArguments().getParcelableArray("actions")[i]);
                }
            }
        });
        
        return view;
    }

    public static interface OnActionSelectedListener extends Serializable {
        void onActionSelected(FileActionEnum action);
    }
}
