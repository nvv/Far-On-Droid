package com.openfarmanager.android.core.dbadapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.model.Bookmark;

import static com.openfarmanager.android.core.dbadapters.BookmarkDBAdapter.Columns.*;

public class BookmarkDBAdapter {

    public static final String TABLE_NAME = "bookmarks";

    public static final class Columns {
        public static final String ID = "id";
        public static final String LABEL = "label";
        public static final String PATH = "path";
    }

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + LABEL + " TEXT not null UNIQUE,"
            + PATH + " TEXT not null"
            + ");";

    public static Cursor getBookmarks() {
        SQLiteDatabase db = DataStorageHelper.getDatabase();
        if(db == null) return null;
        try {
            return db.query(TABLE_NAME, null, null, null, null, null, null);
        } catch(Exception e) {
            e.printStackTrace();
            DataStorageHelper.closeDatabase();
            return null;
        }
    }

    public static long insert(Bookmark bookmark) throws SQLiteException {
        long defaultValue = -1;
        ContentValues values = new ContentValues();
        values.put(Columns.LABEL, bookmark.getBookmarkLabel());
        values.put(Columns.PATH, bookmark.getBookmarkPath());

        SQLiteDatabase db = DataStorageHelper.getDatabase();
        if(db == null) return defaultValue;
        try {
            return db.insert(TABLE_NAME, null, values);
        } catch(Exception e) {
            if (e instanceof SQLiteException) throw (SQLiteException) e;
            e.printStackTrace();
            return defaultValue;
        } finally {
            DataStorageHelper.closeDatabase();
        }
    }

    public static void deleteAll() {
        delete(null);
    }


    public static void delete(Bookmark bookmark) {
        SQLiteDatabase db = DataStorageHelper.getDatabase();
        if(db == null || bookmark == null) return;
        try {
            String where = bookmark == null ? null : "ROWID=" + bookmark.getBookmarkId();
            db.delete(TABLE_NAME, where, null);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DataStorageHelper.closeDatabase();
        }
    }

    public static boolean update(Bookmark bookmark) {
        ContentValues values = new ContentValues();
        values.put(Columns.LABEL, bookmark.getBookmarkLabel());
        values.put(Columns.PATH, bookmark.getBookmarkPath());

        SQLiteDatabase db = DataStorageHelper.getDatabase();
        if(db == null) return false;
        try {
            return db.update(TABLE_NAME, values, "ROWID = " + bookmark.getBookmarkId(), null) != -1;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DataStorageHelper.closeDatabase();
        }
    }
}
