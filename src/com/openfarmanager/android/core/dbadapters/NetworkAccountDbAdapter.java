package com.openfarmanager.android.core.dbadapters;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.openfarmanager.android.core.DataStorageHelper;
import com.openfarmanager.android.model.Bookmark;

import static com.openfarmanager.android.core.dbadapters.NetworkAccountDbAdapter.Columns.*;

/**
 * @author Vlad Namashko
 */
public class NetworkAccountDbAdapter {

    public static final String TABLE_NAME = "network_accounts";

    public static final class Columns {
        public static final String ID = "id";
        public static final String USER_NAME = "user_name";
        public static final String NETWORK_TYPE = "newtork_type";
        public static final String AUTH_DATA = "auth_data";
    }

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USER_NAME + " TEXT not null,"
            + NETWORK_TYPE + " INTEGER not null,"
            + AUTH_DATA + " TEXT not null"
            + ");";

    public static Cursor getAccountById(long id) {
        SQLiteDatabase db = DataStorageHelper.getDatabase();
        if(db == null) return null;
        try {
            return db.query(TABLE_NAME, null, ID + "=" + id, null, null, null, null);
        } catch(Exception e) {
            e.printStackTrace();
            DataStorageHelper.closeDatabase();
            return null;
        }
    }

    public static Cursor getAccounts(int type) {
        SQLiteDatabase db = DataStorageHelper.getDatabase();
        if(db == null) return null;
        try {
            return db.query(TABLE_NAME, null, NETWORK_TYPE + "=" + type, null, null, null, null);
        } catch(Exception e) {
            e.printStackTrace();
            DataStorageHelper.closeDatabase();
            return null;
        }
    }

    public static long insert(String userName, int networkType, String authData) throws SQLiteException {
        long defaultValue = -1;
        ContentValues values = new ContentValues();
        values.put(USER_NAME, userName);
        values.put(NETWORK_TYPE, networkType);
        values.put(AUTH_DATA, authData);

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

    public static int count(int type) {
        SQLiteDatabase db = DataStorageHelper.getDatabase();
        int defaultValue = 0;
        if(db == null) return defaultValue;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(1) FROM " + TABLE_NAME + " WHERE " + NETWORK_TYPE + "=" + type, null);
            return (cursor == null || !cursor.moveToFirst()) ?
                    defaultValue : cursor.getInt(0);
        } catch(Exception e) {
            e.printStackTrace();
            return defaultValue;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
            DataStorageHelper.closeDatabase();
        }
    }

    public static void delete(long id) {
        SQLiteDatabase db = DataStorageHelper.getDatabase();
        if(db == null) return;
        try {
            db.delete(TABLE_NAME, "ROWID=" + id, null);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DataStorageHelper.closeDatabase();
        }
    }

}
