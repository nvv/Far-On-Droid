package com.openfarmanager.android.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.archive.ArchiveScanner;
import com.openfarmanager.android.filesystem.ArchiveFile;
import com.openfarmanager.android.filesystem.FileProxy;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Vlad Namashko
 */
public class ArchiveEntryAdapter extends FlatFileSystemAdapter {

    private List<FileProxy> mArchiveEntries;
    private ArchiveScanner.File mCurrentLevelParent;

    public ArchiveEntryAdapter(ArchiveScanner.File node) {
        setItems(node);
    }

    public void setItems(ArchiveScanner.File node) {
        mArchiveEntries = new ArrayList<FileProxy>();
        mCurrentLevelParent = node;
        if (node != null && node.getSortedChildren() != null && !node.getSortedChildren().isEmpty()) {
            for (ArchiveScanner.File file : node.getSortedChildren()) {
                mArchiveEntries.add(new ArchiveFile(file));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArchiveEntries.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if (i == 0) {
            return new ArchiveFile(ArchiveScanner.File.createUpperNode(mCurrentLevelParent));
        }
        return mArchiveEntries.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(App.sInstance.getApplicationContext()).inflate(R.layout.panel_item, null);
        }

        FileProxy item = (FileProxy) getItem(i);

        TextView name = (TextView) view.findViewById(R.id.item_name);
        name.setText(item.getName());
        TextView size = (TextView) view.findViewById(R.id.item_info);

        if (mSelectedFiles.contains(item)) {
            name.setTextColor(Color.YELLOW);
            size.setTextColor(Color.YELLOW);
        } else if (item.isDirectory()) {
            name.setTextColor(Color.WHITE);
            size.setTextColor(Color.WHITE);
        } else {
            name.setTextColor(Color.CYAN);
            size.setTextColor(Color.CYAN);
        }

        if (item.isRoot()) {
            size.setText(R.string.folder_root);
        } else if (item.isUpNavigator()) {
            size.setText(R.string.folder_up);
        } else if (item.isDirectory()) {
            size.setText(R.string.folder);
        } else {
            size.setText(formatSize(item.getSize()));
        }
        return view;
    }

    public void setSelectedFilesInArchive(List<ArchiveFile> selectedFiles) {
        mSelectedFiles.clear();
        mSelectedFiles.addAll(selectedFiles);
    }

}
