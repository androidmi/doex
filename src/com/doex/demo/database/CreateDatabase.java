
package com.doex.demo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CreateDatabase extends SQLiteOpenHelper {
    private static final String TABLE = "test";

    public CreateDatabase(Context context) {
        super(context, "create.db", null, 1);
    }

    public void Insert() {
        ContentValues values = new ContentValues();
        values.put("number", "123456");
        values.put("date", System.currentTimeMillis());
        getWritableDatabase().insert(TABLE, null, values);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(TABLE);
        sb.append(" (");
        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append("number TEXT UNIQUE ON CONFLICT REPLACE,");
        sb.append(" date LONG");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
