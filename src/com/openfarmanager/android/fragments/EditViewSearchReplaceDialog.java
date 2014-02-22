package com.openfarmanager.android.fragments;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;

public abstract class EditViewSearchReplaceDialog extends EditViewDialog {

    protected void restoreSettings(View view) {
        SharedPreferences preferences = App.sInstance.getSharedPreferences("EDIT_VIEW_DIALOG", 0);
        ((EditText) view.findViewById(R.id.search_pattern)).setText(preferences.getString("pattern", ""));
        try {
            ((EditText) view.findViewById(R.id.replace_to)).setText(preferences.getString("replaceTo", ""));
        } catch (NullPointerException ignore) { }
    }

    protected void saveSettings(View view) {
        String pattern = ((EditText) view.findViewById(R.id.search_pattern)).getText().toString();

        SharedPreferences.Editor editor = App.sInstance.getSharedPreferences("EDIT_VIEW_DIALOG", 0).edit()
                .putString("pattern", pattern);

        try {
            String replaceTo = ((EditText) view.findViewById(R.id.replace_to)).getText().toString();
            editor.putString("replaceTo", replaceTo);
        } catch (Exception ignore) { }

        editor.commit();
    }

}
