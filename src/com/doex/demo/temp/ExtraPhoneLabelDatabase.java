
package com.doex.demo.temp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.provider.BaseColumns;

import org.json.JSONArray;

import java.io.File;

public class ExtraPhoneLabelDatabase {

    private Context mContext;

    private static final String DB_NAME = "label";

    private static ExtraPhoneLabelDatabase sInstance;

    private static final File SDCARD_PATH = Environment.getExternalStorageDirectory();
    private static final String DB_DIRECTOR = SDCARD_PATH.getPath() + File.separator
            + "PhoneLabel";
    private static final String DB_PATH = DB_DIRECTOR + File.separator + DB_NAME;

    public ExtraPhoneLabelDatabase(Context context) {
        mContext = context;
    }

    public static ExtraPhoneLabelDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ExtraPhoneLabelDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    private static final String TAG = "ExtrPhoneLabelDatabase";

    private static final String TAG_TABLE = "phone_label";
    public static final String COUNT = "hot";
    public static final String NUMBER = "phone";
    public static final String LABEL = "tag";

    private static final String[] REPORT_COLUMNS = {
            BaseColumns._ID, NUMBER, LABEL, COUNT
    };

    private SQLiteDatabase getWritableDatabase() {
        synchronized (this) {
            File file = new File(DB_PATH);
            if (!file.exists()) {
                File dbDirectot = new File(DB_DIRECTOR);
                if (!dbDirectot.exists()) {
                    dbDirectot.mkdir();
                }
                SQLiteDatabase db = mContext.openOrCreateDatabase(DB_PATH,
                        Context.MODE_WORLD_WRITEABLE, null);
                onCreate(db);
                if (db != null) {
                    db.close();
                }
            }
            return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    private SQLiteDatabase getReadableDatabase(Context context) {
        synchronized (this) {
            return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        }
    }

    public Cursor getTop10(Context context) {
        try {

            Cursor cursor = getReadableDatabase(context).query(true, TAG_TABLE, REPORT_COLUMNS,
                    null, null, null, null,
                    COUNT + " desc", "10");
            return cursor;
        } catch (Exception e) {
            return null;
        }
    }

    public void insert(Context context, JSONArray array)
            throws Exception {
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
                sb.append("','");
                sb.append(a.getString(1));
                sb.append("');");
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

    public void insertValues(Context context, String number, int count, int label)
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

    private static void closeDb(SQLiteDatabase db) {
        if (db != null) {
            db.close();
        }
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(TAG_TABLE);
        sb.append(" (");
        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(COUNT + " LONG NOT NULL,");
        sb.append(NUMBER + " TEXT UNIQUE NOT NULL,");
        sb.append(LABEL + " INTEGER NOT NULL");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO upgrade
    }
}
