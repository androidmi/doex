
package com.dianxinos.demo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.dianxinos.demo.utils.DbUtils;
import com.dianxinos.demo.utils.DbUtils.CursorProvider;
import com.dianxinos.demo.utils.DbUtils.DbCallable;

import org.json.JSONArray;

public class PhoneLabelDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "label";
    private static final int DB_VERSION = 1;

    public PhoneLabelDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static final String TAG = "PhoneLabelDatabase";

    private static final String TAG_TABLE = "phone_label";
    public static final String COUNT = "hot";
    public static final String NUMBER = "phone";
    public static final String LABEL = "tag";

    private static final String RINGONCE_LABEL_TYPE = "0";

    private static PhoneLabelDatabase sInstance;

    private static final String[] REPORT_COLUMNS = {
            BaseColumns._ID, NUMBER, LABEL, COUNT
    };

    public static PhoneLabelDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PhoneLabelDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    public Cursor getTop10() {
        try {
            Cursor cursor = getReadableDatabase().query(TAG_TABLE,
                    PhoneLabelModel.FromCursorFactory.getProjection(),
                    null, null, null, null,
                    COUNT + " desc", "10");
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor getAllLabels() {
        try {
            Cursor cursor = getReadableDatabase().query(TAG_TABLE, REPORT_COLUMNS,
                    null, null, null, null,
                    COUNT + " desc");
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PhoneLabelModel getMarkedLableByNumber(final String number) {
        if (TextUtils.isEmpty(number)) {
            return null;
        }
        final CursorProvider phoneCursorProvider = new CursorProvider() {

            @Override
            public Cursor getCursor() throws SQLException {
                SQLiteDatabase db = getReadableDatabase();
                final String[] projection = PhoneLabelModel.FromCursorFactory
                        .getProjection();
                final String selection = NUMBER + " = ?";
                final String[] selectionArgs = {
                        number
                };
                return db.query(false, TAG_TABLE, projection, selection,
                        selectionArgs, null, null, null, "1");
            }

        };
        final DbCallable<PhoneLabelModel> phoneCallable = new DbCallable<PhoneLabelModel>() {

            @Override
            public PhoneLabelModel call(Cursor cursor) throws SQLException {
                if (cursor != null && cursor.moveToFirst()) {
                    return PhoneLabelModel.create(cursor);
                }
                return null;
            }

        };
        return DbUtils.dbCall(phoneCallable, phoneCursorProvider);
    }

    static int count = 1;

    public void insert(JSONArray phoneLabelList)
            throws Exception {
        if (phoneLabelList == null || phoneLabelList.length() == 0) {
            Log.i(TAG, "list empty");
            return;
        }
        Log.i(TAG, "insert data");
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            for (int i = 0; i < phoneLabelList.length(); i++) {
                JSONArray array = phoneLabelList.getJSONArray(i);
                PhoneLabelMappingModel model = PhoneLabelMappingModel.fromJson(array);
                ContentValues values = new ContentValues();
                // values.put(NUMBER, model.getNumber());
                values.put(NUMBER, count++);
                values.put(LABEL, model.getLabel());
                values.put(COUNT, model.getCount());
                db.insertWithOnConflict(TAG_TABLE, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("insert label data exception:" + e.getMessage());
        } finally {
            Log.i(TAG, "count:" + count);
            closeDb(db);
        }
    }

    public void deleteAllData() throws Exception {
        deleteData(null, null);
    }

    public void deletePhoneLabelData() throws Exception {
        String whereClause = LABEL + " != ?";
        String[] whereArgs = {
                RINGONCE_LABEL_TYPE
        };
        deleteData(whereClause, whereArgs);
    }

    private void deleteData(String whereClause, String[] whereArgs) throws Exception {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.delete(TAG_TABLE, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("delete phone label data exception:" + e.getMessage());
        } finally {
            closeDb(db);
        }
    }

    public void deleteRingonceLabelData() throws Exception {
        String whereClause = LABEL + " = ?";
        String[] whereArgs = {
                RINGONCE_LABEL_TYPE
        };
        deleteData(whereClause, whereArgs);
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
        sb.append(NUMBER + " TEXT UNIQUE NOT NULL,");
        sb.append(LABEL + " INTEGER NOT NULL,");
        sb.append(COUNT + " INTEGER NOT NULL");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
