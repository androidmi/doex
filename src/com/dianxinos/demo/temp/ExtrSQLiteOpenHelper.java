
package com.dianxinos.demo.temp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public abstract class ExtrSQLiteOpenHelper {

    private static final String TAG = "ExtrSQLiteOpenHelper";
    private SQLiteDatabase mDatabase;
    private String mDBPath;
    private String mDBDirector;
    private Context mContext;
    private static final String SD_CARD_PATH = Environment.getExternalStorageDirectory().getPath();

    private static final int READ = 1;
    private static final int WRITE = 2;

    private boolean mIsOpen;
    private int mNewVersion;
    private boolean mIsInitializing;

    public ExtrSQLiteOpenHelper(Context context, String name, int version) {
        mContext = context;
        mDBDirector = fillPath(SD_CARD_PATH, mContext.getPackageName());
        // mDBPath = fillPath(mDBDirector, name);
        mDBPath = name;
        mNewVersion = version;
    }

    public ExtrSQLiteOpenHelper(Context context) {
        mContext = context;
    }

    private SQLiteDatabase getDB(int type) {
        switch (type) {
            case READ:
                return SQLiteDatabase.openDatabase(mDBPath, null, SQLiteDatabase.OPEN_READONLY);
            case WRITE:
                return SQLiteDatabase.openDatabase(mDBPath, null, SQLiteDatabase.OPEN_READWRITE);
            default:
                throw new IllegalArgumentException("invalidate args");
        }
    }

    public SQLiteDatabase getWritableDatabase() {
        synchronized (this) {
            return getDatabaseLocked(true);
        }
    }

    public SQLiteDatabase getReadableDatabase() {
        synchronized (this) {
            return getDatabaseLocked(false);
        }
    }

    private synchronized void create() {
        File file = new File(mDBPath);
        if (!file.exists()) {
            File dbDirectot = new File(mDBDirector);
            if (!dbDirectot.exists()) {
                dbDirectot.mkdir();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(mDBPath, null);
            onCreate(db);
            if (db != null) {
                db.close();
            }
        }
    }

    private SQLiteDatabase getDatabaseLocked(boolean writable) {
        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                // Darn! The user closed the database by calling
                // mDatabase.close().
                mDatabase = null;
            } else if (!writable || !mDatabase.isReadOnly()) {
                // The database is already open for business.
                return mDatabase;
            }
        }
        if (mIsInitializing) {
            throw new IllegalStateException("getDatabase called recursively");
        }

        SQLiteDatabase db = mDatabase;
        try {
            mIsInitializing = true;

            if (db != null) {
                if (writable && db.isReadOnly()) {
                    // db.reopenReadWrite();
                }
            } else {
                try {
                    if (writable) {
//                        db = mContext.openOrCreateDatabase(mDBPath, Context.MODE_WORLD_WRITEABLE,
//                                null);
                        db = mContext.openOrCreateDatabase(mDBPath, Context.MODE_WORLD_READABLE,
                                null);
                    } else {
                        db = SQLiteDatabase.openDatabase(mDBPath, null, SQLiteDatabase.OPEN_READWRITE);
                    }
                } catch (SQLiteException ex) {
                    if (writable) {
                        throw ex;
                    }
                    Log.e(TAG, "Couldn't open " + mDBPath
                            + " for writing (will try read-only):", ex);
                    db = SQLiteDatabase.openDatabase(mDBPath, null, SQLiteDatabase.OPEN_READONLY);
                }
            }

            final int version = db.getVersion();
            if (version != mNewVersion) {
                if (db.isReadOnly()) {
                    throw new SQLiteException("Can't upgrade read-only database from version " +
                            db.getVersion() + " to " + mNewVersion + ": " + mDBPath);
                }

                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else {
                        if (version > mNewVersion) {
                            onDowngrade(db, version, mNewVersion);
                        } else {
                            onUpgrade(db, version, mNewVersion);
                        }
                    }
                    db.setVersion(mNewVersion);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            if (db.isReadOnly()) {
                Log.w(TAG, "Opened " + mDBPath + " in read-only mode");
            }

            mDatabase = db;
            return db;
        } finally {
            mIsInitializing = false;
            if (db != null && db != mDatabase) {
                db.close();
            }
        }
    }

    public abstract void onCreate(SQLiteDatabase db);

    public abstract void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    private String fillPath(String path1, String path2) {
        return path1 + File.separator + path2;
    }

}
