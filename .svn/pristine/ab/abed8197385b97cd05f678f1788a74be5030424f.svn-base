package com.openfarmanager.android.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.utils.ParcelableWrapper;

/**
 * Search params dialog
 */
public class SearchActionDialog extends BaseDialog {

    public static SearchActionDialog newInstance(boolean onlyFileSearch, OnSearchConfirmedListener listener) {
        SearchActionDialog dialog = new SearchActionDialog();
        Bundle args = new Bundle();
        args.putBoolean("file_search", onlyFileSearch);
        args.putParcelable("listener", new ParcelableWrapper<OnSearchConfirmedListener>(listener));
        dialog.setArguments(args);

        return dialog;
    }

    public static interface OnSearchConfirmedListener {
        public void onSearchConfirmed(String fileMask, String keyword, boolean isCaseSensitive, boolean isWholeWords);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Action_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_search, container, false);
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        EditText text = (EditText) view.findViewById(R.id.destination);
        view.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object listenerArg = getArguments().getParcelable("listener");
                // very weird, but who knows
                if (listenerArg != null) {
                    //noinspection unchecked
                    OnSearchConfirmedListener listener = ((ParcelableWrapper<OnSearchConfirmedListener>) listenerArg).value;
                    if (listener != null) {
                        String fileMask = ((EditText) view.findViewById(R.id.destination)).getText().toString();
                        String keyword = ((EditText) view.findViewById(R.id.keyword)).getText().toString();
                        boolean caseSensitive = ((CheckBox) view.findViewById(R.id.case_sensitive)).isChecked();
                        boolean wholeWords = ((CheckBox) view.findViewById(R.id.whole_words)).isChecked();
                        saveSettings(fileMask, keyword, caseSensitive, wholeWords);
                        listener.onSearchConfirmed(
                                fileMask,
                                keyword,
                                caseSensitive,
                                wholeWords);
                    }
                }
                dismiss();
            }
        });
        restoreSettings(view);
        text.setSelection(text.getText().length());
        text = (EditText) view.findViewById(R.id.keyword);
        text.setSelection(text.getText().length());

        if (getArguments().getBoolean("file_search")) {
            view.findViewById(R.id.keyword_label).setVisibility(View.GONE);
            view.findViewById(R.id.keyword).setVisibility(View.GONE);
            view.findViewById(R.id.case_sensitive).setVisibility(View.GONE);
            view.findViewById(R.id.whole_words).setVisibility(View.GONE);
        }

        return view;
    }

    private void restoreSettings(View view) {
        SharedPreferences preferences = App.sInstance.getSharedPreferences("SEARCH_DIALOG", 0);
        ((EditText) view.findViewById(R.id.destination)).setText(preferences.getString("filemask", "*"));
        ((EditText) view.findViewById(R.id.keyword)).setText(preferences.getString("keyword", ""));
        ((CheckBox) view.findViewById(R.id.case_sensitive)).setChecked(preferences.getBoolean("case_sensitive", false));
        ((CheckBox) view.findViewById(R.id.whole_words)).setChecked(preferences.getBoolean("whole_words", false));
    }

    private void saveSettings(String fileMask, String keyword, boolean caseSensitive, boolean wholeWords) {
        App.sInstance.getSharedPreferences("SEARCH_DIALOG", 0).edit()
                .putString("filemask", fileMask)
                .putString("keyword", keyword)
                .putBoolean("case_sensitive", caseSensitive)
                .putBoolean("whole_words", wholeWords).commit();
    }
}
