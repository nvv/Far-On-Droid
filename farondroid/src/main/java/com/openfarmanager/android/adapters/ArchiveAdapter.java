package com.openfarmanager.android.adapters;

import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.filesystem.ArchiveFile;
import com.openfarmanager.android.filesystem.FileProxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class ArchiveAdapter extends FileSystemAdapter {

//    private List<FileProxy> mArchiveEntries;
    private ArchiveScanner.File mCurrentLevelParent;

    public ArchiveAdapter(ArchiveScanner.File node) {
        setItems(node);
    }

    public void setItems(ArchiveScanner.File node) {
        mFiles = new ArrayList<>();
        mCurrentLevelParent = node;
        if (node != null && node.getSortedChildren() != null && !node.getSortedChildren().isEmpty()) {
            mFiles.addAll(node.getSortedChildren());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFiles.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if (i == 0) {
            return new ArchiveFile(ArchiveScanner.File.createUpperNode(mCurrentLevelParent));
        }
        return mFiles.get(i - 1);
    }

    @Override
    protected void bindView(ViewHolder holder, final int position) {

        FileProxy item = (FileProxy) getItem(position);

        holder.name.setText(item.getName());

        Settings settings = App.sInstance.getSettings();
        if (mSelectedFiles.contains(item)) {
            holder.name.setTextColor(settings.getSecondaryColor());
            holder.info.setTextColor(settings.getSecondaryColor());
        } else if (item.isDirectory()) {
            holder.name.setTextColor(settings.getFolderColor());
            holder.info.setTextColor(settings.getFolderColor());
        } else {
            holder.name.setTextColor(settings.getTextColor());
            holder.info.setTextColor(settings.getTextColor());
        }

        holder.configureCell(settings);

        if (item.isRoot()) {
            holder.info.setText(R.string.folder_root);
        } else if (item.isUpNavigator()) {
            holder.info.setText(R.string.folder_up);
        } else if (item.isDirectory()) {
            holder.info.setText(R.string.folder);
        } else {
            holder.info.setText(formatSize(item.getSize()));
        }
    }
}
