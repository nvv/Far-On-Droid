package com.openfarmanager.android.core.dbadapters;

import android.database.sqlite.SQLiteDatabase;

import static com.openfarmanager.android.core.dbadapters.VendorDbAdapter.Columns.*;

/**
 * @author Vlad Namashko.
 */
public class VendorDbAdapter {

    private static final String TABLE_NAME = "mac_vendors";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + Columns.MAC + " INT NOT NULL, "
            + Columns.VENDOR + " TEXT NOT NULL);"
            + "CREATE INDEX mac_index on mac_vendors (mac)";
    private static final String DATA_FILE_NAME = "mac_vendors_data";

    public static void createTable(SQLiteDatabase db) {
        DbAdapterTools.createTableLoadData(db, CREATE_TABLE, DATA_FILE_NAME);
    }

    public static String getVendor(int mac) {
        return DbAdapterTools.getField(mac, TABLE_NAME, MAC, VENDOR);
    }

    public static final class Columns {
        public static final String MAC = "mac";
        public static final String VENDOR = "vendor";
    }
}
