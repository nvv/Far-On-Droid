package com.openfarmanager.android.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.model.NetworkEnum;

/**
 * @author Vlad Namashko
 */
public class NetworkChooserAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return NetworkEnum.sOrderedItems.length;
    }

    @Override
    public Object getItem(int i) {
        return NetworkEnum.values()[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = View.inflate(App.sInstance.getApplicationContext(), R.layout.network_type_item, null);
        }

        NetworkEnum networkEnum = NetworkEnum.valuesList()[i];

        convertView.setTag(networkEnum);
        ((TextView) convertView.findViewById(R.id.text)).setText(NetworkEnum.getNetworkLabel(networkEnum));

        return convertView;
    }
}
