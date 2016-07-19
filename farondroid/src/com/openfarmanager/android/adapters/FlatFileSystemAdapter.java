package com.openfarmanager.android.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.FontManager;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.core.archive.MimeTypes;
import com.openfarmanager.android.core.bookmark.BookmarkManager;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemFile;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.utils.CustomFormatter;
import com.openfarmanager.android.utils.Extensions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FlatFileSystemAdapter extends BaseAdapter {

    private static float sScaledDensity;

    static {
        sScaledDensity = App.sInstance.getResources().getDisplayMetrics().scaledDensity;
    }

    protected File mBaseDir;
    protected List<FileProxy> mSelectedFiles = new ArrayList<FileProxy>();
    protected List<FileProxy> mFiles = new ArrayList<FileProxy>();
    boolean mIsRoot;
    private String mFilter;
    private OnFolderScannedListener mListener;

    public static SimpleDateFormat sDateFormat = new SimpleDateFormat("dd MM yyyy HH:mm");

    public FlatFileSystemAdapter(File baseDir, OnFolderScannedListener listener) {
        mListener = listener;
        setBaseDir(baseDir);
    }

    protected FlatFileSystemAdapter() {

    }

    @Override
    public int getCount() {
        return mFiles.size() + (mIsRoot ? 0 : 1);
    }

    @Override
    public Object getItem(int i) {
        if (mIsRoot) {
            return mFiles.get(i);
        }
        if (i == 0) {
            return new FileSystemFile(mBaseDir, "..");
        }
        return mFiles.get(i - 1);
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
        TextView info = (TextView) view.findViewById(R.id.item_info);

        int size = App.sInstance.getSettings().getMainPanelFontSize();
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        info.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

        Settings settings = App.sInstance.getSettings();

        Typeface typeface = settings.getMainPanelFontType();
        name.setTypeface(typeface);
        info.setTypeface(typeface);

        int margin = (int) sScaledDensity * settings.getMainPanelCellMargin();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) name.getLayoutParams();
        params.setMargins(0, margin, 0, margin);
        name.setLayoutParams(params);

        File fileItem = (File) item;

        if (mSelectedFiles.contains(item)) {
            setColor(name, info, settings.getSelectedColor());
        } else if ((!fileItem.canRead() || fileItem.isHidden()) && !item.isVirtualDirectory()) {
            setColor(name, info, settings.getHiddenColor());
        } else if (item.isDirectory()) {
            setColor(name, info, settings.getFolderColor());
        } else if (ArchiveUtils.getMimeType(fileItem).equals(MimeTypes.MIME_APPLICATION_ANDROID_PACKAGE)) {
            setColor(name, info, settings.getInstallColor());
        } else if (ArchiveUtils.isArchiveFile(fileItem)) {
            setColor(name, info, settings.getArchiveColor());
        } else {
            setColor(name, info, settings.getTextColor());
        }

        if (item.isUpNavigator()) {
            info.setText(R.string.folder_up);
        } else if (item.isDirectory()) {
            if (item.isRoot()) {
                info.setText(R.string.folder_root);
            } else if (item.isVirtualDirectory()) {
                info.setText(R.string.virtual_folder);
            } else {
                info.setText(R.string.folder);
            }
        } else {
            int type = Extensions.tryParse(App.sInstance.getSettings().getFileInfoType(), 0);
            String value = "";
            switch (type) {
                case 0: default:
                    value = formatSize(item.getSize());
                    break;
                case 1:
                    long lastModified = item.lastModifiedDate();
                    value = lastModified == 0 ? "" : sDateFormat.format(new Date(lastModified));
                    break;
                case 2:
                    File file = (File) item;
                    value += file.canRead() ? "r" : "-";
                    value += file.canWrite() ? "w" : "-";

                    break;
            }

            info.setText(value);
        }
        return view;
    }

    private void setColor(TextView name, TextView size, int color) {
        name.setTextColor(color);
        size.setTextColor(color);
    }

    public void setSelectedFiles(List<FileProxy> selectedFiles) {
        mSelectedFiles.clear();
        mSelectedFiles.addAll(selectedFiles);
    }

    public void clearSelectedFiles() {
        mSelectedFiles.clear();
    }

    public void setBaseDir(File baseDir) {
        setBaseDir(baseDir, -1);
    }

    public void setBaseDir(File baseDir, final Integer selection) {
        if (baseDir == null) {
            return;
        }

        mBaseDir = baseDir;
        mIsRoot = FileSystemScanner.sInstance.isRoot(baseDir);
        clearSelectedFiles();

        final BookmarkManager bookmarkManager = App.sInstance.getBookmarkManager();
        final String path = mBaseDir.getAbsolutePath();

        if (path.equals(bookmarkManager.getBookmarksFolder())) {
            mFiles.clear();
            List<Bookmark> bookmarks = bookmarkManager.getBookmarks();
            for (Bookmark bookmark : bookmarks) {
                mFiles.add(new FileSystemFile(mBaseDir, bookmark.getBookmarkLabel(), bookmark));
            }
            notifyDataSetChanged();
        } else {
            new AsyncTask<Void, Void, List<FileProxy>>() {
                @Override
                protected List<FileProxy> doInBackground(Void... params) {
                    List<FileProxy> files = FileSystemScanner.sInstance.fallingDown(mBaseDir, mFilter);
                    if (bookmarkManager.isBookmarksEnabled() && path.equals(bookmarkManager.getBookmarksPath())) {
                        files.add(new FileSystemFile(mBaseDir, BookmarkManager.BOOKMARKS_FOLDER, true));
                        FileSystemScanner.sInstance.sort(files);
                    }

                    return files;
                }

                @Override
                protected void onPostExecute(List<FileProxy> aVoid) {
                    mFiles = aVoid;
                    notifyDataSetChanged();
                    if (mListener != null) {
                        mListener.onScanFinished(selection);
                    }
                }
            }.execute();

        }


    }

    protected String formatSize(long length) {
        return CustomFormatter.formatBytes(length);
    }

    public void filter(String obj) {
        mFilter = obj;
        setBaseDir(mBaseDir);
        notifyDataSetChanged();
    }

    public void resetFilter() {
        mFilter = null;
    }

    public int getItemPosition(File oldDir) {
        if (mFiles == null) {
            return 0;
        }

        String old = oldDir.getName();
        for (int i = 0; i < mFiles.size(); i++) {
            if (old.equals(mFiles.get(i).getName())) {
                return i + (mIsRoot ? 0 : 1);
            }
        }
        return 0;
    }

    public static interface OnFolderScannedListener {
        void onScanFinished(Integer selection);
    }
}
