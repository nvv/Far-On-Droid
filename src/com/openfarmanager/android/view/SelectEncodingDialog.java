package com.openfarmanager.android.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.adapters.SelectEncodingAdapter;
import static com.openfarmanager.android.controllers.EditViewController.*;

import java.io.File;
import java.nio.charset.Charset;

/**
 * author: Vlad Namashko
 */
public class SelectEncodingDialog extends Dialog {

    private Handler mHandler;
    private File mSelectedFile;
    private View mDialogView;
    private CheckBox mSaveAsDefault;

    public SelectEncodingDialog(Context context, Handler handler, File selectedFile) {
        super(context, R.style.Action_Dialog);
        mHandler = handler;
        mSelectedFile = selectedFile;
        setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogView = View.inflate(App.sInstance.getApplicationContext(), R.layout.dialog_select_encoding, null);
        mSaveAsDefault = (CheckBox) mDialogView.findViewById(R.id.save_as_default);
        ListView encodings = (ListView) mDialogView.findViewById(R.id.encoding_list);
        SelectEncodingAdapter adapter = new SelectEncodingAdapter(mSelectedFile, getDefaultCharset());
        encodings.setAdapter(adapter);
        encodings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Charset character = (Charset) view.getTag();
                mHandler.sendMessage(mHandler.obtainMessage(MSG_SELECT_ENCODING,
                        new Pair<Boolean, Charset>(mSaveAsDefault.isChecked(), character)));
                dismiss();
            }
        });
        encodings.setSelection(adapter.getDefaultItemPosition());
        setContentView(mDialogView);
    }

    private String getDefaultCharset() {
        String charset = App.sInstance.getSettings().getDefaultCharset();
        return charset != null ? charset : Charset.defaultCharset().name();
    }

}
