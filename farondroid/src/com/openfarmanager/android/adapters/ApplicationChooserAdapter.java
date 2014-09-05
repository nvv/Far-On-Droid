package com.openfarmanager.android.adapters;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class ApplicationChooserAdapter extends BaseAdapter {

    private List<ResolveInfo> mAvailableApplications;
    private List<ResolveInfo> mAllApplications;
    private PackageManager mPackageManager;

    private boolean mShowAllApplications;
    private int mAvailableApps;

    public ApplicationChooserAdapter(List<ResolveInfo> apps, List<ResolveInfo> allApps) {
        mAvailableApplications = apps;
        mAllApplications = allApps;
        mPackageManager = App.sInstance.getPackageManager();

        mAvailableApps = mAvailableApplications.size();

        Comparator<ResolveInfo> comparator = new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
                return String.valueOf(resolveInfo.loadLabel(mPackageManager)).compareTo(String.valueOf(resolveInfo2.loadLabel(mPackageManager)));
            }
        };
        Collections.sort(mAvailableApplications, comparator);
        Collections.sort(mAllApplications, comparator);
    }

    @Override
    public int getCount() {
        int count = 1; // 'show/hide all' item
        count += mAvailableApps == 0 ? 1 : mAvailableApps; // if no available apps for this intent found - one line for error message
        if (mShowAllApplications) {
            count += mAllApplications.size();
        }
        return count;
    }

    @Override
    public ResolveInfo getItem(int position) {
        if (position < mAvailableApplications.size()) {
            return mAvailableApplications.get(position);
        } else {
            int topItems = mShowAllApplications && mAvailableApplications.size() == 0 ? 2 : 1;
            return mAllApplications.get(position - mAvailableApplications.size() - topItems);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(App.sInstance.getApplicationContext(), R.layout.application_chooser_item, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.app_title);
        ImageView image = (ImageView) convertView.findViewById(R.id.app_image);

        if (position == 0 && mAvailableApps == 0) {
            // no available apps for this intent and currently we are rendering first item - show error
            title.setText(R.string.error_no_app_to_open_file);
            image.setVisibility(View.GONE);
            convertView.setOnClickListener(null);
            return convertView;
        }

        boolean showDelimiter = (mAvailableApps == 0 && position == 1) ||
                (mAvailableApps > 0 && position == mAvailableApps);
        if (showDelimiter) { // 'show all' item
            title.setText(mShowAllApplications ? R.string.hide_all : R.string.show_all);
            image.setVisibility(View.GONE);
            convertView.setTag(R.string.is_delimiter, true);
            return convertView;
        }

        ResolveInfo info = getItem(position);

        image.setImageDrawable(info.activityInfo.loadIcon(mPackageManager));
        image.setVisibility(View.VISIBLE);
        title.setText(info.activityInfo.loadLabel(mPackageManager));
        convertView.setTag(R.string.is_delimiter, false);

        return convertView;
    }

    public void delimiterClicked() {
        mShowAllApplications = !mShowAllApplications;
        notifyDataSetChanged();
    }
}