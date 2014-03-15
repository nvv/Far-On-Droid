package com.openfarmanager.android.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.model.NetworkEnum;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedMap;

/**
 * @author Vlad Namashko
 */
public class SelectEncodingAdapter extends BaseAdapter {

    private File mSelectedFile;
    private ArrayList<Charset> mCharsets = new ArrayList<Charset>();
    private String mDefaultCharsetKey;
    private int mDefaultItemPosition;

    public SelectEncodingAdapter(File selectedFile, String recommendedCharset) {
        mSelectedFile = selectedFile;
        mDefaultCharsetKey = recommendedCharset;

        SortedMap availableCharset = Charset.availableCharsets();
        Set keys = availableCharset.keySet();
        int i = 0;
        for (Object key : keys) {
            String keyValue = (String) key;
            Charset charset = (Charset) availableCharset.get(keyValue);
            mCharsets.add(charset);

            if (charset.name().equals(mDefaultCharsetKey)) {
                mDefaultItemPosition = i;
                if (mDefaultItemPosition > 5) {
                    mDefaultItemPosition -= 5;
                }
            }
            i++;
        }
    }

    @Override
    public int getCount() {
        return mCharsets.size();
    }

    @Override
    public Object getItem(int i) {
        return mCharsets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public int getDefaultItemPosition() {
        return mDefaultItemPosition;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = View.inflate(App.sInstance.getApplicationContext(), android.R.layout.simple_list_item_2, null);
        }

        Charset charset = mCharsets.get(i);

        convertView.setTag(charset);
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(charset.displayName());

        return convertView;
    }

}
