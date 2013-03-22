
package com.doex.demo.storage;

import android.content.Context;

import com.doex.demo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

public class StorageManager {

    private static final String TAG = "StorageManager";

    private static StorageManager sInstance;
    private Context mContext;

    private StorageManager(Context context) {
        mContext = context;
    }

    public static StorageManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new StorageManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public void initDatabase(StorageDatabase db, JSONArray array) {
        try {
            db.insert(array);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSDCardDatabase(Context context, JSONArray array) {
        initDatabase(getDatabase(context, true), array);
    }

    public void initLocalDatabase(Context context, JSONArray array) {
        initDatabase(getDatabase(context, false), array);
    }

    public StorageDatabase getDatabase(Context context, boolean isSDCard) {
        if (isSDCard) {
            return StorageDatabase.getSDCardInstance(context);
        } else {
            return StorageDatabase.getInstance(context);
        }
    }

    public StorageDatabase getDatabase() {
        StorageDatabase db = null;
        if (Utils.isSDCardReady()) {
            db = StorageDatabase.getSDCardInstance(mContext);
        } else {
            db = StorageDatabase.getInstance(mContext);
        }
        if (!db.hasData()) {
            initDatabase(db, Utils.getData(mContext));
        }
        return db;
    }

}
