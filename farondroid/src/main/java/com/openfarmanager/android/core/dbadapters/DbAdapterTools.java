package com.openfarmanager.android.core.dbadapters;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.openfarmanager.android.App;
import com.openfarmanager.android.core.DataStorageHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Vlad Namashko.
 */
public class DbAdapterTools {

    public static void createTableLoadData(SQLiteDatabase db, String createTable, String dataFileName) {
        db.execSQL(createTable);

        AssetManager assetManager = App.sInstance.getResources().getAssets();
        InputStream in = null;
        try {
            in = assetManager.open(dataFileName);

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                db.execSQL(line);
            }
            br.close();

        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String getField(int key, String tableName, String keyName, String fieldName) {
        String value = "Unknown";

        ContentValues values = new ContentValues();
        values.put(keyName, key);

        SQLiteDatabase database = DataStorageHelper.getDatabase();

        Cursor cursor = database.query(tableName, new String[]{fieldName}, keyName + " = " + key, null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        cursor.close();
        DataStorageHelper.closeDatabase();

        return value;
    }
}
