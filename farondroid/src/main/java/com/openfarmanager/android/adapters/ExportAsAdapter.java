package com.openfarmanager.android.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.openfarmanager.android.App;
import com.openfarmanager.android.filesystem.GoogleDriveFile;
import com.openfarmanager.android.googledrive.api.Api;
import com.openfarmanager.android.googledrive.model.File;
import com.openfarmanager.android.model.MimeTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * author: Vlad Namashko
 */
public class ExportAsAdapter extends BaseAdapter {

    private HashMap<String, String> mExportLinks;

    public ExportAsAdapter(HashMap<String, String> exportLinks) {
        mExportLinks = exportLinks;
    }

    @Override
    public int getCount() {
        return mExportLinks.size();
    }

    @Override
    public Object getItem(int i) {
        return mExportLinks.values().toArray()[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = View.inflate(App.sInstance.getApplicationContext(), android.R.layout.simple_list_item_2, null);
        }

        Map.Entry<String, String> item = (Map.Entry<String, String>) mExportLinks.entrySet().toArray()[i];

        convertView.setTag(item.getValue());
        String name = File.getExportLinkAlias(item.getKey());
        ((TextView) convertView.findViewById(android.R.id.text1)).setText(name == null ? item.getKey() : name);

        return convertView;
    }
}