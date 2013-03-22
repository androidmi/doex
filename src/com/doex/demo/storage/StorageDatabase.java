
package com.doex.demo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.doex.demo.utils.FileUtils;

import org.json.JSONArray;

public class StorageDatabase extends SQLiteOpenHelper {

    private static final String TAG = "StorageDatabase";
    private static final String DB_NAME = "badge";
    public static final String SDCARD_DB_PATH = "/sdcard/DX-Dialer/data";
    private static final String SDCARD_DB_NAME = FileUtils.fillPath(SDCARD_DB_PATH, "badge");
    private static final int DB_VERSION = 1;

    private static StorageDatabase sInstance;
    private static StorageDatabase sSDCardInstance;

    public StorageDatabase(Context context, final String name) {
        super(context, name, null, DB_VERSION);
    }

    public static StorageDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StorageDatabase(context.getApplicationContext(), DB_NAME);
        }
        return sInstance;
    }

    public static StorageDatabase getSDCardInstance(Context context) {
        if (sSDCardInstance == null) {
            sSDCardInstance = new StorageDatabase(context.getApplicationContext(), SDCARD_DB_NAME);
        }
        return sSDCardInstance;
    }

    private static final String TAG_TABLE = "phone_label";
    public static final String COUNT = "hot";
    public static final String NUMBER = "phone";
    public static final String LABEL = "tag";

    private static final String[] REPORT_COLUMNS = {
            BaseColumns._ID, NUMBER, LABEL, COUNT
    };

    public Cursor getTop10() {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            Cursor cursor = db.query(TAG_TABLE,
                    REPORT_COLUMNS,
                    null, null, null, null,
                    null, null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // closeDb(db);
        }
    }

    public boolean hasData() {
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            Cursor cursor = db.query(TAG_TABLE, REPORT_COLUMNS,
                    null, null, null, null,
                    COUNT + " desc", "1");
            return cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeDb(db);
        }
    }

    public void insert(JSONArray array) {
        Log.i(TAG, "insert");
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            for (int i = 0; i < array.length(); i++) {
                StringBuilder sb = new StringBuilder();
                JSONArray a = array.getJSONArray(i);
                sb.append("insert into phone_label(hot,phone,tag) values(");
                sb.append(a.getInt(2));
                sb.append(",'");
                sb.append(a.getString(0));
                sb.append("',");
                sb.append(a.getString(1));
                sb.append(");");
                db.execSQL(sb.toString());
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDb(db);
        }
    }

    public void insertValues(String number, int count, int label)
            throws Exception {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NUMBER, number);
            values.put(COUNT, count);
            values.put(LABEL, label);
            db.insert(TAG_TABLE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDb(db);
        }
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TAG_TABLE, null, null);
    }

    private static void closeDb(SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(TAG_TABLE);
        sb.append(" (");
        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(COUNT + " INTEGER NOT NULL,");
        sb.append(NUMBER + " TEXT UNIQUE NOT NULL,");
        sb.append(LABEL + " INTEGER NOT NULL");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO upgrade
    }

}
