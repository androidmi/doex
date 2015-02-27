
package com.doex.demo.database;

import android.test.AndroidTestCase;

import java.io.File;

public class Databasetest extends AndroidTestCase {
    public void testInsert() {
        File file = getContext().getDatabasePath("create.db");
        file.delete();
        CreateDatabase db = new CreateDatabase(mContext);
        db.Insert();
        db.Insert();
        db.Insert();
        db.Insert();
        db.Insert();
        db.Insert();
        db.Insert();
    }
}
