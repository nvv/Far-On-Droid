package com.openfarmanager.android.adapters;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.openfarmanager.android.App;
import com.openfarmanager.android.R;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.core.archive.ArchiveUtils;
import com.openfarmanager.android.core.archive.MimeTypes;
import com.openfarmanager.android.core.bookmark.BookmarkManager;
import com.openfarmanager.android.filesystem.FileProxy;
import com.openfarmanager.android.filesystem.FileSystemFile;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.model.OpenDirectoryActionListener;
import com.openfarmanager.android.utils.CustomFormatter;
import com.openfarmanager.android.utils.Extensions;
import com.openfarmanager.android.utils.StorageUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Vlad Namashko
 */
public class FileSystemAdapter extends RecyclerView.Adapter<FileSystemAdapter.ViewHolder> implements SectionTitleProvider {

    private static float sScaledDensity;

    static {
        sScaledDensity = App.sInstance.getResources().getDisplayMetrics().scaledDensity;
    }

    protected File mBaseDir;
    protected List<FileProxy> mSelectedFiles = new ArrayList<FileProxy>();
    protected List<FileProxy> mFiles = new ArrayList<FileProxy>();
    boolean mIsRoot;
    private String mFilter;

    private OpenDirectoryActionListener mListener;

    private OnItemClickListener mOnItemClickListener;

    public static SimpleDateFormat sDateFormat = new SimpleDateFormat("dd MM yyyy HH:mm");

    public FileSystemAdapter(File baseDir, Integer selection, OpenDirectoryActionListener listener) {
        mListener = listener;
        setBaseDir(baseDir, selection == null ? -1 : selection, true);
    }

    protected FileSystemAdapter() {

    }

    public Object getItem(int i) {
        if (mIsRoot) {
            return mFiles.get(i);
        }
        if (i == 0) {
            return new FileSystemFile(mBaseDir, "..");
        }
        return mFiles.get(i - 1);
    }

    public List<FileProxy> getFiles() {
        return mFiles;
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

    public void setBaseDir(final File baseDir, final Integer selection) {
        setBaseDir(baseDir, selection, false);
    }

    private void setBaseDir(final File baseDir, final Integer selection, final boolean restoreDefaultPath) {
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
                    if (files != null && bookmarkManager.isBookmarksEnabled() && path.equals(bookmarkManager.getBookmarksPath())) {
                        files.add(new FileSystemFile(mBaseDir, BookmarkManager.BOOKMARKS_FOLDER, true));
                        FileSystemScanner.sInstance.sort(files);
                    }

                    return files;
                }

                @Override
                protected void onPostExecute(List<FileProxy> files) {
                    if (files != null) {
                        mFiles = files;
                        notifyDataSetChanged();
                        mListener.onDirectoryOpened(baseDir, selection);
                    } else {
                        if (restoreDefaultPath) {
                            setBaseDir(StorageUtils.getSdCard());
                        } else {
                            mListener.onError();
                        }
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


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(App.sInstance.getApplicationContext()).inflate(R.layout.panel_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        bindView(holder, position);
        setupListeners(holder, position);
    }

    protected void bindView(ViewHolder holder, final int position) {
        FileProxy item = (FileProxy) getItem(position);
        File fileItem = (File) item;

        Settings settings = App.sInstance.getSettings();

        holder.name.setText(item.getName());

        if (mSelectedFiles.contains(item)) {
            setColor(holder.name, holder.info, settings.getSelectedColor());
        } else if ((!fileItem.canRead() || fileItem.isHidden()) && !item.isVirtualDirectory()) {
            setColor(holder.name, holder.info, settings.getHiddenColor());
        } else if (item.isDirectory()) {
            setColor(holder.name, holder.info, settings.getFolderColor());
        } else if (ArchiveUtils.getMimeType(fileItem).equals(MimeTypes.MIME_APPLICATION_ANDROID_PACKAGE)) {
            setColor(holder.name, holder.info, settings.getInstallColor());
        } else if (ArchiveUtils.isArchiveFile(fileItem)) {
            setColor(holder.name, holder.info, settings.getArchiveColor());
        } else {
            setColor(holder.name, holder.info, settings.getTextColor());
        }

        holder.configureCell(settings);

        if (item.isUpNavigator()) {
            holder.info.setText(R.string.folder_up);
        } else if (item.isDirectory()) {
            if (item.isRoot()) {
                holder.info.setText(R.string.folder_root);
            } else if (item.isVirtualDirectory()) {
                holder.info.setText(R.string.virtual_folder);
            } else {
                holder.info.setText(R.string.folder);
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

            holder.info.setText(value);
        }
    }

    protected void setupListeners(ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.onItemLongClick(v, position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles.size() + (mIsRoot ? 0 : 1);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public String getSectionTitle(int position) {
        if (position == 0) {
            return "";
        } else {
            FileProxy fileProxy = (FileProxy) getItem(position);
            return fileProxy.getName().substring(0, 1);
        }

//        return getItem(position).substring(0, 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView info;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.item_name);
            info = (TextView) view.findViewById(R.id.item_info);
        }

        public void configureCell(Settings settings) {
            int size = App.sInstance.getSettings().getMainPanelFontSize();
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            info.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

            Typeface typeface = settings.getMainPanelFontType();
            name.setTypeface(typeface);
            info.setTypeface(typeface);

            int margin = (int) sScaledDensity * settings.getMainPanelCellMargin();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) name.getLayoutParams();
            params.setMargins(0, margin, 0, margin);
            name.setLayoutParams(params);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}
