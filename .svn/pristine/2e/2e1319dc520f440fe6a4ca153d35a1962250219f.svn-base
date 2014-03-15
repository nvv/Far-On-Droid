package com.openfarmanager.android.core.bookmark;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import com.openfarmanager.android.App;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.core.dbadapters.BookmarkDBAdapter;
import com.openfarmanager.android.model.Bookmark;
import com.openfarmanager.android.model.TaskStatusEnum;

import java.util.ArrayList;
import java.util.List;

public class BookmarkManager {

    private static final String BOOKMARKS_ENABLED = "bookmarks_enabled";
    private static final String BOOKMARKS_PATH = "bookmarks_path";

    public static final String BOOKMARKS_FOLDER = "bookmarks";

    public boolean isBookmarksEnabled() {
        return getPreferences().getBoolean(BOOKMARKS_ENABLED, true);
    }

    public String getBookmarksPath() {
        return getPreferences().getString(BOOKMARKS_PATH, "/");
    }

    public String getBookmarksFolder() {
        return getBookmarksPath() + BOOKMARKS_FOLDER;
    }

    protected SharedPreferences getPreferences() {
        return App.sInstance.getSharedPreferences("bookmark_settings", 0);
    }

    public TaskStatusEnum createBookmark(String path, String label) {
        Bookmark bookmark = new Bookmark(path, label);
        long id = -1;
        try {
            id = BookmarkDBAdapter.insert(bookmark);
        } catch (SQLiteException e) {
            e.printStackTrace();
            return TaskStatusEnum.ERROR_CREATE_BOOKMARK;
        }

        return id == -1 ? TaskStatusEnum.ERROR_CREATE_BOOKMARK : TaskStatusEnum.OK;
    }

    public void deleteBookmark(Bookmark bookmark) {
        try {
            BookmarkDBAdapter.delete(bookmark);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public List<Bookmark> getBookmarks() {
        List<Bookmark> bookmarks = new ArrayList<Bookmark>();

        Cursor cursor = BookmarkDBAdapter.getBookmarks();
        if (cursor != null) {
            try {
                int idxId = cursor.getColumnIndex(BookmarkDBAdapter.Columns.ID);
                int idxLabel = cursor.getColumnIndex(BookmarkDBAdapter.Columns.LABEL);
                int idxPath = cursor.getColumnIndex(BookmarkDBAdapter.Columns.PATH);

                while (cursor.moveToNext()) {
                    Bookmark bookmark = new Bookmark(cursor.getString(idxPath), cursor.getString(idxLabel));
                    bookmark.setBookmarkId(cursor.getLong(idxId));
                    bookmarks.add(bookmark);
                }
            } finally {
                cursor.close();
                DataStorageHelper.closeDatabase();
            }
        }

        return bookmarks;
    }
}
