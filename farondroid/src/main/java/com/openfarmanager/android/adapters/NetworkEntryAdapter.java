package com.openfarmanager.android.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.filesystem.FakeFile;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.utils.Extensions;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class NetworkEntryAdapter extends FileSystemAdapter {

    //private List<FileProxy> mEntries;
    //private String mParentPath;
    private FileProxy mUpNavigator;

    public NetworkEntryAdapter(List<FileProxy> entries, FileProxy upNavigator) {
        setItems(entries, upNavigator);
    }

    public void setItems(List<FileProxy> entries, FileProxy upNavigator) {
        mFiles = entries;
        mUpNavigator = upNavigator;
        mIsRoot = upNavigator.isRoot();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFiles.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if (i == 0) {
            return mUpNavigator;
        }
        return mFiles.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    protected void bindView(ViewHolder holder, final int position) {

        FileProxy item = (FileProxy) getItem(position);

        holder.name.setText(item.getName());

        Settings settings = App.sInstance.getSettings();

//        int fontSize = settings.getMainPanelFontSize();
//        holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
//        holder.info.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
//
//        Typeface typeface = settings.getMainPanelFontType();
//        holder.name.setTypeface(typeface);
//        holder.info.setTypeface(typeface);

        if (mSelectedFiles.contains(item)) {
            holder.name.setTextColor(settings.getSelectedColor());
            holder.info.setTextColor(settings.getSelectedColor());
        } else if (item.isDirectory()) {
            holder.name.setTextColor(settings.getFolderColor());
            holder.info.setTextColor(settings.getFolderColor());
        } else {
            holder.name.setTextColor(settings.getTextColor());
            holder.info.setTextColor(settings.getTextColor());
        }

        holder.configureCell(settings);

        FakeFile fakeFile = null;
        if (item instanceof FakeFile) {
            fakeFile = (FakeFile) item;
        }

        if (item.isRoot() || (fakeFile != null && fakeFile.isRoot())) {
            holder.info.setText(R.string.folder_root);
        } else if (item.isUpNavigator() || (fakeFile != null && fakeFile.isUpNavigator())) {
            holder.info.setText(R.string.folder_up);
        } else if (item.isVirtualDirectory()) {
            holder.info.setText(R.string.virtual_folder);
        } else if (item.isDirectory()) {
            holder.info.setText(R.string.folder);
        } else {
            int type = Extensions.tryParse(App.sInstance.getSettings().getFileInfoType(), 0);
            String value = "";
            switch (type) {
                case 0: default:
                    value = formatSize(item.getSize());
                    break;
                case 1:
                    value = sDateFormat.format(new Date(item.lastModifiedDate()));
                    break;
                case 2:
                    value += "rw";
                    break;
            }

            holder.info.setText(value);
        }
    }
}
