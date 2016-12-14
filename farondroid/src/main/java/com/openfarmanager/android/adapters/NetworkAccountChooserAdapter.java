package com.openfarmanager.android.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.network.NetworkApi;
import com.openfarmanager.android.model.NetworkAccount;

import java.util.List;

/**
 * @author Vlad Namashko
 */
public class NetworkAccountChooserAdapter extends BaseAdapter {

    private NetworkApi mApi;
    private List<NetworkAccount> mAccounts;
    private OnDeleteItemListener mListener;

    public NetworkAccountChooserAdapter(NetworkApi api, OnDeleteItemListener listener) {
        mApi = api;
        mListener = listener;
        mAccounts = mApi.getAuthorizedAccounts();
    }

    public void dataSetChanged() {
        mAccounts = mApi.getAuthorizedAccounts();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAccounts.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if (i == 0) {
            return mApi.newAccount();
        }
        return mAccounts.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = View.inflate(App.sInstance.getApplicationContext(), R.layout.network_account_item, null);
        }

        final NetworkAccount account = (NetworkAccount) getItem(i);

        convertView.setTag(account);
        ((TextView) convertView.findViewById(R.id.text)).setText(account.getUserName());

        convertView.findViewById(R.id.delete).setVisibility(i == 0 ? View.GONE : View.VISIBLE);
        convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAccountDelete(account);
            }
        });

        return convertView;
    }

    public static interface OnDeleteItemListener {
        void onAccountDelete(NetworkAccount account);
    }
}
