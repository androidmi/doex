
package com.doex.demo.chart.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.doex.demo.chart.model.ChartModel;
import com.doex.demo.utils.DbUtils;
import com.doex.demo.utils.DbUtils.CursorProvider;
import com.doex.demo.utils.DbUtils.DbCallable;
import com.doex.demo.utils.Logger;
public class ChartDatabase extends SQLiteOpenHelper {

    private static final String TAG = "ChartDatabase";

    public ChartDatabase(Context context) {
        super(context, CHART_DB_NAME, null, CHART_DB_VERSION);
    }

    private static final String CHART_DB_NAME = "chart";
    private static final int CHART_DB_VERSION = 1;

    private static ChartDatabase mInstance;

    public static ChartDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ChartDatabase(context);
        }
        return mInstance;
    }

    public void createTable(String name, String[] columns, String[] type) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("");
    }

    public void dropAllData() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null);
        DbUtils.closeDB(db);
    }

    public void insert(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE, null, values);
        DbUtils.closeDB(db);
    }

    public ChartModel getChartModelBySeries(final int series) {
        CursorProvider cursorProvider = new CursorProvider() {

            @Override
            public Cursor getCursor() throws SQLException {
                SQLiteDatabase db = getWritableDatabase();
                String selection = SERIES + " = ?";
                String[] selectArgs = {
                        String.valueOf(series)
                };
                return db.query(TABLE, ChartModel.FromCursorFactory.getProjection(), selection,
                        selectArgs,
                        null,
                        null, "_id desc");
            }

        };
        DbCallable<ChartModel> callable = new DbCallable<ChartModel>() {

            @Override
            public ChartModel call(Cursor cursor) throws SQLException {
                return ChartModel.create(cursor);
            }
        };
        return DbUtils.dbCall(callable, cursorProvider);
    }

    private static final String TABLE = "chart";
    public static final String SERIES = "series";
    public static final String XDATA = "xdata";
    public static final String YDATA = "ydata";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.i(TAG, "onCreate");
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(TABLE);
        sb.append(" (");
        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(SERIES + " INTEGER NOT NULL ,");
        sb.append(XDATA + " double NOT NULL,");
        sb.append(YDATA + " double NOT NULL");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
